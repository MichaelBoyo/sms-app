package com.decoded.ussd.services.notification;

import com.decoded.ussd.data.dtos.SmsRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class SMSNotificationServiceImpl implements SMSNotificationService {
    private final RestTemplate restTemplate;

    @Override
    @Async
    public void sendSms(SmsRequest smsRequest) {
        log.info("request => {}", smsRequest);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "App 85948857fdc97006de563302d3a6e753-52ba24f1-1e8e-4898-a5c7-c80aa8637c80");
        HttpEntity<SmsRequest> req = new HttpEntity<>(smsRequest, httpHeaders);
        ResponseEntity<Object> res = restTemplate.postForEntity("https://ejwd9n.api.infobip.com/sms/2/text/advanced",
                req, Object.class);

        log.info("res => {}", res);
        log.info("status code => {}", res.getStatusCode());

    }
}
