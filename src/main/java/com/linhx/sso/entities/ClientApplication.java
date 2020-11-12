package com.linhx.sso.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * ClientApplication
 *
 * @author linhx
 * @since 07/11/2020
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Document
public class ClientApplication extends Base {
    @Transient
    public static final String SEQ_NAME = "ClientApplication";

    @Id
    private Long id;
    @Indexed(unique=true)
    private String uuid;
    @Indexed(unique=true)
    private String host;
    @Indexed(unique=true)
    private String clientId;
    @Indexed(unique=true)
    private String secret;
    @Indexed(unique=true)
    private String accessTokenSecret;
    @Indexed(unique=true)
    private String refreshTokenSecret;
    private String signInUrl;
}
