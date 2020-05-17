package com.devellany.sample.account.application;

import com.devellany.sample.account.domain.Account;
import com.devellany.sample.account.domain.AccountConfirm;
import com.devellany.sample.account.domain.enums.AuthType;
import com.devellany.sample.account.infra.AccountConfirmRepository;
import com.devellany.sample.account.infra.AccountRepository;
import com.devellany.sample.account.infra.exception.AlreadyConfirmTokenException;
import com.devellany.sample.account.infra.exception.NoMatchingTokenException;
import com.devellany.sample.account.infra.exception.UnknownEmailException;
import com.devellany.sample.account.ui.form.ChangeEmailForm;
import com.devellany.sample.account.ui.form.SignUpForm;
import com.devellany.sample.account.ui.params.EmailConfirmParams;
import com.devellany.sample.common.domain.EmailMessage;
import com.devellany.sample.common.infra.config.AppProperties;
import com.devellany.sample.common.infra.email.EmailService;
import com.devellany.sample.common.infra.handler.CustomException;
import com.devellany.sample.config.MockMvcTest;
import com.devellany.sample.config.TestAccountHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

@MockMvcTest
class AccountServiceTest {
    @Autowired AccountService accountService;
    @Autowired AccountRepository accountRepository;
    @Autowired AccountConfirmRepository accountConfirmRepository;

    @Autowired AppProperties appProperties;
    @Autowired TestAccountHelper testAccountHelper;

    @MockBean EmailService emailService;

    @BeforeEach
    void beforeEach() {
        testAccountHelper.createUser();
        reset(emailService);
    }

    @AfterEach
    void afterEach() {
        testAccountHelper.resetForCreateUser();
        reset(emailService);
    }

    @Test @DisplayName("신규 회원 가입")
    void create_new_account() {
        testAccountHelper.resetForCreateUser();
        reset(emailService);

        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setNickname("test");
        signUpForm.setAccountName("test");
        signUpForm.setEmail("test@email.com");
        signUpForm.setPassword("12345678");

        accountService.processNewAccount(signUpForm);

        Account account = accountRepository.findByEmail(signUpForm.getEmail());
        AccountConfirm accountConfirm = accountConfirmRepository.findTopByAuthTypeEqualsAndAuthKeyOrderByRegDtmDesc(
                AuthType.EMAIL, account.getEmail()
        ).orElse(AccountConfirm.EMPTY);

        assertNotNull(account);
        assertNotEquals(account.getPassword(), "12345678");
        assertNotNull(accountConfirm.getToken());
        verify(emailService).sendEmail(any(EmailMessage.class));
    }

    @Test @DisplayName("이메일 인증 성공")
    void email_confirm_success() throws CustomException {
        AccountConfirm accountConfirm = accountConfirmRepository.findTopByAuthTypeEqualsAndAuthKeyOrderByRegDtmDesc(
                AuthType.EMAIL, TestAccountHelper.EMAIL
        ).orElse(AccountConfirm.EMPTY);

        EmailConfirmParams emailConfirmParams = EmailConfirmParams.builder()
                .email(accountConfirm.getAuthKey())
                .token(accountConfirm.getToken())
                .build();

        Account account = accountService.processEmailConfirm(emailConfirmParams);
        assertTrue(accountConfirm.isVerifiedStatus());
        assertNotNull(account);
    }

    @Test @DisplayName("이메일 인증 실패 - 인증된 메일")
    void email_confirm_fail_already() {
        AccountConfirm accountConfirm = accountConfirmRepository.findTopByAuthTypeEqualsAndAuthKeyOrderByRegDtmDesc(
                AuthType.EMAIL, TestAccountHelper.EMAIL
        ).orElse(AccountConfirm.EMPTY);

        EmailConfirmParams emailConfirmParams = EmailConfirmParams.builder()
                .email(accountConfirm.getAuthKey())
                .token(accountConfirm.getToken())
                .build();

        accountConfirm.completeEmailToken(emailConfirmParams);
        assertThrows(AlreadyConfirmTokenException.class, () -> accountService.processEmailConfirm(emailConfirmParams));
        assertTrue(accountConfirm.isVerifiedStatus());
    }

