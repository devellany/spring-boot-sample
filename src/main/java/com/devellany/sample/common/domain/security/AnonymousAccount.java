package com.devellany.sample.common.domain.security;

import com.devellany.sample.account.domain.Account;
import lombok.Getter;

@Getter
public class AnonymousAccount {
    private static final AnonymousAccount anonymousAccount = new AnonymousAccount();
    private final Account account = Account.EMPTY;

    public static AnonymousAccount of () {
        return anonymousAccount;
    }

    private AnonymousAccount() {

    }
}
