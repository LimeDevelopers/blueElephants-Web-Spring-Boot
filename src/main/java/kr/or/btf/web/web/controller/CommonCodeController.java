package kr.or.btf.web.web.controller;

import kr.or.btf.web.common.annotation.CurrentUser;
import kr.or.btf.web.domain.web.Account;
import kr.or.btf.web.domain.web.CommonCode;
import kr.or.btf.web.services.web.CommonCodeService;
import kr.or.btf.web.web.form.CommonCodeForm;
import kr.or.btf.web.web.form.SearchForm;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class CommonCodeController extends BaseCont {

    private final CommonCodeService commonCodeService;

    @GetMapping("/soulGod/commonCode/list")
    public String list(Model model,
                          @ModelAttribute SearchForm searchForm,
                          @PageableDefault Pageable pageable) {

        return "/soulGod/commonCode/list";
    }


    @ResponseBody
    @PostMapping("/api/soulGod/commonCode/save")
    public String save(Model model,
                                   @CurrentUser Account account,
                                   @RequestBody CommonCodeForm commonCodeForm,
                                   RedirectAttributes redirectAttributes) {

        commonCodeForm.setRegPsId(account.getLoginId());
        commonCodeForm.setRegDtm(LocalDateTime.now());
        commonCodeForm.setUpdPsId(account.getLoginId());
        commonCodeForm.setUpdDtm(LocalDateTime.now());
        commonCodeForm.setDelAt("N");

        boolean result = commonCodeForm.getId() == 0
                ? commonCodeService.insert(commonCodeForm) : commonCodeService.update(commonCodeForm);

        String msg = "fail";
        if (result) {
            msg = "ok";
        } else {
            msg = "fail";
        }
        return msg;
    }

    @ResponseBody
    @PostMapping("/api/soulGod/commonCode/delete")
    public String delete(Model model,
                                @CurrentUser Account account,
                                @RequestBody CommonCodeForm commonCodeForm,
                                RedirectAttributes redirectAttributes) {

        if (commonCodeForm.getId() == null) {
            redirectAttributes.addFlashAttribute("message", "잘못된 접근입니다.");
            return "redirect:" + "/soulGod/systemCode/list";
        }

        commonCodeForm.setUpdPsId(account.getLoginId());
        commonCodeForm.setUpdDtm(LocalDateTime.now());
        commonCodeForm.setDelAt("Y");

        boolean result = commonCodeService.delete(commonCodeForm);

        String msg = "fail";
        if (result) {
            msg = "ok";
        } else {
            msg = "fail";
        }
        return msg;
    }

    @ResponseBody
    @PostMapping("/api/commonCode/listForUppCdPid")
    public List<CommonCode> listForUppCdPid(Model model,
                           @RequestBody CommonCodeForm commonCodeForm) {
        List<CommonCode> list = commonCodeService.menuListForUppCdPid(commonCodeForm.getPrntCodePid());
        return list;
    }

    @ResponseBody
    @PostMapping("/api/soulGod/commonCode/load")
    public CommonCode load(Model model,
                                     @RequestBody CommonCodeForm commonCodeForm) {
        CommonCode byId = commonCodeService.findById(commonCodeForm.getId());
        return byId;
    }



    @ResponseBody
    @PostMapping("/api/commonCode/tree")
    public List<Map<String, Object>> tree(Model model,
                                                    @ModelAttribute CommonCode commonCode,
                                                    @CurrentUser Account account) throws Exception {

        List<Map<String, Object>> retList = new ArrayList<>();
        Map<String, Object> tmp = null;
        List<CommonCode> commonCodeList = commonCodeService.findAll();

        for (CommonCode commonCodeVo2 : commonCodeList) {
            if (commonCodeVo2.getPrntCodePid() == null) {
                tmp = new HashMap<>();

                tmp.put("text", commonCodeVo2.getCodeNm());
                tmp.put("id", commonCodeVo2.getId());
                tmp.put("items", getMakeTree(commonCodeVo2.getId(), commonCodeList));

                retList.add(tmp);
            }
        }
        return retList;
    }

    private List<Map<String, Object>> getMakeTree(Long uppCdPid, List<CommonCode> subList) {

        List<Map<String, Object>> retList = new ArrayList<>();
        Map<String, Object> tmp = null;
        try {

            for (CommonCode commonCodeVo2 : subList) {
                if (commonCodeVo2.getPrntCodePid() != null && commonCodeVo2.getPrntCodePid().equals(uppCdPid)) {
                    tmp = new HashMap<>();

                    tmp.put("text", commonCodeVo2.getCodeNm());
                    tmp.put("id", commonCodeVo2.getId());
                    tmp.put("items", getMakeTree(commonCodeVo2.getId(), subList));

                    retList.add(tmp);
                }
            }
        } catch (Exception e) {
            // LOGGER.error(e.getMessage());
            // LOGGER.error(e.getStackTrace());
        }

        return retList;
    }
}
