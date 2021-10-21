package kr.or.btf.web.web.form;

import kr.or.btf.web.domain.web.enums.UserRollType;
import kr.or.btf.web.domain.web.enums.UserStatusType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Transient;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

/**
 * MemberForm [register from vo]
 * @author : jerry
 * @version : 1.0.0
 * 작성일 : 2021/08/16
**/
@Getter
@Setter
public class MemberForm extends SearchForm {

    private Long id;

    @NotNull(message = "아이디는 필수 값입니다.")
    @NotBlank(message = "아이디는 필수 값입니다.")
    @Pattern(regexp="^[a-z0-9]{6,12}$", message = "아이디는 영문 소문자, 숫자를 포함해서 6~12자리 이내로 입력해주세요.")
    private String loginId;

    @NotNull(message = "비밀번호는 필수 값입니다.")
    @NotBlank(message = "비밀번호는 필수 값입니다.")
    @Pattern(regexp="^(?=.*[a-zA-Z])(?=.*[^a-zA-Z0-9])(?=.*[0-9]).{8,16}$", message = "비밀번호는 특수문자를 포함하여 8~16자리 이내로 입력해주세요.")
    private String pwd;

    private String tempLoginId;
    private String tempPwd;

    @NotNull(message = "이름은 필수 값입니다.")
    @NotBlank(message = "이름은 필수 값입니다.")
    private String nm;

    @NotNull(message = "성별은 필수 값입니다.")
    @NotBlank(message = "성별은 필수 값입니다.")
    private String sexPrTy;

    @Enumerated(EnumType.STRING)
    private UserRollType mberDvTy;

//    @NotNull(message = "휴대전화번호는 필수 값입니다.")
//    @NotBlank(message = "휴대전화번호는 필수 값입니다.")
    //@Pattern(regexp = "^(01[016789]{1}|02|0[3-9]{1}[0-9]{1})([0-9]{3,4})([0-9]{4})$", message = "휴대전화번호 형식이 올바르지 않습니다.")
    private String moblphon;
//
//    @NotNull(message = "이메일은 필수 값입니다.")
//    @NotBlank(message = "이메일은 필수 값입니다.")
    @Email(message = "이메일형식이 올바르지 않습니다.")
    private String email;

    private String zip;
    private String adres;
    private String dtlAdres;

    @NotNull(message = "닉네임은 필수 값입니다.")
    @NotBlank(message = "닉네임은 필수 값입니다.")
    private String ncnm;

    @NotNull(message = "생년월일은 필수 값입니다.")
    @NotBlank(message = "생년월일은 필수 값입니다.")
    private String brthdy;

    private String secsnDtm;
    private String secsnRsn;
    private LocalDateTime emailAttcDtm;
    private String emailAttcAt;
    private String mobileAttcAt;
    private LocalDateTime mobileAttcDtm;

    private String prtctorNm;
    private String prtctorBrthdy;
    private String prtctorEmail;

    private String prtctorAttcAt;
    private LocalDateTime prtctorAttcDtm;
    private Long crewPid;
    private Long crewFNum;
    private Long groupPid;
    private String groupYn;
    private String freeCard;
    private int BatchArr;

    @Enumerated(EnumType.STRING)
    private UserStatusType sttTy;

    @Transient
    @Pattern(regexp="^(?=.*[a-zA-Z])(?=.*[^a-zA-Z0-9])(?=.*[0-9]).{8,16}$", message = "비밀번호는 특수문자를 포함하여 8~16자리 이내로 입력해주세요.")
    private String pwdChg;
    @Transient
    @Pattern(regexp="^(?=.*[a-zA-Z])(?=.*[^a-zA-Z0-9])(?=.*[0-9]).{8,16}$", message = "비밀번호는 특수문자를 포함하여 8~16자리 이내로 입력해주세요.")
    private String pwdChgChk;
    @Transient
    private String stdnprntId;
    @Transient
    private String stdntId;
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
    private String teacherNm;
    @Transient
    private Integer mberPid;
    @Transient
    private String onlineEdu;
    @Transient
    private String eduReset;
    @Transient
    private String cardReset;
    private String values;

    private String approval = "N";
    private int authEmailChk;
    private int authMobileChk;
    private String sRequestNumber;
    private String sResponseNumber;
}
