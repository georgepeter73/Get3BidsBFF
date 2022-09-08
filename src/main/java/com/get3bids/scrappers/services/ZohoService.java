package com.get3bids.scrappers.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.get3bids.scrappers.cofig.ZohoConfig;
import com.get3bids.scrappers.dti.outscraper.GoogleMapSearchItem;
import com.get3bids.scrappers.dti.zoho.*;
import com.get3bids.scrappers.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.*;

@Slf4j
@Service
public class ZohoService {
    @Autowired
    ZohoConnectionService zohoConnectionService;
    @Autowired
    ZohoConfig zohoConfig;
    @Autowired
    GoogleScrapperService googleScrapperService;

    public Users getUsers()throws JsonProcessingException {
        Users users = null;
        HttpResult httpResult = zohoConnectionService.getWithAccessToken(zohoConfig, zohoConfig.getApiDomain()+"/crm/v3/users?type=AllUsers");
        if(httpResult.getStatusCode() == 200) {
            users = CommonUtils.getObjectMapper().readValue(httpResult.getResult(),Users.class);
        }
        return users;

    }
    private CommonResponse createVendor(String  vendorStr)throws JsonProcessingException{
        CommonResponse commonResponse = null;
        HttpResult httpResult = zohoConnectionService.postWithAccessToken(zohoConfig,zohoConfig.getApiDomain()+"/crm/v2/Vendors/upsert",vendorStr);
        if(httpResult.getStatusCode() == 200) {
            commonResponse = CommonUtils.getObjectMapper().readValue(httpResult.getResult(),CommonResponse.class);
        }
        return commonResponse;
    }
    private CommonResponse createAccount(String  accountStr)throws JsonProcessingException{
        CommonResponse commonResponse = null;
        HttpResult httpResult = zohoConnectionService.postWithAccessToken(zohoConfig,zohoConfig.getApiDomain()+"/crm/v2/Accounts/upsert",accountStr);
        if(httpResult.getStatusCode() == 200) {
            commonResponse = CommonUtils.getObjectMapper().readValue(httpResult.getResult(),CommonResponse.class);
        }
        return commonResponse;
    }
    public boolean runJob(Map<String,Object> inputMap){
        try {
             Users users = getUsers();
            if(users!=null) {
                CommonResponse commonResponse = null;
                List<GoogleMapSearchItem> googleMapSearchItemList = googleScrapperService.googleMapsSearchV2(inputMap);
                int googleRecordCount=1;
                for (GoogleMapSearchItem googleMapSearchItem : googleMapSearchItemList) {
                    commonResponse = createVendor(populateVendor(googleMapSearchItem, users));
                    if(commonResponse!=null) {
                        commonResponse = createAccount(populateAccount(googleMapSearchItem, users, commonResponse));
                        if(commonResponse!=null) {
                            log.info("Company and Location Creation Status : " + commonResponse.getData().get(0).getStatus()+" "+googleRecordCount+" st record created");
                        }else{
                            log.info("Location Creation failed "+googleRecordCount+"st record exit");
                            break;
                        }
                    }else{
                        log.info("Company creation failed "+googleRecordCount+"st record  exit");
                        break;
                    }
                    googleRecordCount++;
                }
            }else{
                log.info("Zoho access token could be expired.");
            }
        }catch(Exception ex){
            log.error(CommonUtils.getExceptionMessage(ex));
        }
        return true;

    }
    private String populateAccount(GoogleMapSearchItem googleMapSearchItem,Users users,CommonResponse commonResponse)throws JsonProcessingException{
        Account account = new Account();
        Owner owner = new Owner();
        owner.setId(users.getUsers().get(0).id);
        String companyId = commonResponse.getData().get(0).getDetails().getId();
        account.setCompany(companyId);
        ArrayList<String> servicesProvided = new ArrayList<>();
        servicesProvided.add(googleMapSearchItem.getType());
        account.setAccount_Name(googleMapSearchItem.getFull_address());
        account.setBilling_City(googleMapSearchItem.getCity());
        account.setBilling_State(googleMapSearchItem.getState());
        account.setBilling_Street(googleMapSearchItem.getStreet());
        account.setBilling_Code(googleMapSearchItem.getPostal_code());
        account.setDescription(googleMapSearchItem.getCategory());
        account.setPropertyCity(googleMapSearchItem.getCity());
        account.setPropertyState(googleMapSearchItem.getState());
        account.setPropertyStreet(googleMapSearchItem.getStreet());
        account.setPropertyZip(googleMapSearchItem.getPostal_code());
        account.setFullAddress(googleMapSearchItem.getFull_address());
        account.setAccount_Type("Company Property");
        account.setServicesProvided(servicesProvided);
        account.setOwner(owner);
        ArrayList<String> duplicateFieldCheck = new ArrayList<>();
        duplicateFieldCheck.add("Account_Name");
        duplicateFieldCheck.add("Property_Street");
        duplicateFieldCheck.add("Full_Address");
        AccountRequest accountRequest = new AccountRequest();
        ArrayList<Account> accounts = new ArrayList<>();
        accounts.add(account);
        accountRequest.setData(accounts);
        accountRequest.setDuplicate_check_fields(duplicateFieldCheck);
        return CommonUtils.getObjectMapper().writeValueAsString(accountRequest);

    }
    private String populateVendor(GoogleMapSearchItem googleMapSearchItem,Users users)throws JsonProcessingException {
            VendorRequest vendorRequest = new VendorRequest();
            ArrayList<Vendor> data = new ArrayList<>();
            Vendor vendor = new Vendor();
            vendor.setVendor_Name(googleMapSearchItem.getName());
            vendor.setCity(googleMapSearchItem.getCity());
            vendor.setStreet(googleMapSearchItem.getStreet());
            vendor.setCountry(googleMapSearchItem.getCountry());
            vendor.setPhone(googleMapSearchItem.getPhone());
            vendor.setState(googleMapSearchItem.getState());
            vendor.setWebsite(googleMapSearchItem.getSite());
            vendor.setDescription(googleMapSearchItem.getCategory());
            vendor.setGoogleId(googleMapSearchItem.getGoogle_id());
            vendor.setGooglRating(String.valueOf(googleMapSearchItem.getRating()));
            vendor.setGoogleReviewLink(googleMapSearchItem.getReviews_link());
            vendor.setBusinessStatus(googleMapSearchItem.getBusiness_status());
            vendor.setAddress(googleMapSearchItem.getFull_address());
            vendor.setTimeZone(googleMapSearchItem.getTime_zone());
            setStarReviews(vendor,googleMapSearchItem);
            vendor.setLatitude(String.valueOf(googleMapSearchItem.getLatitude()));
            vendor.setLongitude(String.valueOf(googleMapSearchItem.getLongitude()));
            vendor.setZip_Code(googleMapSearchItem.getPostal_code());
            vendor.setGoogleVerified(googleMapSearchItem.isVerified());
            vendor.setWorkingHours(googleMapSearchItem.getWorking_hours_old_format());
            vendor.setGoogleCategory(googleMapSearchItem.getCategory());
            setSubTypes(vendor,googleMapSearchItem);
            setWorkingHours(vendor,googleMapSearchItem);
            Owner owner = new Owner();
            owner.setId(users.getUsers().get(0).id);
            vendor.setOwner(owner);
            data.add(vendor);
            vendorRequest.setData(data);
            setDuplicateFields(vendorRequest);
            return CommonUtils.getObjectMapper().writeValueAsString(vendorRequest);


    }
    private void setStarReviews(Vendor vendor,GoogleMapSearchItem googleMapSearchItem){
        vendor.setGoogle1StarReviews(String.valueOf(googleMapSearchItem.getReviews_per_score().get_1()));
        vendor.setGoogle2StarReviews(String.valueOf(googleMapSearchItem.getReviews_per_score().get_2()));
        vendor.setGoogle3StarReviews(String.valueOf(googleMapSearchItem.getReviews_per_score().get_3()));
        vendor.setGoogle4StarReviews(String.valueOf(googleMapSearchItem.getReviews_per_score().get_4()));
        vendor.setGoogle5StarReviews(String.valueOf(googleMapSearchItem.getReviews_per_score().get_5()));
    }
    private void setDuplicateFields(VendorRequest vendorRequest){
        ArrayList<String> duplicateFieldCheck = new ArrayList<>();
        duplicateFieldCheck.add("Phone");
        duplicateFieldCheck.add("Vendor_Name");
        duplicateFieldCheck.add("Street");
        vendorRequest.setDuplicate_check_fields(duplicateFieldCheck);
    }
    private void setSubTypes(Vendor vendor,GoogleMapSearchItem googleMapSearchItem){
        ArrayList<String> subTypes = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(googleMapSearchItem.getSubtypes(),",");
        while (tokenizer.hasMoreTokens()) {
            subTypes.add(tokenizer.nextToken());
        }
        vendor.setSubTypes(subTypes);
    }
    private void setWorkingHours(Vendor vendor,GoogleMapSearchItem googleMapSearchItem){
        StringTokenizer weekdays = new StringTokenizer(googleMapSearchItem.getWorking_hours_old_format(),"|");
        int weeDayCount = 0;
        while (weekdays.hasMoreTokens()) {
            if(weeDayCount==0) {
                vendor.setMonHours(weekdays.nextToken());
            }
            if(weeDayCount==1) {
                vendor.setTueHours(weekdays.nextToken());
            }
            if(weeDayCount==2) {
                vendor.setWedHours(weekdays.nextToken());
            }
            if(weeDayCount==3) {
                vendor.setThuHours(weekdays.nextToken());
            }
            if(weeDayCount==4) {
                vendor.setFriHours(weekdays.nextToken());
            }
            if(weeDayCount==5) {
                vendor.setSatHours(weekdays.nextToken());
            }
            if(weeDayCount==6) {
                vendor.setSunHours(weekdays.nextToken());
            }
            weeDayCount++;
        }
    }





}
