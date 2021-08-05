package kr.or.btf.web.web.controller;


import kr.or.btf.web.common.Constants;
import kr.or.btf.web.common.annotation.CurrentUser;
import kr.or.btf.web.domain.web.Account;
import kr.or.btf.web.domain.web.Campaign;
import kr.or.btf.web.domain.web.CommonCode;
import kr.or.btf.web.domain.web.FileInfo;
import kr.or.btf.web.domain.web.enums.TableNmType;
import kr.or.btf.web.services.web.CampaignService;
import kr.or.btf.web.services.web.CommonCodeService;
import kr.or.btf.web.services.web.FileInfoService;
import kr.or.btf.web.utils.FileUtilHelper;
import kr.or.btf.web.web.form.CampaignForm;
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
public class CampaignController extends BaseCont {

    private final CampaignService campaignService;
    private final CommonCodeService commonCodeService;
    private final FileInfoService fileInfoService;

    @RequestMapping("/soulGod/campaign/list/{dvCodePid}")
    public String list(Model model,
                       @PageableDefault Pageable pageable,
                       @PathVariable(name = "dvCodePid") Long dvCodePid,
                       @ModelAttribute SearchForm searchForm) {

        model.addAttribute("form", searchForm);
        model.addAttribute("dvCodePid", dvCodePid);

        CampaignForm campaignForm = new CampaignForm();

        campaignForm.setDvCodePid(dvCodePid);

        Page<Campaign> campaigns = campaignService.list(pageable, searchForm, campaignForm);
        model.addAttribute("campaigns", campaigns);


        model.addAttribute("mc", "campaign");
        return "/soulGod/campaign/list";
    }

    @GetMapping("/soulGod/campaign/detail/{id}")
    public String detail(Model model,
                         @PathVariable(name = "id") Long id,
                         @Value("${Globals.fileStoreUriPath}") String filepath,
                         @Value("${common.code.crewGalleryCdPid}") Long crewCdPid) {

        Campaign campaign = campaignService.load(id);
        model.addAttribute("form", campaign);

        FileInfoForm fileInfoForm = new FileInfoForm();
        fileInfoForm.setDataPid(campaign.getId());
        fileInfoForm.setTableNm(TableNmType.TBL_CAMPAIGN.name());
        List<FileInfo> fileList = fileInfoService.list(fileInfoForm);
        model.addAttribute("fileList", fileList);

        CommonCode commonCode = commonCodeService.findById(campaign.getDvCodePid());

        model.addAttribute("dvCodeNm", commonCode.getCodeNm());

        model.addAttribute("crewCdPid", crewCdPid);
        model.addAttribute("filePath", filepath+"/"+ Constants.FOLDERNAME_CAMPAIGN);

        return "/soulGod/campaign/detail";
    }

    @GetMapping("/soulGod/campaign/register/{dvCodePid}")
    public String register(Model model,
                           @PathVariable(name = "dvCodePid") Long dvCodePid,
                           @Value("${common.code.campaignCode}") Long campaignCode,
                           @Value("${common.code.crewGalleryCdPid}") Long crewGalleryCdPid) {

        //Campaign campaign = campaignService.load(id);

        Campaign campaign = new Campaign();
        campaign.setDvCodePid(dvCodePid);
        model.addAttribute("form", campaign);

        CommonCode commonCode = commonCodeService.findById(dvCodePid);
        model.addAttribute("dvCodeNm", commonCode.getCodeNm());

        List<CommonCode> commonCodes = commonCodeService.getCommonCodeParent(campaignCode);
        model.addAttribute("commonCodes", commonCodes);

        model.addAttribute("crewGalleryCdPid", crewGalleryCdPid);
        model.addAttribute("accept", StringUtil.join(",", ArrayUtils.addAll(FileUtilHelper.imageExt)));
        model.addAttribute("allAccept", StringUtil.join(",", ArrayUtils.addAll(FileUtilHelper.getAllExt())));

        return "/soulGod/campaign/register";
    }

