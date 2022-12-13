package com.neverdroid.ecoflow.bot.service;

import com.neverdroid.ecoflow.bot.model.QueryDeviceQuota;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class EcoFlow {

    private final RestTemplate restTemplate;

    @Value("${appKey}")
    String appKey;

    @Value("${secretKey}")
    String secretKey;

    public EcoFlow(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public QueryDeviceQuota getDeviceQuota(String deviceId) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();

        headers.add("Content-Type", "application/json");
        headers.add("appKey", appKey);
        headers.add("secretKey", secretKey);

        ResponseEntity<QueryDeviceQuota> entity = restTemplate.exchange(
                "https://api.ecoflow.com/iot-service/open/api/device/queryDeviceQuota?sn=" + deviceId, HttpMethod.GET, new HttpEntity<>(headers),
                QueryDeviceQuota.class);

        return entity.getBody();
    }
}
