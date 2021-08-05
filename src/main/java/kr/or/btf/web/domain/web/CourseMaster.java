package kr.or.btf.web.domain.web;

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
@Table(name = "tbl_course_master")
@DynamicUpdate
public class CourseMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "crs_mst_pid")
    private Long id;

    @Column(name = "mber_dv_ty")
    private String mberDvTy;

    @Column(name = "crs_nm")
    private String crsNm;

    @Column(name = "crs_cn")
    private String crsCn;

    @Column(name = "img_fl")
    private String imgFl;

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
    @Column(name = "open_at")
    private String openAt;

    @Transient
    private String regPsNm;
    @Transient
    private Long atnlcReqPid;
    @Transient
    private Long fieldCnt;
    @Transient
    private Long requestCnt;
    @Transient
    private LocalDateTime requestRegDtm;
    @Transient
    private Long ingCnt;
    @Transient
    private Long crsCnt;
    @Transient
    private Long downloadCnt;
    @Transient
    private Long completeCnt;

}
