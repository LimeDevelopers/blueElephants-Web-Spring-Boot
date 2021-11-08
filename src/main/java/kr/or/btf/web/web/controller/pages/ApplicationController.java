package kr.or.btf.web.web.controller.pages;



import kr.or.btf.web.common.annotation.CurrentUser;
import kr.or.btf.web.domain.web.*;
import kr.or.btf.web.domain.web.enums.AppRollType;
import kr.or.btf.web.domain.web.enums.MberDvType;
import kr.or.btf.web.domain.web.enums.UserRollType;
import kr.or.btf.web.services.web.ApplicationService;
import kr.or.btf.web.web.form.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.html.HTMLModElement;

import java.util.List;


@Slf4j
@Controller
@RequiredArgsConstructor
public class  ApplicationController {
    private final ApplicationService applicationService;

    //등록 수행 컨트롤러
    @PostMapping(value = "/pages/application/partners/partnersregister")
    public String PartnersRegister(@ModelAttribute ApplicationForm applicationForm ,
                                   @RequestParam("attachedFile") MultipartFile attachedFile ,
                                   @CurrentUser Account account,
                                   Model model ,
                                   Error error ) throws Exception {
        if(account != null) {
            applicationForm.setMberPid(account.getId());
        }
        applicationService.partnersRegister(applicationForm , attachedFile);
        model.addAttribute("mc", "application");
        model.addAttribute("pageTitle", "지지크루");

        return "redirect:";
    }

    @PostMapping(value = "/pages/application/zzcrew/zzcrewRegister")
    public String zzCrewRegister(@ModelAttribute ApplicationForm applicationForm ,
                                 @RequestPart("attachedFile") MultipartFile attachedFile ,
                                 Model model ,
                                 @CurrentUser Account account ,
                                 Error error) throws Exception {
        if(account != null) {
            applicationForm.setMberPid(account.getId());
        }
        applicationService.zzcrewRegister(applicationForm , attachedFile);
        model.addAttribute("mc", "application");
        model.addAttribute("pageTitle", "지지크루");

        return "pages/application/zzcrew";
    }

    @PostMapping("/pages/application/zzdeclareRegister")
    public String zzdeclarationRegister(@ModelAttribute ApplicationForm applicationForm ,
                                 @RequestParam("attachedFile") MultipartFile attachedFile ,
                                 @CurrentUser Account account ,
                                 Model model ,
                                 Error error) throws Exception {
        model.addAttribute("mc", "application");
        model.addAttribute("pageTitle", "지지선언");

        applicationService.zzdeclareRegister(applicationForm , attachedFile);

        return "pages/application/zzdeclaration";
    }

    //승인여부 변경 컨트롤러 applicationapproval
    @ResponseBody
    @PostMapping("/api/soulGod/application/updateApporaval/{id}")
    public Boolean applicationApporval(@PathVariable(name = "id") String id){
        Boolean result = false;
        String[] pid = id.split(",");
        if (pid.length > 1) {
            for (int i = 0; i < pid.length; i++) {
                result = applicationService.updateApproval(pid[i]);
            }
        } else {
            result = applicationService.updateApproval(pid[0]);
        }

        return result;
    }


    @RequestMapping("/pages/application/preeducationList")
    public String PreEducationList(Model model,
                                   @CurrentUser Account account,
                                   Pageable pageable,
                                   SearchForm searchForm) {
        // 수연님 작업 후 제거
        if(account == null) {
            model.addAttribute("altmsg", "로그인 후 이용가능합니다.");
            model.addAttribute("locurl", "/login");
            return "/message";
        } else {
            if(account.getMberDvTy().equals(UserRollType.INSTRUCTOR) || account.getMberDvTy().equals(UserRollType.TEACHER)){
                Page<PreventionMaster> preventionPage = applicationService.getPreEduMstList(pageable);
                model.addAttribute("preList", preventionPage);
            } else {
                model.addAttribute("altmsg", "접근 권한이 없습니다.");
                model.addAttribute("locurl", "/");
                return "/message";
            }
        }

        // 작업 후 제거
        Page<PreventionMaster> preventionPage = applicationService.getPreEduMstList(pageable);
        model.addAttribute("preList", preventionPage);

        model.addAttribute("mc", "application");
        model.addAttribute("pageTitle", "예방교육");
        return "pages/application/preeducationList";
    }

