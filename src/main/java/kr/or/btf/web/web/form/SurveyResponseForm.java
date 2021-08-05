package kr.or.btf.web.web.form;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SurveyResponseForm extends SearchForm {

    private Long id;
    private String answerCnts;
    private Long rspPsPid;
    private Long aswPid;
    private Long qesitmPid;
    private Long qustnrPid;
}
