package kr.or.btf.web.web.form;

import kr.or.btf.web.domain.web.enums.SurveyDvType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Getter
@Setter
public class SurveyQuestionItemForm extends SearchForm {

    private Long id;
    private String aswDvTy;
    private String qestnQesitm;
    private Integer answerCnt;
    private Integer rspnsCnt;
    private Long qustnrPid;

    @Enumerated(EnumType.STRING)
    private SurveyDvType dvTy;

}
