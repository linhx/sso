package com.linhx.sso.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.io.Serializable;
import java.util.Date;

/**
 * Base
 *
 * @author linhx
 * @date 08/04/2020
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class Base implements Serializable {
    @CreatedDate
    Date createdAt;
    Long createBy;
    @LastModifiedDate
    Date updatedAt;
    Long updateBy;
}
