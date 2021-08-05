package kr.or.btf.web.domain.web;

import kr.or.btf.web.domain.web.enums.AdviceReservationDvType;
import kr.or.btf.web.domain.web.enums.HelpTargetDvType;
import kr.or.btf.web.domain.web.enums.ProcessType;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_advice_reservation")
@DynamicUpdate
public class AdviceReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "advc_rsv_pid")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name="dv_ty")
    private AdviceReservationDvType dvTy;

    @Enumerated(EnumType.STRING)
    @Column(name="mber_dv_ty")
    private HelpTargetDvType mberDvTy;

    @Column(name="nm")
    private String nm;

    @Column(name="area_code_pid")
    private Long areaCodePid;

    @Column(name="ttl")
    private String ttl;

    @Column(name="cnts")
    private String cnts;

    @Column(name="telno")
    private String telno;

    @Column(name="hope_st_ymd")
    private Date hopeStYmd;

    @Column(name="hope_end_ymd")
    private Date hopeEndYmd;

    @Enumerated(EnumType.STRING)
    @Column(name="process_ty")
    private ProcessType processTy;

    @Column(name = "reg_ps_id")
    private String regPsId;

    @Column(name = "reg_dtm")
    private LocalDateTime regDtm;

    @Column(name = "upd_ps_id")
    private String updPsId;

    @Column(name = "upd_dtm")
    private LocalDateTime updDtm;

    @Column(name="del_at")
    private String  delAt;

    @Column(name="mber_pid")
    private Long mberPid;

    @Transient
    private String regPsNm;
    @Transient
    private String areaCodeNm;
    @Transient
    private String hopeTimeCodeNm;

}
