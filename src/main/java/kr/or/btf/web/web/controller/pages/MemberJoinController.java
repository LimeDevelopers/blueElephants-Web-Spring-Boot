package kr.or.btf.web.web.controller.pages;

import kr.or.btf.web.common.Constants;
import kr.or.btf.web.common.exceptions.ValidCustomException;
import kr.or.btf.web.domain.web.Account;
import kr.or.btf.web.domain.web.MemberCrew;
import kr.or.btf.web.domain.web.MobileAuthLog;
import kr.or.btf.web.services.web.MailService;
import kr.or.btf.web.services.web.MemberService;
import kr.or.btf.web.services.web.MobileAuthLogService;
import kr.or.btf.web.utils.AESEncryptor;
import kr.or.btf.web.web.controller.BaseCont;
import kr.or.btf.web.web.form.GroupForm;
import kr.or.btf.web.web.form.MemberForm;
import kr.or.btf.web.web.form.MobileAuthLogForm;
import kr.or.btf.web.web.validator.MemberFormValidator;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberJoinController extends BaseCont {

    private final MemberService memberService;
    private final MemberFormValidator memberFormValidator;
    private final MobileAuthLogService mobileAuthLogService;
    private final JavaMailSender javaMailSender;
    private final MailService mailService;

    @GetMapping(value = "/pages/member/joinPick")
    public String joinPickPage(Model model) {
        model.addAttribute("mc", "memberJoin");
        return "/pages/member/joinPick";
    }

    @GetMapping({"/pages/member/idFind"})
    public String idFind(Model model,
                         HttpSession session) {

        model.addAttribute("mc", "memberJoin");
        return "/pages/member/idFind";
    }

    @GetMapping({"/pages/member/pwFind"})
    public String pwFind(Model model,
                         HttpSession session) {

        model.addAttribute("mc", "memberJoin");
        return "/pages/member/pwFind";
    }

    @GetMapping({"/pages/member/joinAgree"})
    public String joinAgree(Model model,
                            String dv,
                            HttpSession session) {
        String page;
        model.addAttribute("mc", "memberJoin");
        if (dv.equals("nm")) {
            model.addAttribute("ty", "mb");
            page = "/pages/member/joinAgree";
        } else if (dv.equals("crew")) {
            model.addAttribute("ty", "et");
            page = "/pages/member/joinAgree";
        } else {
            page = "/pages/member/joinPick";
        }
        return page;
    }

    @GetMapping({"/pages/member/crew_register"})
    public String crew_register(Model model,
                                GroupForm crewForm,
                                HttpSession session) {
        model.addAttribute("mc", "crewJoin");
        return "/pages/member/crew_register";
    }

    @GetMapping({"/pages/member/register"})
    public String register(Model model,
                           MemberForm memberForm,
                           HttpSession session) {

        model.addAttribute("mc", "memberJoin");
        return "/pages/member/register";
    }

    /**
     * 단체 가입 서비스로직
     *
     * @param groupForm
     * @param attachedFile 이슈 : 로컬서버는 파일 업로드 안됨.
     * @date : 2021/08/20
     * @auther : jerry
     **/
    @PostMapping("/api/member/groupInsert")
    public String insert(Model model,
                         @ModelAttribute GroupForm groupForm,
                         @RequestParam("attachedFile") MultipartFile attachedFile,
                         Errors errors) throws Exception {
        String msg = "";
        boolean result = false;
        if (groupForm.getAuthMobileChk() == 2) {
            groupForm.setMobileAttcAt("Y");
            groupForm.setMobileAttcDtm(LocalDateTime.now());
            MobileAuthLogForm mobileAUthLogForm = new MobileAuthLogForm();
            mobileAUthLogForm.setDmnNo(groupForm.getSRequestNumber());
            mobileAUthLogForm.setRspNo(groupForm.getSResponseNumber());
            mobileAUthLogForm.setMbtlnum(groupForm.getMoblphon());
            MobileAuthLog load = mobileAuthLogService.load(mobileAUthLogForm);
            if (load == null) {
                log.info("휴대폰 정보 로드 에러 : " + ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors.getFieldErrors()));
            }
        }

        if (groupForm.getId() == null) {
            try {
                result = memberService.groupInsert(groupForm, attachedFile);
            } catch (ValidCustomException ve) {
                log.info("그룹 멤버 가입 실패 : " + ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ve));
            }
        }

        if (result) {
            msg = "가입 심사 후 가입됩니다. (소요기간 2 ~ 3일)";
            model.addAttribute("mc", "memberJoin");
            model.addAttribute("rsMsg", msg);
        } else {
            log.info("/api/member/groupInsert -> error : " + ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors.getFieldErrors()));
        }
        return "/login";
    }


    /**
     * 생성일 : 21.08.15
     * 생성자 : 김재일
     *
     * @param model
     * @param memberForm
     * @param errors
     * @param bindingResult
     **/
    @PostMapping("/api/member/insert")
    public ResponseEntity insert(Model model,
                                 @ModelAttribute @Valid MemberForm memberForm,
                                 Errors errors,
                                 BindingResult bindingResult) {
        boolean result = false;
        String msg = "";
        /*if (memberService.existsSpace(memberForm.getLoginId())) {
            bindingResult.rejectValue("loginId", "invalid ID", new Object[]{memberForm.getLoginId()}, "아이디에는 공백을 사용 할 수 없습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bindingResult.getFieldError("loginId").getDefaultMessage());
        }*/
        String year = memberForm.getBrthdy().substring(0, 4);
        Calendar cal = Calendar.getInstance();
//        if ((cal.get(Calendar.YEAR) - Integer.parseInt(year)) < 14) { //청소년인지 확인
//            if (memberForm.getPrtctorNm() == null || "".equals(memberForm.getPrtctorNm()) ||
//                    memberForm.getPrtctorBrthdy() == null || "".equals(memberForm.getPrtctorBrthdy()) ||
//                    memberForm.getPrtctorEmail() == null || "".equals(memberForm.getPrtctorEmail()) ||
//                    memberForm.getPrtctorEmail().split("@").length > 2 || StringUtils.countMatches(memberForm.getEmail(), " ") > 0 ||
//                    StringUtils.countMatches(memberForm.getLoginId(), " ") > 0 || StringUtils.countMatches(memberForm.getPwd(), " ") > 0 ||
//                    StringUtils.countMatches(memberForm.getPrtctorEmail(), " ") > 0) {
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors.getFieldErrors());
//            }
//        } else { //청소년 아닐경우
//        }

        if (memberForm.getAuthEmailChk() == 2) {
            memberForm.setEmailAttcAt("Y");
            memberForm.setMobileAttcAt("N");
            if (memberForm.getEmail().split("@").length > 2 || StringUtils.countMatches(memberForm.getEmail(), " ") > 0 ||
                    StringUtils.countMatches(memberForm.getLoginId(), " ") > 0 || StringUtils.countMatches(memberForm.getPwd(), " ") > 0
            ) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors.getFieldErrors());
            }
        }
        if (memberForm.getAuthMobileChk() == 2) {//컨트롤러 validation
            //휴대폰 인증 여부 확인
            log.info("휴대폰 인증 여부 확인");
            memberForm.setEmailAttcAt("N");
            memberForm.setMobileAttcAt("Y");
            MobileAuthLogForm mobileAUthLogForm = new MobileAuthLogForm();
            mobileAUthLogForm.setDmnNo(memberForm.getSRequestNumber());
            mobileAUthLogForm.setRspNo(memberForm.getSResponseNumber());
            mobileAUthLogForm.setMbtlnum(memberForm.getMoblphon());
            MobileAuthLog load = mobileAuthLogService.load(mobileAUthLogForm);
            if (load == null) {
                log.info("load null");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors.getFieldErrors());
            }
        }
        if (memberForm.getId() == null) {
            try {
                result = memberService.insert(memberForm);
            } catch (ValidCustomException ve) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ve);
            }
        }

        if (result) {
            msg = "회원가입이 완료되었습니다.";
            return ResponseEntity.ok(msg);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors.getFieldErrors());
        }
    }

    @PostMapping("/api/member/update")
    public ResponseEntity update(Model model,
                                 @ModelAttribute @Valid MemberForm memberForm,
                                 Errors errors) {
        boolean result = false;
        String msg = "";
        if (!errors.hasErrors()) {
            if (memberForm.getId() != null) {
                try {
                    result = memberService.update(memberForm);
                } catch (ValidCustomException ve) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ve.getErrors()[0]);
                }
            } else {
                msg = "사용자를 찾을 수 없습니다.";
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ValidCustomException(msg, "altmsg"));
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors.getFieldErrors());
        }

        if (result) {
            msg = "회원정보가 수정되었습니다.";
            return ResponseEntity.ok(msg);
        } else {
            msg = "실패했습니다. 관리자에게 문의해주세요.";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ValidCustomException(msg, "altmsg"));
        }
    }

    @PostMapping("/pages/member/pwdUpdate")
    public ResponseEntity pwdUpdate(Model model,
                                    @ModelAttribute @Valid MemberForm memberForm,
                                    Errors errors) {
        boolean result = false;
        String msg = "";
        List<FieldError> errList = new ArrayList<>();
        if (memberForm.getId() != null) {
            if (!errors.hasFieldErrors("pwd") && !errors.hasFieldErrors("pwdChg") && !errors.hasFieldErrors("pwdChgChk")) {
                try {
                    result = memberService.update(memberForm);
                } catch (ValidCustomException ve) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ve.getErrors());
                }
            } else {
                if (errors.hasFieldErrors("pwd")) {
                    errList.addAll(errors.getFieldErrors("pwd"));
                }
                if (errors.hasFieldErrors("pwdChg")) {
                    errList.addAll(errors.getFieldErrors("pwdChg"));
                }
                if (errors.hasFieldErrors("pwdChgChk")) {
                    errList.addAll(errors.getFieldErrors("pwdChgChk"));
                }
                if (!memberForm.getPwdChg().equals(memberForm.getPwdChgChk())) {
                    msg = "변경 비밀번호와 변경 비밀번호 확인이 일치하지 않습니다.";
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ValidCustomException(msg, "altmsg"));
                }

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errList);
            }
        } else {
            msg = "사용자를 찾을 수 없습니다.";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ValidCustomException(msg, "altmsg"));
        }

        if (result) {
            msg = "비밀번호가 변경되었습니다.";
            return ResponseEntity.ok(msg);
        } else {
            msg = "실패했습니다. 관리자에게 문의해주세요.";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ValidCustomException(msg, "altmsg"));
        }
    }

    @ResponseBody
    @PostMapping("/api/member/isExistsByLoginId")
    public ResponseEntity isExistsByLoginId(Model model,
                                            @ModelAttribute MemberForm memberForm,
                                            BindingResult bindingResult) {

        if (memberService.existsByLoginId(memberForm.getLoginId())) {
            bindingResult.rejectValue("loginId", "invalid ID", new Object[]{memberForm.getLoginId()}, "이미 사용중인 아이디입니다.");
        }
        if (memberService.existsSpace(memberForm.getLoginId())) {
            bindingResult.rejectValue("loginId", "invalid ID", new Object[]{memberForm.getLoginId()}, "아이디에는 공백을 사용 할 수 없습니다.");
        }
        //memberFormValidator.validate(memberForm, bindingResult);
        if (bindingResult.hasFieldErrors("loginId")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bindingResult.getFieldError("loginId").getDefaultMessage());
        } else {
            return ResponseEntity.ok(memberForm);
        }
    }


    // 이메일 인증
    @PostMapping("/api/member/CheckMail")
    @ResponseBody
    public int SendMail(Model model,
                        @ModelAttribute MemberForm memberForm) throws Exception {
        int tempKey;
        log.info("이메일 : " + memberForm.getEmail());
        Account account = new Account();
        account.setEmail(memberForm.getEmail());
        if (memberService.existsByEmail(memberForm.getEmail())) {
            tempKey = -1;
        } else {
            tempKey = memberService.gen6Digit();
            mailService.mailSend(memberService.sendEmailTempAuthKey(account, tempKey));
        }
        return tempKey;
    }


    @ResponseBody
    @PostMapping("/api/member/isExistsByEmail")
    public ResponseEntity isExistsByEmail(Model model,
                                          @ModelAttribute MemberForm memberForm,
                                          BindingResult bindingResult) {
        log.info("이메일 : " + memberForm.getEmail());
        if (memberService.existsByEmail(memberForm.getEmail())) {
            bindingResult.rejectValue("email", "invalid EMAIL", new Object[]{memberForm.getEmail()}, "이미 사용중인 이메일입니다.");
        } else {

        }
        //memberFormValidator.validate(memberForm, bindingResult);
        if (bindingResult.hasFieldErrors("email")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bindingResult.getFieldError("email").getDefaultMessage());
        } else {
            return ResponseEntity.ok(memberForm);
        }
    }

    // id찾기
    // 이름, 이메일로 member 존재여부 판단.
    // 존재한다면 id값 보여주고 로그인화면으로 보내기
    @ResponseBody
    @PostMapping("/api/member/idFind")
    public String idFind(Model model,
                         @RequestBody MemberForm memberForm) {
        String loginId = "";

        Account byNmAndEmail = memberService.findByNmAndEmail(memberForm.getNm(), memberForm.getEmail());
        if (byNmAndEmail != null && !byNmAndEmail.getLoginId().isEmpty()) {
            loginId = byNmAndEmail.getLoginId();
        }

        return loginId;
    }

    // pw 임시 비밀번호 발급
    // 이름, 이메일로 member 존재여부 판단.
    // 존재한다면 임시 비밀번호 발생 후, 메일로 전송
    @ResponseBody
    @PostMapping("/api/member/sendTempPw")
    public Account pwFind(Model model,
                          @RequestBody MemberForm memberForm) {
        boolean result = false;

        Account account = new Account();
        if (!memberForm.getLoginId().isEmpty() && !memberForm.getEmail().isEmpty()) {
            Account check = memberService.loadByLoginId(memberForm.getLoginId());
            if (check == null || check.getLoginId() == null) {
                return null;
            }
            if (!memberForm.getEmail().equals(check.getEmail())) {
                return null;
            }
            result = memberService.updateTempPw(memberForm);
            if (result) {
                Account load = memberService.findByLoginIdAndDelAt(memberForm.getLoginId(), "N");
                account.setId(load.getId());
                account.setLoginId(load.getLoginId());
            }

        }

        return account;
    }

    //메일인증 재발송
    @PostMapping("/api/member/mailAuthResend")
    public String mailAuthResend(Model model,
                                 @ModelAttribute MemberForm memberForm) throws Exception {
        boolean result = false;

        if (memberForm.getLoginId() != null) {
            Account account = memberService.loadByLoginId(memberForm.getLoginId());
            if (account != null) {
                if (account.getEmailAttcAt().equals("Y") && account.getPrtctorAttcAt().equals("Y")) {
                    model.addAttribute("altmsg", "이미 인증처리 된 아이디 입니다.");
                    model.addAttribute("locurl", "/login");
                    return "/message";
                }
                memberForm.setEmailAttcAt(account.getEmailAttcAt());
                memberForm.setPrtctorAttcAt(account.getPrtctorAttcAt());
                result = memberService.updateEmailAttc(memberForm);
            }
        }

        if (result) {
            model.addAttribute("altmsg", "재발송 처리됐습니다. 인증 후 로그인해주세요.");
        } else {
            model.addAttribute("altmsg", "재발송 실패했습니다. 인증 메일을 재발송 해주세요.");
        }
        model.addAttribute("locurl", "/login");
        return "/message";
    }

    //메일인증
    // 삭제예정
    @GetMapping("/api/member/mailAuth")
    public String mailAuth(Model model,
                           @RequestParam(name = "authKey") String authKey) throws Exception {
        boolean result = false;

        AESEncryptor aesEncryptor = AESEncryptor.getInstance(Constants.AESEncryptKey);
        String authKeyStr = aesEncryptor.decrypt(authKey);

        String loginId = authKeyStr.substring(0, authKeyStr.indexOf("|"));
        LocalDateTime emailAttcDtm = LocalDateTime.parse(authKeyStr.substring(authKeyStr.indexOf("|") + 1));

        MemberForm memberForm = new MemberForm();
        memberForm.setLoginId(loginId);
        memberForm.setEmailAttcDtm(emailAttcDtm);
        if (memberForm.getLoginId() != null && memberForm.getEmailAttcDtm() != null) {
            Account account = memberService.loadByLoginId(memberForm.getLoginId());
            if (account != null) {
                if (account.getEmailAttcAt().equals("Y")) {
                    model.addAttribute("altmsg", "이미 인증처리 된 아이디 입니다.");
                    model.addAttribute("locurl", "/login");
                    return "/message";
                } else {
                    LocalDateTime limitLdt = LocalDateTime.now();
                    LocalDateTime reqLdt = memberForm.getEmailAttcDtm().plusHours(1L);
                    if (limitLdt.isBefore(reqLdt)) { // 인증 가능 1시간 체크
                        memberForm.setEmailAttcAt("Y");
                        result = memberService.updateEmailAttc(memberForm);
                    } else {
                        model.addAttribute("altmsg", "인증 가능 시간(1시간) 초과로, 인증 실패했습니다. 인증 메일을 재발송 해주세요.");
                        model.addAttribute("locurl", "/login");
                        return "/message";
                    }
                }
            }
        }

        if (result) {
            model.addAttribute("altmsg", "인증 처리됐습니다. 로그인해주세요.");
        } else {
            model.addAttribute("altmsg", "인증 실패했습니다. 관리자에게 문의해주세요.");
        }
        model.addAttribute("locurl", "/login");
        return "/message";
    }

    @GetMapping("/api/member/parentMailAuth")
    public String parentMailAuth(Model model,
                                 @RequestParam(name = "authKey") String authKey) throws Exception {
        boolean result = false;

        AESEncryptor aesEncryptor = AESEncryptor.getInstance(Constants.AESEncryptKey);
        String authKeyStr = aesEncryptor.decrypt(authKey);

        String loginId = authKeyStr.substring(0, authKeyStr.indexOf("|"));
        LocalDateTime emailAttcDtm = LocalDateTime.parse(authKeyStr.substring(authKeyStr.indexOf("|") + 1));

        MemberForm memberForm = new MemberForm();
        memberForm.setLoginId(loginId);
        memberForm.setPrtctorAttcDtm(emailAttcDtm);
        if (memberForm.getLoginId() != null && memberForm.getPrtctorAttcDtm() != null) {
            Account account = memberService.loadByLoginId(memberForm.getLoginId());
            if (account != null) {
                if (account.getPrtctorAttcAt().equals("Y")) {
                    model.addAttribute("altmsg", "이미 인증처리 된 아이디 입니다.");
                    model.addAttribute("locurl", "/login");
                    return "/message";
                } else {
                    LocalDateTime limitLdt = LocalDateTime.now();
                    LocalDateTime reqLdt = memberForm.getPrtctorAttcDtm().plusHours(1L);
                    if (limitLdt.isBefore(reqLdt)) { // 인증 가능 1시간 체크
                        memberForm.setPrtctorAttcAt("Y");
                        result = memberService.updateEmailAttc(memberForm);
                    } else {
                        model.addAttribute("altmsg", "인증 가능 시간(1시간) 초과로, 인증 실패했습니다. 인증 메일을 재발송 해주세요.");
                        model.addAttribute("locurl", "/login");
                        return "/message";
                    }
                }
            }
        }

        if (result) {
            model.addAttribute("altmsg", "인증 처리됐습니다. 로그인해주세요.");
        } else {
            model.addAttribute("altmsg", "인증 실패했습니다. 관리자에게 문의해주세요.");
        }
        model.addAttribute("locurl", "/login");
        return "/message";

    }
    @ResponseBody
    @GetMapping(value = "/api/member/srchCrew")
    public List<MemberCrew> srchCrew(Model model, @RequestParam(name = "srchCrewNm") String CrewNm) {

        List<MemberCrew> memberCrewList = memberService.srchCrewList(CrewNm);

        if(memberCrewList.size() == 0) {
            System.out.println("조회값 없음");
        }

        return memberCrewList;
    }
}
