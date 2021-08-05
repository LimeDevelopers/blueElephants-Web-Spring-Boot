package kr.or.btf.web.web.form;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InspectionQuestionItemForm extends SearchForm {

    private Long id;
    private Long dvCodePid;
    private String aswDvTy;
    private String qestnQesitm;
    private Integer answerCnt;
    private Integer rspnsCnt;
    private String lwprtQesitmAt;
    private Long inspctPid;
    private Long upperQesitmPid;
}
