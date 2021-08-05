package kr.or.btf.web.web.form;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PeerAnswerItemForm extends SearchForm {

    private Long id;
    private String answerCnts;
    private Integer score;
    private String regPsId;
    private LocalDateTime regDtm;
    private String updPsId;
    private LocalDateTime updDtm;
    private String delAt;
    private Long qesitmPid;

}
