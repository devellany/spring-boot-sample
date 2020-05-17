package com.devellany.sample.account.ui.form;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class ChangeEmailForm {
    private String accountName;

    private String password;

    @NotBlank(message = "필수 입력 값입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String changeEmail;
}
