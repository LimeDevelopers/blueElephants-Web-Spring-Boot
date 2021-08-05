package kr.or.btf.web.web.form;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SurveyAnswerItemForm extends SearchForm {

    private Long id;
    private String answerCnts;
    private Long qesitmPid;

}
