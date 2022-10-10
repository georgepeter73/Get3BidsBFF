package com.get3bids.acculynx.controller;

import com.get3bids.acculynx.dto.LeadDTO;
import com.get3bids.acculynx.services.AccuLynxService;
import com.get3bids.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
@Slf4j
@RestController
public class AccuLynxController {
    @Autowired
    AccuLynxService accuLynxService;
    @PostMapping("/api/v1/test")
    public void test(String firstName,String lastName,String email){
       try {
           log.info("***API Called*** " + firstName + " " + lastName + " " + email);
           LeadDTO leadDTO = new LeadDTO();
           leadDTO.setFirstName(firstName);
           leadDTO.setLastName(lastName);
           leadDTO.setEmailAddress(email);
           accuLynxService.createLead(leadDTO);
       }catch(Exception ex){
           log.error(CommonUtils.getExceptionMessage(ex));
       }
    }
}
