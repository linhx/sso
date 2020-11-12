package com.linhx.sso.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
public class ClientApplication {
    private Long id;
    private String uuid;
    private String host;
    private String clientId;
    private String secret;
    private String accessTokenSecret;
    private String refreshTokenSecret;
    private String signInUrl;
}
