package com.devellany.sample.common.infra.handler;

import com.devellany.sample.account.application.AccountService;
import com.devellany.sample.account.domain.AccountConfirm;
import com.devellany.sample.account.domain.enums.AuthType;
import com.devellany.sample.account.infra.AccountConfirmRepository;
import com.devellany.sample.account.infra.AccountRepository;
import com.devellany.sample.account.ui.params.EmailConfirmParams;
import com.devellany.sample.common.application.AuthenticationHelper;
import com.devellany.sample.config.MockMvcTest;
import com.devellany.sample.config.TestAccountHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.RedirectStrategy;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@MockMvcTest
class SignInSuccessHandlerTest {
    @Autowired AccountService accountService;
    @Autowired AccountRepository accountRepository;
    @Autowired AccountConfirmRepository accountConfirmRepository;

    @Autowired TestAccountHelper testAccountHelper;

    @Mock RedirectStrategy redirectStrategy;

    @BeforeEach
    void beforeEach(){
        testAccountHelper.createUser();
        testAccountHelper.signInUser();
    }

    @AfterEach
    void afterEach() {
        testAccountHelper.resetForCreateUser();
        reset(redirectStrategy);
    }

    @Test @DisplayName("이메일 인증 처리")
    void email_confirmed() throws ServletException, IOException {
        SignInSuccessHandler signInSuccessHandler = new SignInSuccessHandler(redirectStrategy, accountConfirmRepository);
        signInSuccessHandler.onAuthenticationSuccess(mock(HttpServletRequest.class), mock(HttpServletResponse.class), AuthenticationHelper.getAuthentication());

        AccountConfirm accountConfirm = accountConfirmRepository.findTopByAuthTypeEqualsAndAuthKeyOrderByRegDtmDesc(
                AuthType.EMAIL, AuthenticationHelper.getAccountEmail()
        ).orElse(AccountConfirm.EMPTY);

        EmailConfirmParams emailConfirmParams = EmailConfirmParams.builder()
                .email(accountConfirm.getAuthKey())
                .token(accountConfirm.getToken())
                .build();

        assertNotNull(accountConfirm);
        assertTrue(accountConfirm.completeEmailToken(emailConfirmParams));
        assertTrue(accountConfirm.isVerifiedStatus());
    }

    @Test @DisplayName("이메일 미인증 처리")
    void email_unconfirmed() throws IOException, ServletException {
        SignInSuccessHandler signInSuccessHandler = new SignInSuccessHandler(redirectStrategy, accountConfirmRepository);
        signInSuccessHandler.onAuthenticationSuccess(mock(HttpServletRequest.class), mock(HttpServletResponse.class), AuthenticationHelper.getAuthentication());

        AccountConfirm accountConfirm = accountConfirmRepository.findTopByAuthTypeEqualsAndAuthKeyOrderByRegDtmDesc(
                AuthType.EMAIL, AuthenticationHelper.getAccountEmail()
        ).orElse(AccountConfirm.EMPTY);

        assertNotNull(accountConfirm);
        assertFalse(accountConfirm.isVerifiedStatus());
        verify(redirectStrategy).sendRedirect(any(HttpServletRequest.class), any(HttpServletResponse.class), eq("/account/email/confirm"));
    }
}