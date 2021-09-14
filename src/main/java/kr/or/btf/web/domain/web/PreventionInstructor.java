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
@Table(name = "tbl_prevention_instructor")
@DynamicUpdate
/**
 * ApplicationLecture 예방교육 신청 Entity
 * @author : jerry
 * @version : 1.0.0
 * 작성일 : 2021/09/14
 **/
public class PreventionInstructor {
    /*
    * pre_ins_pid BIGINT auto_increment NOT NULL COMMENT '예방교사 신청 pid' PRIMARY KEY,
	mber_pid BIGINT DEFAULT NULL COMMENT '멤버 pid',
	fl_pid BIGINT DEFAULT NULL COMMENT '파일 pid',
	enroll_period varchar(100) DEFAULT NULL COMMENT '재학기간',
	schl_nm varchar(50) DEFAULT NULL COMMENT '학교이름',
	major varchar(100) DEFAULT NULL COMMENT '전공학과',
	grd_status varchar(50) DEFAULT NULL COMMENT '졸업여부',
	exp_period varchar(100) DEFAULT NULL COMMENT '경력기간',
	exp_nm varchar(50) DEFAULT NULL COMMENT '기관명',
	exp_position varchar(50) DEFAULT NULL COMMENT '직급',
	exp_content longtext DEFAULT NULL COMMENT '경력활동 설명',
	qualifications longtext DEFAULT NULL COMMENT '자격사항',
	edu_matters longtext DEFAULT NULL COMMENT '교육사항',
	awards longtext DEFAULT NULL COMMENT '수상경력',
	self_driving_at CHAR(1) DEFAULT NULL COMMENT '자가 운전여부',
	sns_status varchar(300) DEFAULT NULL COMMENT 'sns 여부',
	sns_url varchar(2000) DEFAULT NULL COMMENT 'sns 주소',
	temp_save CHAR(1) DEFAULT NULL COMMENT '임시저장 여부',
	reg_dtm DATETIME DEFAULT NULL COMMENT '생성 일시',
	upt_dtm DATETIME DEFAULT NULL COMMENT '업데이트 일시',
	del_at CHAR(1) DEFAULT NULL COMMENT '삭제여부',
	approval CHAR(1) DEFAULT NULL COMMENT '승인여부'*/
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pre_ins_pid")
    private Long id;

    @Column(name="mber_pid")
    private Long mberPid;

    @Column(name="fl_pid")
    private Long flPid;

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
}
