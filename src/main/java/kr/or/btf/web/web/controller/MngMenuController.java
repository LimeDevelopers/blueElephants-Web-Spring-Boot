package kr.or.btf.web.web.controller;


import kr.or.btf.web.common.annotation.CurrentUser;
import kr.or.btf.web.domain.web.Account;
import kr.or.btf.web.domain.web.CommonCode;
import kr.or.btf.web.domain.web.MngMenu;
import kr.or.btf.web.services.web.CommonCodeService;
import kr.or.btf.web.services.web.MngMenuService;
import kr.or.btf.web.web.form.MngMenuForm;
import kr.or.btf.web.web.form.SearchForm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class MngMenuController extends BaseCont{

    private final MngMenuService mngMenuService;
    private final CommonCodeService commonCodeService;

    @RequestMapping("/soulGod/mngMenu/list")
    public String list(Model model,
                       @ModelAttribute SearchForm searchForm,
                       @Value("${common.code.mnGbnCdPid}") Long mnGbnCdPid) {

        model.addAttribute("form", searchForm);

        List<CommonCode> menuGroupCodes = commonCodeService.menuListForUppCdPid(mnGbnCdPid);
        model.addAttribute("menuGroupCodes", menuGroupCodes);

        List<MngMenu> mngMenus = mngMenuService.list(searchForm);
        model.addAttribute("mngMenus", mngMenus);

        model.addAttribute("mc", "menu");
        return "/soulGod/mngMenu/list";
    }


    @ResponseBody
    @PostMapping("/api/soulGod/mngMenu/load")
    public MngMenu detail(Model model,
                          @RequestBody MngMenuForm form) {

        MngMenu load = mngMenuService.load(form);

        return load;
    }

    @PostMapping("/api/soulGod/mngMenu/save")
    public String registerProc(Model model,
                               @ModelAttribute MngMenuForm mngMenuForm,
                               @CurrentUser Account account,
                               HttpServletResponse response,
                               RedirectAttributes redirect) {

        mngMenuForm.setRegDtm(LocalDateTime.now());
        mngMenuForm.setUpdDtm(LocalDateTime.now());
        mngMenuForm.setRegPsId(account.getLoginId());
        mngMenuForm.setUpdPsId(account.getLoginId());
        mngMenuForm.setDelAt("N");

        if (mngMenuForm.getNewwinAt() == null) mngMenuForm.setNewwinAt("N");

        boolean result = false;

        if (mngMenuForm.getId() == null) {
            //form.setId(null);
            result = mngMenuService.insert(mngMenuForm);
        } else {
            result = mngMenuService.update(mngMenuForm);
        }

        redirect.addAttribute("srchMnGbnCdPid", mngMenuForm.getSrchMnGbnCdPid());
        return "redirect:/soulGod/mngMenu/list";
    }

    @PostMapping("/api/soulGod/mngMenu/delete")
    public String delete(Model model,
                         @RequestParam(name = "id") Long[] ids,
                         @CurrentUser Account account,
                         HttpServletResponse response,
                         RedirectAttributes redirect) throws Exception {

        List<MngMenuForm> menuFormList = new ArrayList<MngMenuForm>();
        MngMenuForm form = new MngMenuForm();
        for (Long id : ids) {
            form.setId(id);
            menuFormList.add(form);
        }
        mngMenuService.delete(menuFormList);

        //redirect.addAttribute("srch_menu_group_cd_id", menu.getSrch_menu_group_cd_id());
        return "redirect:/soulGod/mngMenu/list";
    }


    @ResponseBody
    @PostMapping("/api/soulGod/mngMenu/gnb")
    public List<MngMenu> gnb(Model model,
                             @CurrentUser Account account,
                             HttpServletResponse response,
                             RedirectAttributes redirect,
                             @Value("${common.code.mnGbnCdPid}") Long mnGbnCdPid) throws Exception {

        MngMenuForm form = new MngMenuForm();
        form.setMenuGroupCdPid(mnGbnCdPid);
        //form.setRegPsId(account.getId());
        //form.setUpdPsId(account.get());
        //form.setEmpno(account.getEmpno());
        List<MngMenu> gnbs = mngMenuService.gbnList(form);

        return gnbs;
    }

    @ResponseBody
    @PostMapping("/api/soulGod/mngMenu/lnb")
    public List<Map<String, Object>> lnb(Model model,
                                         @CurrentUser Account account,
                                         HttpServletResponse response,
                                         RedirectAttributes redirect,
                                         @Value("${common.code.mnGbnCdPid}") Long mnGbnCdPid) throws Exception {

        MngMenuForm form = new MngMenuForm();
        form.setUserPid(account.getId());
        List<MngMenu> lnbs = mngMenuService.lnbList(form);


        List<Map<String, Object>> lnbMaps = new ArrayList<>();
        Map<String, Object> lnbMap = null;

        List<CommonCode> menuGroupCodes = commonCodeService.menuListForUppCdPid(mnGbnCdPid);
        for (CommonCode menuGroupCode : menuGroupCodes) {
            lnbMap = new HashMap<>();
            lnbMap.put("menuNm", menuGroupCode.getCodeNm());
            List<Map<String, Object>> lnbSubMaps = new ArrayList<>();
            Map<String, Object> lnbSubMap = null;
            for (MngMenu lnb : lnbs) {
                if(menuGroupCode.getId().equals(lnb.getMenuGroupCdPid())){
                    lnbSubMap = new HashMap<>();
                    lnbSubMap.put("id", lnb.getId());
                    lnbSubMap.put("menuNm", lnb.getMenuNm());
                    lnbSubMap.put("menuUrl", lnb.getMenuUrl());

                    lnbSubMaps.add(lnbSubMap);
                }
            }

            if (lnbSubMaps.size() != 0) {
                lnbMap.put("lnbs", lnbSubMaps);

                lnbMaps.add(lnbMap);
            }
        }

        return lnbMaps;
    }

}
