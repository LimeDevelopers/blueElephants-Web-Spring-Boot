package kr.or.btf.web.domain.web;

import kr.or.btf.web.domain.web.enums.ContentsDvType;
import kr.or.btf.web.domain.web.enums.EventType;
import kr.or.btf.web.domain.web.enums.StatusType;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_event")
@DynamicUpdate
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_pid")
    private Long id;

    @Column(name="ttl")
    private String ttl;

    @Column(name="cn")
    private String cn;

    @Column(name="st_ymd")
    private String stYmd;

    @Column(name="ed_ymd")
    private String edYmd;

    @Column(name="spot_dtl")
    private String spotDtl;

    @Enumerated(EnumType.STRING)
    @Column(name="stt_ty")
    private StatusType statusType;

    @Column(name="img_fl")
    private String imgFl;

    @Enumerated(EnumType.STRING)
    @Column(name = "cntnts_dv_ty")
    private ContentsDvType cntntsDvTy;

    @Column(name="cntnts_url")
    private String cntntsUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "fx_se_ty")
    private EventType fxSeTy;

    @Column(name = "read_cnt")
    private Integer readCnt;

    @Column(name = "reg_ps_id")
    private String regPsId;
    @Column(name = "reg_dtm")
    private LocalDateTime regDtm;
    @Column(name = "upd_ps_id")
    private String updPsId;
    @Column(name = "upd_dtm")
    private LocalDateTime updDtm;
    @Column(name = "del_at")
    private String delAt;

    @Transient
    private String regPsNm;
    @Transient
    private String prevNext;
    @Transient
    private String fxSeTyName;
    @Transient
    private String fxSeTyClass;

}
