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
@Table(name = "tbl_prevention_master")
@DynamicUpdate
/**
 * ApplicationLecture 예방교육 신청 Entity
 * @author : jerry
 * @version : 1.0.0
 * 작성일 : 2021/09/14
**/
public class PreventionMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pre_mst_pid")
    private Long id;

    @Column(name="mber_pid")
    private Long mberPid;

    @Column(name="pre_pid")
    private Long prePid;

    @Column(name="schl_nm")
    private String schlNm;

    @Column(name = "grade")
    private Integer grade;

    @Column(name="address")
    private String address;

    @Column(name="tel")
    private String tel;

    @Column(name="classes_num")
    private Integer classesNum;

    @Column(name="personnel")
    private Integer personnel;

    @Column(name = "hp_schd1_personnel")
    private String hpSchd1Personnel;

    @Column(name = "hp_schd1_et")
    private String hpSchd1Et;

    @Column(name = "hp_schd1_wt")
    private String hpSchd1Wt;

    @Column(name = "hp_schd2_personnel")
    private String hpSchd2Personnel;

    @Column(name = "hp_schd2_et")
    private String hpSchd2Et;

    @Column(name = "hp_schd2_wt")
    private String hpSchd2Wt;

    @Column(name = "result_qna1")
    private String resultQna1;

    @Column(name = "result_qna2")
    private String resultQna2;

    @Column(name = "result_qna3")
    private String resultQna3;

    @Column(name = "result_qna4")
    private String resultQna4;

    @Column(name = "result_qna5")
    private String resultQna5;

    @Column(name = "reg_dtm")
    private LocalDateTime regDtm;

    @Column(name = "upd_dtm")
    private LocalDateTime updDtm;

    @Column(name = "del_at")
    private String delAt;

    @Column(name = "approval")
    private String approval;

    @Column(name = "temp_save")
    private String tempSave;



    @Column(name = "nm")
    private String nm;

    @Column(name = "email")
    private String email;

    @Column(name = "direct_num")
    private String directNum;

    @Column(name = "task")
    private String task;

    @Column(name = "moblphon")
    private String moblphon;


    /*@Transient
    private String nm;
    @Transient
    private String moblphon;
    @Transient
    private String email;*/
    @Transient
    private Long preCnt;

}
