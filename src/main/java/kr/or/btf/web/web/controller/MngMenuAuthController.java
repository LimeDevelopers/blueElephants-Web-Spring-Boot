package kr.or.btf.web.web.controller;


import kr.or.btf.web.common.annotation.CurrentUser;
import kr.or.btf.web.domain.web.Account;
import kr.or.btf.web.domain.web.CommonCode;
import kr.or.btf.web.domain.web.MngMenu;
import kr.or.btf.web.domain.web.MngMenuAuth;
import kr.or.btf.web.services.web.CommonCodeService;
import kr.or.btf.web.services.web.MemberService;
import kr.or.btf.web.services.web.MngMenuAuthService;
import kr.or.btf.web.services.web.MngMenuService;
import kr.or.btf.web.web.form.MngMenuAuthForm;
import kr.or.btf.web.web.form.MngMenuForm;
import kr.or.btf.web.web.form.SearchForm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MngMenuAuthController extends BaseCont{

    private final MngMenuService mngMenuService;
    private final MngMenuAuthService mngMenuAuthService;
    private final MemberService memberService;
    private final CommonCodeService commonCodeService;

    @RequestMapping("/soulGod/mngMenuAuth/list")
    public String list(Model model,
                       @PageableDefault Pageable pageable,
                       @ModelAttribute SearchForm searchForm,
                       @ModelAttribute MngMenuForm mngMenuAuthForm,
                       @Value("${common.code.mnGbnCdPid}") Long mnGbnCdPid) {

        model.addAttribute("form", mngMenuAuthForm);

        List<Account> members = memberService.listByAdminUser(searchForm);
        model.addAttribute("members", members);

        List<MngMenu> mngMenus = mngMenuService.list(mngMenuAuthForm);
        model.addAttribute("mngMenus", mngMenus);

        List<CommonCode> menuGroupCodes = commonCodeService.menuListForUppCdPid(mnGbnCdPid);
        model.addAttribute("menuGroupCodes", menuGroupCodes);

        model.addAttribute("mc", "menu");
        return "/soulGod/mngMenuAuth/list";
    }


    @ResponseBody
    @PostMapping("/api/soulGod/mngMenuAuth/authList")
    public List<MngMenuAuth> authList(Model model,
                                      @RequestBody MngMenuAuthForm mngMenuAuthForm) {

        List<MngMenuAuth> list = mngMenuAuthService.list(mngMenuAuthForm);

        return list;
    }

    @ResponseBody
    @RequestMapping("/api/soulGod/mngMenuAuth/authSave")
    public String authSave(Model model,
                           HttpServletResponse response,
                           @RequestBody MngMenuAuthForm mngMenuAuthForm,
                           @CurrentUser Account account) {

        boolean result = false;
        if (mngMenuAuthForm.getConfmAt().equalsIgnoreCase("Y")) {
            result = mngMenuAuthService.insert(mngMenuAuthForm);
        } else {
            result = mngMenuAuthService.delete(mngMenuAuthForm);
        }

        String msg = "";
        if (result) {
            msg = "ok";
            response.setStatus(200);
        } else {
            msg = "fail";
            response.setStatus(401);
        }

        response.setContentType("text/html");
        response.setCharacterEncoding("utf-8");

        return msg;
    }

    @ResponseBody
    @RequestMapping("/api/soulGod/mngMenuAuth/authAllSave")
    public String authAllSave(Model model,
                                   @RequestBody MngMenuAuthForm mngMenuAuthForm,
                                   HttpServletResponse response,
                                   @CurrentUser Account account) {

        boolean result = false;

        result = mngMenuAuthService.InsertToAll(mngMenuAuthForm);

        String msg = "fail";
        if (result) {
            msg = "ok";
            response.setStatus(200);
        } else {
            msg = "fail";
            response.setStatus(401);
        }

        response.setContentType("text/html");
        response.setCharacterEncoding("utf-8");
        //model.addAttribute("msg", msg);

        return msg;
    }
    @ResponseBody
    @RequestMapping("/api/soulGod/mngMenuAuth/authAllDelete")
    public String authAllDelete(Model model,
                                   @RequestBody MngMenuAuthForm mngMenuAuthForm,
                                   HttpServletResponse response,
                                   @CurrentUser Account account) {

        boolean result = false;

        result = mngMenuAuthService.deleteAll(mngMenuAuthForm);

        String msg = "fail";
        if (result) {
            msg = "ok";
            response.setStatus(200);
        } else {
            msg = "fail";
            response.setStatus(401);
        }

        response.setContentType("text/html");
        response.setCharacterEncoding("utf-8");
        //model.addAttribute("msg", msg);

        return msg;
    }
}
