package kr.or.btf.web.web.form;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PeerResponsePersonForm extends SearchForm {

    private Long id;
    private String areaNm;
    private String schlNm;
    private Integer grade;
    private String ban;
    private Integer no;
    private String teacherNm;
    private String regPsId;
    private LocalDateTime regDtm;
    private String updPsId;
    private LocalDateTime updDtm;
    private String delAt;
    private Long peerPid;
    private Long mberPid;
}
