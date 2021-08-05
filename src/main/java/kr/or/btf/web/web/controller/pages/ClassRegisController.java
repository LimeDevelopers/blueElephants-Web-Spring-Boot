package kr.or.btf.web.web.controller.pages;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
public class ClassRegisController {

    @RequestMapping("/pages/classRegis/intro")
    public String intro(Model model) {


        model.addAttribute("mc", "classRegis");
        return "/pages/classRegis/intro";
    }


}
