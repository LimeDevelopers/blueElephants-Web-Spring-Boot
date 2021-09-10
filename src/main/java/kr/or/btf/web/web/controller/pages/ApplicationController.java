package kr.or.btf.web.web.controller.pages;



import kr.or.btf.web.common.annotation.CurrentUser;
import kr.or.btf.web.domain.web.Account;
import kr.or.btf.web.domain.web.enums.AppRollType;
import kr.or.btf.web.services.web.ApplicationService;
import kr.or.btf.web.web.controller.BaseCont;
import kr.or.btf.web.web.form.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class  ApplicationController {
    private final ApplicationService applicationService;


    @PostMapping(value = "/pages/application/partnersRegister/register")
    public String PartnersRegister(@ModelAttribute ApplicationForm applicationForm ,
                                   @RequestParam("attachedFile") MultipartFile attachedFile ,
                                   @CurrentUser Account account,
                                   Error error ) throws Exception {
        if(account != null) {
            applicationForm.setMberPid(account.getId());
        }
        applicationService.partnersApplier(applicationForm , attachedFile);

        return "pages/application/partners";
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

    @GetMapping("/pages/application/zzcrew")
    public String zzcrew(Model model) {
        model.addAttribute("mc", "application");
        model.addAttribute("pageTitle", "지지크루");
        return "/pages/application/zzcrew";
    }

    @GetMapping("/pages/application/zzdeclareation")
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

}