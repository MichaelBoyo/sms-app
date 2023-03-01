package com.decoded.ussd.services.sessionService;

import com.decoded.ussd.data.models.UssdSession;

public interface SessionService {
    UssdSession createUssdSession(UssdSession session);
    UssdSession getUssdSession(String id);
    UssdSession update(UssdSession session);
}