    @Test @DisplayName("이메일 인증 실패 - 토큰 오류")
    void email_confirm_fail_token() {
        AccountConfirm accountConfirm = accountConfirmRepository.findTopByAuthTypeEqualsAndAuthKeyOrderByRegDtmDesc(
                AuthType.EMAIL, TestAccountHelper.EMAIL
        ).orElse(AccountConfirm.EMPTY);

        EmailConfirmParams emailConfirmParams = EmailConfirmParams.builder()
                .email(accountConfirm.getAuthKey())
                .token("UNKNOWN_TOKEN")
                .build();

        assertThrows(NoMatchingTokenException.class, () -> accountService.processEmailConfirm(emailConfirmParams));
        assertFalse(accountConfirm.isVerifiedStatus());
    }

    @Test @DisplayName("이메일 인증 실패 - 이메일 확인 불가")
    void email_confirm_mismatch_email() {
        EmailConfirmParams emailConfirmParams = EmailConfirmParams.builder()
                .email("test@email.com")
                .token("UNKNOWN_TOKEN")
                .build();

        assertThrows(UnknownEmailException.class, () -> accountService.processEmailConfirm(emailConfirmParams));
    }

    @Test @DisplayName("이메일 재발송 - 성공")
    void resend_email_success() {
        AccountConfirm beforeConfirm = accountConfirmRepository.findTopByAuthTypeEqualsAndAuthKeyOrderByRegDtmDesc(
                AuthType.EMAIL, TestAccountHelper.EMAIL
        ).orElse(AccountConfirm.EMPTY);

        EmailConfirmParams emailConfirmParams = EmailConfirmParams.builder()
                .email(beforeConfirm.getAuthKey())
                .build();

        accountService.resendEmailForConfirm(emailConfirmParams);

        AccountConfirm afterConfirm = accountConfirmRepository.findTopByAuthTypeEqualsAndAuthKeyOrderByRegDtmDesc(
                AuthType.EMAIL, TestAccountHelper.EMAIL
        ).orElse(AccountConfirm.EMPTY);

        assertFalse(afterConfirm.isVerifiedStatus());
        assertNotEquals(beforeConfirm.getToken(), afterConfirm.getToken());
    }

    @Test @DisplayName("이메일 재발송 - 실패")
    void resend_email_fail() {
        EmailConfirmParams emailConfirmParams = EmailConfirmParams.builder()
                .email("test@email.com")
                .build();

        assertThrows(UnknownEmailException.class, () -> accountService.resendEmailForConfirm(emailConfirmParams));
    }

    @Test @DisplayName("이메일 변경")
    void change_email_account() {
        ChangeEmailForm changeEmailForm = new ChangeEmailForm();

        changeEmailForm.setAccountName(TestAccountHelper.USERNAME);
        changeEmailForm.setChangeEmail("test@email.com");

        accountService.processChangeEmail(changeEmailForm);

        AccountConfirm beforeConfirm = accountConfirmRepository.findTopByAuthTypeEqualsAndAuthKeyOrderByRegDtmDesc(
                AuthType.EMAIL, TestAccountHelper.EMAIL
        ).orElse(AccountConfirm.EMPTY);

        AccountConfirm afterConfirm = accountConfirmRepository.findTopByAuthTypeEqualsAndAuthKeyOrderByRegDtmDesc(
                AuthType.EMAIL, "test@email.com"
        ).orElse(AccountConfirm.EMPTY);

        assertNotNull(beforeConfirm);
        assertNotNull(afterConfirm);
        assertEquals(beforeConfirm.getAccountName(), afterConfirm.getAccountName());
        assertNotEquals(beforeConfirm.getAuthKey(), afterConfirm.getAuthKey());
        assertNotEquals(beforeConfirm.getToken(), afterConfirm.getToken());
        verify(emailService).sendEmail(any(EmailMessage.class));
    }
}