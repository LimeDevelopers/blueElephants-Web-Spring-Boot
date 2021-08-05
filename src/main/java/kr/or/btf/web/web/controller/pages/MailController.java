package kr.or.btf.web.web.controller.pages;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
public class MailController {

    @RequestMapping("/pages/email/mailAuthForm")
    public String mailAuthForm(Model model,
                               @Value("${Globals.domain.full}") String domain) {

        model.addAttribute("name", "홍길동");
        model.addAttribute("link", "");
        model.addAttribute("domain", domain);
        model.addAttribute("mc", "mail");
        return "/pages/email/mailAuthForm";
    }

    @RequestMapping("/pages/email/pwChangeForm")
    public String list(Model model,
                       @Value("${Globals.domain.full}") String domain) {
        model.addAttribute("name", "홍길동");
        model.addAttribute("link", "");
        model.addAttribute("domain", domain);
        model.addAttribute("mc", "mail");
        return "/pages/email/pwChangeForm";
    }


}
