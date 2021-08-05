package kr.or.btf.web.domain.web;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PeerResponsePersonKey implements Serializable {

    private Long id;
    private String areaNm;
    private String schlNm;
    private Integer grade;
    private String ban;
    private Integer no;
    private String teacherNm;

}
