package com.devellany.sample.account.domain;

import com.devellany.sample.account.domain.enums.AuthType;
import com.devellany.sample.account.domain.enums.VerifiedStatus;
import com.devellany.sample.account.infra.AccountConfirmRepository;
import com.devellany.sample.account.ui.params.EmailConfirmParams;
import com.devellany.sample.common.domain.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class AccountConfirm extends BaseEntity {
    @Transient public static final AccountConfirm EMPTY = new AccountConfirm();

    @Id @GeneratedValue
    private Long id;

    @Column(nullable = false, length = 15)
    private String accountName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private AuthType authType;

    @Column(nullable = false)
    private String authKey;

    @Column(nullable = false, length = 50)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private VerifiedStatus verifiedStatus;

    public static AccountConfirm generateEmailCheckToken(Account account, AccountConfirmRepository accountConfirmRepository) {
        AccountConfirm accountConfirm = new AccountConfirm();

        accountConfirm.accountName = account.getAccountName();
        accountConfirm.authType = AuthType.EMAIL;
        accountConfirm.authKey = account.getEmail();
        accountConfirm.verifiedStatus = VerifiedStatus.WAITING;
        accountConfirm.token = UUID.randomUUID().toString();

        return accountConfirmRepository.save(accountConfirm);
    }

    public Boolean completeEmailToken(EmailConfirmParams params) {
        if (!this.isEmailType()) {
            return false;
        }

        if (!this.authKey.equals(params.getEmail())) {
            return false;
        }

        if (!this.token.equals(params.getToken())) {
            return false;
        }

        this.verifiedStatus = VerifiedStatus.CONFIRM;
        return true;
    }

    public Boolean isEmpty() {
        return this == AccountConfirm.EMPTY;
    }

    public Boolean isEmailType() {
        return this.authType.equals(AuthType.EMAIL);
    }

    public Boolean isVerifiedStatus() {
        return this.verifiedStatus.equals(VerifiedStatus.CONFIRM);
    }

    public Boolean availableToken(Integer min) {
        return this.regDtm.plusMinutes(min).isAfter(LocalDateTime.now());
    }
}