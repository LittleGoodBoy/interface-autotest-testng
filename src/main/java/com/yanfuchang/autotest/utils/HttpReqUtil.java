package com.yanfuchang.autotest.utils;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * 发送http请求工具类
 */
public class HttpReqUtil {
    //cookie存储
    private static CookieStore cookieStore = new BasicCookieStore();
    //从xml配置文件中获取请求头配置信息
    private static Map<String, String> headerConfig = new ParseXmlUtil().getHeaderConfig();
    //从xml配置文件中获取请求配置信息
    private static Map<String, String> reqConfig = new ParseXmlUtil().getReqConfig();

    /**
     * 请求配置
     */
    public static void httpReqConfig(HttpRequestBase httpRequestBase) {
        //header配置
        for (Entry<String, String> entry : headerConfig.entrySet()) {
            httpRequestBase.setHeader(entry.getKey(), entry.getValue());
        }
        //请求超时设置
        RequestConfig config = RequestConfig.custom()
                .setConnectionRequestTimeout(Integer.valueOf(reqConfig.get("reqTimeout")))
                .build();
        httpRequestBase.setConfig(config);
    }

    public static String sendGet(String url, String param) {
        //初始化
        String result = null;
        CloseableHttpResponse response = null;
        String finalUrl = url + "?" + param;
        //创建httpclient
        CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultCookieStore(cookieStore)//配置每次请求都带上cookie
                .build();
        try {
            //实例化一个get请求对象
            HttpGet httpGet = new HttpGet(finalUrl);
            //添加header
            httpReqConfig(httpGet);
            //发送请求
            response = httpclient.execute(httpGet);
            //获取响应内容
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                result = EntityUtils.toString(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                if (httpclient != null) {
                    httpclient.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static String sendPost(String url, String param) {
        //初始化
        String result = null;
        CloseableHttpResponse response = null;

        //创建httpclient
        CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultCookieStore(cookieStore)
                .build();
        try {
            try {
                HttpPost httpPost = new HttpPost(url);

                //添加header
                httpReqConfig(httpPost);
                StringEntity stringEntity = new StringEntity(param, "UTF-8");
                if (new ParseJsonToMapUtil().isJsonString(param)) {
                    //请求json格式参数
                    stringEntity.setContentType(reqConfig.get("reqContentTypeJson"));
                } else {
                    //请求form格式参数
                    stringEntity.setContentType(reqConfig.get("reqContentTypeForm"));
                }

                //发送post请求
                httpPost.setEntity(stringEntity);
                response = httpclient.execute(httpPost);

                //获取响应内容
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    HttpEntity entity = response.getEntity();
                    result = EntityUtils.toString(entity);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    //关闭数据流
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    public static void main(String[] args) {


        //案例
        String url_a = "http://www.nuandao.com/public/lazyentrance";
        String param_a = "isajax=1&remember=1&email=1477222170@qq.com&password=testing1?&agreeterms=1&itype=&book=1&m=0.2757277030262314";
        sendPost(url_a, param_a);


        //案例
        String url_b = "http://www.nuandao.com/shopping/cart";
        String param_b = "countdown=1&m=0.3810762454661424";
        sendPost(url_b, param_b);
		
		/*
		//案例
		String url_c = "http://www.nuandao.com/Ajax/personal";
		String param_c = "default=1&pagesign=user&url=http%3A%2F%2Fwww.nuandao.com%2Fuser%2Fmyorder&m=0.7948146575033792";
		sendPost(url_c,param_c);
		 
		//案例
		String url_d = "https://m.jiuxian.com/m_v1/goods/get_goods_num_by_province";
		String param_d = "goods_id=28045&province_id=500";
		sendPost(url_d,param_d);
		*/
    }
}
