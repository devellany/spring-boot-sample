package com.devellany.sample.account.infra;

import com.devellany.sample.account.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
public interface AccountRepository extends JpaRepository<Account, Long>, QuerydslPredicateExecutor<Account> {
    Optional<Account> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<Account> findByAccountName(String accountName);

    boolean existsByAccountName(String accountName);

    boolean existsByNickname(String nickname);
}