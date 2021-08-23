package kr.or.btf.web.domain.web;

import com.sun.org.glassfish.gmbal.Description;
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
    @Description("단체명")
    private Long id;

    @Column(name="group_nm")
    @Description("단체 이름")
    private String groupNm;

    @Column(name="charger_nm")
    @Description("담당자")
    private String chrNm;

    @Column(name="b_num")
    @Description("사업자등록번호")
    private Integer bNum;

    @Column(name="b_license_attc")
    @Description("사업자등록증 파일 등록여부")
    private String bLicenseAttc;

    @Column(name = "attc_yn")
    @Description("승인 여부")
    private String attcYn;

    @Column(name="attc_dtm")
    @Description("승인 일시")
    private Long attcDtm;

    @Column(name="reg_dtm")
    @Description("등록일")
    private LocalDateTime regDtm;

    @Column(name="mber_pid")
    @Description("멤버 고유번호")
    private Long mberPid;

    @Column(name="fl_pid")
    @Description("파일 고유번호")
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
