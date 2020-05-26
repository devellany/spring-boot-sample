package com.devellany.sample.common.infra.handler;

import com.devellany.sample.account.domain.Account;
import com.devellany.sample.account.domain.AccountConfirm;
import com.devellany.sample.account.domain.enums.AuthType;
import com.devellany.sample.common.domain.security.UserAccount;
import com.devellany.sample.account.infra.AccountConfirmRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component @RequiredArgsConstructor
public class SignInSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private final RedirectStrategy redirectStrategy;
    private final AccountConfirmRepository accountConfirmRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        Object principal = authentication.getPrincipal();
        if (!isUserAccount(principal)) {
            throw new NoClassDefFoundError();
        }

        UserAccount userAccount = (UserAccount) principal;
        Account account = userAccount.getAccount();
        AccountConfirm accountConfirm = accountConfirmRepository.findTopByAuthTypeEqualsAndAuthKeyOrderByRegDtmDesc(
                AuthType.EMAIL, account.getEmail()
        ).orElse(AccountConfirm.EMPTY);

        if (!accountConfirm.isVerifiedStatus()) {
            this.clearAuthenticationAttributes(request);
            redirectStrategy.sendRedirect(request, response, "/account/email/confirm");
        } else {
            super.onAuthenticationSuccess(request, response, authentication);
        }
    }

    private Boolean isUserAccount(Object obj) {
        return obj instanceof UserAccount;
    }
}
