package com.get3bids.acculynx.services;

import com.get3bids.exception.Get3BidsException;
import com.get3bids.scrappers.cofig.AccuLynxConfig;
import com.get3bids.scrappers.dti.zoho.HttpResult;
import com.get3bids.utils.CommonUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccuLynxConnectionService {
    @Autowired
    AccuLynxConfig accuLynxConfig;
    public  HttpResult post(String URL,String requestObject){
        HttpResponse response = null;
        String result = null;
        var httpResult = new HttpResult();
        try(CloseableHttpClient httpclient = HttpClients.custom().build()) {
            var builder = new URIBuilder(accuLynxConfig.getBaseURL()+URL);
            var uri = builder.build();
            var request = new HttpPost(uri);
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Authorization","Bearer "+accuLynxConfig.getApiKey());
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
