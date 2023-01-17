package com.dfssi.dataplatform.analysis.utils;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class HttpRequest {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private String LOG_TAG = "Http Request";
    public static final String CHARSET = "UTF-8";

    private PoolingHttpClientConnectionManager cm = null;
    private CloseableHttpClient httpClient = null;

    public HttpRequest(String logTag) {
        LOG_TAG = logTag;
        this.init();
    }

    public void init() {
        cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(100);

        httpClient = HttpClients.custom().setConnectionManager(cm).build();
    }

    public String buildUrlForGet(String url, Map<String, Object> params) throws IOException {
        if (params != null && !params.isEmpty()) {
            List<NameValuePair> pairs = new ArrayList<NameValuePair>(params.size());
            for (String key : params.keySet()) {
                pairs.add(new BasicNameValuePair(key, params.get(key).toString()));
            }
            url += "?" + EntityUtils.toString(new UrlEncodedFormEntity(pairs, CHARSET));
        }

        return url;
    }

    public String get(String url, Map<String, Object> params) throws IOException {
        CloseableHttpResponse httpResponse = null;
        try {
            HttpGet httpGet = new HttpGet(this.buildUrlForGet(url, params));
            httpResponse = httpClient.execute(httpGet);

            return this.readStringFromResponse(httpResponse);
        } catch (IOException e) {
            logger.error(LOG_TAG + "Fail to send get request. url=" + url, e);
            throw e;
        } finally {
            this.closeResponse(httpResponse);
        }
    }

    public String post(String url, Map<String, Object> params) throws IOException {
        CloseableHttpResponse httpResponse = null;
        try {
            List<NameValuePair> pairs = null;
            if (params != null && !params.isEmpty()) {
                pairs = new ArrayList<NameValuePair>(params.size());
                for (String key : params.keySet()) {
                    pairs.add(new BasicNameValuePair(key, params.get(key).toString()));
                }
            }
            HttpPost httpPost = new HttpPost(url);
            if (pairs != null && pairs.size() > 0) {
                httpPost.setEntity(new UrlEncodedFormEntity(pairs, CHARSET));
            }
            httpResponse = httpClient.execute(httpPost);

            return this.readStringFromResponse(httpResponse);
        } catch (IOException e) {
            logger.error(LOG_TAG + "Fail to send post request. url=" + url, e);
            throw e;
        } finally {
            this.closeResponse(httpResponse);
        }
    }

    private String readStringFromResponse(CloseableHttpResponse httpResponse) throws IOException {
        if (httpResponse == null) {
            return null;
        }

        HttpEntity entity = httpResponse.getEntity();
        if (null != entity) {
            return EntityUtils.toString(entity, CHARSET);
        }

        return null;
    }

    private void closeResponse(CloseableHttpResponse httpResponse) {
        if (httpResponse != null) {
            try {
                EntityUtils.consume(httpResponse.getEntity());
                httpResponse.close();
            } catch (IOException e) {
                logger.error(LOG_TAG + "Fail to close response.", e);
            }
        }
    }
}