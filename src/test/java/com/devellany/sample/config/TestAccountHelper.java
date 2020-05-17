package com.devellany.sample.config;

import com.devellany.sample.account.application.AccountService;
import com.devellany.sample.account.domain.Account;
import com.devellany.sample.account.domain.security.UserAccount;
import com.devellany.sample.account.infra.AccountRepository;
import com.devellany.sample.account.ui.form.SignUpForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TestAccountHelper {
    public static final String USERNAME = "devellany";
    public static final String EMAIL = "devellany@dico.me";
    public static final String PASSWORD = "12345678";

    @Autowired  private AccountService accountService;
    @Autowired  AccountRepository accountRepository;

    public void createUser() {
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setNickname("devellany");
        signUpForm.setAccountName(USERNAME);
        signUpForm.setEmail(EMAIL);
        signUpForm.setPassword(PASSWORD);
        accountService.processNewAccount(signUpForm);
    }

    public void signInUser() {
        Account account = accountRepository.findByAccountName(USERNAME);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new UserAccount(account),
                account.getPassword(),
                List.of(new SimpleGrantedAuthority(account.getRole().getTableValue())));

        SecurityContextHolder.getContext().setAuthentication(token);
    }

    public void resetForCreateUser() {
        accountRepository.deleteAll();
    }
}
