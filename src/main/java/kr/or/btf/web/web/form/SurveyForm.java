package kr.or.btf.web.web.form;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SurveyForm extends SearchForm {

    private Long id;
    private String dvTy;
    private String ttl;
    private String stYmd;
    private String endYmd;
    private String qustnrCn;
    private String opnAt;
    private String mberDvTy;

}
