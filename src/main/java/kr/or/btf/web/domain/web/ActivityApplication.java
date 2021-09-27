package kr.or.btf.web.domain.web;

import kr.or.btf.web.domain.web.enums.AppRollType;
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
@Table(name = "tbl_application")
@DynamicUpdate
public class ActivityApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "app_pid")
    private Long id;

    @Column(name = "mber_pid")
    private Long mberPid;

    @Column(name = "fl_pid")
    private Long flPid;

    //행사신청 컬럼
    @Column(name = "event_pid")
    private Long event_pid;

    @Column(name = "contest_pid")
    private Long contest_pid;

   /* @Column(name = "login_id")
    private String loginId;*/

    @Column(name = "nm")
    private String nm;

    @Column(name = "affi")
    private String affi;

    @Column(name = "crew_nm")
    private String crewNm;

    @Enumerated(EnumType.STRING)
    @Column(name = "app_dv_ty")
    private AppRollType appDvTy;

    @Column(name = "moblphon")
    private String moblphon;

    @Column(name = "email")
    private String email;

    @Column(name = "reg_dtm")
    private LocalDateTime regDtm;

    /*@Column(name = "upd_dtm")
    private LocalDateTime updDtm;*/

    @Column(name = "del_at")
    private String delAt;

    @Column(name = "approval")
    private String approval;

    @Column(name = "brthday")
    private String brthday;

    //지지선언 컬럼
    @Column(name = "reason")
    private String reason;

    @Column(name = "schedule")
    private String schedule;

    @Column(name = "root")
    private String root;

    @Column(name = "schl_nm")
    private String schlNm;

    @Column(name = "represent_phone")
    private String representPhone;

    @Column(name = "adress")
    private String Adress;

    @Column(name = "student_num")
    private Integer studentNum;

    @Column(name = "accidents_num")
    private Integer accidentsNum;

    @Column(name = "principal_nm")
    private String principalNm;

    @Column(name = "principal_phone")
    private String principalPhone;

    @Column(name = "principal_email")
    private String principalEmail;

    @Column(name = "size")
    private String size;

    @Column(name = "theme")
    private String theme;

    @Column(name = "title")
    private String title;

    @Column(name = "detail")
    private String detail;

    @Column(name = "field")
    private String field;

    @Column(name = "field_dv")
    private String fieldDv;

    @Column(name = "content")
    private String content;
}
