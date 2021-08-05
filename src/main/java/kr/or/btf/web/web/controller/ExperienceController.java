package kr.or.btf.web.web.controller;


import kr.or.btf.web.common.Constants;
import kr.or.btf.web.common.annotation.CurrentUser;
import kr.or.btf.web.domain.web.Account;
import kr.or.btf.web.domain.web.CommonCode;
import kr.or.btf.web.domain.web.Experience;
import kr.or.btf.web.domain.web.FileInfo;
import kr.or.btf.web.domain.web.enums.TableNmType;
import kr.or.btf.web.services.web.CommonCodeService;
import kr.or.btf.web.services.web.ExperienceService;
import kr.or.btf.web.services.web.ExperienceTargetService;
import kr.or.btf.web.services.web.FileInfoService;
import kr.or.btf.web.utils.FileUtilHelper;
import kr.or.btf.web.web.form.ExperienceForm;
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
public class ExperienceController extends BaseCont {

    private final ExperienceService experienceService;
    private final ExperienceTargetService experienceTargetService;
    private final CommonCodeService commonCodeService;
    private final FileInfoService fileInfoService;

    @RequestMapping("/soulGod/experience/list/{dvCodePid}")
    public String list(Model model,
                       @PageableDefault Pageable pageable,
                       @PathVariable(name = "dvCodePid") Long dvCodePid,
                       @ModelAttribute SearchForm searchForm) {

        model.addAttribute("form", searchForm);
        model.addAttribute("dvCodePid", dvCodePid);

        ExperienceForm experienceForm = new ExperienceForm();

        experienceForm.setDvCodePid(dvCodePid);

        Page<Experience> experiences = experienceService.list(pageable, searchForm, experienceForm);
        model.addAttribute("experiences", experiences);


        model.addAttribute("mc", "experience");
        return "/soulGod/experience/list";
    }

    @GetMapping("/soulGod/experience/detail/{id}")
    public String detail(Model model,
                         @Value("${Globals.fileStoreUriPath}") String filepath,
                         @PathVariable(name = "id") Long id,
                         @Value("${common.code.experienceVideoPid}") Long experienceVideoPid,
                         @Value("${common.code.experienceVoicePid}") Long experienceVoicePid,
                         @Value("${common.code.experienceMessagePid}") Long experienceMessagePid) {

        Experience experience = experienceService.load(id);
        model.addAttribute("form", experience);

        CommonCode dvCodeNm = commonCodeService.findById(experience.getDvCodePid());
        model.addAttribute("dvCodeNm", dvCodeNm.getCodeNm());

        FileInfoForm fileInfoForm = new FileInfoForm();
        fileInfoForm.setDataPid(id);
        fileInfoForm.setTableNm(TableNmType.TBL_EXPERIENCE.name());
        List<FileInfo> fileList = fileInfoService.list(fileInfoForm);
        model.addAttribute("fileList", fileList);

        model.addAttribute("videoPid", experienceVideoPid);
        model.addAttribute("voicePid", experienceVoicePid);
        model.addAttribute("messagePid", experienceMessagePid);

        model.addAttribute("filePath", filepath+"/"+ Constants.FOLDERNAME_EXPERIENCE);

        return "/soulGod/experience/detail";
    }

    @GetMapping("/soulGod/experience/register/{dvCodePid}")
    public String register(Model model,
                           @PathVariable(name = "dvCodePid") Long dvCodePid,
                           @Value("${common.code.experienceCode}") Long experienceCode,
                           @Value("${common.code.experienceVideoPid}") Long experienceVideoPid,
                           @Value("${common.code.experienceVoicePid}") Long experienceVoicePid,
                           @Value("${common.code.experienceMessagePid}") Long experienceMessagePid) {

        model.addAttribute("dvCodePid", dvCodePid);

        CommonCode codeLoad = commonCodeService.findById(dvCodePid);
        model.addAttribute("codeLoad", codeLoad);

        //Experience experience = experienceService.load(id);
        Experience experience = new Experience();
        experience.setDvCodePid(dvCodePid);
        model.addAttribute("form", experience);

        List<CommonCode> commonCodes = commonCodeService.getCommonCodeParent(experienceCode);
        model.addAttribute("commonCodes", commonCodes);

        model.addAttribute("imageAccept", StringUtil.join(",", ArrayUtils.addAll(FileUtilHelper.imageExt)));
        model.addAttribute("audioAccept", StringUtil.join(",", ArrayUtils.addAll(FileUtilHelper.audioExt)));

        model.addAttribute("videoPid", experienceVideoPid);
        model.addAttribute("voicePid", experienceVoicePid);
        model.addAttribute("messagePid", experienceMessagePid);

        return "/soulGod/experience/register";
    }

