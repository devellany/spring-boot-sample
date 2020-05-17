package com.devellany.sample.account.domain;

import com.devellany.sample.account.domain.enums.AccountRole;
import com.devellany.sample.common.domain.BaseEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter @Setter // used spring security, please unused service code!
@NoArgsConstructor @AllArgsConstructor @Builder
public class Account extends BaseEntity {
    @Id @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false, length = 15, updatable = false)
    private String accountName;

    @Column(unique = true, nullable = false, length = 50)
    private String email;

    @Column(unique = true, nullable = false, length = 20)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private AccountRole role;

    @PrePersist
    private void prePersist() {
        this.role = this.role == null ? AccountRole.USER : this.role;
    }

    public void changeEmail(String email) {
        this.email = email;
    }


}
