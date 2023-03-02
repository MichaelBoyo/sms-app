package com.decoded.ussd.services.notification;

import com.decoded.ussd.data.dtos.SmsRequest;

public interface SMSNotificationService {

    void sendSms(SmsRequest smsRequest);
}
