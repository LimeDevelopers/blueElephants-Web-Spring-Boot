package kr.or.btf.web.web.controller;


import kr.or.btf.web.common.Constants;
import kr.or.btf.web.common.annotation.CurrentUser;
import kr.or.btf.web.domain.web.*;
import kr.or.btf.web.domain.web.enums.LoginLogMessage;
import kr.or.btf.web.domain.web.enums.UserRollType;
import kr.or.btf.web.services.web.*;
import kr.or.btf.web.utils.StringHelper;
import kr.or.btf.web.web.form.*;
import kr.or.btf.web.web.validator.MemberFormValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class MemberController extends BaseCont{

    private final MemberService memberService;
    private final MemberSchoolService memberSchoolService;
    private final MemberParentService memberParentService;
    private final MemberTeacherService memberTeacherService;
    private final CommonCodeService commonCodeService;
    private final MemberFormValidator memberFormValidator;
    private final PasswordEncoder passwordEncoder;

    @RequestMapping("/soulGod/member/list")
    public String list(Model model,
                       @PageableDefault Pageable pageable,
                       @ModelAttribute SearchForm searchForm,
                       @CurrentUser Account account) {
        searchForm.setGroupDv("N");
        model.addAttribute("form", searchForm);

        searchForm.setUserRollType(account.getMberDvTy());
        Page<Account> members = memberService.list(pageable, searchForm);
        model.addAttribute("members", members);
        model.addAttribute("totCnt", members.isEmpty() ? 0 : members.getContent().size());


        model.addAttribute("mc", "member");
        model.addAttribute("dv", "member");
        return "/soulGod/member/list";
    }

    @RequestMapping("/soulGod/member/groupList")
    public String groupList(Model model,
                       @PageableDefault Pageable pageable,
                       @ModelAttribute SearchForm searchForm,
                       @CurrentUser Account account) {
        searchForm.setGroupDv("Y");
        model.addAttribute("form", searchForm);

        searchForm.setUserRollType(account.getMberDvTy());
        Page<Account> members = memberService.list(pageable, searchForm);
        model.addAttribute("members", members);
        model.addAttribute("totCnt", members.isEmpty() ? 0 : members.getContent().size());

        model.addAttribute("mc", "member");
        model.addAttribute("dv", "group");
        return "/soulGod/member/list";
    }

    @ResponseBody
    @PostMapping("/api/soulGod/member/load")
    public Account load(Model model,
                          @RequestBody MemberForm memberForm) {

        Account load = memberService.load(memberForm.getId());

        return load;
    }

    @GetMapping("/soulGod/member/detail/{id}")
    public String detail(Model model,
                          @PathVariable(name = "id") Long id) {

        Account load = memberService.load(id);
        model.addAttribute("form", load);
        if(load.getMberDvTy().equals(UserRollType.GROUP) || load.getMberDvTy().equals(UserRollType.CREW)){

        } else {

            if (load.getMberDvTy() == UserRollType.STUDENT) {
                /* 학교 정보 s*/
                MemberSchoolForm memberSchoolForm = new MemberSchoolForm();
                memberSchoolForm.setMberPid(load.getId());
                List<MemberSchool> memberSchools = memberSchoolService.list(memberSchoolForm);
                model.addAttribute("memberSchools", memberSchools);
                /* 학교 정보 e*/

                /*교원 정보 s*/
                List<Object> teachers = new ArrayList<>();

                for (MemberSchool memberSchool : memberSchools) {
                    if (memberSchool.getAreaNm() != null && !"".equals(memberSchool.getAreaNm())
                            && memberSchool.getSchlNm() != null && !"".equals(memberSchool.getSchlNm())
                            && memberSchool.getGrade() != null && !"".equals(memberSchool.getGrade())
                            && memberSchool.getBan() != null
                    ) {
                        MemberTeacherForm memberTeacherForm = new MemberTeacherForm();
                        memberTeacherForm.setSchlNm(memberSchool.getSchlNm());
                        memberTeacherForm.setGrade(memberSchool.getGrade());
                        memberTeacherForm.setBan(memberSchool.getBan());
                        List<MemberTeacher> memberTeachers = memberTeacherService.list(memberTeacherForm);

                        for (MemberTeacher memberTeacher : memberTeachers) {
                            Account teacherLoad = memberService.load(memberTeacher.getMberPid());
                            teachers.add(teacherLoad);
                        }
                    }
                }

                /*교원 정보 e*/

                /*부모 정보 s*/
                MemberParentForm memberParentForm = new MemberParentForm();
                memberParentForm.setStdntId(load.getLoginId());
                List<MemberParent> memberParents = memberParentService.list(memberParentForm);

                List<Object> parents = new ArrayList<>();

                for (MemberParent memberParent : memberParents) {
                    Account parent = memberService.loadByLoginId(memberParent.getStdnprntId());
                    parents.add(parent);
                }

                model.addAttribute("userList", teachers);
                model.addAttribute("parents", parents);
                /*부모 정보 e*/

            } else if (load.getMberDvTy() == UserRollType.TEACHER) {
                /* 학교 정보 s*/
                MemberTeacher memberTeacher = memberTeacherService.loadByMber(load.getId());
                model.addAttribute("memberTeacher", memberTeacher);
                /* 학교 정보 e*/

                List<Map<String,Object>> studentList = new ArrayList<>();

                if (
                        (memberTeacher.getAreaNm() != null && !"".equals(memberTeacher.getAreaNm())) &&
                        (memberTeacher.getSchlNm() != null && !"".equals(memberTeacher.getSchlNm())) &&
                        (memberTeacher.getGrade() != null && !"".equals(memberTeacher.getGrade())) &&
                        (memberTeacher.getBan() != null && !"".equals(memberTeacher.getBan()))
                ) {
                    /*학생 정보 s*/
                    MemberSchoolForm memberSchoolForm = new MemberSchoolForm();
                    memberSchoolForm.setSchlNm(memberTeacher.getSchlNm());
                    memberSchoolForm.setGrade(memberTeacher.getGrade());
                    memberSchoolForm.setBan(memberTeacher.getBan());
                    List<MemberSchool> students = memberSchoolService.listForAdminTeacher(memberSchoolForm);

                    Map<Integer, List<MemberSchool>> testMap = students.stream().collect(Collectors.groupingBy(MemberSchool::getNo));

                    testMap.forEach((key,value) -> {
                        Map<String,Object> map = new HashMap<>();
                        List<MemberSchool> memberParents = new ArrayList<>();
                        MemberSchool v = testMap.get(key).get(0);
                        map.put("student",v);
                        for (MemberSchool parentInfo : value) {
                            if (parentInfo.getParentNm() != null) {

                                memberParents.add(parentInfo);
                            }
                        }
                        map.put("memberParents",memberParents);

                        studentList.add(map);
                    });
                }

                model.addAttribute("studentList",studentList);

            } else if (load.getMberDvTy() == UserRollType.PARENT) {

                MemberParentForm memberParentForm = new MemberParentForm();
                memberParentForm.setStdnprntId(load.getLoginId());

                List<MemberParent> childList = memberParentService.listForAdminParent(memberParentForm);

                model.addAttribute("childList",childList);

            }
        }
        return "/soulGod/member/detail";
    }
    // 회원관리 - 회원정보수정
    @GetMapping("/soulGod/member/modify/{id}")
    public String memberModify(Model model,
                               @PathVariable(name = "id") Long id) {

        Account load = memberService.load(id);
        model.addAttribute("form", load);

        return "/soulGod/member/modify";
    }

    static <T> Collector<T,?,List<T>> toSortedList(Comparator<? super T> c) {
        return Collectors.collectingAndThen(
                Collectors.toCollection(()->new TreeSet<>(c)), ArrayList::new);
    }

    @PostMapping("/soulGod/member/refreshPassword")
    public String refreshPassword(Model model,
                                  @ModelAttribute MemberForm form,
                                  @CurrentUser Account account,
                                  HttpServletRequest request) {

        form.setPwd(passwordEncoder.encode(Constants.DEFAULT_PASSWORD));

        LoginCnntLogs log = new LoginCnntLogs();
        log.setCnctDtm(LocalDateTime.now());
        log.setCnctId(account.getLoginId());
        log.setCnctIp(StringHelper.getClientIP(request));
        log.setSuccesAt("Y");
        log.setFailCnt(0);
        log.setRsn(LoginLogMessage.REFRESHPASSWORD.getValue());

        boolean result = memberService.setPasswordUpdate(form, log);


        model.addAttribute("locurl", "/soulGod/member/detail/"+form.getId());
        if (result) {
            model.addAttribute("altmsg", "비밀번호가 초기화되었습니다.");
        } else {
            model.addAttribute("altmsg", "실패했습니다.");
        }
        return "/message";
    }

    @GetMapping("/soulGod/my/detail")
    public String myDetail(Model model,
                         @CurrentUser Account account) {

        Account load = memberService.load(account.getId());
        model.addAttribute("form", load);

        return "/soulGod/my/detail";
    }

    @GetMapping("/soulGod/my/modify")
    public String myModify(Model model,
                         @CurrentUser Account account) {

        Account load = memberService.load(account.getId());
        model.addAttribute("form", load);

        return "/soulGod/my/modify";
    }

    @PostMapping("/api/soulGod/my/save")
    public String myRegisterProc(Model model,
                               @ModelAttribute MemberForm memberForm,
                               @CurrentUser Account account,
                               HttpServletResponse response,
                               RedirectAttributes redirect) {

        boolean result = false;
        memberForm.setEmailAttcAt("Y");
        memberForm.setEmailAttcDtm(LocalDateTime.now());
        memberForm.setRegPsId(account.getLoginId());
        memberForm.setRegDtm(LocalDateTime.now());
        memberForm.setUpdPsId(account.getLoginId());
        memberForm.setUpdDtm(LocalDateTime.now());
        memberForm.setDelAt("N");

        if ("".equals(memberForm.getZip())) {
            memberForm.setZip(null);
        }

        result = memberService.update(memberForm);

        return "redirect:/soulGod/my/detail";
    }


    @PostMapping("/api/soulGod/member/save")
    public String registerProc(Model model,
                               @ModelAttribute MemberForm memberForm,
                               @CurrentUser Account account,
                               HttpServletResponse response,
                               RedirectAttributes redirect) {

        boolean result = false;
        memberForm.setEmailAttcAt("Y");
        memberForm.setEmailAttcDtm(LocalDateTime.now());
        memberForm.setRegPsId(account.getLoginId());
        memberForm.setRegDtm(LocalDateTime.now());
        memberForm.setUpdPsId(account.getLoginId());
        memberForm.setUpdDtm(LocalDateTime.now());
        memberForm.setDelAt("N");

        if ("".equals(memberForm.getZip())) {
            memberForm.setZip(null);
        }

        if (memberForm.getId() == null) {
            result = memberService.insert(memberForm);
        } else {
            result = memberService.update(memberForm);
        }

        return "redirect:/soulGod/member/list";
    }

    @GetMapping({"/soulGod/member/register"})
    public String register(Model model,
                           MemberForm memberForm,
                           HttpSession session) {

        model.addAttribute("mc", "member");
        return "/soulGod/member/register";
    }

    /*@PostMapping("/api/soulGod/member/delete")
    public String delete(Model model,
                         @RequestParam(name = "id") Long[] ids,
                         @CurrentUser Account account,
                         HttpServletResponse response,
                         RedirectAttributes redirect) throws Exception {

        MemberForm form = new MemberForm();
        for (Long id : ids) {

            memberService.delete(id, account, "Y");
        }

        return "redirect:/soulGod/member/list";
    }*/

}
