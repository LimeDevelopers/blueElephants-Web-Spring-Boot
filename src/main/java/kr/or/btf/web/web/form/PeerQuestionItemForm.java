package kr.or.btf.web.web.form;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PeerQuestionItemForm extends SearchForm {

    private Long id;
    private String aswDvTy;
    private String qestnQesitm;
    private Integer answerCnt;
    private Integer rspnsCnt;
    private String regPsId;
    private LocalDateTime regDtm;
    private String updPsId;
    private LocalDateTime updDtm;
    private String delAt;
    private Long peerPid;

}
