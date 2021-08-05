package kr.or.btf.web.web.controller.pages;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
public class SubLayoutController {

    @RequestMapping("/pages/layout/subLayout")
    public String list(Model model) {


        model.addAttribute("mc", "subLayout");
        return "/pages/layout/subLayout";
    }


}
