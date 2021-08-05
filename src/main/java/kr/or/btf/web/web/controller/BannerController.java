package kr.or.btf.web.web.controller;

import kr.or.btf.web.common.Constants;
import kr.or.btf.web.common.annotation.CurrentUser;
import kr.or.btf.web.domain.web.Account;
import kr.or.btf.web.domain.web.Banner;
import kr.or.btf.web.domain.web.FileInfo;
import kr.or.btf.web.domain.web.enums.TableNmType;
import kr.or.btf.web.services.web.BannerService;
import kr.or.btf.web.services.web.FileInfoService;
import kr.or.btf.web.utils.FileUtilHelper;
import kr.or.btf.web.web.form.BannerForm;
import kr.or.btf.web.web.form.FileInfoForm;
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

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class BannerController extends BaseCont {

    private final BannerService bannerService;
    private final FileInfoService fileInfoService;

    @RequestMapping("/soulGod/banner/list")
    public String list(Model model,
                       @PageableDefault Pageable pageable,
                       @ModelAttribute SearchForm searchForm,
                       @ModelAttribute BannerForm bannerForm) {

        model.addAttribute("form", searchForm);

        Page<Banner> banners = bannerService.list(pageable, searchForm, bannerForm);
        model.addAttribute("banners", banners);
        model.addAttribute("banDvTy", bannerForm.getBanDvTy());

        model.addAttribute("mc", "inspection");
        return "/soulGod/banner/list";
    }

    @GetMapping("/soulGod/banner/detail/{id}")
    public String detail(Model model,
                         @PathVariable(name = "id") Long id,
                         @Value("${Globals.fileStoreUriPath}") String filepath) {

        Banner banner = bannerService.load(id);
        model.addAttribute("form", banner);

        model.addAttribute("banDvTy", banner.getBanDvTy());

        FileInfoForm fileInfoForm = new FileInfoForm();
        fileInfoForm.setDataPid(id);
        fileInfoForm.setTableNm(TableNmType.TBL_BANNER.name());
        List<FileInfo> fileList = fileInfoService.list(fileInfoForm);
        model.addAttribute("fileList", fileList);

        model.addAttribute("filePath", filepath+"/"+ Constants.FOLDERNAME_BANNER);

        return "/soulGod/banner/detail";
    }

    @GetMapping("/soulGod/banner/register")
    public String register(Model model,
                           @ModelAttribute BannerForm bannerForm) {

        model.addAttribute("form", bannerForm);
        model.addAttribute("imageAccept", StringUtil.join(",", ArrayUtils.addAll(FileUtilHelper.imageExt)));

        return "/soulGod/banner/register";
    }

    @PostMapping(value = "/soulGod/banner/register", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String registerProc(Model model,
                               @ModelAttribute BannerForm bannerForm,
                               @RequestParam(name = "attachImgFl", required = false) MultipartFile attachImgFl,
                               @RequestParam(name = "attachMoImgFl", required = false) MultipartFile attachMoImgFl,
                               @CurrentUser Account account,
                               HttpServletResponse response,
                               RedirectAttributes redirect) throws Exception {

        bannerForm.setRegDtm(LocalDateTime.now());
        bannerForm.setRegPsId(account.getLoginId());
        bannerForm.setUpdDtm(LocalDateTime.now());
        bannerForm.setUpdPsId(account.getLoginId());
        bannerForm.setDelAt("N");

        Banner banner = new Banner();
        if (bannerForm.getId() == null) {
            banner = bannerService.insert(bannerForm, attachImgFl, attachMoImgFl);
        } else {
            bannerService.update(bannerForm, attachImgFl, attachMoImgFl);
        }


        return "redirect:/soulGod/banner/list";
    }

    @GetMapping("/soulGod/banner/modify/{id}")
    public String modify(Model model,
                         @PathVariable(name = "id") Long id) {

        Banner banner = bannerService.load(id);
        model.addAttribute("form", banner);
        model.addAttribute("banDvTy", banner.getBanDvTy());

        FileInfoForm fileInfoForm = new FileInfoForm();
        fileInfoForm.setDataPid(id);
        fileInfoForm.setTableNm(TableNmType.TBL_BANNER.name());
        List<FileInfo> fileList = fileInfoService.list(fileInfoForm);
        model.addAttribute("fileList", fileList);

        model.addAttribute("imageAccept", StringUtil.join(",", ArrayUtils.addAll(FileUtilHelper.imageExt)));

        return "/soulGod/banner/modify";
    }

    @PostMapping("/soulGod/banner/delete")
    public String delete(Model model,
                         @ModelAttribute BannerForm bannerForm,
                         @CurrentUser Account account) throws Exception {

        bannerForm.setUpdDtm(LocalDateTime.now());
        bannerForm.setUpdPsId(account.getLoginId());
        bannerForm.setDelAt("Y");
        bannerService.delete(bannerForm);

        return "redirect:/soulGod/banner/list";
    }
}
