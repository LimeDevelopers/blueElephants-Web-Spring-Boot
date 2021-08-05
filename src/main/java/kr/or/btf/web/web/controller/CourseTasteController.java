package kr.or.btf.web.web.controller;


import kr.or.btf.web.common.Constants;
import kr.or.btf.web.common.annotation.CurrentUser;
import kr.or.btf.web.domain.web.Account;
import kr.or.btf.web.domain.web.CourseTaste;
import kr.or.btf.web.services.web.CommonCodeService;
import kr.or.btf.web.services.web.CourseTasteService;
import kr.or.btf.web.utils.FileUtilHelper;
import kr.or.btf.web.web.form.CourseTasteForm;
import kr.or.btf.web.web.form.SearchForm;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class CourseTasteController extends BaseCont {

    private final CourseTasteService courseTasteService;
    private final CommonCodeService commonCodeService;

    @RequestMapping("/soulGod/courseTaste/list")
    public String list(Model model,
                       @PageableDefault Pageable pageable,
                       @CurrentUser Account account,
                       @ModelAttribute SearchForm searchForm) {

        model.addAttribute("form", searchForm);

        searchForm.setUserPid(account.getId());
        Page<CourseTaste> courseTastes = courseTasteService.list(pageable, searchForm);
        model.addAttribute("courseTastes", courseTastes);

        model.addAttribute("mc", "courseTastes");
        return "/soulGod/courseTaste/list";
    }

    @GetMapping("/soulGod/courseTaste/detail/{id}")
    public String detail(Model model,
                         @Value("${Globals.fileStoreUriPath}") String filePath,
                         @PathVariable(name = "id") Long id) {

        CourseTaste courseTaste = courseTasteService.load(id);
        model.addAttribute("form", courseTaste);

        model.addAttribute("filePath", filePath + '/' + Constants.FOLDERNAME_COURSETASTE);

        return "/soulGod/courseTaste/detail";
    }

    @GetMapping("/soulGod/courseTaste/register")
    public String register(Model model) {

        //Course course = courseService.load(id);
        CourseTaste courseTaste = new CourseTaste();
        model.addAttribute("form", courseTaste);

        model.addAttribute("accept", StringUtil.join(",", ArrayUtils.addAll(FileUtilHelper.imageExt)));

        return "/soulGod/courseTaste/register";
    }

    @PostMapping(value = "/soulGod/courseTaste/register", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String registerProc(Model model,
                               @ModelAttribute CourseTasteForm form,
                               @RequestParam(name = "attachImgFl", required = false) MultipartFile attachImgFl,
                               @CurrentUser Account account,
                               HttpServletResponse response,
                               RedirectAttributes redirect) throws Exception {

        form.setRegDtm(LocalDateTime.now());
        form.setRegPsId(account.getLoginId());
        form.setUpdDtm(LocalDateTime.now());
        form.setUpdPsId(account.getLoginId());
        form.setDelAt("N");

        boolean reult = false;
        reult = courseTasteService.insert(form, attachImgFl);

        //redirect.addAttribute("srchMnGbnCdPid", courseForm.getSrchMnGbnCdPid());
        return "redirect:/soulGod/courseTaste/list";
    }

    @GetMapping("/soulGod/courseTaste/modify/{id}")
    public String modify(Model model,
                         @PathVariable(name = "id") Long id) {

        CourseTaste courseTaste = courseTasteService.load(id);
        model.addAttribute("form", courseTaste);

        model.addAttribute("accept", StringUtil.join(",", ArrayUtils.addAll(FileUtilHelper.imageExt)));

        return "/soulGod/courseTaste/modify";
    }

    @PostMapping(value = "/soulGod/courseTaste/modify", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String modifyProc(Model model,
                             @ModelAttribute CourseTasteForm form,
                             @RequestParam(name = "attachImgFl", required = false) MultipartFile attachImgFl,
                             @CurrentUser Account account,
                             HttpServletResponse response,
                             RedirectAttributes redirect) {

        form.setUpdDtm(LocalDateTime.now());
        form.setUpdPsId(account.getLoginId());

        boolean result = courseTasteService.update(form, attachImgFl);
        //redirect.addAttribute("srchMnGbnCdPid", courseForm.getSrchMnGbnCdPid());

        return "redirect:/soulGod/courseTaste/detail/" + form.getId();
    }

    @PostMapping("/soulGod/courseTaste/delete")
    public String delete(Model model,
                         @ModelAttribute CourseTasteForm form,
                         @CurrentUser Account account) throws Exception {

        form.setUpdDtm(LocalDateTime.now());
        form.setUpdPsId(account.getLoginId());
        form.setDelAt("Y");
        courseTasteService.delete(form);

        return "redirect:/soulGod/courseTaste/list";
    }

    @RequestMapping("/courseTaste/video/{id}")
    public String video(Model model,
                        HttpServletRequest request,
                        @Value("${Globals.fileStoreUriPath}") String filePath,
                        @PathVariable(name = "id") Long id,
                        @CurrentUser Account account) throws Exception {

        CourseTaste courseTaste = courseTasteService.load(id);

        model.addAttribute("form", courseTaste);

        return "/popup/video";
    }

}