    @RequestMapping("pages/application/InsRegister")
    public String InsRegisterPage(Model model,
                                   @CurrentUser Account account) {
        if(account == null) {
            model.addAttribute("altmsg", "로그인 후 이용가능합니다.");
            model.addAttribute("locurl", "/login");
            return "/message";
        } else {
            if(!account.getMberDvTy().equals(UserRollType.NORMAL)){
                model.addAttribute("altmsg", "일반회원만 이용 가능합니다.");
                model.addAttribute("locurl", "/");
                return "/message";
            } else {
                PreventionInstructor preventionInstructor = applicationService.getPreIns(account.getId());
                if(preventionInstructor != null) {
                    if(preventionInstructor.getTempSave().equals("W")) {
                        model.addAttribute("altmsg", "이미 신청완료되었습니다. \n 결과 승인은 평일 기준 3-5일 소요됩니다.");
                        model.addAttribute("locurl", "/pages/myPage/profile");
                        return "/message";
                    } else {
                        model.addAttribute("preList", preventionInstructor);
                    }
                }
            }
        }
        model.addAttribute("mc", "application");
        model.addAttribute("pageTitle", "강사교육");
        return "pages/application/InsRegister";
    }
    @PostMapping("/api/application/registerPreIns")
    public String registerPreIns(Model model,
                                 @CurrentUser Account account,
                                 PreventionInstructorForm preventionInstructorForm) {
        boolean result = false;
        if(account!=null) {
            if(account.getMberDvTy().equals(UserRollType.NORMAL)) {
                PreventionInstructor preventionInstructor = applicationService.getPreIns(account.getId());
                if(preventionInstructor != null) {
                    if(preventionInstructor.getTempSave().equals("W")){
                        model.addAttribute("altmsg", "이미 신청완료되었습니다. \n 결과 승인은 평일 기준 3-5일 소요됩니다.");
                        model.addAttribute("locurl", "/pages/application/preeducationList");
                        return "/message";
                    }
                    result = applicationService.updatePreIns(preventionInstructorForm);
                } else {
                    preventionInstructorForm.setMberPid(account.getId());
                    result = applicationService.registerPreIns(preventionInstructorForm);
                }    
            }
            if(account.getMberDvTy().equals(UserRollType.INSTRUCTOR)) {
                
            }
        } else {
            if(account!=null){
                model.addAttribute("altmsg", "권한이 없습니다.");
                model.addAttribute("locurl", "/");
                return "/message";
            } else {
                model.addAttribute("altmsg", "로그인 후 이용가능합니다.");
                model.addAttribute("locurl", "/login");
            }
            return "/message";
        }
        if(!result){
            model.addAttribute("altmsg", "에러발생! 관리자에게 문의하세요.");
            model.addAttribute("locurl", "/pages/application/preeducationList");
            return "/message";
        }

        model.addAttribute("mc", "MyPage");
        model.addAttribute("pageTitle", "교육신청관리");
        return "redirect:/pages/myPage/profile";
    }

    @PostMapping("/api/application/registerPreEdu")
    public String registerPreEdu(Model model,
                                 @CurrentUser Account account,
                                 PreventionMasterForm preventionMasterForm) {
        boolean result;
        if(account!=null && account.getMberDvTy().equals(UserRollType.TEACHER)) {
            PreventionMaster preventionMaster = applicationService.getPreEduMst(preventionMasterForm.getPrePid(), account.getId());
            if(preventionMaster != null) {
                if(preventionMaster.getTempSave().equals("N")){
                    model.addAttribute("altmsg", "이미 신청완료된 학교입니다. \n 결과 승인은 평일 기준 3-5일 소요됩니다.");
                    model.addAttribute("locurl", "/pages/application/preeducationList");
                    return "/message";
                }
                result = applicationService.updatePreEdu(preventionMasterForm);
            } else {
                preventionMasterForm.setMberPid(account.getId());
                result = applicationService.registerPreEdu(preventionMasterForm);
            }
        } else {
            if(account!=null){
                model.addAttribute("altmsg", "선생님만 이용 가능합니다.");
                model.addAttribute("locurl", "/");
            } else {
                model.addAttribute("altmsg", "로그인 후 이용가능합니다.");
                model.addAttribute("locurl", "/login");
            }
            return "/message";
        }
        if(!result){
            model.addAttribute("altmsg", "에러발생! 관리자에게 문의하세요.");
            model.addAttribute("locurl", "/pages/application/preeducationList");
            return "/message";
        }

        model.addAttribute("mc", "MyPage");
        model.addAttribute("pageTitle", "교육신청관리");
        return "redirect:/pages/myPage/insWorkPreEduList";
    }

