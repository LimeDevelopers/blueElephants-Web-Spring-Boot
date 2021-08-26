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
@Table(name = "tbl_member_school")
@DynamicUpdate
public class MemberSchool {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schl_pid")
    private Long id;

    @Column(name = "area_nm")
    private String areaNm;

    @Column(name = "schl_nm")
    private String schlNm;

    @Column(name = "grade")
    private Integer grade;

    @Column(name = "ban")
    private String ban;

    @Column(name = "no")
    private Integer no;

    @Column(name = "teacher_nm")
    private String teacherNm;

    @Column(name = "reg_dtm")
    private LocalDateTime regDtm;

    @Column(name = "mber_pid")
    private Long mberPid;

    @Column(name = "tchr_pid")
    private Long thcrPid;

    @Transient
    private String mberNm;
    @Transient
    private String mberLoginId;
    @Transient
    private String studentNm;
    @Transient
    private String studentSexPrTy;
    @Transient
    private String studentLoginId;
    @Transient
    private LocalDateTime studentRegDtm;
    @Transient
    private LocalDateTime studentSecsnDtm;
    @Transient
    private String studentMoblphon;
    @Transient
    private String studentEmail;
    @Transient
    private String parentNm;
    @Transient
    private String parentSexPrTy;
    @Transient
    private String parentLoginId;
    @Transient
    private LocalDateTime parentRegDtm;
    @Transient
    private LocalDateTime parentSecsnDtm;
    @Transient
    private String parentMoblphon;
    @Transient
    private String parentEmail;


}
