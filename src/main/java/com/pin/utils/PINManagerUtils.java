package com.pin.utils;

import com.pin.dto.MSISDNResponse;
import com.pin.dto.PINResponse;
import com.pin.model.MSISDN;
import com.pin.model.PIN;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PINManagerUtils {

    public static final Pattern MSISDN_FORMAT_PATTERN = Pattern.compile("\\+[0-9]{11,12}");

    public static final Pattern PIN_FORMAT_PATTERN = Pattern.compile("[0-9]{4}");

    public static PIN generatePIN(MSISDN msisdn) {
        String pinNumber;
        PIN pin = new PIN();
        Random random = new Random();
        List<PIN> pinList = msisdn.getPinList() == null ? new ArrayList<>()
                : msisdn.getPinList().stream().filter(p -> !p.getDiscarded()).collect(Collectors.toList());

        do {
            // Generate a random 4-digit PIN till is different from the ones in the given pinList
            pinNumber = String.format("%04d", random.nextInt(10000));
        }
        while (pinList.contains(pinNumber));

        // Set new PIN number
        pin.setPinNumber(pinNumber);
        pin.setMsisdn(msisdn);

        return pin;
    }

    public static MSISDNResponse convertToDTO(MSISDN msisdn) {
        MSISDNResponse msisdnResponse = new MSISDNResponse();
        msisdnResponse.setPhoneNumber(msisdn.getPhoneNumber());

        List<PINResponse> pinResponseList = new ArrayList<>();
        msisdn.getPinList().forEach(p -> pinResponseList.add(convertToDTO(p)));
        msisdnResponse.setPinList(pinResponseList);

        return msisdnResponse;
    }

    public static PINResponse convertToDTO(PIN pin) {
        PINResponse pinResponse = new PINResponse();
        pinResponse.setPinNumber(pin.getPinNumber());
        pinResponse.setCreationDateTime(pin.getCreationDateTime());
        pinResponse.setValidationAttempts(pin.getValidationAttempts());
        pinResponse.setDiscarded(pin.getDiscarded());
        pinResponse.setDiscardedDateTime(pin.getDiscardedDateTime());

        return pinResponse;
    }

}
