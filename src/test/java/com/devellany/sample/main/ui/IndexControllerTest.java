package com.devellany.sample.main.ui;

import com.devellany.sample.account.application.AccountService;
import com.devellany.sample.account.infra.AccountConfirmRepository;
import com.devellany.sample.account.infra.AccountRepository;
import com.devellany.sample.config.MockMvcTest;
import com.devellany.sample.config.TestAccountHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@MockMvcTest
class IndexControllerTest {
    @Autowired MockMvc mockMvc;
    @Autowired AccountService accountService;
    @Autowired AccountRepository accountRepository;
    @Autowired AccountConfirmRepository accountConfirmRepository;

    @Autowired TestAccountHelper testAccountHelper;

    @BeforeEach
    void beforeEach() {
        testAccountHelper.createUser();
    }

    @AfterEach
    void afterEach() {
        testAccountHelper.resetForCreateUser();
    }

    @Test @DisplayName("초기 화면 - 비로그인")
    void main_unauthenticated() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("/account/sign-in"))
                .andExpect(unauthenticated());
    }

    @Test @DisplayName("초기 화면 - 로그인")
    void main_authenticated() throws Exception {
        testAccountHelper.signInUser();

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("/main/index"))
                .andExpect(authenticated().withUsername(TestAccountHelper.USERNAME));
    }
}