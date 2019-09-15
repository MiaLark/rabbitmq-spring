package com.wangwei.util;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author: wangwei
 * @date: 2019-09-15 15:53
 */
@Slf4j
public class BankUtil {

    public static String getBankName(String card){

        String urlString = new StringBuilder().append("https://ccdcapi.alipay.com/validateAndCacheCardInfo.json")
                .append("?cardNo=")
                .append(card.trim())
                .append("&cardBinCheck=true").toString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(MediaType.APPLICATION_FORM_URLENCODED_VALUE+";charset=UTF-8"));
        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();

        CompletableFuture<ResponseEntity<String>> supplyAsync = CompletableFuture.supplyAsync(() -> restTemplate.exchange(urlString, HttpMethod.GET, httpEntity, String.class));

        ResponseEntity<String> responseEntity = null;

        try {
            /*获取响应数据*/
            responseEntity = supplyAsync.get();
        }
        catch (InterruptedException | ExecutionException e) {
            log.error("async同盾反欺诈信息异步查询失败 ==> [{}]", e.getMessage());
        }

        if (Objects.isNull(responseEntity) || !responseEntity.getStatusCode().equals(HttpStatus.OK)) {
            log.warn("TONGDUN-RES-BODY ==> [{}]", responseEntity);
            return null;
        }

        log.info("TONGDUN-RES-BODY ==> [{}]", responseEntity.getBody());
        BnakInfo bnakInfo = JSON.parseObject(responseEntity.getBody(), BnakInfo.class);

        return bnakInfo.getBank();
    }

    public static String getBankLogo(){
        return "";
    }
}