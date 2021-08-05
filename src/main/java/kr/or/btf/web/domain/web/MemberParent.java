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
@Table(name = "tbl_member_parent")
@DynamicUpdate
public class MemberParent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stdnprnt_pid")
    private Long id;


    @Column(name="stdnprnt_id")
    private String stdnprntId;

    @Column(name="stdnt_id")
    private String stdntId;

    @Column(name = "reg_dtm")
    private LocalDateTime regDtm;

    @Transient
    private Long mberPid;

    /*@Transient
    private String mberNm;
    @Transient
    private String mberSexPrTy;
    @Transient
    private String mberLoginId;
    @Transient
    private LocalDateTime mberRegDtm;
    @Transient
    private LocalDateTime mberSecsnDtm;
    @Transient
    private String mberMoblphon;
    @Transient
    private String mberEmail;*/

    @Transient
    private String childNm;
    @Transient
    private String childSexPrTy;
    @Transient
    private String childLoginId;
    @Transient
    private LocalDateTime childRegDtm;
    @Transient
    private LocalDateTime childSecsnDtm;
    @Transient
    private String childMoblphon;
    @Transient
    private String childEmail;

    @Transient
    private String childSchlNm;
    @Transient
    private Integer childGrade;
    @Transient
    private String childBan;
    @Transient
    private Integer childNo;

    @Transient
    private String teacherNm;
    @Transient
    private String teacherSexPrTy;
    @Transient
    private String teacherLoginId;
    @Transient
    private LocalDateTime teacherRegDtm;
    @Transient
    private LocalDateTime teacherSecsnDtm;
    @Transient
    private String teacherMoblphon;
    @Transient
    private String teacherEmail;

}
