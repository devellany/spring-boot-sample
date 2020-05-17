package com.devellany.sample.common.infra.email;

import com.devellany.sample.common.domain.EmailMessage;

public interface EmailService {
    void sendEmail(EmailMessage emailMessage);
}
