package kr.or.btf.web.domain.web;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * MemberGroup [tbl_member_group table entity]
 * @author : jerry
 * @version : 1.0.0
 * 작성일 : 2021/08/20
 **/
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_member_group")
@DynamicUpdate
public class MemberGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_pid")
    private Long id;

    @Column(name="group_nm")
    private String groupNm;

    @Column(name="charger_nm")
    private String chrNm;

    @Column(name="b_num")
    private String bNum;

    @Column(name="b_license_attc")
    private String bLicenseAttc;

    @Column(name = "attc_yn")
    private String attcYn;

    @Column(name="attc_dtm")
    private Long attcDtm;

    @Column(name="reg_dtm")
    private LocalDateTime regDtm;

    @Column(name="mber_pid")
    private Long mberPid;

    @Column(name="fl_pid")
    private Long flPid;

    @Transient
    private String mberNm;
    @Transient
    private String mberLoginId;
    @Transient
    private LocalDateTime mberRegDtm;
    @Transient
    private String mberMoblphon;
    @Transient
    private String mberEmail;
}
