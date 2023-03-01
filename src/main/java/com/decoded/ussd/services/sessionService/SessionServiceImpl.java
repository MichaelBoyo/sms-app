package com.decoded.ussd.services.sessionService;

import com.decoded.ussd.data.models.UssdSession;
import com.decoded.ussd.data.repositories.UssdSessionRepository;

import com.decoded.ussd.services.walletService.UssdException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final UssdSessionRepository ussdSessionRepository;

    @Override
    public UssdSession createUssdSession(UssdSession session) {
        return ussdSessionRepository.save(session);
    }

    @Override
    public UssdSession getUssdSession(String id) {
        return ussdSessionRepository.findById(id).orElse(null);
    }

    @Override
    public UssdSession update(UssdSession session) {
        if (ussdSessionRepository.existsById(session.getId())) {
            return ussdSessionRepository.save(session);
        }
        throw new UssdException("session not found");
    }


}
