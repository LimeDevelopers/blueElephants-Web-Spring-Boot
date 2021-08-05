package kr.or.btf.web.web.controller.pages;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
public class EtcController {

    @RequestMapping("/pages/etc/privacy")
    public String privacy(Model model) {
        model.addAttribute("mc", "privacy");
        model.addAttribute("pageTitle", "개인정보처리방침");
        return "/pages/etc/privacy";
    }
    @RequestMapping("/pages/etc/terms")
    public String terms(Model model) {
        model.addAttribute("mc", "terms");
        model.addAttribute("pageTitle", "이용약관");
        return "/pages/etc/terms";
    }

    @RequestMapping("/pages/etc/mailDeny")
    public String mailDeny(Model model) {
        model.addAttribute("mc", "mailDeny");
        model.addAttribute("pageTitle", "이메일무단수집거부");
        return "/pages/etc/mailDeny";
    }

    @RequestMapping("/pages/etc/sitemap")
    public String sitemap(Model model) {
        model.addAttribute("mc", "sitemap");
        model.addAttribute("pageTitle", "사이트맵");
        return "/pages/etc/sitemap";
    }
}
