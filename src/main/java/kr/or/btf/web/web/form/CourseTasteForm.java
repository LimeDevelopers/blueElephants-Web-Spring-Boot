package kr.or.btf.web.web.form;

import kr.or.btf.web.domain.web.enums.ContentsDvType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Getter
@Setter
public class CourseTasteForm extends SearchForm{

    private Long id;
    private String ttl;
    private String cn;
    private String imgFl;
    @Enumerated(EnumType.STRING)
    private ContentsDvType cntntsDvTy;
    private String cntntsUrl;

}
