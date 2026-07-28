package io.angate.AnGate;

import io.angate.AnGate.dto.booking.BookingRequest;
import io.angate.AnGate.entity.Event;
import io.angate.AnGate.entity.TicketType;
import io.angate.AnGate.entity.Users;
import io.angate.AnGate.repository.BookingRepository;
import io.angate.AnGate.repository.EventRepository;
import io.angate.AnGate.repository.TicketTypeRepository;
import io.angate.AnGate.repository.UserRepository;
import io.angate.AnGate.service.BookingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class BookingConcurrencyTest {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private TicketTypeRepository ticketTypeRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private BookingService bookingService;

    @Test
    void shouldAllowOnlyOneBooking() throws Exception {

        Event event = new Event();
        event.setTitle("Samay raina comedy show");
        event.setDescription("India's most funny standup comedy show");
        event.setVenue("Mumbai , central club ");
        event.setStartTime(LocalDateTime.parse("2026-07-30T18:30:00"));
        event.setStatus(Event.Status.UPCOMING);

        TicketType ticketType = new TicketType();
        ticketType.setName("General");
        ticketType.setTotalTickets(1);
        ticketType.setAvailableTickets(1);
        ticketType.setPrice(BigDecimal.valueOf(999.99));
        ticketType.setEvent(event);

        List<TicketType> ticketTypes = List.of(ticketType);
        event.setTicketTypes(ticketTypes);

        Users users = new Users();
        users.setFullName("arijit");
        users.setEmailId("Nil@Gmail.com");
        users.setPassword("1234");
        users.setRole(Users.Role.USER);
        users.setGender(Users.Gender.MALE);

        Users users1 = new Users();
        users1.setFullName("abhi");
        users1.setEmailId("Abhijit@Gmail.com");
        users1.setPassword("1234");
        users1.setRole(Users.Role.USER);
        users1.setGender(Users.Gender.MALE);

        event = eventRepository.save(event);
        System.out.println(ticketType.getId());

        users = userRepository.save(users);
        users1 = userRepository.save(users1);


        BookingRequest bookingRequest = new BookingRequest();
        bookingRequest.setUserId(users.getId());
        bookingRequest.setTicketTypeId(ticketType.getId());
        bookingRequest.setQuantity(1);

        BookingRequest bookingRequest1 = new BookingRequest();
        bookingRequest1.setUserId(users1.getId());
        bookingRequest1.setTicketTypeId(ticketType.getId());
        bookingRequest1.setQuantity(1);

        ExecutorService executor = Executors.newFixedThreadPool(2);

        CountDownLatch latch = new CountDownLatch(1);

        Future<?> future1 = executor.submit(() -> {
            latch.await();
            bookingService.bookAnEvent(bookingRequest);
            return null;
        });

        Future<?> future2 = executor.submit(() -> {
            latch.await();
            bookingService.bookAnEvent(bookingRequest1);
            return null;
        });

        latch.countDown();

        try {
            future1.get();
        } catch (ExecutionException e) {
            System.out.println("Future 1 failed: " + e.getCause());
        }

        try {
            future2.get();
        } catch (ExecutionException e) {
            System.out.println("Future 2 failed: " + e.getCause());
        }

        executor.shutdown();

        assertEquals(1, bookingRepository.count());

        TicketType updated =
                ticketTypeRepository.findById(ticketType.getId())
                        .orElseThrow();

        assertEquals(0, updated.getAvailableTickets());

    }

}
