package com.devellany.sample.common.domain;

import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {
    @CreatedDate
    @Column(nullable = false)
    protected LocalDateTime regDtm;

    @CreatedBy
    @Column(nullable = false, length = 15)
    protected String regrId;

    @LastModifiedDate
    @Column(nullable = false)
    protected LocalDateTime modDtm;

    @LastModifiedBy
    @Column(nullable = false, length = 15)
    protected String modrId;

}
