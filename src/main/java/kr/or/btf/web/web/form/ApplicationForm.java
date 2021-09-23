package kr.or.btf.web.web.form;

import kr.or.btf.web.domain.web.enums.AppRollType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ApplicationForm extends SearchForm{
    private Long appPid;
    private Long mberPid;
    private Long flPid;
    private Long eventPid;

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
    private String brthday;

    private String reason;
    private String applireason;
    private String schedule;
    private String root;
    private String schlNm;
    private String representPhone;
    private String adress;
    private Integer studentNum;
    private Integer accidentsNum;
    private String principalNm;
    private String principalPhone;
    private String principalEmail;
    private String size;
    private String year;
    private String month;
    private String day;

    private String theme;
    private String title;
    private String detail;
    private String field;
    private String content;

    private String t_nm;
    private String t_moblphon;
    private String t_brthday;
    private String t_affi;
    private String t_nm1;
    private String t_moblphon1;
    private String t_brthday1;
    private String t_affi1;
    private String t_nm2;
    private String t_moblphon2;
    private String t_brthday2;
    private String t_affi2;
    private String t_nm3;
    private String t_moblphon3;
    private String t_brthday3;
    private String t_affi3;
    private String t_nm4;
    private String t_moblphon4;
    private String t_brthday4;
    private String t_affi4;
    private String t_nm5;
    private String t_moblphon5;
    private String t_brthday5;
    private String t_affi5;
}
