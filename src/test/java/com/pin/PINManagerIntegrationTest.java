package com.pin;

import com.pin.controller.PINController;
import com.pin.dto.MSISDNRequest;
import com.pin.dto.ValidatePINRequest;
import com.pin.model.MSISDN;
import com.pin.model.PIN;
import com.pin.repository.MSISDNRepository;
import com.pin.repository.PINRepository;
import com.pin.utils.PINManagerTestUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = PINManagerApplication.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PINManagerIntegrationTest {

    @MockBean
    private MSISDNRepository msisdnRepository;

    @MockBean
    private PINRepository pinRepository;

    @Autowired
    private PINController pinController;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = standaloneSetup(this.pinController).build();
    }

    @Test
    public void createPINTest1() throws Exception {
        // Set Mock result for MSISDN query
        Mockito.when(msisdnRepository.findByPhoneNumber(ArgumentMatchers.anyString())).thenReturn(Optional.empty());

        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.put(PINManagerTestUtils.CREATE_PIN_SERVICE_PATH)
                        .content(PINManagerTestUtils.asJsonString(PINManagerTestUtils.getMSISDNRequestSample()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        Assert.assertTrue(PINManagerTestUtils.isValidPINFormat(result.getResponse().getContentAsString()));
    }

    @Test
    public void createPINTest2() throws Exception {
        // Set test input
        MSISDNRequest msisdnRequest = PINManagerTestUtils.getMSISDNRequestSample();

        // Create mock result for MSISDN
        MSISDN msisdn = PINManagerTestUtils.getMSISDNSample();

        List<PIN> pinList = new ArrayList<>();
        for (int i = 0; i <= 3; i++) {
            if (i == 3) {
                mockMvc.perform(
                        MockMvcRequestBuilders.put(PINManagerTestUtils.CREATE_PIN_SERVICE_PATH)
                                .content(PINManagerTestUtils.asJsonString(msisdnRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isNoContent());
            }
            else {
                // Set Mock result for MSISDN query
                Mockito.when(msisdnRepository.findByPhoneNumber(msisdnRequest.getPhoneNumber())).thenReturn(Optional.of(msisdn));

                // Get service result
                MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.put(PINManagerTestUtils.CREATE_PIN_SERVICE_PATH)
                                .content(PINManagerTestUtils.asJsonString(msisdnRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isCreated())
                        .andReturn();

                // Get result and validate its format
                String pinNumber = result.getResponse().getContentAsString();
                Assert.assertTrue(PINManagerTestUtils.isValidPINFormat(result.getResponse().getContentAsString()));

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
        // Set Mock result for MSISDN query
        Mockito.when(msisdnRepository.findByPhoneNumber(ArgumentMatchers.anyString())).thenReturn(Optional.empty());

        mockMvc.perform(
                MockMvcRequestBuilders.put(PINManagerTestUtils.VALIDATE_PIN_SERVICE_PATH)
                        .content(PINManagerTestUtils.asJsonString(PINManagerTestUtils.getValidatePINRequestSample()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void validatePINTest2() throws Exception {
        // Set test input
        ValidatePINRequest validatePINRequest = PINManagerTestUtils.getValidatePINRequestSample();

        // Create mock result for MSISDN
        MSISDN msisdn = PINManagerTestUtils.getMSISDNSample();

        // Create mock result for PIN and store it into MSISDN
        PIN pin = PINManagerTestUtils.getPINSample(msisdn);
        msisdn.setPinList(Arrays.asList(pin));

        // Set Mock result for MSISDN query
        Mockito.when(msisdnRepository.findByPhoneNumber(validatePINRequest.getPhoneNumber())).thenReturn(Optional.of(msisdn));

        for (int i = 0; i <= 3; i++) {
            if (i == 3) {
                mockMvc.perform(
                        MockMvcRequestBuilders.put(PINManagerTestUtils.VALIDATE_PIN_SERVICE_PATH)
                                .content(PINManagerTestUtils.asJsonString(validatePINRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isNotFound());
            }
            else {
                mockMvc.perform(
                        MockMvcRequestBuilders.put(PINManagerTestUtils.VALIDATE_PIN_SERVICE_PATH)
                                .content(PINManagerTestUtils.asJsonString(validatePINRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk());
            }
        }
    }

}
