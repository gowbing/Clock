package com.miracle.clock.utils.http;

import com.miracle.clock.model.event.HttpClientGetEvent;
import com.miracle.clock.utils.normal.StringUtils;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by hss on 2017/7/9.
 */

public class HttpClient {
    private static HttpClient mInstance = new HttpClient();

    public static HttpClient getInstance() {
        return mInstance;
    }

    public void getFormReq(String urlReq) {
        try {
            // Configure and open a connection to the site you will send the request
            URL url = new URL(urlReq);
            URLConnection urlConnection = url.openConnection();
            // 设置doOutput属性为true表示将使用此urlConnection写入数据
            urlConnection.setDoOutput(false);
            // 定义待写入数据的内容类型，我们设置为application/x-www-form-urlencoded类型
            urlConnection.setRequestProperty("content-type", "application/x-www-form-urlencoded");
//            // 得到请求的输出流对象
//            OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
//            // 把数据写入请求的Body
//            out.write(param);
//            out.flush();
//            out.close();

            // 从服务器读取响应
            InputStream inputStream = urlConnection.getInputStream();
            String encoding = "utf-8";
            String body = new String(StringUtils.toByteArray(inputStream), encoding);
            Logger.d(body);
            EventBus.getDefault().post(new HttpClientGetEvent(body));
        } catch (IOException e) {
            Logger.d(String.valueOf(e));
        }
    }

    public void postFormReq(String urlReq, String param) {
        try {
            // Configure and open a connection to the site you will send the request
            URL url = new URL(urlReq);
            URLConnection urlConnection = url.openConnection();
            // 设置doOutput属性为true表示将使用此urlConnection写入数据
            urlConnection.setDoOutput(true);
            // 定义待写入数据的内容类型，我们设置为application/x-www-form-urlencoded类型
            urlConnection.setRequestProperty("content-type", "application/x-www-form-urlencoded");
            // 得到请求的输出流对象
            OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
            // 把数据写入请求的Body
            out.write(param);
            out.flush();
            out.close();

            // 从服务器读取响应
            InputStream inputStream = urlConnection.getInputStream();
            String encoding = "utf-8";
            String body = new String(StringUtils.toByteArray(inputStream), encoding);
            Logger.d(body);
        } catch (IOException e) {
            Logger.d(String.valueOf(e));
        }
    }
}
