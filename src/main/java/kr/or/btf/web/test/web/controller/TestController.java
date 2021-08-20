package kr.or.btf.web.test.web.controller;

import kr.or.btf.web.domain.web.BoardData;
import kr.or.btf.web.services.web.BoardDataService;
import kr.or.btf.web.test.web.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/test")
public class TestController {
    @Autowired
    TestService testService;


    @GetMapping(value = "/page")
    public String getMenuList(){
        return "/pages/blueElephant/testPage";
    }

    @GetMapping(value = "/excelDown")
    public String testExcel(Model model) {
        Pageable pageable = null;
        model.addAttribute("noticeList", testService.getNewsListData(pageable));
        return "/pages/blueElephant/testPage";
    }

    //디자인페이지 리다이렉션 start
    @GetMapping(value = "/_temp/_batchManagement")
    public String openBatchManagement(){
        return "_temp/_batchManagement";
    }

    @GetMapping(value = "/_temp/_intro")
    public String openIntro(){
        return "/_temp/_intro";
    }

    @RequestMapping(value = "/_temp/_character")
    public String openCharacter(){
        return "/_temp/_character";
    }

    @RequestMapping(value = "/_temp/_friends")
    public String openFriends(){
        return "/_temp/_friends";
    }

    @RequestMapping(value = "/_temp/_activity")
    public String openActivity(){
        return "/_temp/_activity";
    }

    @RequestMapping(value = "/_temp/_schedule")
    public String openSchedule(){
        return "/_temp/_schedule";
    }

    @RequestMapping(value = "/_temp/_location")
    public String openLocation(){
        return "/_temp/_location";
    }

    @RequestMapping(value = "/_temp/_idFind")
    public String openIdfind(){
        return "/_temp/_idFind";
    }

    @RequestMapping(value = "/_temp/_pwFind")
    public String openPwfind(){
        return " /_temp/_idFind";
    }
    //디자인페이지 리다이렉션 end
}
