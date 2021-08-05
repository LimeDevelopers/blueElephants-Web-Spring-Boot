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
@Table(name = "tbl_course")
@DynamicUpdate
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "crs_pid")
    private Long id;


    @Column(name="step_ty")
    private String stepTy;

    @Column(name="mber_dv_ty")
    private String mberDvTy;

    @Column(name="crs_nm")
    private String crsNm;

    @Column(name="crs_cn")
    private String crsCn;

    @Column(name="img_fl")
    private String imgFl;

    @Transient
    private Long courseItemCnt;
    @Transient
    private Long courseTargetCnt;
    @Transient
    private String regPsNm;
    @Transient
    private Long atnlcReqPid;
    @Transient
    private Long crsMstPid;
    @Transient
    private String mberDvTyNm;
    @Transient
    private String stepTyNm;

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

}
