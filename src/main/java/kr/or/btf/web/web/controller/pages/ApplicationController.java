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

import java.util.Optional;

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
                                   Error error ) throws Exception {
        if(account != null) {
            applicationForm.setMberPid(account.getId());
        }
        applicationService.partnersRegister(applicationForm , attachedFile);

        return "pages/application/partnersRegister";
    }

    @PostMapping(value = "/pages/application/zzcrew/zzcrewregister")
    public String ZzCrewRegister(@ModelAttribute ApplicationForm applicationForm ,
                                 @RequestParam("attachedFile") MultipartFile attachedFile ,
                                 @CurrentUser Account account ,
                                 Error error) throws Exception {
        if(account != null) {
            applicationForm.setMberPid(account.getId());
        }
        applicationService.zzcrewRegister(applicationForm , attachedFile);

        return "pages/application/zzcrew";
    }

    @PostMapping("/pages/application/zzdeclareRegister")
    public String zzdeclarationRegister(@ModelAttribute ApplicationForm applicationForm ,
                                 @RequestParam("attachedFile") MultipartFile attachedFile ,
                                 @CurrentUser Account account ,
                                 Error error) throws Exception {
        String Schedule = applicationForm.getYear() + applicationForm.getMonth() + applicationForm.getDay();
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
        if(account == null) {
            model.addAttribute("altmsg", "로그인 후 이용가능합니다.");
            model.addAttribute("locurl", "/login");
            return "/message";
        } else {
            if(!account.getMberDvTy().equals(UserRollType.INSTRUCTOR)){
                model.addAttribute("altmsg", "예방 강사만 이용 가능합니다.");
                model.addAttribute("locurl", "/");
                return "/message";
            } else {
                Page<Prevention> preventionPage = applicationService.getPreEduList(pageable, searchForm);
                model.addAttribute("preList", preventionPage);
            }
        }
        model.addAttribute("mc", "application");
        model.addAttribute("pageTitle", "예방교육");
        return "pages/application/preeducationList";
    }

    @PostMapping("/api/application/registerPreEdu")
    public String registerPreEdu(Model model,
                                 @CurrentUser Account account,
                                 PreventionMasterForm preventionMasterForm) {
        boolean result;
        if(account!=null && account.getMberDvTy().equals(UserRollType.INSTRUCTOR)) {
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
                model.addAttribute("altmsg", "예방 강사만 이용 가능합니다.");
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

    @GetMapping("/pages/application/preeducationDetail/{id}")
    public String preeducationDetail(Model model,
                               @PathVariable("id") Long id,
                               @CurrentUser Account account) {
        if(account == null) {
            model.addAttribute("altmsg", "로그인 후 이용가능합니다.");
            model.addAttribute("locurl", "/login");
            return "/message";
        } else {
            if(!UserRollType.INSTRUCTOR.equals(account.getMberDvTy())){
                model.addAttribute("altmsg", "예방 강사만 이용 가능합니다.");
                model.addAttribute("locurl", "/");
                return "/message";
            } else {
                PreventionMaster preventionMaster = applicationService.getPreEduMst(id,account.getId());
                Prevention prevention = applicationService.getPreEdu(id);
                model.addAttribute("prevention", prevention);
                if(preventionMaster==null){
                    if(prevention.getDelAt().equals("Y")){
                        model.addAttribute("altmsg", "삭제된 게시글입니다.");
                        model.addAttribute("locurl", "/pages/application/preeducationList");
                        return "/message";
                    }
                } else {
                    model.addAttribute("preventionMaster", preventionMaster);
                }
            }
        }
        model.addAttribute("pre_pid", id);
        model.addAttribute("mc", "application");
        model.addAttribute("pageTitle", "예방교육");
        return "pages/application/preeducationDetail";
    }
    //페이지 이동 컨트롤러
    @GetMapping("/pages/application/preeducation/{id}")
    public String PreEducation(Model model,
                               @PathVariable("id") Long id,
                               @CurrentUser Account account) {
        if(account == null) {
            model.addAttribute("altmsg", "로그인 후 이용가능합니다.");
            model.addAttribute("locurl", "/login");
            return "/message";
        } else {
            if(!UserRollType.INSTRUCTOR.equals(account.getMberDvTy())){
                model.addAttribute("altmsg", "예방 강사만 이용 가능합니다.");
                model.addAttribute("locurl", "/");
                return "/message";
            } else {
                PreventionMaster preventionMaster = applicationService.getPreEduMst(id,account.getId());
                if(preventionMaster==null){
                    Prevention prevention = applicationService.getPreEdu(id);
                    model.addAttribute("prevention", prevention);
                    if(prevention.getDelAt().equals("Y")){
                        model.addAttribute("altmsg", "삭제된 게시글입니다.");
                        model.addAttribute("locurl", "/pages/application/preeducationList");
                        return "/message";
                    }
                } else {
                    if(preventionMaster.getTempSave().equals("N")){
                        model.addAttribute("altmsg", "이미 신청완료된 학교입니다. \n 결과 승인은 평일 기준 3-5일 소요됩니다.");
                        model.addAttribute("locurl", "/pages/application/preeducationList");
                        return "/message";
                    }
                    model.addAttribute("preventionMst", preventionMaster);
                }
            }
        }
        model.addAttribute("pre_pid", id);
        model.addAttribute("mc", "application");
        model.addAttribute("pageTitle", "예방교육");
        return "pages/application/preeducationRegister";
    }

    @GetMapping("/pages/application/inseducation")
    public String InsEducation(Model model) {
        model.addAttribute("mc", "application");
        model.addAttribute("pageTitle", "강사교육");
        return "pages/application/inseducation";
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


    //등록페이지 이동 컨트롤러
    @GetMapping("/pages/application/zzcrew")
    public String zzcrew(Model model ,
                         @ModelAttribute ApplicationForm applicationForm) {
        if(AppRollType.CREW.getName().equals(applicationForm.getAppDvTy())) {

        }
        model.addAttribute("mc", "application");
        model.addAttribute("pageTitle", "지지크루");
        return "pages/application/zzcrew";
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
        model.addAttribute("pageTitle", "파트너스");
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
    @GetMapping("/pages/application/eventList/{id}")
    public String event(Model model,
                        ApplicationForm applicationForm,
                        @PathVariable("id") Long id,
                        @CurrentUser Account account){
        Event event = applicationService.getEventData(id);
        if(event == null){
            model.addAttribute("altmsg", "정상적인 경로를 이용하세요.");
            model.addAttribute("locurl", "/pages/application/eventList");
            return "/message";
        }
        model.addAttribute("epid",id);
        model.addAttribute("mc", "application");
        model.addAttribute("pageTitle", "행사");

        return "pages/application/eventRegister";
    }

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

    /*@GetMapping("/pages/application/eventList/{id}")
    public String contest(Model model,
                        ApplicationForm applicationForm,
                        @PathVariable("id") Long id,
                        @CurrentUser Account account){
        Event event = applicationService.getEventData(id);
        if(event == null){
            model.addAttribute("altmsg", "정상적인 경로를 이용하세요.");
            model.addAttribute("locurl", "/pages/application/contestList");
            return "/message";
        }
        model.addAttribute("cpid",id);
        model.addAttribute("mc", "application");
        model.addAttribute("pageTitle", "공모전");

        return "pages/application/eventRegister";
    }*/

    @GetMapping("/pages/application/zzcrewRegister")
    public String zzcrewRegister(Model model) {
        model.addAttribute("mc", "application");
        model.addAttribute("pageTitle", "지지크루");
        return "pages/application/zzcrewRegister";
    }

}