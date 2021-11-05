package kr.or.btf.web.web.form;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Getter
@Setter
public class PreventionApprovalForm extends SearchForm{
    private Long praPid;
    private Long preMstpid;
    private Long mberPid;
    private String hopeDtm;
    private String approval;
    private LocalDateTime approvalDtm;
}
