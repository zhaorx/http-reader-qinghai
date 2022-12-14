package com.hy.http;


import com.hy.http.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class SchedulerTask {
    private static final Logger logger = LoggerFactory.getLogger(SchedulerTask.class);
    @Value("${push-url}")
    private String pushUrl;
    @Value("${push-multi-url}")
    private String pushMultiUrl;
    @Value("${data-url}")
    private String dataUrl;
    @Value("${token}")
    private String token;
    @Value("${region}")
    private String region;
    @Value("#{${unit-map}}")
    private Map<String, String> unitMap;
    private String sep = "_";

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Scheduled(fixedDelayString = "${interval}")
    public void transferSchedule() {
        logger.info("starting transfer...");
        Result r = this.getRecentData();



        if (r.getData() == null || !r.getStatus()) {
            return;
        }

        String dateStr = sdf.format(new Date());
        List<Gas> list = new ArrayList<>();
        for (int i = 0; i < r.getData().size(); i++) {
            DataItem item = r.getData().get(i);

            for (int j = 0; j < item.getParameters().size(); j++) {
                Param p = item.getParameters().get(j);

                Gas g = new Gas();
                g.setTs(dateStr);
                g.setRegion(region);
                g.setPoint(region + sep + item.getLineCode() + sep + p.getParaCode());
                g.setPname(region + sep + item.getLineName() + sep + p.getParaName());
                g.setValue(p.getParaValue());
                g.setUnit(unitMap.get(p.getParaCode()));
                g.setRegion(region);
                logger.debug("######point_data:" + g.toString());
                list.add(g);
            }
        }
        WritterResult result = this.addMultiTaos(list);
        logger.info(result.getMessage());
    }

    public Result getRecentData() {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentLength(0);
        requestHeaders.add("token", token);
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(new HashMap(), requestHeaders);

        // ???????????????????????????
        ResponseEntity<Result> rentity = restTemplate.exchange(dataUrl, HttpMethod.POST, httpEntity, Result.class);
        return rentity.getBody();
    }

    public WritterResult addMultiTaos(List<Gas> list) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("gasList", list);

        HttpEntity<Map<String, Object>> r = new HttpEntity<>(requestBody, requestHeaders);
        WritterResult result = restTemplate.postForObject(pushMultiUrl, r, WritterResult.class);

        return result;
    }

    private WritterResult addTaos(Gas data) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        RestTemplate restTemplate = new RestTemplate();

        String dateString = sdf.format(data.getTs());

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("ts", dateString);
        requestBody.put("point", data.getPoint());
        requestBody.put("pname", data.getPname());
        requestBody.put("unit", data.getUnit());
        requestBody.put("region", data.getRegion());
        requestBody.put("value", data.getValue());

        HttpEntity<Map<String, Object>> r = new HttpEntity<>(requestBody, requestHeaders);

        // ???????????????????????????
        WritterResult result = restTemplate.postForObject(pushUrl, r, WritterResult.class);

        return result;

    }
}
