package kr.or.btf.web.domain.web;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_member_teacher_log")
@DynamicUpdate
public class MemberTeacherLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tcher_hst_pid")
    private Long id;


    @Column(name="area_nm")
    private String areaNm;

    @Column(name="schl_nm")
    private String schlNm;

    @Column(name="grade")
    private Integer grade;

    @Column(name="ban")
    private String ban;

    @Column(name="mber_pid")
    private Long mberPid;


}
