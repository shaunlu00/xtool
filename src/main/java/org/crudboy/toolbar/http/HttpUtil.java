package org.crudboy.toolbar.http;

import com.google.common.base.Strings;
import org.apache.http.*;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.crudboy.toolbar.EmptyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpUtil {

    private static CloseableHttpClient httpclient = HttpClientBuilder.create().build();

    private static Logger logger = LoggerFactory.getLogger(HttpUtil.class);

    private static String sendRequest(HttpRequestBase httpRequest) throws IOException {
        httpRequest.addHeader(HTTP.CONTENT_ENCODING,"UTF-8");
        String result = null;
        try (CloseableHttpResponse resp = httpclient.execute(httpRequest)) {
            HttpEntity entity = resp.getEntity();
            result = (entity == null ? null : EntityUtils.toString(entity, Consts.UTF_8));
            StatusLine statusLine = resp.getStatusLine();
            if(statusLine.getStatusCode() >= 300) {
                EntityUtils.consume(entity);
                String errorMsg = Strings.lenientFormat("Http status abnormal-status code is %s, response is %s", resp.getStatusLine().getStatusCode(), result);
                logger.error(errorMsg);
                throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
            }
        }
        return result;
    }

    private static byte[] sendRequestWithByteArrayResp(HttpRequestBase httpRequest) throws IOException {
        httpRequest.addHeader(HTTP.CONTENT_ENCODING,"UTF-8");
        byte[] result = null;
        try (CloseableHttpResponse resp = httpclient.execute(httpRequest)) {
            HttpEntity entity = resp.getEntity();
            result = (entity == null ? null : EntityUtils.toByteArray(entity));
            StatusLine statusLine = resp.getStatusLine();
            if(statusLine.getStatusCode() >= 300) {
                EntityUtils.consume(entity);
                String errorMsg = Strings.lenientFormat("Http status abnormal-status code is %s, response is %s", resp.getStatusLine().getStatusCode(), result);
                logger.error(errorMsg);
                throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
            }
        }
        return result;
    }

    private static void prepareHeaders(HttpRequestBase httpRequest, Map<String, String> headers) {
        if (!EmptyUtil.isEmpty(headers)) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpRequest.addHeader(entry.getKey(), entry.getValue());
            }
        }
    }

    public static String doGet(String url) throws IOException {
        HttpGet httpget = new HttpGet(url);
        return sendRequest(httpget);
    }

    public static String doPost(String url) throws IOException {
        HttpPost httpost = new HttpPost(url);
        return sendRequest(httpost);
    }

    public static byte[] doPostWithByteArrayResp(String url) throws IOException {
        HttpPost httpost = new HttpPost(url);
        return sendRequestWithByteArrayResp(httpost);
    }

    public static String doGet(String url, Map<String, String> headers) throws IOException {
        HttpGet httpget = new HttpGet(url);
        prepareHeaders(httpget, headers);
        return sendRequest(httpget);
    }

    public static String doPost(String url, Map<String, String> headers) throws IOException {
        HttpPost httpost = new HttpPost(url);
        prepareHeaders(httpost, headers);
        return sendRequest(httpost);
    }

    public static String doPostJsonRequest(String url, Map<String, String> headers, String jsonStr) throws IOException {
        HttpPost httpost = new HttpPost(url);
        prepareHeaders(httpost, headers);
        StringEntity entity = new StringEntity(jsonStr, Consts.UTF_8);
//        entity.setContentEncoding("UTF-8");
        entity.setContentType("application/json");
        httpost.setEntity(entity);
        return sendRequest(httpost);
    }

    public static String doPostFormRequest(String url, Map<String, String> headers, Map<String, String> formparams) throws IOException {
        HttpPost httpost = new HttpPost(url);
        prepareHeaders(httpost, headers);
        List<NameValuePair> params = new ArrayList<>();
        if (!EmptyUtil.isEmpty(formparams)) {
            for (Map.Entry<String, String> entry : formparams.entrySet()) {
                params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
        }
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, Consts.UTF_8);
        httpost.setEntity(entity);
        return sendRequest(httpost);
    }

    public static String doPostMultipartRequest(String url, Map<String, String> headers, Map<String, String> formparams, String fileFieldKey, File file) throws IOException {
        HttpPost httpost = new HttpPost(url);
        prepareHeaders(httpost, headers);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        if (!EmptyUtil.isEmpty(formparams)) {
            for (Map.Entry<String, String> entry : formparams.entrySet()) {
                builder.addTextBody(entry.getKey(), entry.getValue());
            }
        }
        builder.addBinaryBody(fileFieldKey, file, ContentType.MULTIPART_FORM_DATA, file.getName());
        httpost.setEntity(builder.build());
        return sendRequest(httpost);
    }
}
