package kr.or.btf.web.web.form;

import kr.or.btf.web.domain.web.enums.ContentsDvType;
import kr.or.btf.web.domain.web.enums.EventType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
public class ContestForm extends SearchForm{


    private Long ContestPid;

    private String Ttl;

    private String Cn;

    private String StYmd;

    private String EdYmd;

    private String ImgFl;

    private String OrganDtl; //공모전 주최자

    private String Field;  //분야

    private ContentsDvType CntntsDvTy;

    private String CntntsUrl;

    private EventType FxSeTy;

    private Integer ReadCnt;

    private String RegPsId;

    private LocalDateTime RegDtm;

    private String UpdPsId;

    private LocalDateTime UpdDtm;

    private String DelAt;
}