    @PostMapping(value = "/soulGod/campaign/register", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String registerProc(Model model,
                               @ModelAttribute CampaignForm campaignForm,
                               @RequestParam(name = "attachImgFl", required = false) MultipartFile attachImgFl,
                               @RequestParam(name = "attachedFile", required = false) MultipartFile[] attachedFile,
                               @CurrentUser Account account,
                               HttpServletResponse response,
                               RedirectAttributes redirect) throws Exception {

        campaignForm.setRegDtm(LocalDateTime.now());
        campaignForm.setRegPsId(account.getLoginId());
        campaignForm.setUpdDtm(LocalDateTime.now());
        campaignForm.setUpdPsId(account.getLoginId());
        campaignForm.setDelAt("N");

        Campaign campaign = campaignService.insert(campaignForm, attachImgFl, attachedFile);

        //redirect.addAttribute("srchMnGbnCdPid", campaignForm.getSrchMnGbnCdPid());
        return "redirect:/soulGod/campaign/list/" + campaignForm.getDvCodePid();
    }

    @GetMapping("/soulGod/campaign/modify/{id}")
    public String modify(Model model,
                         @PathVariable(name = "id") Long id,
                         @Value("${common.code.campaignCode}") Long campaignCode,
                         @Value("${common.code.crewGalleryCdPid}") Long crewCdPid) {

        Campaign campaign = campaignService.load(id);
        model.addAttribute("form", campaign);

        FileInfoForm fileInfoForm = new FileInfoForm();
        fileInfoForm.setDataPid(campaign.getId());
        fileInfoForm.setTableNm(TableNmType.TBL_CAMPAIGN.name());
        List<FileInfo> fileList = fileInfoService.list(fileInfoForm);
        model.addAttribute("fileList", fileList);

        CommonCode commonCode = commonCodeService.findById(campaign.getDvCodePid());
        model.addAttribute("dvCodeNm", commonCode.getCodeNm());

        List<CommonCode> commonCodes = commonCodeService.getCommonCodeParent(campaignCode);
        model.addAttribute("commonCodes", commonCodes);

        model.addAttribute("crewCdPid", crewCdPid);
        model.addAttribute("accept", StringUtil.join(",", ArrayUtils.addAll(FileUtilHelper.imageExt)));
        model.addAttribute("allAccept", StringUtil.join(",", ArrayUtils.addAll(FileUtilHelper.getAllExt())));

        return "/soulGod/campaign/modify";
    }

    @PostMapping(value = "/soulGod/campaign/modify", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String modifyProc(Model model,
                             @ModelAttribute CampaignForm campaignForm,
                             @RequestParam(name = "attachImgFl", required = false) MultipartFile attachImgFl,
                             @RequestParam(name = "attachedFile", required = false) MultipartFile[] attachedFile,
                             @CurrentUser Account account,
                             HttpServletResponse response,
                             RedirectAttributes redirect) {

        campaignForm.setUpdDtm(LocalDateTime.now());
        campaignForm.setUpdPsId(account.getLoginId());

        boolean result = campaignService.update(campaignForm, attachImgFl, attachedFile);
        //redirect.addAttribute("srchMnGbnCdPid", campaignForm.getSrchMnGbnCdPid());

        return "redirect:/soulGod/campaign/detail/" + campaignForm.getId();
    }

    @PostMapping("/soulGod/campaign/delete")
    public String delete(Model model,
                         @ModelAttribute CampaignForm campaignForm,
                         @CurrentUser Account account) throws Exception {

        campaignForm.setUpdDtm(LocalDateTime.now());
        campaignForm.setUpdPsId(account.getLoginId());
        campaignForm.setDelAt("Y");
        campaignService.delete(campaignForm);

        return "redirect:/soulGod/campaign/list/" + campaignForm.getDvCodePid();
    }

}