    @ResponseBody
    @GetMapping("/api/application/preeducation/{id}")
    public Boolean setPreeducation(Model model,
                                  @PathVariable("id") Long id,
                                  @CurrentUser Account account) {
        if(account == null) {
            return false;
        }
        applicationService.setPreeducation(id, account.getId());
        return true;
    }

    // 예방강의 희망 학교 정보 상세보기 load..
    @GetMapping("/pages/application/preeducationDetail/{id}")
    public String preeducationDetail(Model model,
                               @PathVariable("id") Long id,
                               @CurrentUser Account account) {
        // 수연님 작업 후 제거
        if(account == null) {
            model.addAttribute("altmsg", "로그인 후 이용가능합니다.");
            model.addAttribute("locurl", "/login");
            return "/message";
        } else {
            if(UserRollType.INSTRUCTOR.equals(account.getMberDvTy()) || UserRollType.TEACHER.equals(account.getMberDvTy())){
                PreventionMaster preventionMaster = applicationService.getPreEduMstData(id);
                if(preventionMaster==null){
                    model.addAttribute("altmsg", "존재하지않는 게시글입니다.");
                    model.addAttribute("locurl", "/pages/application/preeducationList");
                    return "/message";
                } else {
                    if(UserRollType.INSTRUCTOR.equals(account.getMberDvTy())){
                        Prevention prevention = applicationService.getPreAt(id, account.getId());
                        if(prevention != null) {
                            model.addAttribute("prevention", prevention);
                        }
                    }
                    model.addAttribute("preventionMaster", preventionMaster);
                }
            } else {
                model.addAttribute("altmsg", "접근 권한이 없습니다.");
                model.addAttribute("locurl", "/");
                return "/message";
            }
        }
        // 작업 후 제거
        PreventionMaster preventionMaster = applicationService.getPreEduMstData(id);
        model.addAttribute("preventionMaster", preventionMaster);

        model.addAttribute("pre_pid", id);
        model.addAttribute("mc", "application");
        model.addAttribute("pageTitle", "예방교육");
        return "pages/application/preeducationDetail";
    }
    // 학교 신청 등록
    @GetMapping(value = "/pages/application/preeducation")
    public String PreEducation(Model model,
                               @CurrentUser Account account) {
        if(account == null) {
            model.addAttribute("altmsg", "로그인 후 이용가능합니다.");
            model.addAttribute("locurl", "/login");
            return "/message";
        } else {
            if(UserRollType.TEACHER.equals(account.getMberDvTy())){
                MemberTeacher mt = applicationService.getSchoolData(account.getId());
                if(mt==null){
                    model.addAttribute("altmsg", "학교 정보가 존재하지않습니다.");
                    model.addAttribute("locurl", "/pages/application/preeducationList");
                    return "/message";
                } else {
                    PreventionMaster preventionMaster = applicationService.getPreEduMstData(account.getId());
                    if(preventionMaster != null) {
                        if(preventionMaster.getTempSave().equals("N")){
                            model.addAttribute("altmsg", "이미 신청완료된 학교입니다. \n 결과 승인은 평일 기준 3-5일 소요됩니다.");
                            model.addAttribute("locurl", "/pages/application/preeducationList");
                            return "/message";
                        }
                        model.addAttribute("preventionMst", preventionMaster);
                    }
                    model.addAttribute("schlData", mt);
                }
            } else {
                model.addAttribute("altmsg", "예방 강사만 이용 가능합니다.");
                model.addAttribute("locurl", "/");
                return "/message";
            }
        }
        model.addAttribute("mc", "application");
        model.addAttribute("pageTitle", "예방교육");
        return "pages/application/preeducationRegister";
    }

