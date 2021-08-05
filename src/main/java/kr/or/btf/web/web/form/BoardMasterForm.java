package kr.or.btf.web.web.form;

import kr.or.btf.web.domain.web.enums.BoardType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Getter
@Setter
public class BoardMasterForm extends SearchForm{

    private Long id;
    private String bbsNm;

    @Enumerated(EnumType.STRING)
    private BoardType bbsTy;

    private String sntncHead;

    private String bbsUpendCn;
    private String pwd;

}
