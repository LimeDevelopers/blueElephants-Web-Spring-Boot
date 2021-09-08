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


}