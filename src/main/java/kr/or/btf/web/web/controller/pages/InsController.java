package kr.or.btf.web.web.controller.pages;

import kr.or.btf.web.common.annotation.CurrentUser;
import kr.or.btf.web.domain.web.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
public class InsController {
    @RequestMapping("/pages/myPage/insProfile")
    public String insProfile(Model model,
                             @CurrentUser Account account) {

        model.addAttribute("form", account);
        model.addAttribute("mc", "insMyPage");
        model.addAttribute("pageTitle", "계정정보");
        return "/pages/myPage/insProfile";
    }

}
