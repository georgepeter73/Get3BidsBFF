package com.get3bids.scrappers.services;

import com.get3bids.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.HashMap;

@Slf4j
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@EnableConfigurationProperties
public class ZohoServiceTest {
    @Autowired
    ZohoService zohoService;
    public static final String GOOGLE_MAPS_SEARCH_QUERY = "roof contractors in florida";
    public static final int GOOGLE_MAPS_SEARCH_RECORD_LIMIT = 1;
    public static final int GOOGLE_MAPS_SEARCH_REVIEW_LIMIT = 20;

    @Test
    public void runJobTest(){
        try {
            HashMap inputMap = new HashMap<String, Object>() {{
                put("query", GOOGLE_MAPS_SEARCH_QUERY);
                put("reviewsLimit", GOOGLE_MAPS_SEARCH_REVIEW_LIMIT);
                put("limit", GOOGLE_MAPS_SEARCH_RECORD_LIMIT);
                put("language", "en");
            }};
            zohoService.runJob(inputMap);
        }catch(Exception ex){
            log.error(CommonUtils.getExceptionMessage(ex));
        }
    }
}
