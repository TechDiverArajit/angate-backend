package io.angate.AnGate.service;
import io.angate.AnGate.entity.Booking;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class EmailServiceImp implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendBookingInformation(Booking booking, byte[] qr) {


        try {


            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true);

            helper.setTo(booking.getUsers().getEmailId());

            helper.setSubject("Booking Confirmed : " + booking.getBookingReference());

            String html = """
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">

<style>

body{
    margin:0;
    padding:40px 0;
    background:#f5f5f7;
    font-family:-apple-system,BlinkMacSystemFont,"Segoe UI",Roboto,Arial,sans-serif;
}

.wrapper{
    width:100%%;
}

.card{

    max-width:620px;
    margin:auto;

    background:#ffffff;

    border-radius:24px;

    overflow:hidden;

    border:1px solid #e5e5e7;

}

.header{

    text-align:center;

    padding:45px 40px;

    border-bottom:1px solid #efefef;

}

.logo{

    font-size:34px;

    font-weight:700;

    color:#111;

    letter-spacing:-1px;

}

.subtitle{

    margin-top:8px;

    color:#6e6e73;

    font-size:16px;

}

.content{

    padding:40px;

}

.success{

    font-size:28px;

    font-weight:700;

    color:#111;

    margin-bottom:10px;

}

.message{

    color:#6e6e73;

    line-height:1.7;

    margin-bottom:35px;

}

.info{

    border:1px solid #ededed;

    border-radius:18px;

    overflow:hidden;

}

.row{

    display:flex;

    justify-content:space-between;

    padding:18px 22px;

    border-bottom:1px solid #f0f0f0;

}

.row:last-child{

    border-bottom:none;

}

.label{

    color:#8e8e93;

    font-size:14px;

}

.value{

    color:#111;

    font-weight:600;

}

.notice{

    margin-top:30px;

    background:#f7f7f8;

    border-radius:16px;

    padding:22px;

    color:#555;

    line-height:1.7;

}

.footer{

    padding:30px;

    text-align:center;

    color:#999;

    font-size:13px;

    border-top:1px solid #efefef;

}

</style>

</head>

<body>

<div class="wrapper">

<div class="card">

<div class="header">

<div class="logo">
🎟 AnGate
</div>

<div class="subtitle">
Event Booking Platform
</div>

</div>

<div class="content">

<div class="success">

Booking Confirmed

</div>

<div class="message">

Thank you for your purchase.
Your booking has been successfully confirmed.

</div>

<div class="info">

<div class="row">
<div class="label">Event</div>
<div class="value">%s</div>
</div>

<div class="row">
<div class="label">Booking Reference</div>
<div class="value">%s</div>
</div>

<div class="row">
<div class="label">Ticket Type</div>
<div class="value">%s</div>
</div>

<div class="row">
<div class="label">Quantity: </div>
<div class="value">%d</div>
</div>

<div class="row">
<div class="label">Total Paid: </div>
<div class="value">₹ %.2f</div>
</div>

<div class="row">
<div class="label">Status: </div>
<div class="value">Confirmed ✅</div>
</div>

</div>

<div class="notice">

Your QR ticket has been attached with this email.

Please present the QR code at the event entrance for quick check-in.

</div>

</div>

<div class="footer">

© 2026 AnGate

</div>

</div>

</div>

</body>

</html>
""".formatted(
                    booking.getTicketType().getEvent().getTitle(),
                    booking.getBookingReference(),
                    booking.getTicketType().getName(),
                    booking.getQuantity(),
                    booking.getTotalPrice()
            );
            helper.setFrom("angate.corp@gmail.com", "AnGate");
            helper.setText(html , true);
            helper.addInline("ticketQr",new ByteArrayResource(qr),"image/png");
            helper.addAttachment("ticket-qr.png", new ByteArrayResource(qr));
            mailSender.send(message);
        }
        catch (Exception e) {
            e.printStackTrace();
        }



    }
}
