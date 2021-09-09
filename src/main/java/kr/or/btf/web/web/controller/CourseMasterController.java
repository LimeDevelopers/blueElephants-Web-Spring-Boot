package kr.or.btf.web.web.controller;


import kr.or.btf.web.common.Constants;
import kr.or.btf.web.common.annotation.CurrentUser;
import kr.or.btf.web.domain.web.*;
import kr.or.btf.web.domain.web.enums.MberDvType;
import kr.or.btf.web.domain.web.enums.StepType;
import kr.or.btf.web.domain.web.enums.SurveyDvType;
import kr.or.btf.web.services.web.*;
import kr.or.btf.web.utils.FileUtilHelper;
import kr.or.btf.web.web.form.CourseMasterForm;
import kr.or.btf.web.web.form.CourseMasterRelForm;
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
public class CourseMasterController extends BaseCont {

    private final CourseMasterService courseMasterService;
    private final CourseMasterRelService courseMasterRelService;
    private final CourseService courseService;
    private final CourseTasteService courseTasteService;
    private final SurveyService surveyService;
    private final InspectionService inspectionService;
    private final CourseRequestService courseRequestService;

    @RequestMapping("/soulGod/courseMaster/list")
    public String list(Model model,
                       @PageableDefault Pageable pageable,
                       @CurrentUser Account account,
                       @ModelAttribute SearchForm searchForm) {

        model.addAttribute("form", searchForm);

        Page<CourseMaster> masterSeqs = courseMasterService.list(pageable,searchForm);
        model.addAttribute("masterSeqs", masterSeqs);

        model.addAttribute("mc", "course");
        return "/soulGod/courseMaster/list";
    }

    @GetMapping("/soulGod/courseMaster/register")
    public String register(Model model) {

        CourseMaster masterSeq = new CourseMaster();
        model.addAttribute("form", masterSeq);

        model.addAttribute("accept", StringUtil.join(",", ArrayUtils.addAll(FileUtilHelper.imageExt)));

        return "/soulGod/courseMaster/register";
    }

