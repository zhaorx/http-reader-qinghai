package com.hy.http;


import com.hy.http.model.Gas;
import com.hy.http.model.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class SchedulerTask {
    private static final Logger logger = LoggerFactory.getLogger(SchedulerTask.class);
    @Value("${push-url}")
    private String pushUrl;
    @Value("${data-url}")
    private String dataUrl;

    @Scheduled(fixedDelayString = "${interval}")
    public void transferSchedule() {
        logger.info("starting transfer...");
        List<Gas> list = this.getRecentGas();
        Result result = this.batchAddTaos(list);
        logger.info(result.getMessage());
//        Result result = this.addTaos(list.get(0));
//        System.out.println(result.getMessage());
    }

    private Result addTaos(Gas data) {
        String url = "http://localhost:6666/gas/add";
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        RestTemplate restTemplate = new RestTemplate();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = sdf.format(data.getTs());

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("ts", dateString);
        requestBody.put("point", data.getPoint());
        requestBody.put("pname", data.getPname());
        requestBody.put("unit", data.getUnit());
        requestBody.put("region", data.getRegion());
        requestBody.put("value", data.getValue());

        HttpEntity<Map<String, Object>> r = new HttpEntity<>(requestBody, requestHeaders);

        // 请求服务端添加玩家
        Result result = restTemplate.postForObject(url, r, Result.class);

        return result;

    }

    private Result batchAddTaos(List<Gas> list) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        RestTemplate restTemplate = new RestTemplate();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        List<String> tsList = list.stream().map(item -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(item.getTs())).collect(Collectors.toList());
        List<Double> valueList = list.stream().map(Gas::getValue).collect(Collectors.toList());

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("tss", tsList);
        requestBody.put("values", valueList);
        requestBody.put("point", list.get(0).getPoint());
        requestBody.put("pname", list.get(0).getPname());
        requestBody.put("unit", list.get(0).getUnit());
        requestBody.put("region", list.get(0).getRegion());
        HttpEntity<Map<String, Object>> r = new HttpEntity<>(requestBody, requestHeaders);

        // 请求服务端添加玩家
        Result result = restTemplate.postForObject(pushUrl, r, Result.class);
        return result;
    }


    public List<Gas> getRecentGas() {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        RestTemplate restTemplate = new RestTemplate();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(new Date());
        rightNow.add(Calendar.MONTH, -1);
        Date dt1 = rightNow.getTime();
        String ts_start = sdf.format(dt1);
        Map<String, String> map = new HashMap();
        map.put("ts_start", ts_start);
        map.put("ts_end", sdf.format(new Date()));

        // 请求服务端添加玩家
        Result result = restTemplate.getForObject(dataUrl, Result.class, map);

        return result.getList();
    }
}
