package kr.or.btf.web.web.controller;

import kr.or.btf.web.common.Constants;
import kr.or.btf.web.common.annotation.CurrentUser;
import kr.or.btf.web.domain.web.*;
import kr.or.btf.web.domain.web.enums.TableNmType;
import kr.or.btf.web.services.web.CommonCodeService;
import kr.or.btf.web.services.web.FileInfoService;
import kr.or.btf.web.services.web.PostscriptImageService;
import kr.or.btf.web.services.web.PostscriptService;
import kr.or.btf.web.utils.FileUtilHelper;
import kr.or.btf.web.web.form.FileInfoForm;
import kr.or.btf.web.web.form.PostscriptForm;
import kr.or.btf.web.web.form.PostscriptImageForm;
import kr.or.btf.web.web.form.SearchForm;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class PostscriptController {
    private final PostscriptService postscriptService;
    private final PostscriptImageService postscriptImageService;
    private final CommonCodeService commonCodeService;
    private final FileInfoService fileInfoService;



    @RequestMapping("/soulGod/postscript/list")
    public String list(Model model,
                       @PageableDefault Pageable pageable,
                       @Value("${common.code.srtCodePid}") Long srtCodePid,
                       @CurrentUser Account account,
                       @ModelAttribute SearchForm searchForm) {

        model.addAttribute("form", searchForm);

        searchForm.setUserPid(account.getId());
        Page<Postscript> postscripts = postscriptService.list(pageable,searchForm);
        model.addAttribute("postscripts", postscripts);

        List<CommonCode> srtCodes = commonCodeService.getCommonCodeParent(srtCodePid);
        model.addAttribute("srtCodes", srtCodes);

        model.addAttribute("mc", "course");
        return "/soulGod/postscript/list";
    }

    //관리자 디테일
    @GetMapping("/soulGod/postscript/detail/{id}")
    public String Detail(Model model,
                         @PathVariable(name = "id") Long id,
                         @Value("${Globals.fileStoreUriPath}") String filepath) {

        PostscriptForm postscriptForm = new PostscriptForm();
        postscriptForm.setId(id);
        Postscript postscript = postscriptService.loadByForm(postscriptForm);
        model.addAttribute("form", postscript);

        CommonCode byId = commonCodeService.findById(postscript.getSrtCodePid());   //중분류
        CommonCode byId1 = commonCodeService.findById(byId.getPrntCodePid());       //대분류
        model.addAttribute("codeNm", byId1.getCodeNm());

        FileInfoForm fileInfoForm = new FileInfoForm();
        fileInfoForm.setDataPid(postscript.getId());
        fileInfoForm.setTableNm(TableNmType.TBL_POSTSCRIPT.name());
        List<FileInfo> fileList = fileInfoService.list(fileInfoForm);
        model.addAttribute("fileList", fileList);

        model.addAttribute("filePath", filepath+"/"+ Constants.FOLDERNAME_POSTSCRIPT);

        PostscriptImageForm postscriptImageForm = new PostscriptImageForm();
        postscriptImageForm.setPostscriptPid(postscript.getId());
        List<PostscriptImage> postscriptImageList = postscriptImageService.list(postscriptImageForm);
        model.addAttribute("postscriptImageList", postscriptImageList);

        return "/soulGod/postscript/detail";
    }

    @PostMapping("/soulGod/postscript/delete")
    public String delete(Model model,
                         @ModelAttribute PostscriptForm postscriptForm,
                         @CurrentUser Account account) throws Exception {

        postscriptForm.setUpdDtm(LocalDateTime.now());
        postscriptForm.setUpdPsId(account.getLoginId());
        postscriptForm.setDelAt("Y");
        postscriptService.delete(postscriptForm);

        return "redirect:/soulGod/postscript/list";
    }

    @GetMapping("/soulGod/postscript/modify/{id}")
    public String modify(Model model,
                         @PathVariable(name = "id") Long id,
                         @Value("${common.code.srtCodePid}") Long srtCodePid) {

        PostscriptForm postscriptForm = new PostscriptForm();
        postscriptForm.setId(id);
        Postscript postscript = postscriptService.loadByForm(postscriptForm);
        model.addAttribute("form", postscript);

        PostscriptImageForm postscriptImageForm = new PostscriptImageForm();
        postscriptImageForm.setPostscriptPid(postscript.getId());

        List<PostscriptImage> postscriptImageList = postscriptImageService.list(postscriptImageForm);
        model.addAttribute("postscriptImageList", postscriptImageList);

        List<CommonCode> srtCodes = commonCodeService.getCommonCodeParent(srtCodePid);
        model.addAttribute("srtCodes", srtCodes);

        model.addAttribute("accept", StringUtil.join(",", ArrayUtils.addAll(FileUtilHelper.imageExt)));

        return "/soulGod/postscript/modify";
    }

    @PostMapping( "/soulGod/postscript/modify")
    public String modifyProc(Model model,
                             @ModelAttribute PostscriptForm form,
                             @RequestParam(name = "thumbnailFile", required = false) MultipartFile thumbnailFile,
                             @RequestParam(name = "dsc", required = false) String[] dsc,
                             @CurrentUser Account account,
                             HttpServletResponse response,
                             RedirectAttributes redirect) {

        form.setUpdDtm(LocalDateTime.now());
        form.setUpdPsId(account.getLoginId());

        boolean result = postscriptService.update(form, thumbnailFile, dsc);

        return "redirect:/soulGod/postscript/detail/" + form.getId();
    }

    @PostMapping("/api/postscript/register")
    public String register(Model model,
                               @ModelAttribute PostscriptForm postscriptForm,
                               @RequestParam(name = "thumbnailFile", required = false) MultipartFile thumbnailFile,
                               @RequestParam(name = "attachedFile", required = false) MultipartFile[] attachedFile,
                               @RequestParam(name = "dsc", required = false) String[] dsc,
                               @CurrentUser Account account,
                               HttpServletResponse response,
                               RedirectAttributes redirect,
                               HttpServletRequest request) throws Exception {

        boolean result = false;

        postscriptForm.setDelAt("N");
        postscriptForm.setRegPsId(account.getLoginId());
        postscriptForm.setUpdPsId(account.getLoginId());
        postscriptForm.setRegDtm(LocalDateTime.now());
        postscriptForm.setUpdDtm(LocalDateTime.now());

        result = postscriptService.insert(postscriptForm, thumbnailFile, attachedFile, dsc);

        if (result) {
            model.addAttribute("altmsg", "저장되었습니다.");
            model.addAttribute("locurl", "/pages/activity/postscript");
            return "/message";

        } else {
            model.addAttribute("altmsg", "실패되었습니다 관리자에게 문의 하세요");
            model.addAttribute("locurl", "/pages/activity/postscript");
            return "/message";
        }
    }

    @GetMapping("/pages/activity/postscriptDetail/{id}")
    public String detail(Model model,
                         @PathVariable(name = "id") Long id,
                         @Value("${Globals.fileStoreUriPath}") String filePath) {

        PostscriptForm postscriptForm = new PostscriptForm();
        postscriptForm.setId(id);
        Postscript postscript = postscriptService.loadByForm(postscriptForm);
        model.addAttribute("form", postscript);

        CommonCode byId = commonCodeService.findById(postscript.getSrtCodePid());   //중분류
        CommonCode byId1 = commonCodeService.findById(byId.getPrntCodePid());       //대분류
        model.addAttribute("codeNm", byId1.getCodeNm());

        FileInfoForm fileInfoForm = new FileInfoForm();
        fileInfoForm.setDataPid(postscript.getId());
        fileInfoForm.setTableNm(TableNmType.TBL_POSTSCRIPT.name());
        List<FileInfo> fileList = fileInfoService.list(fileInfoForm);
        model.addAttribute("fileList", fileList);

        PostscriptImageForm postscriptImageForm = new PostscriptImageForm();
        postscriptImageForm.setPostscriptPid(postscript.getId());
        List<PostscriptImage> postscriptImageList = postscriptImageService.list(postscriptImageForm);
        model.addAttribute("postscriptImageList", postscriptImageList);

        model.addAttribute("filePath", filePath + '/' + Constants.FOLDERNAME_POSTSCRIPT);

        model.addAttribute("mc", "activity");
        model.addAttribute("pageTitle", "교육");

        return "/pages/activity/postscriptDetail";
    }

    @GetMapping("/pages/activity/postscriptModify/{id}")
    public String modify(Model model,
                         @PathVariable(name = "id") Long id,
                         @Value("${common.code.srtCodePid}") Long srtCodePid,
                         @Value("${Globals.fileStoreUriPath}") String filePath) {

        PostscriptForm postscriptForm = new PostscriptForm();
        postscriptForm.setId(id);
        Postscript postscript = postscriptService.loadByForm(postscriptForm);
        model.addAttribute("form", postscript);

        CommonCode byId = commonCodeService.findById(postscript.getSrtCodePid());   //중분류
        CommonCode byId1 = commonCodeService.findById(byId.getPrntCodePid());       //대분류
        model.addAttribute("codeNm", byId1.getCodeNm());

        FileInfoForm fileInfoForm = new FileInfoForm();
        fileInfoForm.setDataPid(postscript.getId());
        fileInfoForm.setTableNm(TableNmType.TBL_POSTSCRIPT.name());
        List<FileInfo> fileList = fileInfoService.list(fileInfoForm);
        model.addAttribute("fileList", fileList);

        PostscriptImageForm postscriptImageForm = new PostscriptImageForm();
        postscriptImageForm.setPostscriptPid(postscript.getId());
        List<PostscriptImage> postscriptImageList = postscriptImageService.list(postscriptImageForm);
        model.addAttribute("postscriptImageList", postscriptImageList);

        List<CommonCode> srtCodes = commonCodeService.getCommonCodeParent(srtCodePid);
        model.addAttribute("srtCodes", srtCodes);

        model.addAttribute("accept", StringUtil.join(",", ArrayUtils.addAll(FileUtilHelper.imageExt)));

        model.addAttribute("filePath", filePath + '/' + Constants.FOLDERNAME_POSTSCRIPT);

        model.addAttribute("mc", "activity");
        model.addAttribute("pageTitle", "교육");

        return "/pages/activity/postscriptModify";
    }

    @PostMapping("/api/postscript/update")
    public String update(Model model,
                         @ModelAttribute PostscriptForm postscriptForm,
                         @RequestParam(name = "imagePid", required = false) Long[] imagePid,
                         @RequestParam(name = "thumbnailFile", required = false) MultipartFile thumbnailFile,
                         @RequestParam(name = "attachedFile", required = false) MultipartFile[] attachedFiles,
                         @RequestParam(name = "dsc", required = false) String[] dsc,
                         @CurrentUser Account account,
                         HttpServletResponse response,
                         RedirectAttributes redirect,
                         HttpServletRequest request) throws Exception {

        boolean result = false;

        postscriptForm.setDelAt("N");
        postscriptForm.setUpdPsId(account.getLoginId());
        postscriptForm.setUpdDtm(LocalDateTime.now());

        result = postscriptService.updateForRegstUser(postscriptForm, imagePid, thumbnailFile, attachedFiles, dsc);

        if (result) {
            model.addAttribute("altmsg", "저장되었습니다.");
            model.addAttribute("locurl", "/pages/activity/postscript");
            return "/message";

        } else {
            model.addAttribute("altmsg", "실패되었습니다 관리자에게 문의 하세요");
            model.addAttribute("locurl", "/pages/activity/postscript");
            return "/message";
        }
    }

    @PostMapping("/api/postscript/delete")
    public String deleteForRegistUser(Model model,
                         @ModelAttribute PostscriptForm postscriptForm,
                         @CurrentUser Account account) throws Exception {

        postscriptForm.setUpdDtm(LocalDateTime.now());
        postscriptForm.setUpdPsId(account.getLoginId());
        postscriptForm.setDelAt("Y");
        postscriptService.delete(postscriptForm);

        return "redirect:/pages/activity/postscript";
    }

    @ResponseBody
    @PostMapping("/api/postscriptImage/delete")
    public String deleteFile(Model model,
                             @RequestBody PostscriptImageForm form,
                             @CurrentUser Account account,
                             HttpServletResponse response) throws Exception {

        boolean result = postscriptImageService.deleteByFlPid(form);
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
        return msg;
    }
}
