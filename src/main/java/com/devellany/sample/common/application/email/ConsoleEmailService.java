package com.devellany.sample.common.application.email;

import com.devellany.sample.common.application.EmailService;
import com.devellany.sample.common.domain.EmailMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Profile({"local", "test"})
@Component
public class ConsoleEmailService implements EmailService {

    @Override
    public void sendEmail(EmailMessage emailMessage) {
        log.info("sent email: {}", emailMessage.getMessage());
    }
}
