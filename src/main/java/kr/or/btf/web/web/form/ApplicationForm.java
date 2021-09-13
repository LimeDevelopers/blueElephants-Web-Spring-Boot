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

    private String reason;
    private String applireason;
    private String schedule;
    private String root;
    private String schlNm;
    private String representPhone;
    private String schlAdress;
    private Integer studentNum;
    private Integer accidentsNum;
    private String principalNm;
    private String principalPhone;
    private String principalEmail;
    private String size;
    private String year;
    private String month;
    private String day;

}
