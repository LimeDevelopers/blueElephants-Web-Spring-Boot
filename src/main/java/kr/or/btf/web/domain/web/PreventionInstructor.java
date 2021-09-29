package kr.or.btf.web.domain.web;


import kr.or.btf.web.domain.web.enums.InstructorDvTy;
import kr.or.btf.web.domain.web.enums.UserRollType;
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
@Table(name = "tbl_prevention_instructor")
@DynamicUpdate
/**
 * ApplicationLecture 예방교육 신청 Entity
 * @author : jerry
 * @version : 1.0.0
 * 작성일 : 2021/09/14
 **/
public class PreventionInstructor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pre_ins_pid")
    private Long id;

    @Column(name="mber_pid")
    private Long mberPid;

    @Column(name="fl_pid")
    private Long flPid;

    @Column(name="thumb_img")
    private String thumbImg;

    @Column(name="enroll_period")
    private String enrollPeriod;

    @Column(name="schl_nm")
    private String schlNm;

    @Column(name="major")
    private String major;

    @Column(name="grd_status")
    private String grdStatus;

    @Column(name="exp_period")
    private String expPeriod;

    @Column(name = "exp_nm")
    private String expNm;

    @Column(name = "exp_position")
    private String expPosition;

    @Column(name = "exp_content")
    private String expContent;

    @Column(name="enroll_period2")
    private String enrollPeriod2;

    @Column(name="schl_nm2")
    private String schlNm2;

    @Column(name="major2")
    private String major2;

    @Column(name="grd_status2")
    private String grdStatus2;

    @Column(name="exp_period2")
    private String expPeriod2;

    @Column(name = "exp_nm2")
    private String expNm2;

    @Column(name = "exp_position2")
    private String expPosition2;

    @Column(name = "exp_content2")
    private String expContent2;

    @Column(name="enroll_period3")
    private String enrollPeriod3;

    @Column(name="schl_nm3")
    private String schlNm3;

    @Column(name="major3")
    private String major3;

    @Column(name="grd_status3")
    private String grdStatus3;

    @Column(name="exp_period3")
    private String expPeriod3;

    @Column(name = "exp_nm3")
    private String expNm3;

    @Column(name = "exp_position3")
    private String expPosition3;

    @Column(name = "exp_content3")
    private String expContent3;

    @Column(name = "qualifications")
    private String qualifications;

    @Column(name = "edu_matters")
    private String eduMatters;

    @Column(name = "awards")
    private String awards;

    @Column(name = "self_driving_at")
    private String selfDrivingAt;

    @Column(name = "sns_status")
    private String snsStatus;

    @Column(name = "sns_url")
    private String snsUrl;

    @Column(name = "temp_save")
    private String tempSave;

    @Column(name = "reg_dtm")
    private LocalDateTime regDtm;

    @Column(name = "upd_dtm")
    private LocalDateTime updDtm;

    @Column(name = "del_at")
    private String delAt;

    @Column(name = "approval")
    private String approval;

    @Enumerated(EnumType.STRING)
    @Column(name = "ins_type")
    private InstructorDvTy InsType;

    @Transient
    private String loginId;

    @Transient
    private String nm;

    @Transient
    private String sexPrTy;

    @Transient
    private UserRollType mberDvTy;

    @Transient
    private String moblphon;

    @Transient
    private String email;

    @Transient
    private String brthdy;
}
