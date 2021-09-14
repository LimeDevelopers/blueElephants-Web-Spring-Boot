package kr.or.btf.web.web.form;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
public class PreventionMasterForm extends SearchForm{
    private Long preMstPid;
    private Long mberPid;
    private Long prePid;
    private String schlNm;
    private String address;
    private String tel;
    private Integer classesNum;
    private Integer personnel;
    private String hpSchd1Personnel;
    private String hpSchd1Et;
    private String hpSchd1Wt;
    private String hpSchd2Personnel;
    private String hpSchd2Et;
    private String hpSchd2Wt;
    private String resultQna1;
    private String resultQna2;
    private String resultQna3;
    private String resultQna4;
    private String resultQna5;
    private LocalDateTime regDtm;
    private LocalDateTime updDtm;
    private String delAt;
    private String approval;
    private String tempSave;
}
