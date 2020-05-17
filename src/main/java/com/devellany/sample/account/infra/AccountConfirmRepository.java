package com.devellany.sample.account.infra;

import com.devellany.sample.account.domain.AccountConfirm;
import com.devellany.sample.account.domain.enums.AuthType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Transactional(readOnly = true)
public interface AccountConfirmRepository extends JpaRepository<AccountConfirm, Long>, QuerydslPredicateExecutor<AccountConfirm> {

    Optional<AccountConfirm> findTopByAuthTypeEqualsAndAuthKeyOrderByRegDtmDesc(AuthType authType, String authKey);

}
