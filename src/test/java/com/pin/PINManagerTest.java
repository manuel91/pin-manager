package com.pin;

import com.pin.model.MSISDN;
import com.pin.model.PIN;
import com.pin.repository.MSISDNRepository;
import com.pin.repository.PINRepository;
import com.pin.service.PINManagerService;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class PINManagerTest extends BaseUnitTest {

    @Mock
    private MSISDNRepository msisdnRepository;

    @Mock
    private PINRepository pinRepository;

    @InjectMocks
    private PINManagerService pinManagerService;

    private static final Pattern PIN_FORMAT_PATTERN = Pattern.compile("[0-9]{4}");

    @Test
    public void createPINTest1() throws Exception {
        Mockito.when(msisdnRepository.findByPhoneNumber(ArgumentMatchers.anyString())).thenReturn(Optional.empty());
        Assert.assertTrue(PIN_FORMAT_PATTERN.matcher(pinManagerService.createPIN("+34999112233")).matches());
    }

    @Test
    public void createPINTest2() throws Exception {
        // Set test input
        String phoneNumber = "+34999112233";

        // Create mock result for MSISDN
        MSISDN msisdn = new MSISDN();
        msisdn.setId(1L);
        msisdn.setPhoneNumber(phoneNumber);

        List<PIN> pinList = new ArrayList<>();
        for (int i = 0; i <= 3; i++) {
            if (i == 3) {
                Assert.assertNull(pinManagerService.createPIN(phoneNumber));
            }
            else {
                // Set Mock result for MSISDN query
                Mockito.when(msisdnRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.of(msisdn));

                // Get result and validate its format
                String pinNumber = pinManagerService.createPIN(phoneNumber);
                Assert.assertTrue(PIN_FORMAT_PATTERN.matcher(pinNumber).matches());

                // If correct, then create PIN object to update MSISDN mock query
                PIN pin = new PIN();
                pin.setId(1L + Long.valueOf(i));
                pin.setPinNumber(pinNumber);
                pin.setMsisdn(msisdn);

                // Update mock MSISDN
                pinList.add(pin);
                msisdn.setPinList(pinList);
            }
        }
    }

    @Test
    public void validatePINTest1() throws Exception {
        Mockito.when(msisdnRepository.findByPhoneNumber(ArgumentMatchers.anyString())).thenReturn(Optional.empty());
        Assert.assertFalse(pinManagerService.validatePIN("+34999112233", "1234"));
    }

    @Test
    public void validatePINTest2() throws Exception {
        // Set test input
        String phoneNumber = "+34999112233";
        String pinNumber = "1234";

        // Create mock result for MSISDN
        MSISDN msisdn = new MSISDN();
        msisdn.setId(1L);
        msisdn.setPhoneNumber(phoneNumber);

        // Create mock result for PIN and store it into MSISDN
        PIN pin = new PIN();
        pin.setId(1L);
        pin.setMsisdn(msisdn);
        pin.setPinNumber(pinNumber);
        msisdn.setPinList(Arrays.asList(pin));

        // Set Mock result for MSISDN query
        Mockito.when(msisdnRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.of(msisdn));

        for (int i = 0; i <= 3; i++) {
            if (i == 3) {
                Assert.assertFalse(pinManagerService.validatePIN(phoneNumber, pinNumber));
            }
            else {
                Assert.assertTrue(pinManagerService.validatePIN(phoneNumber, pinNumber));
            }
        }
    }

}
