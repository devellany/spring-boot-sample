package com.devellany.sample.account.ui.validator;

import com.devellany.sample.account.infra.AccountRepository;
import com.devellany.sample.account.ui.form.SignUpForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class SignUpFormValidator implements Validator {

    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(SignUpForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        SignUpForm signUpForm = (SignUpForm) target;

        if (!signUpForm.isConfirmPassword()) {
            errors.rejectValue("password", "invalid.password", "패스워드가 동일하지 않습니다.");
        }

        if (accountRepository.existsByEmail(signUpForm.getEmail())) {
            errors.rejectValue("email", "invalid.email", new Object[]{signUpForm.getEmail()}, "이미 사용 중인 이메일입니다.");
        }

        if (accountRepository.existsByAccountName(signUpForm.getAccountName())) {
            errors.rejectValue("accountName", "invalid.accountName", new Object[]{signUpForm.getAccountName()}, "이미 사용 중인 계정입니다.");
        }

        if (accountRepository.existsByNickname(signUpForm.getNickname())) {
            errors.rejectValue("nickname", "invalid.nickname", new Object[]{signUpForm.getNickname()}, "이미 사용 중인 닉네임입니다.");
        }
    }
}
