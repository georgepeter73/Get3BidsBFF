package com.get3bids.acculynx.services;

import com.get3bids.AbstractTest;
import com.get3bids.acculynx.dto.LeadDTO;
import com.get3bids.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@Slf4j
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@EnableConfigurationProperties
public class AccuLynxServiceTest  extends AbstractTest {
    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
    }
    @Autowired
    AccuLynxService accuLynxService;
    @Test
    public void createLeadTest(){
        try {
            LeadDTO leadDTO = new LeadDTO();
            leadDTO.setFirstName("George");
            leadDTO.setLastName("Peter");
            leadDTO.setPhoneNumber1("5614798793");
            accuLynxService.createLead(leadDTO);
        }catch(Exception ex){
            log.error(CommonUtils.getExceptionMessage(ex));
        }
    }
}
