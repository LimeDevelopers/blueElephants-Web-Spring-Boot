package kr.or.btf.web.domain.web;

import kr.or.btf.web.domain.web.enums.UserRollType;
import kr.or.btf.web.domain.web.enums.UserStatusType;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_member")
@DynamicUpdate
public class Account implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mber_pid")
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
    private Integer zip;
    private String adres;

    @Column(name = "dtl_adres")
    private String dtlAdres;
    private String ncnm;
    private String brthdy;


    @Column(name = "secsn_dtm")
    private LocalDateTime secsnDtm;

    @Column(name = "secsn_rsn")
    private String secsnRsn;

    @Enumerated(EnumType.STRING)
    @Column(name = "stt_ty")
    private UserStatusType sttTy;

    @Column(name = "reg_ps_id")
    private String regPsId;
    @Column(name = "reg_dtm")
    private LocalDateTime regDtm;
    @Column(name = "upd_ps_id")
    private String updPsId;
    @Column(name = "upd_dtm")
    private LocalDateTime updDtm;
    @Column(name = "del_at")
    private String delAt;
    @Column(name = "email_attc_dtm")
    private LocalDateTime emailAttcDtm;
    @Column(name = "email_attc_at")
    private String emailAttcAt;

    @Transient
    private String areaNm;
    @Transient
    private String schlNm;
    @Transient
    private Integer grade;
    @Transient
    private String ban;
    @Transient
    private Integer no;
    @Transient
    private LocalDateTime cnctDtm;

    @Column(name = "prtctor_nm")
    private String prtctorNm;
    @Column(name = "prtctor_brthdy")
    private String prtctorBrthdy;
    @Column(name = "prtctor_email")
    private String prtctorEmail;
    @Column(name = "prtctor_attc_at")
    private String prtctorAttcAt;
    @Column(name = "prtctor_attc_dtm")
    private LocalDateTime prtctorAttcDtm;

    @Transient
    private List<MemberRoll> authorites;
    @Transient
    private Long cVal;
    @Transient
    private Integer mberNo;

    @Builder
    public Account(Long id, String loginId, String nm, String pwd, String ncnm, LocalDateTime regDtm, LocalDateTime updDtm, UserRollType mberDvTy, List<MemberRoll> authorites, String moblphon) {

        this.id = id;
        this.loginId = loginId;
        this.nm = nm;
        this.pwd = pwd;
        this.ncnm = ncnm;
        this.regDtm = regDtm;
        this.updDtm = updDtm;
        this.mberDvTy = mberDvTy;
        this.authorites = authorites;
        this.moblphon = moblphon;
    }

    public void encodingPwd(PasswordEncoder pwdEncoder) {
        //pwdEncoder = new MessageDigestPasswordEncoder("SHA-256");
        //pwdEncoder = new StandardPasswordEncoder();
        this.pwd = pwdEncoder.encode(this.pwd);
    }

    /*@OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<INIPay> iniPay = new ArrayList<>();*/
}
