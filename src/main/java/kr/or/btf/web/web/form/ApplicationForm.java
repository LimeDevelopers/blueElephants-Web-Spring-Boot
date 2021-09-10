package kr.or.btf.web.web.form;

import kr.or.btf.web.domain.web.enums.AppRollType;
import kr.or.btf.web.domain.web.enums.UserRollType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class ApplicationForm extends SearchForm{
    private Long appPid;
    private Long mberPid;
    private Long flPid;
    /*private String loginId;*/
    private String nm;
    private String affi;
    private String crewNm;
    private AppRollType appDvTy;
    private String moblphon;
    private String email;
    private LocalDateTime regDtm;
    private LocalDateTime updDtm;
    private String delAt;
    private String approval;
}
