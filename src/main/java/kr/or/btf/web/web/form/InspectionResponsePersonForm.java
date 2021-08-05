package kr.or.btf.web.web.form;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InspectionResponsePersonForm extends SearchForm {

    private Long id;
    private String loginId;
    private String nm;
    private String mberDvty;
    private String inspctDvTy;
    private Long inspctPid;
    private Long atnlcReqPid;
}
