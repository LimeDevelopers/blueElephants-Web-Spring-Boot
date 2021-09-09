package kr.or.btf.web.web.controller.pages;


import kr.or.btf.web.common.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
public class BlueElephantController {

    @RequestMapping("/pages/blueElephant/cyberBullying")
    public String intro(Model model) {
        model.addAttribute("mc", "blueElephant");
        model.addAttribute("pageTitle", "사이버폭력");
        return "/pages/blueElephant/cyberBullying";
    }

    @RequestMapping("/pages/blueElephant/blueElephant")
    public String character(Model model,
                            @Value("${Globals.fileStoreUriPath}") String filePath) {
        model.addAttribute("filePath", filePath + "/" + Constants.FOLDERNAME_IMAGES);
        model.addAttribute("mc", "blueElephant");
        model.addAttribute("pageTitle", "푸른코끼리");
        return "/pages/blueElephant/blueElephant";
    }

    @RequestMapping("/pages/blueElephant/character")
    public String friends(Model model) {
        model.addAttribute("mc", "blueElephant");
        model.addAttribute("pageTitle", "푸른캐릭터");
        return "/pages/blueElephant/character";
    }

    @RequestMapping("/pages/blueElephant/friends")
    public String activity(Model model) {
        model.addAttribute("mc", "blueElephant");
        model.addAttribute("pageTitle", "푸른친구들");
        return "/pages/blueElephant/friends";
    }

    @RequestMapping("/pages/blueElephant/schedule")
    public String schedule(Model model) {
        model.addAttribute("mc", "blueElephant");
        model.addAttribute("pageTitle", "푸코연혁");
        return "/pages/blueElephant/schedule";
    }

    @RequestMapping("/pages/blueElephant/location")
    public String location(Model model) {
        model.addAttribute("mc", "blueElephant");
        model.addAttribute("pageTitle", "푸코사무국");
        return "/pages/blueElephant/location";
    }


}
