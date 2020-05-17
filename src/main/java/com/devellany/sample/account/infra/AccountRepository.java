package com.devellany.sample.account.infra;

import com.devellany.sample.account.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface AccountRepository extends JpaRepository<Account, Long>, QuerydslPredicateExecutor<Account> {
    boolean existsByEmail(String email);

    Account findByEmail(String email);

    boolean existsByNickname(String nickname);

    Account findByAccountName(String accountName);

    boolean existsByAccountName(String accountName);
}
