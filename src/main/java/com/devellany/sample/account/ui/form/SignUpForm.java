package com.devellany.sample.account.ui.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class SignUpForm {
    @NotBlank(message = "필수 입력 값입니다.")
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣A-Za-z0-9_-]{2,20}$", message = "최소 2자에서 최대 20자까지 입력 가능합니다.")
    private String nickname;

    @NotBlank(message = "필수 입력 값입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    @NotBlank(message = "필수 입력 값입니다.")
    @Pattern(regexp = "^[A-Za-z0-9_-]{2,15}$", message = "영문과 숫자만 가능하며, 최소 2자에서 최대 15자까지 입력 가능합니다.")
    private String accountName;

    @NotBlank(message = "필수 입력 값입니다.")
    @Length(min = 8, max = 50, message = "최소 {min}자 이상 입력해야 합니다.")
    private String password;

    @NotBlank(message = "필수 입력 값입니다.")
    private String passwordConfirm;

    public boolean isConfirmPassword() {
        return password.equals(passwordConfirm);
    }
}
