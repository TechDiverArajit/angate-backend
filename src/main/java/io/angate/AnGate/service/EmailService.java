package io.angate.AnGate.service;

import io.angate.AnGate.entity.Booking;

public interface EmailService {

    void sendBookingInformation(Booking booking , byte[] qr);
}
