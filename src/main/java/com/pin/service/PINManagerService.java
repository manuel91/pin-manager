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
            Set<PIN> pinSet = msisdn.getPinSet();
            if (CollectionUtils.isEmpty(pinSet) || pinSet.size() < 3) {
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

        if (newPin != null && msisdn != null) {
            // Save changes in DB
            msisdnRepository.save(msisdn);
            pinRepository.save(newPin);
            return newPin.getPinNumber();
        }

        return null;
    }

    private PIN generatePIN(MSISDN msisdn) {
        String pinNumber;
        PIN pin = new PIN();
        Random random = new Random();
        Set<PIN> pinSet = msisdn.getPinSet() == null ? new HashSet<>()
                : msisdn.getPinSet().stream().filter(p -> !p.getDiscarded()).collect(Collectors.toSet());
        do {
            // Generate a random 4-digit PIN till is different from the ones in the given pinSet
            pinNumber = String.format("%04d", random.nextInt(10000));
        }
        while (pinSet.contains(pinNumber));

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
            Optional<PIN> pinQuery = msisdn.getPinSet().stream()
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

        msisdnList.forEach(m -> {
            MSISDNResponse msisdnResponse = new MSISDNResponse();
            msisdnResponse.setPhoneNumber(m.getPhoneNumber());

            Set<PINResponse> pinResponseSet = new HashSet<>();
            m.getPinSet().forEach(p -> {
                PINResponse pinResponse = new PINResponse();
                pinResponse.setPinNumber(p.getPinNumber());
                pinResponse.setCreationDate(pinResponse.getCreationDate());
                pinResponse.setValidationAttempts(pinResponse.getValidationAttempts());
                pinResponse.setDiscarded(p.getDiscarded());
                pinResponse.setDiscardedDate(p.getDiscardedDate());
                pinResponseSet.add(pinResponse);
            });

            msisdnResponse.setPinSet(pinResponseSet);
            msisdnResponseList.add(msisdnResponse);
        });

        return msisdnResponseList;
    }

    public Set<PINResponse> getPINSet(String phoneNumber) {
        Set<PINResponse> pinResponseSet = new HashSet<>();
        Optional<MSISDN> msisdnQuery = msisdnRepository.findByPhoneNumber(phoneNumber);

        if (msisdnQuery.isPresent()) {
            MSISDN msisdn = msisdnQuery.get();
            msisdn.getPinSet().forEach(p -> {
                PINResponse pinResponse = new PINResponse();
                pinResponse.setPinNumber(p.getPinNumber());
                pinResponse.setCreationDate(pinResponse.getCreationDate());
                pinResponse.setValidationAttempts(pinResponse.getValidationAttempts());
                pinResponse.setDiscarded(p.getDiscarded());
                pinResponse.setDiscardedDate(p.getDiscardedDate());
                pinResponseSet.add(pinResponse);
            });
        }

        return pinResponseSet;
    }


}
