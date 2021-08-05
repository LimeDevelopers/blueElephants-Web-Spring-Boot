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
@Table(name = "tbl_member_teacher")
@DynamicUpdate
public class MemberTeacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tcher_pid")
    private Long id;


    @Column(name="area_nm")
    private String areaNm;

    @Column(name="schl_nm")
    private String schlNm;

    @Column(name="grade")
    private Integer grade;

    @Column(name="ban")
    private String ban;

    @Column(name = "reg_dtm")
    private LocalDateTime regDtm;

    @Column(name="mber_pid")
    private Long mberPid;

    @Transient
    private String mberNm;
    @Transient
    private String mberLoginId;
    @Transient
    private String mberSexPrTy;
    @Transient
    private LocalDateTime mberRegDtm;
    @Transient
    private LocalDateTime mberSecsnDtm;
    @Transient
    private String mberMoblphon;
    @Transient
    private String mberEmail;

}
