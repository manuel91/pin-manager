package com.pin.controller;

import com.pin.dto.MSISDNRequest;
import com.pin.dto.MSISDNResponse;
import com.pin.dto.PINResponse;
import com.pin.dto.ValidatePINRequest;
import com.pin.service.PINManagerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/pin-service")
public class PINController {

    @Autowired
    private PINManagerService pinManagerService;

    @PutMapping(path = "/create")
    public ResponseEntity<String> createPIN(@RequestBody MSISDNRequest request) {
        try {
            String pinNumber = pinManagerService.createPIN(request.getPhoneNumber());
            return pinNumber != null ?
                    new ResponseEntity<>(pinNumber, HttpStatus.CREATED) :
                    new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        catch(Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/validate")
    public ResponseEntity<String> validatePIN(@RequestBody ValidatePINRequest request) {
        try {
            return pinManagerService.validatePIN(request.getPhoneNumber(), request.getPinNumber()) ?
                    new ResponseEntity<>(HttpStatus.OK) :
                    new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        catch(Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/msisdn/all")
    public ResponseEntity<List<MSISDNResponse>> getAllMSISDN() {
        List<MSISDNResponse> msisdnResponseList = new ArrayList<>();

        try {
            msisdnResponseList = pinManagerService.getAllMSISDN();
            return !msisdnResponseList.isEmpty() ?
                    new ResponseEntity<>(msisdnResponseList, HttpStatus.OK) :
                    new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        catch(Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/pins")
    public ResponseEntity<Set<PINResponse>> getPINSet(MSISDNRequest msisdnRequest) {
        Set<PINResponse> pinResponseSet = new HashSet<>();

        try {

            pinResponseSet = pinManagerService.getPINSet(msisdnRequest.getPhoneNumber());
            return !pinResponseSet.isEmpty() ?
                    new ResponseEntity<>(pinResponseSet, HttpStatus.OK) :
                    new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        catch(Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
