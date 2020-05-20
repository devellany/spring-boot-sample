package com.devellany.sample.common.application;

import com.devellany.sample.account.domain.Account;
import com.devellany.sample.account.domain.security.UserAccount;
import com.devellany.sample.account.infra.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Component @RequiredArgsConstructor
public class AuthenticationHelper implements UserDetailsService {
    private final AccountRepository accountRepository;

    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private static UserAccount getPrincipal() {
        return (UserAccount) AuthenticationHelper.getAuthentication().getPrincipal();
    }

    public static String getAccountEmail() {
        return AuthenticationHelper.getPrincipal().getAccount().getEmail();
    }

    public static void signIn(Account account) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new UserAccount(account),
                account.getPassword(),
                List.of(new SimpleGrantedAuthority(account.getRole().getTableValue())));

        SecurityContextHolder.getContext().setAuthentication(token);
    }

    public static void SignOut(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = AuthenticationHelper.getAuthentication();
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(username).orElse(Account.EMPTY);
        if (account.isEmpty()) {
            account = accountRepository.findByAccountName(username).orElse(Account.EMPTY);
        }

        if (account.isEmpty()) {
            throw new UsernameNotFoundException(username);
        }

        return new UserAccount(account);
    }
}
