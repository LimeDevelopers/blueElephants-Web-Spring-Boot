package kr.or.btf.web.web.form;

import kr.or.btf.web.domain.web.enums.ProcessType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Getter
@Setter
public class AdviceAnswerForm {

    private Long id;
    private String ttl;
    private String cn;
    private String memo;

    @Enumerated(EnumType.STRING)
    private ProcessType processType;

    private Long advcReqPid;
    private Long mberPid;
    private String regPsId;
    private String updPsId;
    private String regDtm;

}