    @GetMapping("/pages/application/insInfo")
    public String InsEducation(Model model) {
        model.addAttribute("mc", "application");
        model.addAttribute("pageTitle", "강사교육");
        return "pages/application/insInfo";
    }

    @GetMapping("/pages/application/zzdeclaration")
    public String zzdeclaration(Model model) {
        model.addAttribute("mc", "application");
        model.addAttribute("pageTitle", "지지선언");
        return "pages/application/zzdeclaration";
    }

    @GetMapping("/pages/application/contest")
    public String contest(Model model) {
        model.addAttribute("mc", "application");
        model.addAttribute("pageTitle", "공모전");
        return "pages/application/contest";
    }

    @GetMapping("/pages/application/event")
    public String event(Model model) {
        model.addAttribute("mc", "application");
        model.addAttribute("pageTitle", "행사");
        return "pages/application/event";
    }

    @GetMapping("/pages/application/partners")
    public String partnersPage(Model model) {
        model.addAttribute("mc", "application");
        model.addAttribute("pageTitle", "파트너스");
        return "pages/application/partners";
    }
    @GetMapping("/pages/application/zzcrewRegister")
    public String zzcrewRegister(Model model) {
        model.addAttribute("mc", "application");
        model.addAttribute("pageTitle", "지지크루");
        return "pages/application/zzcrewRegister";
    }

    //등록페이지 이동 컨트롤러
    @GetMapping("/pages/application/zzcrew")
    public String zzcrew(Model model ,
                         @ModelAttribute ApplicationForm applicationForm) {
        if(AppRollType.CREW.getName().equals(applicationForm.getAppDvTy())) {

        }
        model.addAttribute("mc", "application");
        model.addAttribute("pageTitle", "지지크루");
        return "pages/preparing";
    }

    @GetMapping("/pages/application/partnersRegister")
    private String appPartnersRegister(Model model,
                                       @ModelAttribute ApplicationForm applicationForm){
        // ROLL 타입이 파트너스일 경우
        if(AppRollType.PARTNERS.getName().equals(applicationForm.getAppDvTy())) {

        }
        model.addAttribute("mc", "application");
        model.addAttribute("pageTitle", "파트너스");

        return "/pages/application/partnersRegister";
    }
    @GetMapping("/pages/application/zzdeclarationRegister")
    private String zzdeclarationRegister(Model model ,
                                         @ModelAttribute ApplicationForm applicationForm) {
        if(AppRollType.DECLARE.getName().equals(applicationForm.getAppDvTy())) {

        }
        model.addAttribute("mc", "application");
        model.addAttribute("pageTitle", "지지선언");
        return "pages/application/zzdeclarationRegister";
    }
    @PostMapping("/pages/application/eventList/eventRegister")
    public String eventRegister(Model model ,
                                ApplicationForm applicationForm ,
                                Pageable pageable ,
                                SearchForm searchForm ,
                                @RequestParam("attachedFile") MultipartFile attachedFile ,
                                @CurrentUser Account account) throws Exception {

        System.out.println("1" + applicationForm.getEventPid());
        System.out.println("2" + applicationForm.getNm());
        System.out.println("3" + applicationForm.getAffi());
        System.out.println("4" + applicationForm.getMoblphon());

        applicationService.eventRegister(applicationForm , attachedFile);
        model.addAttribute("mc", "application");
        model.addAttribute("pageTitle", "행사");

        Page<Event> EventPage = applicationService.getEventList(pageable,searchForm);
        model.addAttribute("eventList" , EventPage);


        return "/pages/application/eventList";
    }

    //행사신청 이동컨트롤러


    //리스트 페이지 이동 컨트롤러
    @RequestMapping("/pages/application/eventList")
    public String EventList(Model model ,
                            @CurrentUser Account account,
                            Pageable pageable,
                            SearchForm searchForm) {
        Page<Event> EventPage = applicationService.getEventList(pageable,searchForm);
        model.addAttribute("eventList" , EventPage);

        model.addAttribute("mc", "application");
        model.addAttribute("pageTitle", "행사");
        return "pages/application/eventList";
    }

