package com.devellany.sample.account.domain.security;

import com.devellany.sample.account.domain.Account;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

@Getter
public class UserAccount extends User {
    private final Account account;


    public UserAccount(Account account) {
        super(account.getAccountName(),
                account.getPassword(),
                List.of(new SimpleGrantedAuthority(account.getRole().getTableValue()))
        );

        this.account = account;
    }
}
