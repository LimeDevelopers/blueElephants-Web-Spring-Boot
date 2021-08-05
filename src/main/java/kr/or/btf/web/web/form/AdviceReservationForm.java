package kr.or.btf.web.web.form;

import kr.or.btf.web.domain.web.enums.AdviceReservationDvType;
import kr.or.btf.web.domain.web.enums.HelpTargetDvType;
import kr.or.btf.web.domain.web.enums.ProcessType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;

@Getter
@Setter
public class AdviceReservationForm extends SearchForm {


    private Long id;
    @Enumerated(EnumType.STRING)
    private AdviceReservationDvType dvTy;
    @Enumerated(EnumType.STRING)
    private HelpTargetDvType mberDvTy;
    private String nm;
    private Long areaCodePid;
    private String ttl;
    private String cnts;
    private String telno;
    private String hopeStYmd;
    private String hopeEndYmd;
    private Long hopeTimeCodeId;
    @Enumerated(EnumType.STRING)
    private ProcessType processTy;
    private String regPsId;
    private LocalDateTime regDtm;
    private String updPsId;
    private LocalDateTime updDtm;
    private String  delAt;
    private Long mberPid;
}
