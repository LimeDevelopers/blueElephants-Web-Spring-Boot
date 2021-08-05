package kr.or.btf.web.web.form;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseMasterForm extends SearchForm {

    private Long id;
    private String mberDvTy;
    private String crsNm;
    private String crsCn;
    private String imgFl;
    private String openAt;
}
