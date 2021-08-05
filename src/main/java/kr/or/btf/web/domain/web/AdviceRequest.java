package kr.or.btf.web.domain.web;

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
@Table(name = "tbl_advice_request")
@DynamicUpdate
public class AdviceRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "advc_req_pid")
    private Long id;

    @Column(name="ttl")
    private String ttl;

    @Column(name="cn")
    private String cn;

    @Enumerated(EnumType.STRING)
    @Column(name="mber_dv_ty")
    private HelpTargetDvType mberDvTy;

    @Column(name="incdnt_st_ymd")
    private Date incdntStYmd;

    @Column(name="incdnt_end_ymd")
    private Date incdntEndYmd;

    @Enumerated(EnumType.STRING)
    @Column(name="process_ty")
    private ProcessType processTy;

    @Column(name="bdy_dmge_code_pid")
    private Long bdyDmgeCodePid;

    @Column(name="mind_dmge_code_pid")
    private Long mindDmgeCodePid;

    @Column(name="physicl_dmge_code_pid")
    private Long physiclDmgeCodePid;

    @Column(name="pwd")
    private String pwd;

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

    @Column(name = "mber_pid")
    private Long mberPid;

    @Transient
    private String regPsNm;
    @Transient
    private String updPsNm;
    @Transient
    private LocalDateTime answerDtm;
    @Transient
    private String bdyDmgeCodeNm;
    @Transient
    private String mindDmgeCodeNm;
    @Transient
    private String physiclDmgeCodeNm;

}
