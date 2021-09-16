//package kr.or.btf.web.domain.web;
//
//import kr.or.btf.web.domain.web.enums.ContentsDvType;
//import kr.or.btf.web.domain.web.enums.EventType;
//import kr.or.btf.web.domain.web.enums.StatusType;
//import lombok.*;
//import org.hibernate.annotations.DynamicUpdate;
//
//import javax.persistence.*;
//import java.time.LocalDateTime;
//
//@Getter
//@Setter
//@EqualsAndHashCode(of = "id")
//@AllArgsConstructor
//@NoArgsConstructor
//@Entity
//@Table(name = "tbl_contest")
//@DynamicUpdate
//public class Contest {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "contest_pid")
//    private Long id;
//
//    @Column(name="ttl")
//    private String ttl;
//
//    @Column(name="cn")
//    private String cn;
//
//    @Column(name="st_ymd")
//    private String stYmd;
//
//    @Column(name="ed_ymd")
//    private String edYmd;
//
//    @Column(name="img_fl")
//    private String imgFl;
//
//    @Column(name = "organ_dtl")
//    private String organ_dtl; //공모전 주최자
//
//    @Column(name = "field")
//    private String field;  //분야
//
//    @Enumerated(EnumType.STRING)
//    @Column(name = "cntnts_dv_ty")
//    private ContentsDvType cntntsDvTy;
//
//    @Column(name="cntnts_url")
//    private String cntntsUrl;
//
//    @Enumerated(EnumType.STRING)
//    @Column(name = "fx_se_ty")
//    private EventType fxSeTy;
//
//    @Column(name = "read_cnt")
//    private Integer readCnt;
//
//    @Column(name = "reg_ps_id")
//    private String regPsId;
//
//    @Column(name = "reg_dtm")
//    private LocalDateTime regDtm;
//
//    @Column(name = "upd_ps_id")
//    private String updPsId;
//
//    @Column(name = "upd_dtm")
//    private LocalDateTime updDtm;
//
//    @Column(name = "del_at")
//    private String delAt;
//}
