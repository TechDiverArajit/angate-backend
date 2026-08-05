package io.angate.AnGate.service;

import com.google.zxing.WriterException;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import io.angate.AnGate.Utility.QRCodeGenerator;
import io.angate.AnGate.config.RazorpayConfig;
import io.angate.AnGate.dto.Payment.CheckInRequest;
import io.angate.AnGate.dto.Payment.CheckInResponse;
import io.angate.AnGate.dto.Payment.PaymentVerificationRequest;
import io.angate.AnGate.dto.booking.BookingRequest;
import io.angate.AnGate.dto.booking.BookingResponse;
import io.angate.AnGate.entity.Booking;
import io.angate.AnGate.entity.TicketType;
import io.angate.AnGate.entity.Users;
import io.angate.AnGate.entity.enums.TicketStatus;
import io.angate.AnGate.exception.BookingExistsDeletionException;
import io.angate.AnGate.exception.ResourceNotFoundException;
import io.angate.AnGate.repository.BookingRepository;
import io.angate.AnGate.repository.TicketTypeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentService {

    @Value("${razorpay.key.id}")
    private String keyId;

    private final RazorpayConfig razorpayConfig;


    private final RazorpayClient razorpayClient;
    private final BookingRepository bookingRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final EmailService emailService;

    public BookingResponse createOrder(BookingRequest bookingRequest , Users users) throws BadRequestException, RazorpayException {

        try {
            TicketType ticketType = ticketTypeRepository.findById(bookingRequest.getTicketTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("No ticket found"));
            if (ticketType.getStatus() != TicketStatus.ACTIVE) {
                throw new BadRequestException("ticket is not available");
            }
            if (ticketType.getAvailableTickets() < bookingRequest.getQuantity()) {
                throw new BadRequestException("sorry only " + ticketType.getAvailableTickets() + "available");
            }

            BigDecimal totalPrice = BigDecimal.valueOf(bookingRequest.getQuantity())
                    .multiply(ticketType.getPrice());

            Booking booking = Booking.builder()
                    .users(users)
                    .totalPrice(totalPrice)
                    .quantity(bookingRequest.getQuantity())
                    .ticketType(ticketType)
                    .status(Booking.Status.PENDING)
                    .paymentStatus(Booking.PaymentStatus.PENDING)
                    .bookingReference(UUID.randomUUID().toString())
                    .expiryTime(LocalDateTime.now().plusMinutes(10))
                    .checkedIn(false)
                    .build();

            long amount = totalPrice
                    .multiply(BigDecimal.valueOf(100))
                    .longValueExact();

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amount);
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "ANG-" + booking.getBookingReference());


            ticketType.setAvailableTickets(ticketType.getAvailableTickets()-booking.getQuantity());
            bookingRepository.save(booking);
            Order order = razorpayClient.orders.create(orderRequest);
            booking.setRazorpayOrderId(order.get("id"));
            booking = bookingRepository.save(booking);
            ticketTypeRepository.save(ticketType);

            BookingResponse response = new BookingResponse();
            response.setTicketTypeId(booking.getTicketType().getId());
            response.setId(booking.getId());
            response.setUserId(users.getId());
            response.setTotalPrice(totalPrice);
            response.setStatus(booking.getStatus());
            response.setPaymentStatus(booking.getPaymentStatus());
            response.setEventTitle(ticketType.getEvent().getTitle());
            response.setQuantity(booking.getQuantity());
            response.setCreatedAt(booking.getCreatedAt());
            response.setRazorpayOrderId(order.get("id"));
            response.setAmount(amount);
            response.setCurrency("INR");
            response.setKey(keyId);
            response.setEmailId(booking.getUsers().getEmailId());
            response.setImageUrl(booking.getTicketType().getEvent().getImageUrl());

            return response;
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }

    public void verifyPayment(PaymentVerificationRequest request) throws RazorpayException, IOException, WriterException {
        Booking booking =  bookingRepository.findByRazorpayOrderId(request.getRazorpayOrderId())
                .orElseThrow(()-> new ResourceNotFoundException("Booking not found"));

        JSONObject object = new JSONObject();
        object.put("razorpay_order_id",request.getRazorpayOrderId());
        object.put("razorpay_payment_id",request.getRazorpayPaymentId());
        object.put("razorpay_signature",request.getRazorpaySignature());

        boolean verified = Utils
                .verifyPaymentSignature(object,razorpayConfig.getKeySecret());
        if(!verified){
            throw new BadRequestException("Invalid payment signature");
        }
        if (booking.getPaymentStatus() == Booking.PaymentStatus.SUCCESS) {
            throw new BadRequestException("Payment already verified");
        }
        booking.setStatus(Booking.Status.CONFIRMED);
        booking.setRazorpayPaymentId(request.getRazorpayPaymentId());
        booking.setPaymentStatus(Booking.PaymentStatus.SUCCESS);
        booking.setRazorpaySignature(request.getRazorpaySignature());
        booking.setTicketCode("ANG-"+UUID.randomUUID());
        booking.setCheckedIn(false);
        TicketType ticketType = booking.getTicketType();
        ticketTypeRepository.save(ticketType);
        bookingRepository.save(booking);
        System.out.println("Before sending email");
        byte[] qr = QRCodeGenerator.generateQRCode(booking.getTicketCode());
        emailService.sendBookingInformation(booking , qr);
        System.out.println("After sending email");

    }

    public byte[] generateBookingQr(Long bookingId) throws IOException, WriterException {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(()-> new ResourceNotFoundException("no bookings found"));
        return QRCodeGenerator.generateQRCode(booking.getTicketCode());
    }

    public CheckInResponse scan(CheckInRequest checkInRequest) throws BadRequestException {
        Booking booking = bookingRepository.findByTicketCode(checkInRequest.getTicketCode())
                .orElseThrow(()-> new ResourceNotFoundException("No booking found"));

        if(booking.getCheckedIn()){
            throw new BookingExistsDeletionException("Already checked in");
        }

        if(booking.getPaymentStatus()!= Booking.PaymentStatus.SUCCESS){
            throw new BadRequestException("Payment not completed");
        }

        booking.setCheckedIn(true);
        booking.setCheckedAt(LocalDateTime.now());
        bookingRepository.save(booking);

        CheckInResponse checkInResponse = new CheckInResponse(
                true,
                "Successfully Verified !",
                booking.getTicketType().getEvent().getTitle(),
                booking.getUsers().getFullName(),
                booking.getTicketType().getName());

        return checkInResponse;
    }
}
