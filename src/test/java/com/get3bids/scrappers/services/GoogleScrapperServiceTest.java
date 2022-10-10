package com.get3bids.scrappers.services;

import com.get3bids.AbstractTest;
import com.get3bids.scrappers.dti.outscraper.GoogleMapSearchItem;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.HashMap;
import java.util.List;

@Slf4j
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@EnableConfigurationProperties
public class GoogleScrapperServiceTest extends AbstractTest {
    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
    }
    @Autowired
    GoogleScrapperService googleScrapperService;
    @Test
    public void googleSearchTest(){
        try {
           HashMap inputMap = new HashMap<String, Object>() {{
                put("query", "roof contractors in 34102");
                put("reviewsLimit", 20);
                put("limit", 20);
                put("language", "en");
            }};
            List<GoogleMapSearchItem> googleMapSearchItemList = googleScrapperService.googleMapsSearchV2(inputMap);
           }catch(Exception ex){
            log.error(ex.getMessage());
        }


    }
}
