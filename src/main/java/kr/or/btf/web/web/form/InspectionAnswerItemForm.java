package kr.or.btf.web.web.form;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InspectionAnswerItemForm extends SearchForm {

    private Long id;
    private String answerCnts;
    private String apdAnswerAt;
    private Long qesitmPid;

    private Long inspctPid;
    private String inspctDvTy;
}
