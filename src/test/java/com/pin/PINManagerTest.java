package com.pin;

import com.pin.repository.MSISDNRepository;
import com.pin.repository.PINRepository;
import com.pin.service.PINManagerService;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

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
    public void createPINTest() throws Exception {
        Mockito.when(msisdnRepository.findByPhoneNumber(ArgumentMatchers.anyString())).thenReturn(Optional.ofNullable(null));
        Assert.assertTrue(PIN_FORMAT_PATTERN.matcher(pinManagerService.createPIN("+34999112233")).matches());
    }

}