    @PostMapping(value = "/soulGod/courseMaster/register", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String registerProc(Model model,
                               @ModelAttribute CourseMasterForm form,
                               @RequestParam(name = "attachImgFl", required = false) MultipartFile attachImgFl,
                               @CurrentUser Account account,
                               HttpServletResponse response,
                               RedirectAttributes redirect) throws Exception {

        form.setRegDtm(LocalDateTime.now());
        form.setRegPsId(account.getLoginId());
        form.setUpdDtm(LocalDateTime.now());
        form.setUpdPsId(account.getLoginId());
        form.setDelAt("N");

        CourseMaster masterSeq = courseMasterService.insert(form, attachImgFl);

        //redirect.addAttribute("srchMnGbnCdPid", courseForm.getSrchMnGbnCdPid());
        return "redirect:/soulGod/courseMaster/list";
    }

    @GetMapping("/soulGod/courseMaster/detail/{id}")
    public String detail(Model model,
                         @Value("${Globals.fileStoreUriPath}") String filePath,
                         @PageableDefault Pageable pageable,
                         @CurrentUser Account account,
                         @ModelAttribute SearchForm searchForm,
                         @PathVariable(name = "id") Long id) {

        CourseMaster masterSeqLoad = courseMasterService.load(id);
        model.addAttribute("form", masterSeqLoad);

        searchForm.setMngPid(id);
        Page<CourseRequest> requests = courseRequestService.list(pageable, searchForm);
        model.addAttribute("requestCnt", requests.stream().count());

        CourseMasterRelForm masterForm = new CourseMasterRelForm();
        masterForm.setCrsMstPid(masterSeqLoad.getId());
        List<CourseMasterRel> masters = courseMasterRelService.list(masterForm);
        for (CourseMasterRel master : masters) {
            model.addAttribute("master"+master.getSn(), master);
        }

        searchForm.setSrchGbn(masterSeqLoad.getMberDvTy());
        Page<Course> courses = courseService.list(pageable, searchForm);
        model.addAttribute("courses", courses);

        Page<Survey> surveys = surveyService.list(pageable, searchForm);
        model.addAttribute("surveys", surveys);


        model.addAttribute("filePath", filePath + '/' + Constants.FOLDERNAME_COURSEMASTERSEQ);

        return "/soulGod/courseMaster/detail";
    }

    @GetMapping("/soulGod/courseMaster/modify/{id}")
    public String modify(Model model,
                         @PathVariable(name = "id") Long id,
                         @PageableDefault Pageable pageable,
                         @ModelAttribute SearchForm searchForm) {

        CourseMaster masterSeqLoad = courseMasterService.load(id);
        model.addAttribute("form", masterSeqLoad);

        searchForm.setMngPid(masterSeqLoad.getId());
        Page<CourseRequest> requests = courseRequestService.list(pageable, searchForm);
        model.addAttribute("requestCnt", requests.stream().count());

        model.addAttribute("accept", StringUtil.join(",", ArrayUtils.addAll(FileUtilHelper.imageExt)));

        return "/soulGod/courseMaster/modify";
    }

    @PostMapping(value = "/soulGod/courseMaster/modify", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String modifyProc(Model model,
                             @ModelAttribute CourseMasterForm form,
                             @RequestParam(name = "attachImgFl", required = false) MultipartFile attachImgFl,
                             @CurrentUser Account account,
                             HttpServletResponse response,
                             RedirectAttributes redirect) {

        form.setUpdDtm(LocalDateTime.now());
        form.setUpdPsId(account.getLoginId());

        if ("Y".equals(form.getOpenAt())) {
            Long aLong = courseMasterRelService.countByCrsMstPid(form.getId());
            // 수정중 김재일
//            if (aLong != Constants.satisfSvySn.longValue()) {
//                model.addAttribute("altmsg","모든 강좌/설문을 등록하지 않으면 게시 할 수 없습니다.");
//                model.addAttribute("locurl","/soulGod/courseMaster/modify/"+form.getId());
//                return "/message";
//            }
        }

        boolean result = courseMasterService.update(form,attachImgFl);
        //redirect.addAttribute("srchMnGbnCdPid", courseForm.getSrchMnGbnCdPid());

        return "redirect:/soulGod/courseMaster/detail/" + form.getId();
    }

    @PostMapping("/soulGod/courseMaster/delete")
    public String delete(Model model,
                         @ModelAttribute CourseMasterForm form,
                         @CurrentUser Account account) throws Exception {

        form.setUpdDtm(LocalDateTime.now());
        form.setUpdPsId(account.getLoginId());
        form.setDelAt("Y");
        courseMasterService.delete(form);

        return "redirect:/soulGod/courseMaster/list";
    }

    @ResponseBody
    @PostMapping("/api/courseMaster/item/delete")
    public String courseMatserItemDelete(Model model,
                                         @RequestBody CourseMasterRelForm form,
                                         HttpServletResponse response) {

        boolean result = false;

        if (form.getSn() == 2 || form.getSn() == 6) {
            result = courseMasterRelService.inspctDelete(form);
        } else {
            result = courseMasterRelService.delete(form);
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
    @PostMapping("/api/courseMaster/courseTasteList")
    public Page<CourseTaste> courseTasteList(Model model,
                                   @RequestBody SearchForm searchForm,
                                   @PageableDefault Pageable pageable) {

        Page<CourseTaste> courseTasteList = courseTasteService.list(pageable,searchForm);

        return courseTasteList;
    }

    @ResponseBody
    @PostMapping("/api/courseMaster/courseList")
    public Page<Course> courseList(Model model,
                                   @RequestBody SearchForm searchForm,
                                   @PageableDefault Pageable pageable) {

        Page<Course> courseList = courseService.list(pageable,searchForm);

        for (Course item : courseList.getContent()) {
            item.setMberDvTyNm(MberDvType.valueOf(item.getMberDvTy()).getName());
            item.setStepTyNm(StepType.valueOf(item.getStepTy()).getName());
        }

        return courseList;
    }

    @ResponseBody
    @PostMapping("/api/courseMaster/surveyList")
    public Page<Survey> surveyList(Model model,
                                   @RequestBody SearchForm searchForm,
                                   @PageableDefault Pageable pageable) {

        searchForm.setSurveyDvType(SurveyDvType.SATISFACTION);
        Page<Survey> surveyList = surveyService.list(pageable,searchForm);

        for (Survey item : surveyList.getContent()) {
            item.setMberDvTyNm(MberDvType.valueOf(item.getMberDvTy()).getName());
            item.setDvTyNm(SurveyDvType.valueOf(item.getDvTy()).getName());
        }

        return surveyList;
    }

    @ResponseBody
    @PostMapping("/api/courseMaster/inspectionList")
    public Page<Inspection> inspectionList(Model model,
                                   @RequestBody SearchForm searchForm,
                                   @PageableDefault Pageable pageable) {

        Page<Inspection> inspections = inspectionService.list(pageable, searchForm);
        model.addAttribute("inspections", inspections);

        for (Inspection item : inspections.getContent()) {
            item.setMberDvTyNm(MberDvType.valueOf(item.getMberDvTy()).getName());
        }

        return inspections;
    }

    @ResponseBody
    @PostMapping("/api/soulGod/courseMster/courseRegister")
    public String courseRegister(Model model,
                                 @RequestBody CourseMasterRelForm form,
                                 HttpServletResponse response,
                                 RedirectAttributes redirect) throws Exception {

        boolean result = courseMasterRelService.crsInsert(form);

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
    @PostMapping("/api/soulGod/courseMster/inspectionRegister")
    public String inspectionRegister(Model model,
                                     @RequestBody CourseMasterRelForm form,
                                     HttpServletResponse response,
                                     RedirectAttributes redirect) throws Exception {

        boolean result = courseMasterRelService.inspctInsert(form);

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

    @RequestMapping("/soulGod/courseMasterStatus/list")
    public String statuslist(Model model,
                       @PageableDefault Pageable pageable,
                       @CurrentUser Account account,
                       @ModelAttribute SearchForm searchForm) {

        model.addAttribute("form", searchForm);

        Page<CourseMaster> masterSeqs = courseMasterService.list(pageable,searchForm);
        model.addAttribute("masterSeqs", masterSeqs);

        model.addAttribute("mc", "course");
        return "/soulGod/courseMasterStatus/list";
    }

    @RequestMapping("/soulGod/courseMasterStatus/detail/{crsMstPid}")
    public String requestList(Model model,
                              @PathVariable(name = "crsMstPid") Long crsMstPid,
                              @PageableDefault Pageable pageable,
                              @ModelAttribute SearchForm searchForm) {

        model.addAttribute("form", searchForm);
        model.addAttribute("crsMstPid",crsMstPid);

        searchForm.setCrsMstPid(crsMstPid);
        Page<CourseRequest> requests = courseRequestService.listForMasterStatus(pageable, searchForm);
        model.addAttribute("requests", requests);

        model.addAttribute("mc", "fieldEdu");
        return "/soulGod/courseMasterStatus/detail";
    }

}
