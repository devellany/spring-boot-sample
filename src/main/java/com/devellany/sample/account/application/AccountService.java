package com.devellany.sample.account.application;

import com.devellany.sample.account.domain.Account;
import com.devellany.sample.account.domain.AccountConfirm;
import com.devellany.sample.account.domain.enums.AuthType;
import com.devellany.sample.account.infra.AccountConfirmRepository;
import com.devellany.sample.account.infra.AccountRepository;
import com.devellany.sample.account.infra.exception.AlreadyConfirmTokenException;
import com.devellany.sample.account.infra.exception.ExpiredTokenException;
import com.devellany.sample.account.infra.exception.NoMatchingTokenException;
import com.devellany.sample.account.infra.exception.UnknownEmailException;
import com.devellany.sample.account.ui.form.ChangeEmailForm;
import com.devellany.sample.account.ui.form.SignUpForm;
import com.devellany.sample.account.ui.params.EmailConfirmParams;
import com.devellany.sample.common.application.EmailService;
import com.devellany.sample.common.domain.EmailMessage;
import com.devellany.sample.common.infra.config.AppProperties;
import com.devellany.sample.common.infra.handler.CustomException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;
    private final AccountConfirmRepository accountConfirmRepository;
    private final AppProperties appProperties;
    private final TemplateEngine templateEngine;
    private final ModelMapper modelMapper;

    @Transactional
    public void processNewAccount(SignUpForm signUpForm) {
        signUpForm.setPassword(passwordEncoder.encode(signUpForm.getPassword()));

        Account newAccount = accountRepository.save(
                modelMapper.map(signUpForm, Account.class)
        );

        this.sendSignUpConfirmEmail(newAccount);
    }

    @Transactional
    public Boolean processChangeEmail(ChangeEmailForm changeEmailForm) {
        Account account = accountRepository.findByAccountName(
                changeEmailForm.getAccountName()
        ).orElse(Account.EMPTY);

        if (account.isEmpty()) {
            return false;
        }

        account.changeEmail(changeEmailForm.getChangeEmail());
        this.sendSignUpConfirmEmail(account);

        return true;
    }

    @Transactional
    public Account processEmailConfirm(EmailConfirmParams emailConfirmParams) throws CustomException {
        Account account = accountRepository.findByEmail(emailConfirmParams.getEmail()).orElse(Account.EMPTY);
        AccountConfirm accountConfirm = accountConfirmRepository.findTopByAuthTypeEqualsAndAuthKeyOrderByRegDtmDesc(
                AuthType.EMAIL, emailConfirmParams.getEmail()
        ).orElse(AccountConfirm.EMPTY);

        if (account.isEmpty() || accountConfirm.isEmpty()) {
            throw new UnknownEmailException("No matching email.");
        }

        if (accountConfirm.isVerifiedStatus()) {
            throw new AlreadyConfirmTokenException("Already processed.");
        }

        if (!accountConfirm.availableToken(appProperties.getTokenAvailablePeriod())) {
            throw new ExpiredTokenException("expired token.");
        }

        if (!accountConfirm.completeEmailToken(emailConfirmParams)) {
            throw new NoMatchingTokenException("No matching token data.");
        }

        return account;
    }

    @Transactional
    public void resendEmailForConfirm(EmailConfirmParams emailConfirmParams) throws UnknownEmailException {
        Account account = accountRepository.findByEmail(emailConfirmParams.getEmail()).orElse(Account.EMPTY);
        if (account.isEmpty() || !accountRepository.existsByEmail(emailConfirmParams.getEmail())) {
            throw new UnknownEmailException("No matching email.");
        }

        this.sendSignUpConfirmEmail(account);
    }

    private void sendSignUpConfirmEmail(Account account) {
        AccountConfirm accountConfirm = AccountConfirm.generateEmailCheckToken(account, accountConfirmRepository);

        Context context = new Context();
        context.setVariable("link", appProperties.getHost() + "/account/email/confirm");
        context.setVariable("email", accountConfirm.getAuthKey());
        context.setVariable("token", accountConfirm.getToken());
        context.setVariable("nickname", account.getNickname());
        context.setVariable("app", appProperties);

        EmailMessage emailMessage = EmailMessage.builder()
                .from(appProperties.getHelpEmail())
                .to(account.getEmail())
                .subject("회원 가입 인증")
                .message(templateEngine.process("mail/email-confirm", context))
                .build();

        emailService.sendEmail(emailMessage);
    }
}
