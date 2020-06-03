package com.devellany.sample.account.ui.validator;

import com.devellany.sample.account.domain.Account;
import com.devellany.sample.account.infra.AccountRepository;
import com.devellany.sample.account.ui.form.ChangeEmailForm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class ChangeEmailFormValidator implements Validator {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(ChangeEmailForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ChangeEmailForm changeEmailForm = (ChangeEmailForm)target;

        if (!this.ableAccount(changeEmailForm)) {
            errors.rejectValue("accountName", "invalid.accountName", "아이디 또는 비밀번호가 잘못 입력되었습니다.");
        }

        if (accountRepository.existsByEmail(changeEmailForm.getChangeEmail())) {
            errors.rejectValue("changeEmail", "invalid.changeEmail", "이미 사용 중인 이메일입니다.");
        }
    }

    private Boolean ableAccount(ChangeEmailForm changeEmailForm) {
        Account account = accountRepository.findByAccountName(
                changeEmailForm.getAccountName()
        ).orElse(Account.EMPTY);

        if (account.isEmpty()) {
            return false;
        }

        return passwordEncoder.matches(changeEmailForm.getPassword(), account.getPassword());
    }
}