package com.pin.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pin.dto.MSISDNRequest;
import com.pin.dto.ValidatePINRequest;
import com.pin.model.MSISDN;
import com.pin.model.PIN;

import java.util.regex.Pattern;

public class PINManagerTestUtils {

    private static final Pattern PIN_FORMAT_PATTERN = Pattern.compile("[0-9]{4}");

    public static final String CREATE_PIN_SERVICE_PATH = "/pin-service/pin/create";

    public static final String VALIDATE_PIN_SERVICE_PATH = "/pin-service/pin/validate";

    public static final String CUSTOM_PHONE_NUMBER = "+34999112233";

    public static final String CUSTOM_PIN_NUMBER = "1234";

    public static MSISDNRequest getMSISDNRequestSample() {
        MSISDNRequest msisdnRequest = new MSISDNRequest();
        msisdnRequest.setPhoneNumber(CUSTOM_PHONE_NUMBER);
        return msisdnRequest;
    }

    public static ValidatePINRequest getValidatePINRequestSample() {
        ValidatePINRequest validatePINRequest = new ValidatePINRequest();
        validatePINRequest.setPhoneNumber(CUSTOM_PHONE_NUMBER);
        validatePINRequest.setPinNumber(CUSTOM_PIN_NUMBER);
        return validatePINRequest;
    }

    public static MSISDN getMSISDNSample() {
        MSISDN msisdn = new MSISDN();
        msisdn.setId(1L);
        msisdn.setPhoneNumber(CUSTOM_PHONE_NUMBER);
        return msisdn;
    }

    public static PIN getPINSample(MSISDN msisdn) {
        PIN pin = new PIN();
        pin.setId(1L);
        pin.setMsisdn(msisdn);
        pin.setPinNumber(CUSTOM_PIN_NUMBER);
        return pin;
    }

    public static PIN getPINSample(MSISDN msisdn, String pinNumber) {
        PIN pin = getPINSample(msisdn);
        pin.setPinNumber(pinNumber);
        return pin;
    }

    public static boolean isValidPINFormat(String pinNumber) {
        return PIN_FORMAT_PATTERN.matcher(pinNumber).matches();
    }

    public static String asJsonString(final Object obj) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);

            return objectMapper.writeValueAsString(obj);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
