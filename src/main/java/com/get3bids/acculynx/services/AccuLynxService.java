package com.get3bids.acculynx.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.get3bids.acculynx.dto.LeadDTO;
import com.get3bids.scrappers.dti.zoho.HttpResult;
import com.get3bids.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccuLynxService {
    @Autowired
    AccuLynxConnectionService accuLynxConnectionService ;

    public void createLead(LeadDTO leadDTO) throws JsonProcessingException {
        HttpResult httpResult = accuLynxConnectionService.post("/api/v1/leads", CommonUtils.getObjectMapper().writeValueAsString(leadDTO));

    }
}
