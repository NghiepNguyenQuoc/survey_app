package com.nghiepnguyen.survey.networking;

import android.os.Build;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;


/**
 * Created by nghiep on 10/29/15.
 */
public class SurveyApiClient {
    private static String TAG = "SurveyApiClient";
    private static String charset = "UTF-8";  // Or in Java 7 and later, use the constant: java.nio.charset.StandardCharsets.UTF_8.name()

    public static void get(String urlString, HashMap<String, String> params) {
        try {
            if (params.size() > 0)
                urlString += "?" + getRequestPara(params);

            // init HttpURLConnection
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Accept-Charset", charset);
            urlConnection.setDoOutput(true);
            if (Build.VERSION.SDK != null && Build.VERSION.SDK_INT > 13) {
                urlConnection.setRequestProperty("Connection", "close");
            }
            //urlConnection.setReadTimeout(10 * 1000);


            // get respone
            int responseCode = urlConnection.getResponseCode();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(urlConnection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            //print result
            System.out.println(response.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    public static void post(String urlString, HashMap<String, String> params) {
        try {
            // init HttpURLConnection
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Accept-Charset", charset);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);
            if (Build.VERSION.SDK != null && Build.VERSION.SDK_INT > 13) {
                urlConnection.setRequestProperty("Connection", "close");
            }

            // put data to server
            DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
            wr.writeBytes(getRequestPara(params));
            wr.flush();
            wr.close();

            // get respone
            int responseCode = urlConnection.getResponseCode();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(urlConnection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            //print result
            System.out.println(response.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getRequestPara(HashMap<String, String> params) {
        StringBuilder sbParams = new StringBuilder();
        int i = 0;
        for (String key : params.keySet()) {
            try {
                if (i != 0) {
                    sbParams.append("&");
                }
                sbParams.append(key).append("=")
                        .append(URLEncoder.encode(params.get(key), charset));

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            i++;
        }
        return sbParams.toString();
    }

    /*
    public static void getNoHeader(String absoluteUrl) {

    }

    public static void put(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        getClient().put(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void delete(Context context, String url, AsyncHttpResponseHandler responseHandler) {
        getClient().delete(context, getAbsoluteUrl(url), responseHandler);
    }*/


}
