package kr.or.btf.web.web.controller;


import kr.or.btf.web.common.Constants;
import kr.or.btf.web.common.annotation.CurrentUser;
import kr.or.btf.web.domain.web.Account;
import kr.or.btf.web.domain.web.Event;
import kr.or.btf.web.domain.web.FileInfo;
import kr.or.btf.web.domain.web.enums.TableNmType;
import kr.or.btf.web.services.web.CommonCodeService;
import kr.or.btf.web.services.web.EventService;
import kr.or.btf.web.services.web.FileInfoService;
import kr.or.btf.web.utils.FileUtilHelper;
import kr.or.btf.web.web.form.EventForm;
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
public class EventController extends BaseCont {

    private final EventService eventService;
    private final CommonCodeService commonCodeService;
    private final FileInfoService fileInfoService;

    @RequestMapping("/soulGod/event/list")
    public String list(Model model,
                       @PageableDefault Pageable pageable,
                       @ModelAttribute SearchForm searchForm) {

        model.addAttribute("form", searchForm);

        Page<Event> events = eventService.list(pageable, searchForm);
        model.addAttribute("events", events);


        model.addAttribute("mc", "event");
        return "/soulGod/event/list";
    }

    @GetMapping("/soulGod/event/detail/{id}")
    public String detail(Model model,
                         @PathVariable(name = "id") Long id,
                         @Value("${Globals.fileStoreUriPath}") String filePath) {

        EventForm eventForm = new EventForm();
        eventForm.setId(id);
        Event event = eventService.load(eventForm);
        model.addAttribute("form", event);

        FileInfoForm fileInfoForm = new FileInfoForm();
        fileInfoForm.setDataPid(event.getId());
        fileInfoForm.setTableNm(TableNmType.TBL_EVENT.name());
        List<FileInfo> fileList = fileInfoService.list(fileInfoForm);
        model.addAttribute("fileList", fileList);

        model.addAttribute("filePath", filePath + '/' + Constants.FOLDERNAME_EVENT);

        return "/soulGod/event/detail";
    }

    @GetMapping("/soulGod/event/register")
    public String register(Model model) {

        //Event event = eventService.load(id);
        Event event = new Event();
        model.addAttribute("form", event);

        model.addAttribute("imageAccept", StringUtil.join(",", ArrayUtils.addAll(FileUtilHelper.imageExt)));
        model.addAttribute("videoAccept", StringUtil.join(",", ArrayUtils.addAll(FileUtilHelper.videoExt)));
        model.addAttribute("allAccept", StringUtil.join(",", ArrayUtils.addAll(FileUtilHelper.getAllExt())));

        return "/soulGod/event/register";
    }

    @PostMapping(value = "/soulGod/event/register", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String registerProc(Model model,
                               @ModelAttribute EventForm eventForm,
                               @RequestParam(name = "attachImgFl") MultipartFile attachImgFl,
                               @RequestParam(name = "attachVideoFl", required = false) MultipartFile attachVideoFl,
                               @RequestParam(name = "attachedFile", required = false) MultipartFile[] attachedFile,
                               @CurrentUser Account account,
                               HttpServletResponse response,
                               RedirectAttributes redirect) throws Exception {

        eventForm.setReadCnt(0);
        eventForm.setRegDtm(LocalDateTime.now());
        eventForm.setRegPsId(account.getLoginId());
        eventForm.setUpdDtm(LocalDateTime.now());
        eventForm.setUpdPsId(account.getLoginId());
        eventForm.setDelAt("N");

        Event event = eventService.insert(eventForm, attachImgFl, attachVideoFl, attachedFile);

        //redirect.addAttribute("srchMnGbnCdPid", eventForm.getSrchMnGbnCdPid());
        return "redirect:/soulGod/event/list";
    }

    @GetMapping("/soulGod/event/modify/{id}")
    public String modify(Model model,
                         @PathVariable(name = "id") Long id) {

        EventForm eventForm = new EventForm();
        eventForm.setId(id);
        Event event = eventService.load(eventForm);
        model.addAttribute("form", event);

        FileInfoForm fileInfoForm = new FileInfoForm();
        fileInfoForm.setDataPid(event.getId());
        fileInfoForm.setTableNm(TableNmType.TBL_EVENT.name());
        List<FileInfo> fileList = fileInfoService.list(fileInfoForm);
        model.addAttribute("fileList", fileList);

        model.addAttribute("imageAccept", StringUtil.join(",", ArrayUtils.addAll(FileUtilHelper.imageExt)));
        model.addAttribute("videoAccept", StringUtil.join(",", ArrayUtils.addAll(FileUtilHelper.videoExt)));
        model.addAttribute("allAccept", StringUtil.join(",", ArrayUtils.addAll(FileUtilHelper.getAllExt())));

        return "/soulGod/event/modify";
    }

    @PostMapping(value = "/soulGod/event/modify", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String modifyProc(Model model,
                             @ModelAttribute EventForm eventForm,
                             @RequestParam(name = "attachImgFl") MultipartFile attachImgFl,
                             @RequestParam(name = "attachVideoFl", required = false) MultipartFile attachVideoFl,
                             @RequestParam(name = "attachedFile", required = false) MultipartFile[] attachedFile,
                             @CurrentUser Account account,
                             HttpServletResponse response,
                             RedirectAttributes redirect) {

        eventForm.setUpdDtm(LocalDateTime.now());
        eventForm.setUpdPsId(account.getLoginId());

        boolean result = eventService.update(eventForm, attachImgFl, attachVideoFl, attachedFile);
        //redirect.addAttribute("srchMnGbnCdPid", eventForm.getSrchMnGbnCdPid());

        return "redirect:/soulGod/event/detail/" + eventForm.getId();
    }

    @PostMapping("/soulGod/event/delete")
    public String delete(Model model,
                         @ModelAttribute EventForm eventForm,
                         @CurrentUser Account account) throws Exception {

        eventForm.setUpdDtm(LocalDateTime.now());
        eventForm.setUpdPsId(account.getLoginId());
        eventForm.setDelAt("Y");
        eventService.delete(eventForm);

        return "redirect:/soulGod/event/list";
    }

}
