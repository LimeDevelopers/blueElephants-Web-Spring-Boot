package kr.or.btf.web.web.form;

import kr.or.btf.web.domain.web.enums.ContentsDvType;
import kr.or.btf.web.domain.web.enums.EventType;
import kr.or.btf.web.domain.web.enums.StatusType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Getter
@Setter
public class EventForm extends SearchForm{

    private Long id;
    private String ttl;
    private String cn;
    private String stYmd;
    private String edYmd;
    private String spotDtl;

    @Enumerated(EnumType.STRING)
    private StatusType statusType;

    @Enumerated(EnumType.STRING)
    private ContentsDvType cntntsDvTy;

    @Enumerated(EnumType.STRING)
    private EventType fxSeTy;

    private String imgFl;
    private String cntntsUrl;
    private Integer readCnt;
    private String prevNext;

}
