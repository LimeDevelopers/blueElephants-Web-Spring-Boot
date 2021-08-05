package kr.or.btf.web.web.form;

import kr.or.btf.web.domain.web.enums.HelpTargetDvType;
import kr.or.btf.web.domain.web.enums.ProcessType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;

@Getter
@Setter
public class AdviceRequestForm extends SearchForm {

    private Long id;
    private String ttl;
    private String cn;
    @Enumerated(EnumType.STRING)
    private HelpTargetDvType mberDvTy;
    private String incdntStYmd;
    private String incdntEndYmd;
    @Enumerated(EnumType.STRING)
    private ProcessType processTy;
    private Long bdyDmgeCodePid;
    private Long mindDmgeCodePid;
    private Long physiclDmgeCodePid;
    private String pwd;
    private String regPsId;
    private LocalDateTime regDtm;
    private String updPsId;
    private LocalDateTime updDtm;
    private String  delAt;
    private Long mberPid;
}
