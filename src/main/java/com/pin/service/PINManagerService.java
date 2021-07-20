package com.pin.service;

import com.pin.dto.MSISDNResponse;
import com.pin.dto.PINResponse;
import com.pin.model.MSISDN;
import com.pin.model.PIN;
import com.pin.repository.MSISDNRepository;
import com.pin.repository.PINRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PINManagerService {

    @Autowired
    private MSISDNRepository msisdnRepository;

    @Autowired
    private PINRepository pinRepository;

    public String createPIN(String phoneNumber) {
        // Define global variables
        PIN newPin = null;
        MSISDN msisdn = null;

        // Try to fetch MSISDN from DB by the given phoneNumber
        Optional<MSISDN> msisdnQuery = msisdnRepository.findByPhoneNumber(phoneNumber);

        if (msisdnQuery.isPresent()) {
            msisdn = msisdnQuery.get();
            List<PIN> pinList = msisdn.getPinList();
            if (CollectionUtils.isEmpty(pinList) || pinList.size() < 3) {
                // Create the new PIN for the existent MSISDN
                newPin = generatePIN(msisdn);
            }
        }
        else {
            // Create a new MSISDN
            msisdn = new MSISDN();
            msisdn.setPhoneNumber(phoneNumber);

            // Create a new PIN for the new MSISDN
            newPin = generatePIN(msisdn);
        }

        // Save changes in DB
        if (newPin != null && msisdn != null) {
            if (msisdn.getId() == null) {
                msisdnRepository.save(msisdn);
            }
            pinRepository.save(newPin);
            return newPin.getPinNumber();
        }

        return null;
    }

    private PIN generatePIN(MSISDN msisdn) {
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

    public boolean validatePIN(String phoneNumber, String pinNumber) {
        boolean isValid = false;

        // Try to fetch MSISDN from DB by the given phoneNumber
        Optional<MSISDN> msisdnQuery = msisdnRepository.findByPhoneNumber(phoneNumber);

        if (msisdnQuery.isPresent()) {
            MSISDN msisdn = msisdnQuery.get();
            Optional<PIN> pinQuery = msisdn.getPinList().stream()
                    .filter(p -> p.getPinNumber().equals(pinNumber) && !p.getDiscarded()).findFirst();

            if (pinQuery.isPresent()) {
                PIN pin = pinQuery.get();
                Integer attempts = pin.getValidationAttempts() + 1;

                if (attempts <= 3) {
                    pin.setValidationAttempts(attempts);
                    if (attempts == 3) {
                        pin.setDiscarded(true);
                        pin.setDiscardedDate(LocalDate.now());
                    }

                    isValid = true;
                    pinRepository.save(pin);
                }
            }
        }

        return isValid;
    }

    public List<MSISDNResponse> getAllMSISDN() {
        List<MSISDNResponse> msisdnResponseList = new ArrayList<>();
        List<MSISDN> msisdnList = (List<MSISDN>) msisdnRepository.findAll();

        msisdnList.forEach(m -> msisdnResponseList.add(convertToDTO(m)));

        return msisdnResponseList;
    }

    public List<PINResponse> getPINList(String phoneNumber) {
        List<PINResponse> pinResponseSet = new ArrayList<>();
        Optional<MSISDN> msisdnQuery = msisdnRepository.findByPhoneNumber(phoneNumber);

        if (msisdnQuery.isPresent()) {
            MSISDN msisdn = msisdnQuery.get();
            msisdn.getPinList().forEach(p -> pinResponseSet.add(convertToDTO(p)));
        }

        return pinResponseSet;
    }

    private MSISDNResponse convertToDTO(MSISDN msisdn) {
        MSISDNResponse msisdnResponse = new MSISDNResponse();
        msisdnResponse.setPhoneNumber(msisdn.getPhoneNumber());

        List<PINResponse> pinResponseList = new ArrayList<>();
        msisdn.getPinList().forEach(p -> pinResponseList.add(convertToDTO(p)));
        msisdnResponse.setPinList(pinResponseList);

        return msisdnResponse;
    }

    private PINResponse convertToDTO(PIN pin) {
        PINResponse pinResponse = new PINResponse();
        pinResponse.setPinNumber(pin.getPinNumber());
        pinResponse.setCreationDate(pin.getCreationDate());
        pinResponse.setValidationAttempts(pin.getValidationAttempts());
        pinResponse.setDiscarded(pin.getDiscarded());
        pinResponse.setDiscardedDate(pin.getDiscardedDate());

        return pinResponse;
    }

}
