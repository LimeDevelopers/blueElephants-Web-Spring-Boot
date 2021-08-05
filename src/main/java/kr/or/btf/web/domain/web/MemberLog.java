package kr.or.btf.web.domain.web;

import kr.or.btf.web.domain.web.enums.UserRollType;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_member_log")
@DynamicUpdate
public class MemberLog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mber_hst_pid")
    private Long id;

    @Column(name = "login_id")
    private String loginId;

    private String pwd;
    private String nm;

    @Column(name = "sex_pr_ty")
    private String sexPrTy;

    @Enumerated(EnumType.STRING)
    @Column(name = "mber_dv_ty")
    private UserRollType mberDvTy;

    @Column(name = "moblphon")
    private String moblphon;
    private String email;
    private String zip;
    private String adres;

    @Column(name = "dtl_adres")
    private String dtlAdres;
    private String ncnm;
    private String brthdy;

    @Column(name = "mber_pid")
    private Long mberPid;


    /*@Column(name = "secsn_dtm")
    private String secsnDtm;

    @Column(name = "secsn_rsn")
    private String secsnRsn;*/

    /*@Enumerated(EnumType.STRING)
    @Column(name = "stt_ty")
    private UserStatusType sttTy;*/

    @Column(name = "reg_ps_id")
    private String regPsId;
    @Column(name = "reg_dtm")
    private LocalDateTime regDtm;



/*    @Builder
    public MemberLog(Long id, String loginId, String nm, String pwd, LocalDateTime regDtm, UserRollType mberDvTy) {

        this.id = id;
        this.loginId = loginId;
        this.nm = nm;
        this.pwd = pwd;
        this.regDtm = regDtm;
        this.mberDvTy = mberDvTy;
    }

    public void encodingPwd(PasswordEncoder pwdEncoder) {
        //pwdEncoder = new MessageDigestPasswordEncoder("SHA-256");
        //pwdEncoder = new StandardPasswordEncoder();
        this.pwd = pwdEncoder.encode(this.pwd);
    }*/
}
