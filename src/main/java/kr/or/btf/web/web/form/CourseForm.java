package kr.or.btf.web.web.form;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseForm extends SearchForm {

    private Long id;
    private String stepTy;
    private String mberDvTy;
    private String crsNm;
    private String crsCn;
    private String imgFl;
    private Long courseItemCnt;
    private Long courseTargetCnt;
    private String regPsNm;
    private Long atnlcReqPid;
    private Long crsMstPid;
}
