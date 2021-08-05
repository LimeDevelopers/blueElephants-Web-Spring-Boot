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
@Table(name = "tbl_member_school_log")
@DynamicUpdate
public class MemberSchoolLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schul_hst_pid")
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

}
