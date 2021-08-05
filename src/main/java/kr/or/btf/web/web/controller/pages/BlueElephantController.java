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

    @RequestMapping("/pages/blueElephant/intro")
    public String intro(Model model) {
        model.addAttribute("mc", "blueElephant");
        model.addAttribute("pageTitle", "푸른코끼리");
        return "/pages/blueElephant/intro";
    }

    @RequestMapping("/pages/blueElephant/character")
    public String character(Model model,
                            @Value("${Globals.fileStoreUriPath}") String filePath) {
        model.addAttribute("filePath", filePath + "/" + Constants.FOLDERNAME_IMAGES);
        model.addAttribute("mc", "blueElephant");
        model.addAttribute("pageTitle", "푸코캐릭터");
        return "/pages/blueElephant/character";
    }

    @RequestMapping("/pages/blueElephant/friends")
    public String friends(Model model) {
        model.addAttribute("mc", "blueElephant");
        model.addAttribute("pageTitle", "푸코친구들");
        return "/pages/blueElephant/friends";
    }

    @RequestMapping("/pages/blueElephant/activity")
    public String activity(Model model) {
        model.addAttribute("mc", "blueElephant");
        model.addAttribute("pageTitle", "푸코활동");
        return "/pages/blueElephant/activity";
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
