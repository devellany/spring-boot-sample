package com.devellany.sample.account.ui;

import com.devellany.sample.account.application.AccountService;
import com.devellany.sample.account.domain.Account;
import com.devellany.sample.account.domain.AccountConfirm;
import com.devellany.sample.account.domain.enums.AuthType;
import com.devellany.sample.account.infra.AccountConfirmRepository;
import com.devellany.sample.account.infra.AccountRepository;
import com.devellany.sample.common.application.EmailService;
import com.devellany.sample.common.domain.EmailMessage;
import com.devellany.sample.config.MockMvcTest;
import com.devellany.sample.config.TestAccountHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcTest
class AccountControllerTest {
    @Autowired MockMvc mockMvc;
    @Autowired AccountService accountService;
    @Autowired AccountRepository accountRepository;
    @Autowired AccountConfirmRepository accountConfirmRepository;

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

    @Test @DisplayName("로그인 화면")
    void login_page() throws Exception {
        mockMvc.perform(get("/account/sign-in"))
                .andExpect(status().isOk())
                .andExpect(view().name("/account/sign-in"))
                .andExpect(unauthenticated());
    }

    @Test @DisplayName("로그인 성공 - 메일")
    void login_success_by_email() throws Exception {
        this.signInTest(TestAccountHelper.EMAIL);
    }

    @Test @DisplayName("로그인 성공 - 계정")
    void login_success_by_id() throws Exception {
        this.signInTest(TestAccountHelper.USERNAME);
    }

    @Test @DisplayName("로그인 실패")
    void login_fail() throws Exception {
        mockMvc.perform(post("/account/sign-in")
                .param("username", "unknown")
                .param("password", "000000000")
                .with(csrf())
        ).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/account/sign-in?error"))
                .andExpect(unauthenticated());
    }

    @Test @DisplayName("로그아웃")
    void logout() throws Exception {
        testAccountHelper.signInUser();
        mockMvc.perform(get("/account/sign-out")
        ).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(unauthenticated());
    }

    @Test @DisplayName("회원 가입 화면")
    void sign_up_form() throws Exception {
        mockMvc.perform(get("/account/sign-up"))
                .andExpect(status().isOk())
                .andExpect(view().name("/account/sign-up"))
                .andExpect(model().attributeExists("signUpForm"))
                .andExpect(unauthenticated());
    }

    @Test @DisplayName("회원 가입 - 성공")
    void sign_up_submit_with_correct_input() throws Exception {
        testAccountHelper.resetForCreateUser();
        reset(emailService);

        mockMvc.perform(post("/account/sign-up")
                .param("nickname", "test")
                .param("accountName", "test")
                .param("email", "test@email.com")
                .param("password", "12345678")
                .param("passwordConfirm", "12345678")
                .with(csrf())
        ).andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/account/email/confirm"));

        Account account = accountRepository.findByEmail("test@email.com").orElse(Account.EMPTY);
        AccountConfirm accountConfirm = accountConfirmRepository.findTopByAuthTypeEqualsAndAuthKeyOrderByRegDtmDesc(
                AuthType.EMAIL, account.getEmail()
        ).orElse(AccountConfirm.EMPTY);

        assertNotNull(account);
        assertNotEquals(account.getPassword(), "12345678");
        assertNotNull(accountConfirm.getToken());
        verify(emailService).sendEmail(any(EmailMessage.class));
    }

    @Test @DisplayName("회원 가입 - 비밀번호 확인 불가")
    void sign_up_submit_fail_mismatched_password() throws Exception {
        mockMvc.perform(post("/account/sign-up")
                .param("nickname", "test")
                .param("accountName", "test")
                .param("email", "test@email.com")
                .param("password", "12345678")
                .param("passwordConfirm", "00000000")
                .with(csrf())
        ).andExpect(status().isOk())
                .andExpect(view().name("/account/sign-up"))
                .andExpect(unauthenticated());
    }

    @Test @DisplayName("회원 가입 - 이메일 중복")
    void sign_up_submit_fail_alreay_email() throws Exception {
        mockMvc.perform(post("/account/sign-up")
                .param("nickname", "test")
                .param("accountName", "test")
                .param("email", TestAccountHelper.EMAIL)
                .param("password", "12345678")
                .param("passwordConfirm", "00000000")
                .with(csrf())
        ).andExpect(status().isOk())
                .andExpect(view().name("/account/sign-up"))
                .andExpect(unauthenticated());
    }

    @Test @DisplayName("회원 가입 - 계정 중복")
    void sign_up_submit_fail_alreay_account() throws Exception {
        mockMvc.perform(post("/account/sign-up")
                .param("nickname", "test")
                .param("accountName", TestAccountHelper.USERNAME)
                .param("email", "test@email.com")
                .param("password", "12345678")
                .param("passwordConfirm", "00000000")
                .with(csrf())
        ).andExpect(status().isOk())
                .andExpect(view().name("/account/sign-up"))
                .andExpect(unauthenticated());
    }

    @Test @DisplayName("회원 가입 - 닉네임 중복")
    void sign_up_submit_fail_alreay_nickmane() throws Exception {
        mockMvc.perform(post("/account/sign-up")
                .param("nickname", TestAccountHelper.USERNAME)
                .param("accountName", "test")
                .param("email", "test@email.com")
                .param("password", "12345678")
                .param("passwordConfirm", "00000000")
                .with(csrf())
        ).andExpect(status().isOk())
                .andExpect(view().name("/account/sign-up"))
                .andExpect(unauthenticated());
    }

    private void signInTest(String id) throws Exception {
        mockMvc.perform(post("/account/sign-in")
                .param("username", id)
                .param("password", "12345678")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(authenticated().withUsername(TestAccountHelper.USERNAME));
    }
}