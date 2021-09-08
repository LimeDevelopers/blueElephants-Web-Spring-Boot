package kr.or.btf.web.web.controller.pages;



import kr.or.btf.web.domain.web.enums.AppRollType;
import kr.or.btf.web.web.form.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class  ApplicationController {

    @RequestMapping("/pages/application/partners")
    private String appPartners(Model model,
                               @ModelAttribute ApplicationForm applicationForm){
        // ROLL 타입이 파트너스일 경우
        if(AppRollType.PARTNERS.getName().equals(applicationForm.getAppDvTy())) {

        }
        return "/pages/application/partners";
    }

    @PostMapping("/pages/application/partnersRegister")
    private String appPartnersRegister(Model model,
                               @ModelAttribute ApplicationForm applicationForm){
        // ROLL 타입이 파트너스일 경우
        if(AppRollType.PARTNERS.getName().equals(applicationForm.getAppDvTy())) {

        }
        return "/pages/application/partnersRegister";
    }

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
    @GetMapping("/pages/application/partners")
    public String patners(Model model) {
        model.addAttribute("mc", "application");
        model.addAttribute("pageTitle", "파트너스");
        return "pages/application/partners";
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

}