package com.get3bids.scrappers.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.get3bids.scrappers.cofig.ZohoConfig;
import com.get3bids.scrappers.dti.outscraper.GoogleMapSearchItem;
import com.get3bids.scrappers.dti.zoho.*;
import com.get3bids.scrappers.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
@Slf4j
@Service
public class ZohoService {
    @Autowired
    ZohoConnectionService zohoConnectionService;
    @Autowired
    ZohoConfig zohoConfig;
    @Autowired
    GoogleScrapperService googleScrapperService;
    public final String GOOGLE_MAPS_SEARCH_QUERY = "roof contractors in florida";
    public final int GOOGLE_MAPS_SEARCH_RECORD_LIMIT = 20;
    public final int GOOGLE_MAPS_SEARCH_REVIEW_LIMIT = 20;
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
        HttpResult httpResult = zohoConnectionService.postWithAccessToken(zohoConfig,zohoConfig.getApiDomain()+"/crm/v2/Vendors",vendorStr);
        if(httpResult.getStatusCode() == 201) {
            commonResponse = CommonUtils.getObjectMapper().readValue(httpResult.getResult(),CommonResponse.class);
        }
        return commonResponse;
    }
    private CommonResponse createAccount(String  accountStr)throws JsonProcessingException{
        CommonResponse commonResponse = null;
        HttpResult httpResult = zohoConnectionService.postWithAccessToken(zohoConfig,zohoConfig.getApiDomain()+"/crm/v2/Accounts",accountStr);
        if(httpResult.getStatusCode() == 201) {
            commonResponse = CommonUtils.getObjectMapper().readValue(httpResult.getResult(),CommonResponse.class);
        }
        return commonResponse;
    }
    public boolean runJob(){
        try {
            HashMap inputMap = new HashMap<String, Object>() {{
                put("query", GOOGLE_MAPS_SEARCH_QUERY);
                put("reviewsLimit", GOOGLE_MAPS_SEARCH_REVIEW_LIMIT);
                put("limit", GOOGLE_MAPS_SEARCH_RECORD_LIMIT);
                put("language", "en");
            }};
            Users users = getUsers();
            if(users!=null) {
                CommonResponse commonResponse = null;
                List<GoogleMapSearchItem> googleMapSearchItemList = googleScrapperService.googleMapsSearchV2(inputMap);
                for (GoogleMapSearchItem googleMapSearchItem : googleMapSearchItemList) {
                    commonResponse = createVendor(populateVendor(googleMapSearchItem, users));
                    commonResponse = createAccount(populateAccount(googleMapSearchItem, users, commonResponse));
                    log.info("Company and Location Creation Status : "+commonResponse.getData().get(0).getStatus());
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
        account.setAccount_Name(googleMapSearchItem.getName());
        account.setBilling_City(googleMapSearchItem.getCity());
        account.setBilling_State(googleMapSearchItem.getState());
        account.setBilling_Street(googleMapSearchItem.getStreet());
        account.setDescription(googleMapSearchItem.getCategory());
        account.setPropertyCity(googleMapSearchItem.getCity());
        account.setPropertyState(googleMapSearchItem.getState());
        account.setPropertyStreet(googleMapSearchItem.getStreet());
        account.setPropertyZip(googleMapSearchItem.getPostal_code());
        account.setFullAddress(googleMapSearchItem.getFull_address());
        account.setOwner(owner);
        AccountRequest accountRequest = new AccountRequest();
        ArrayList<Account> accounts = new ArrayList<>();
        accounts.add(account);
        accountRequest.setData(accounts);
        String accountStr = CommonUtils.getObjectMapper().writeValueAsString(accountRequest);
        return accountStr;
    }
    private String populateVendor(GoogleMapSearchItem googleMapSearchItem,Users users)throws JsonProcessingException{
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
            //vendor.setLatitude(String.valueOf(googleMapSearchItem.getLatitude()));
            //vendor.setLongitude(String.valueOf(googleMapSearchItem.getLongitude()));
            vendor.setZip_Code(googleMapSearchItem.getPostal_code());
            Owner owner = new Owner();
            owner.setId(users.getUsers().get(0).id);
            vendor.setOwner(owner);
            data.add(vendor);
            vendorRequest.setData(data);
            String vendorStr = CommonUtils.getObjectMapper().writeValueAsString(vendorRequest);
            return vendorStr;

    }





}