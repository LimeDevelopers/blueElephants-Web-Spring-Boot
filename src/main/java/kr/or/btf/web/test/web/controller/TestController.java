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
    public String testExcel(Model model){
        Pageable pageable = null;
        model.addAttribute("noticeList", testService.getNewsListData(pageable));
        return "/pages/blueElephant/testPage";
    }

}
