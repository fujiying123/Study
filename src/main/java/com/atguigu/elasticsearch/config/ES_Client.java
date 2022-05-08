package com.atguigu.elasticsearch.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.autoconfigure.elasticsearch.RestClientBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.swing.event.CaretListener;
import java.io.IOException;

/**
 * @Description：
 * @Date： 2021/8/12
 * @Author：小影
 */
//@Configuration
public class ES_Client {

    @Bean("restHighLevelClient")
    public RestHighLevelClient getEsClient()  {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("192.168.74.128", 9200, "http"),
                        new HttpHost("192.168.74.130", 9200, "http"),
                        new HttpHost("192.168.74.132", 9200, "http")));
        return client;
    }
}
