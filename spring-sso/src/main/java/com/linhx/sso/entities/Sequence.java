package com.linhx.sso.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author nguyendinhlinhx
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document
public class Sequence {
    @Id
    String seqName;
    Long seq;
}
