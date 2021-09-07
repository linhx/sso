package com.linhx.sso.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * LogoutScheduler
 *
 * @author linhx
 * @since 06/09/2021
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Document
public class LogoutByLoginHistoryScheduler {
    @Transient
    public static final String SEQ_NAME = LogoutByLoginHistoryScheduler.class.getName();

    @Id
    private Long id;
    private Long lhId;
    private Date startAt;
}
