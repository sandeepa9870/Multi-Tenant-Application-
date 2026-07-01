package com.example.multitenant.util;

import java.util.Random;

public class OtpGenerator {
    private static final Random random = new Random();
    private static final int OTP_LENGTH = 6;

    public static String generateOtp() {
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
}

