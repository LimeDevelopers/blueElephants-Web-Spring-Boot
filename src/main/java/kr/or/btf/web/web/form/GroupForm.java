package kr.or.btf.web.web.form;

import kr.or.btf.web.domain.web.enums.UserRollType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Transient;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

/**
 * CrewForm [단체 회원 폼]
 * @author : jerry
 * @version : 1.0.0
 * 작성일 : 2021/08/18
 **/
@Getter @Setter
public class GroupForm extends SearchForm{
    private Long id;
    @NotNull(message = "아이디는 필수 값입니다.")
    @NotBlank(message = "아이디는 필수 값입니다.")
    @Pattern(regexp="^[a-z0-9]{6,12}$", message = "아이디는 영문 소문자, 숫자를 포함해서 6~12자리 이내로 입력해주세요.")
    private String loginId;

    @NotNull(message = "비밀번호는 필수 값입니다.")
    @NotBlank(message = "비밀번호는 필수 값입니다.")
    @Pattern(regexp="^(?=.*[a-zA-Z])(?=.*[^a-zA-Z0-9])(?=.*[0-9]).{8,16}$", message = "비밀번호는 특수문자를 포함하여 8~16자리 이내로 입력해주세요.")
    private String pwd;

    @Email(message = "이메일형식이 올바르지 않습니다.")
    private String email;

    @Pattern(regexp = "^(01[016789]{1}|02|0[3-9]{1}[0-9]{1})([0-9]{3,4})([0-9]{4})$", message = "휴대전화번호 형식이 올바르지 않습니다.")
    private String moblphon;

    private String nm;
    private String sexPrTy;
    private String ncnm;
    private String brthdy;

    /* 크루 정보 */
    private String crewNm;         // 크루명
    private String crewAffi;       // 크루 소속
    private int crewNum;          // 크루 기수
    private String rptNm;          // 크루 대표명

    /* 단체 정보 */
    private String groupNm;        // 단체명
    private String chargerNm;      // 담당자 이름
    private String bNum;           // 사업자 등록번호
    private String b_license_attc;  // 사업자등록증 첨뷰여부
    @Enumerated(EnumType.STRING)
    private UserRollType mberDvTy;

    private LocalDateTime emailAttcDtm;
    private String emailAttcAt;
    private String mobileAttcAt;
    private LocalDateTime mobileAttcDtm;

    @Transient
    @Pattern(regexp="^(?=.*[a-zA-Z])(?=.*[^a-zA-Z0-9])(?=.*[0-9]).{8,16}$", message = "비밀번호는 특수문자를 포함하여 8~16자리 이내로 입력해주세요.")
    private String pwdChg;
    @Transient
    @Pattern(regexp="^(?=.*[a-zA-Z])(?=.*[^a-zA-Z0-9])(?=.*[0-9]).{8,16}$", message = "비밀번호는 특수문자를 포함하여 8~16자리 이내로 입력해주세요.")
    private String pwdChgChk;

    private String crewYn;
    private String groupYn;
    private String approval = "N";
    private int authEmailChk;
    private int authMobileChk;
    private String sRequestNumber;
    private String sResponseNumber;
}