    @PostMapping(value = "/soulGod/experience/register", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String registerProc(Model model,
                               @ModelAttribute ExperienceForm experienceForm,
                               @RequestParam(name = "attachImgFl", required = false) MultipartFile attachImgFl,
                               @RequestParam(name = "attachedFile", required = false) MultipartFile attachedFile,
                               @CurrentUser Account account,
                               @Value("${common.code.experienceVoicePid}") Long experienceVoicePid,
                               HttpServletResponse response,
                               RedirectAttributes redirect) throws Exception {

        if (experienceForm.getDvCodePid() == experienceVoicePid) {
            experienceForm.setCntntsUrl("");
        }
        experienceForm.setRegDtm(LocalDateTime.now());
        experienceForm.setRegPsId(account.getLoginId());
        experienceForm.setUpdDtm(LocalDateTime.now());
        experienceForm.setUpdPsId(account.getLoginId());
        experienceForm.setDelAt("N");

        Experience experience = experienceService.insert(experienceForm, attachImgFl, attachedFile);

        //redirect.addAttribute("srchMnGbnCdPid", experienceForm.getSrchMnGbnCdPid());
        return "redirect:/soulGod/experience/list/" + experienceForm.getDvCodePid();
    }

    @GetMapping("/soulGod/experience/modify/{id}")
    public String modify(Model model,
                         @PathVariable(name = "id") Long id,
                         @Value("${common.code.experienceCode}") Long experienceCode,
                         @Value("${common.code.experienceVideoPid}") Long experienceVideoPid,
                         @Value("${common.code.experienceVoicePid}") Long experienceVoicePid,
                         @Value("${common.code.experienceMessagePid}") Long experienceMessagePid) {

        Experience experience = experienceService.load(id);
        model.addAttribute("form", experience);

        CommonCode codeLoad = commonCodeService.findById(experience.getDvCodePid());
        model.addAttribute("codeLoad", codeLoad);

        List<CommonCode> commonCodes = commonCodeService.getCommonCodeParent(experienceCode);
        model.addAttribute("commonCodes", commonCodes);

        FileInfoForm fileInfoForm = new FileInfoForm();
        fileInfoForm.setDataPid(id);
        fileInfoForm.setTableNm(TableNmType.TBL_EXPERIENCE.name());
        List<FileInfo> fileList = fileInfoService.list(fileInfoForm);
        model.addAttribute("fileList", fileList);

        model.addAttribute("imageAccept", StringUtil.join(",", ArrayUtils.addAll(FileUtilHelper.imageExt)));
        model.addAttribute("audioAccept", StringUtil.join(",", ArrayUtils.addAll(FileUtilHelper.audioExt)));

        model.addAttribute("videoPid", experienceVideoPid);
        model.addAttribute("voicePid", experienceVoicePid);
        model.addAttribute("messagePid", experienceMessagePid);

        return "/soulGod/experience/modify";
    }

    @PostMapping(value = "/soulGod/experience/modify", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String modifyProc(Model model,
                             @ModelAttribute ExperienceForm experienceForm,
                             @RequestParam(name = "attachImgFl", required = false) MultipartFile attachImgFl,
                             @RequestParam(name = "attachedFile", required = false) MultipartFile attachedFile,
                             @CurrentUser Account account,
                             HttpServletResponse response,
                             RedirectAttributes redirect) {

        experienceForm.setUpdDtm(LocalDateTime.now());
        experienceForm.setUpdPsId(account.getLoginId());

        boolean result = experienceService.update(experienceForm, attachImgFl, attachedFile);
        //redirect.addAttribute("srchMnGbnCdPid", experienceForm.getSrchMnGbnCdPid());

        return "redirect:/soulGod/experience/detail/" + experienceForm.getId();
    }

    @PostMapping("/soulGod/experience/delete")
    public String delete(Model model,
                         @ModelAttribute ExperienceForm experienceForm,
                         @CurrentUser Account account,
                         @Value("${common.code.experienceVoicePid}") Long experienceVoicePid) throws Exception {

        experienceForm.setUpdDtm(LocalDateTime.now());
        experienceForm.setUpdPsId(account.getLoginId());
        experienceForm.setDelAt("Y");
        experienceService.delete(experienceForm);

        return "redirect:/soulGod/experience/list/"+experienceVoicePid;
    }

}
