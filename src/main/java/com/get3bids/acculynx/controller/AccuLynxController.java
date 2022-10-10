package com.get3bids.acculynx.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
@Slf4j
@RestController
public class AccuLynxController {
    @PostMapping("/api/v1/test")
    public void test(String firstName,String lastName,String email){
       log.info("***API Called*** "+firstName+" "+lastName+" "+email);
    }
}
