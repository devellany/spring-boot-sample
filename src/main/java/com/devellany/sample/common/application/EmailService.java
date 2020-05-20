package com.devellany.sample.common.application;

import com.devellany.sample.common.domain.EmailMessage;

public interface EmailService {
    void sendEmail(EmailMessage emailMessage);
}
