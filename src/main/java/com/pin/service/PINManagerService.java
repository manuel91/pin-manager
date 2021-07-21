package com.pin.service;

import com.pin.dto.MSISDNResponse;
import com.pin.dto.PINResponse;
import com.pin.exception.InvalidInputException;
import com.pin.model.MSISDN;
import com.pin.model.PIN;
import com.pin.repository.MSISDNRepository;
import com.pin.repository.PINRepository;
import com.pin.utils.PINManagerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PINManagerService {

    @Autowired
    private MSISDNRepository msisdnRepository;

    @Autowired
    private PINRepository pinRepository;

    public String createPIN(String phoneNumber) throws InvalidInputException{
        if (phoneNumber == null || !PINManagerUtils.MSISDN_FORMAT_PATTERN.matcher(phoneNumber).matches()) {
            throw new InvalidInputException(String.format("Invalid format for MSISDN = '%s', the expected input must start by '+' followed by 11 or 12 digits (Ex. +34999112233)", phoneNumber));
        }

        PIN newPin = null;
        MSISDN msisdn = null;
        Optional<MSISDN> msisdnQuery = msisdnRepository.findByPhoneNumber(phoneNumber);

        if (msisdnQuery.isPresent()) {
            msisdn = msisdnQuery.get();
            List<PIN> pinList = (msisdn.getPinList() == null) ? new ArrayList<>()
                    : msisdn.getPinList().stream().filter(p -> !p.getDiscarded()).collect(Collectors.toList());

            if (pinList.size() < 3) {
                // Create the new PIN for the existent MSISDN
                newPin = PINManagerUtils.generatePIN(msisdn);
            }
        }
        else {
            // Create a new MSISDN
            msisdn = new MSISDN();
            msisdn.setPhoneNumber(phoneNumber);

            // Create a new PIN for the new MSISDN
            newPin = PINManagerUtils.generatePIN(msisdn);
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

    public boolean validatePIN(String phoneNumber, String pinNumber) throws InvalidInputException {
        if (phoneNumber == null || !PINManagerUtils.MSISDN_FORMAT_PATTERN.matcher(phoneNumber).matches()) {
            throw new InvalidInputException(String.format("Invalid format for MSISDN = '%s', the expected input must start by '+' followed by 11 or 12 digits (Ex. +34999112233)", phoneNumber));
        }

        if (pinNumber == null || !PINManagerUtils.PIN_FORMAT_PATTERN.matcher(pinNumber).matches()) {
            throw new InvalidInputException(String.format("Invalid format for PIN = '%s', the expected input must be a 4 digit numeric combination", pinNumber));
        }

        boolean found = false;
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
                        pin.setDiscardedDateTime(LocalDateTime.now());
                    }

                    found = true;
                    pinRepository.save(pin);
                }
            }
        }

        return found;
    }

    public List<MSISDNResponse> getAllMSISDN() {
        List<MSISDNResponse> msisdnResponseList = new ArrayList<>();
        List<MSISDN> msisdnList = (List<MSISDN>) msisdnRepository.findAll();
        msisdnList.forEach(m -> msisdnResponseList.add(PINManagerUtils.convertToDTO(m)));

        return msisdnResponseList;
    }

    public List<PINResponse> getPINList(String phoneNumber) throws InvalidInputException {
        if (phoneNumber == null || !PINManagerUtils.MSISDN_FORMAT_PATTERN.matcher(phoneNumber).matches()) {
            throw new InvalidInputException(String.format("Invalid format for MSISDN = '%s', the expected input must start by '+' followed by 11 or 12 digits (Ex. +34999112233)", phoneNumber));
        }

        List<PINResponse> pinResponseSet = new ArrayList<>();
        Optional<MSISDN> msisdnQuery = msisdnRepository.findByPhoneNumber(phoneNumber);

        if (msisdnQuery.isPresent()) {
            MSISDN msisdn = msisdnQuery.get();
            msisdn.getPinList().forEach(p -> pinResponseSet.add(PINManagerUtils.convertToDTO(p)));
        }

        return pinResponseSet;
    }

    @Scheduled(cron = "${com.pin.service.cron.clean.pins}")
    public void cleanExpiredPINList() {
        List<PIN> expiredPINList = pinRepository.findByCreationDateTimeGreaterThanEqualAndDiscardedFalse(LocalDateTime.now());
        if (!expiredPINList.isEmpty()) {
            pinRepository.deleteAll(expiredPINList);
        }
    }

}