    @GetMapping("/pages/application/eventList/{id}")
    public String eventDetail(Model model,
                        ApplicationForm applicationForm,
                        @PathVariable("id") Long id,
                        @CurrentUser Account account){
        Event event = applicationService.getEventData(id);
        List<Event> eventDetail = applicationService.getEventDetail(id);

        if(event == null){
            model.addAttribute("altmsg", "정상적인 경로를 이용하세요.");
            model.addAttribute("locurl", "/pages/application/eventList");
            return "/message";
        }
        model.addAttribute("epid",id);
        model.addAttribute("eventList" , eventDetail);
        model.addAttribute("mc", "application");
        model.addAttribute("pageTitle", "행사");

        return "pages/application/eventDetail";
    }

    @GetMapping("/pages/application/eventList/eventDetail/eventRegister/{id}")
    public String eventRegister(Model model ,
                                  ApplicationForm applicationForm ,
                                  @PathVariable("id") Long id ,
                                  @CurrentUser Account account) {
        Event event = applicationService.getEventData(id);
        List<Event> eventDetail= applicationService.getEventDetail(id);

        if(event == null){
            model.addAttribute("altmsg", "정상적인 경로를 이용하세요.");
            model.addAttribute("locurl", "/pages/application/eventList");
            return "/message";
        }

        model.addAttribute("epid",id);
        model.addAttribute("eventList" , eventDetail);
        model.addAttribute("mc", "application");
        model.addAttribute("pageTitle", "공모전");

        return "pages/application/eventRegister";
    }


    @RequestMapping(value = "/pages/application/contestList")
    public String ContestList(Model model ,
                              @CurrentUser Account account ,
                              Pageable pageable ,
                              SearchForm searchForm) {
        Page<Contest> ContestPage = applicationService.getContestList(pageable,searchForm);
        model.addAttribute("contestList" , ContestPage);

        model.addAttribute("mc", "application");
        model.addAttribute("pageTitle", "공모전");
        return "pages/application/contestList";
    }

    @GetMapping("/pages/application/contestList/{id}")
    public String contestDetail(Model model,
                        ApplicationForm applicationForm,
                        @PathVariable("id") Long id,
                        @CurrentUser Account account){
        Contest contest = applicationService.getContestData(id);
        List<Contest> contestDetail = applicationService.getContestDetail(id);

        if(contest == null){
            model.addAttribute("altmsg", "정상적인 경로를 이용하세요.");
            model.addAttribute("locurl", "/pages/application/contestList");
            return "/message";
        }

        model.addAttribute("cpid",id);
        model.addAttribute("contestList" , contestDetail);
        model.addAttribute("mc", "application");
        model.addAttribute("pageTitle", "공모전");

        return "pages/application/contestDetail";
    }
    @GetMapping("/pages/application/contestList/contestDetail/contestRegister/{id}")
    public String contestReigster(Model model ,
                                ApplicationForm applicationForm ,
                                @PathVariable("id") Long id ,
                                @CurrentUser Account account) {
        Contest contest = applicationService.getContestData(id);
        List<Contest> contestDetail = applicationService.getContestDetail(id);

        if(contest == null){
            model.addAttribute("altmsg", "정상적인 경로를 이용하세요.");
            model.addAttribute("locurl", "/pages/application/contestList");
            return "/message";
        }

        model.addAttribute("cpid",id);
        model.addAttribute("contestList" , contestDetail);
        model.addAttribute("mc", "application");
        model.addAttribute("pageTitle", "공모전");

        return "pages/application/contestRegister";
    }


    @PostMapping(value = "/pages/application/contestList/contestRegister")
    public String contestSubmit(Model model ,
                                  ApplicationForm applicationForm ,
                                  Pageable pageable ,
                                  SearchForm searchForm ,
                                  @CurrentUser Account account ) throws Exception {

        applicationService.contestRegister(applicationForm);

        model.addAttribute("mc", "application");
        model.addAttribute("pageTitle", "공모전");
        Page<Contest> ContestPage = applicationService.getContestList(pageable, searchForm);
        model.addAttribute("contestList", ContestPage);


        return "/pages/application/contestList";
    }
}