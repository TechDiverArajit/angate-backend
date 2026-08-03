package io.angate.AnGate.Utility;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class QRCodeGenerator {

    public static byte[] generateQRCode(String text) throws WriterException  , IOException {
        QRCodeWriter writer = new QRCodeWriter();

        BitMatrix bitMatrix = writer.encode(
                text,
                BarcodeFormat.QR_CODE,
                300,
                300
        );

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix,"PNG", stream);
        return stream.toByteArray();

    }
}
