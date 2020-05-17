package com.devellany.sample.account.domain;

import com.devellany.sample.account.domain.enums.AuthType;
import com.devellany.sample.account.domain.enums.VerifiedStatus;
import com.devellany.sample.account.ui.params.EmailConfirmParams;
import com.devellany.sample.common.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor @AllArgsConstructor @Builder
public class AccountConfirm extends BaseEntity {
    @Transient
    public static final AccountConfirm EMPTY = new AccountConfirm();

    @Id @GeneratedValue
    private Long id;

    @Column(nullable = false, length = 15)
    private String AccountName;

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

    public void generateEmailCheckToken(Account account) {
        this.authType = AuthType.EMAIL;
        this.authKey = account.getEmail();
        this.verifiedStatus = VerifiedStatus.WAITING;
        this.token = UUID.randomUUID().toString();
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
