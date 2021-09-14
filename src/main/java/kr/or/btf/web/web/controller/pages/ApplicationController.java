package kr.or.btf.web.web.controller.pages;



import kr.or.btf.web.common.annotation.CurrentUser;
import kr.or.btf.web.domain.web.Account;
import kr.or.btf.web.domain.web.enums.AppRollType;
import kr.or.btf.web.services.web.ApplicationService;
import kr.or.btf.web.web.form.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequiredArgsConstructor
public class  ApplicationController {
    private final ApplicationService applicationService;

    //등록 수행 컨트롤러
    @PostMapping(value = "/pages/application/partners/partnersregister")
    public String PartnersRegister(@ModelAttribute ApplicationForm applicationForm ,
                                   @RequestParam("attachedFile") MultipartFile attachedFile ,
                                   @CurrentUser Account account,
                                   Model model,
                                   Error error ) throws Exception {
        if(account != null) {
            applicationForm.setMberPid(account.getId());
        }
        model.addAttribute("mc", "application");
        model.addAttribute("pageTitle", "파트너스");

        applicationService.partnersRegister(applicationForm , attachedFile);

        return "pages/application/partnersRegister";
    }

    @PostMapping(value = "/pages/application/zzcrew/zzcrewregister")
    public String ZzCrewRegister(@ModelAttribute ApplicationForm applicationForm ,
                                 @RequestParam("attachedFile") MultipartFile attachedFile ,
                                 @CurrentUser Account account ,
                                 Model model,
                                 Error error) throws Exception {
        if(account != null) {
            applicationForm.setMberPid(account.getId());
        }

        model.addAttribute("mc", "application");
        model.addAttribute("pageTitle", "파트너스");
        applicationService.zzcrewRegister(applicationForm , attachedFile);

        return "pages/application/zzcrew";
    }

    @PostMapping("/pages/application/zzdeclareRegister")
    public String zzdeclareRegister(@ModelAttribute ApplicationForm applicationForm ,
                                 @RequestParam("attachedFile") MultipartFile attachedFile ,
                                 @CurrentUser Account account ,
                                 Model model,
                                 Error error) throws Exception {

        model.addAttribute("mc", "application");
        model.addAttribute("pageTitle", "파트너스");

        //String Schedule = applicationForm.getYear() + applicationForm.getMonth() + applicationForm.getDay();
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



    //페이지 이동 컨트롤러
    @GetMapping("/pages/application/preeducation")
    public String PreEducation(Model model) {
        model.addAttribute("mc", "application");
        model.addAttribute("pageTitle", "예방교육");
        return "pages/application/preeducation";
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
}