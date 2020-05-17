package com.devellany.sample.common.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailMessage {
    private String from;

    private String to;

    private String subject;

    private String message;

}
