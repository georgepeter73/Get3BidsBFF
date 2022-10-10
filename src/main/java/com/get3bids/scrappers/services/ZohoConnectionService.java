package com.get3bids.scrappers.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.get3bids.exception.Get3BidsException;
import com.get3bids.scrappers.cofig.ZohoConfig;
import com.get3bids.scrappers.dti.zoho.HttpResult;
import com.get3bids.scrappers.dti.zoho.TokenRefreshResponse;
import com.get3bids.utils.CommonUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

@Service
public class ZohoConnectionService {

    public TokenRefreshResponse getRefreshToken(ZohoConfig zohoConfig, String fullURL)throws JsonProcessingException {
        TokenRefreshResponse tokenRefreshResponse = null;
        HttpResult httpResult = refreshToken(zohoConfig, fullURL);
        tokenRefreshResponse = CommonUtils.getObjectMapper().readValue(httpResult.getResult(), TokenRefreshResponse.class);
        return tokenRefreshResponse;
    }

    private HttpResult refreshToken(ZohoConfig zohoConfig, String fullURL){
        HttpResponse response = null;
        String result = null;
        var httpResult = new HttpResult();
        try(CloseableHttpClient httpclient = HttpClients.custom().build()) {
            var builder = new URIBuilder(fullURL);
            var uri = builder.build();
            var request = new HttpPost(uri);
            request.setHeader("client_id",zohoConfig.getClientId());
            request.setHeader("client_secret",zohoConfig.getClientSecret());
            request.setHeader("refresh_token",zohoConfig.getRefreshToken());
            request.setHeader("grant_type","refresh_token");
            response = httpclient.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity);
            httpResult.setResult(result);
            httpResult.setStatusCode(statusCode);

        }catch(Exception ex){
            throw new Get3BidsException(CommonUtils.getExceptionMessage(ex));
        }
        return httpResult;
    }


    public  HttpResult getWithAccessToken(ZohoConfig zohoConfig, String fullURL){
        HttpResponse response = null;
        String result = null;
        var httpResult = new HttpResult();
        try(CloseableHttpClient httpclient = HttpClients.custom().build()) {
            var builder = new URIBuilder(fullURL);
            var uri = builder.build();
            var request = new HttpGet(uri);
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Authorization","Zoho-oauthtoken "+zohoConfig.getAccessToken());
            response = httpclient.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity);
            httpResult.setResult(result);
            httpResult.setStatusCode(statusCode);

        }catch(Exception ex){
            throw new Get3BidsException(CommonUtils.getExceptionMessage(ex));
        }
        return httpResult;

    }
    public  HttpResult postWithAccessToken(ZohoConfig zohoConfig, String fullURL,String requestObject){
        HttpResponse response = null;
        String result = null;
        var httpResult = new HttpResult();
        try(CloseableHttpClient httpclient = HttpClients.custom().build()) {
            var builder = new URIBuilder(fullURL);
            var uri = builder.build();
            var request = new HttpPost(uri);
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Authorization","Zoho-oauthtoken "+zohoConfig.getAccessToken());
            var reqEntity = new StringEntity(requestObject);
            request.setEntity(reqEntity);
            response = httpclient.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity);
            httpResult.setResult(result);
            httpResult.setStatusCode(statusCode);

        }catch(Exception ex){
            throw new Get3BidsException(CommonUtils.getExceptionMessage(ex));
        }
        return httpResult;

    }
}
