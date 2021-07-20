package com.pin.controller;

import com.pin.dto.MSISDNRequest;
import com.pin.dto.MSISDNResponse;
import com.pin.dto.PINResponse;
import com.pin.dto.ValidatePINRequest;
import com.pin.exception.InvalidInputException;
import com.pin.service.PINManagerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        catch(InvalidInputException e) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Error-Message", e.getMessage());
            return new ResponseEntity<>(headers, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        catch(Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(path = "/validate")
    public ResponseEntity<String> validatePIN(@RequestBody ValidatePINRequest request) {
        try {
            return pinManagerService.validatePIN(request.getPhoneNumber(), request.getPinNumber()) ?
                    new ResponseEntity<>(HttpStatus.OK) :
                    new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        catch(InvalidInputException e) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Error-Message", e.getMessage());
            return new ResponseEntity<>(headers, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        catch(Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/msisdn/all")
    public ResponseEntity<List<MSISDNResponse>> getAllMSISDN() {
        try {
            return new ResponseEntity<>(pinManagerService.getAllMSISDN(), HttpStatus.OK);
        }
        catch(Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/msisdn/pins")
    public ResponseEntity<List<PINResponse>> getPINList(@RequestBody MSISDNRequest msisdnRequest) {
        try {
            return new ResponseEntity<>(pinManagerService.getPINList(msisdnRequest.getPhoneNumber()), HttpStatus.OK);
        }
        catch(InvalidInputException e) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Error-Message", e.getMessage());
            return new ResponseEntity<>(headers, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        catch(Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
