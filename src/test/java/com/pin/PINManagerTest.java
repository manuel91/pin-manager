package com.pin;

import com.pin.model.MSISDN;
import com.pin.model.PIN;
import com.pin.repository.MSISDNRepository;
import com.pin.repository.PINRepository;
import com.pin.service.PINManagerService;

import com.pin.utils.PINManagerTestUtils;
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
        Assert.assertTrue(PINManagerTestUtils.isValidPINFormat(pinManagerService.createPIN(PINManagerTestUtils.CUSTOM_PHONE_NUMBER)));
    }

    @Test
    public void createPINTest2() throws Exception {
        // Create mock result for MSISDN
        MSISDN msisdn = PINManagerTestUtils.getMSISDNSample();

        List<PIN> pinList = new ArrayList<>();
        for (int i = 0; i <= 3; i++) {
            if (i == 3) {
                Assert.assertNull(pinManagerService.createPIN(PINManagerTestUtils.CUSTOM_PHONE_NUMBER));
            }
            else {
                // Set Mock result for MSISDN query
                Mockito.when(msisdnRepository.findByPhoneNumber(PINManagerTestUtils.CUSTOM_PHONE_NUMBER)).thenReturn(Optional.of(msisdn));

                // Get result and validate its format
                String pinNumber = pinManagerService.createPIN(PINManagerTestUtils.CUSTOM_PHONE_NUMBER);
                Assert.assertTrue(PIN_FORMAT_PATTERN.matcher(pinNumber).matches());

                // If correct, then create PIN object to update MSISDN mock query
                PIN pin = PINManagerTestUtils.getPINSample(msisdn, pinNumber);

                // Update mock MSISDN
                pinList.add(pin);
                msisdn.setPinList(pinList);
            }
        }
    }

    @Test
    public void validatePINTest1() throws Exception {
        Mockito.when(msisdnRepository.findByPhoneNumber(ArgumentMatchers.anyString())).thenReturn(Optional.empty());
        Assert.assertFalse(pinManagerService.validatePIN(PINManagerTestUtils.CUSTOM_PHONE_NUMBER, PINManagerTestUtils.CUSTOM_PIN_NUMBER));
    }

    @Test
    public void validatePINTest2() throws Exception {
        // Create mock result for MSISDN
        MSISDN msisdn = PINManagerTestUtils.getMSISDNSample();

        // Create mock result for PIN and store it into MSISDN
        PIN pin = PINManagerTestUtils.getPINSample(msisdn);
        msisdn.setPinList(Arrays.asList(pin));

        // Set Mock result for MSISDN query
        Mockito.when(msisdnRepository.findByPhoneNumber(PINManagerTestUtils.CUSTOM_PHONE_NUMBER)).thenReturn(Optional.of(msisdn));

        for (int i = 0; i <= 3; i++) {
            boolean isValid = pinManagerService.validatePIN(PINManagerTestUtils.CUSTOM_PHONE_NUMBER, PINManagerTestUtils.CUSTOM_PIN_NUMBER);
            if (i == 3) {
                Assert.assertFalse(isValid);
            }
            else {
                Assert.assertTrue(isValid);
            }
        }
    }

}
