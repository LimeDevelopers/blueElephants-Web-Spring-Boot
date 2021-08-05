package kr.or.btf.web.web.controller;


import kr.or.btf.web.common.Constants;
import kr.or.btf.web.common.annotation.CurrentUser;
import kr.or.btf.web.domain.web.Account;
import kr.or.btf.web.domain.web.Course;
import kr.or.btf.web.domain.web.CourseItem;
import kr.or.btf.web.domain.web.CourseMasterRel;
import kr.or.btf.web.services.web.*;
import kr.or.btf.web.utils.FileUtilHelper;
import kr.or.btf.web.web.form.CourseForm;
import kr.or.btf.web.web.form.CourseItemForm;
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
public class CourseController extends BaseCont {

    private final CourseService courseService;
    private final CourseItemService courseItemService;
    private final CourseMasterRelService courseMasterRelService;
    private final CommonCodeService commonCodeService;
    private final FileInfoService fileInfoService;

    @RequestMapping("/soulGod/course/list")
    public String list(Model model,
                       @PageableDefault Pageable pageable,
                       @CurrentUser Account account,
                       @ModelAttribute SearchForm searchForm) {

        model.addAttribute("form", searchForm);

        searchForm.setUserPid(account.getId());
        Page<Course> courses = courseService.list(pageable, searchForm);
        model.addAttribute("courses", courses);

        model.addAttribute("mc", "course");
        return "/soulGod/course/list";
    }

    @GetMapping("/soulGod/course/detail/{id}")
    public String detail(Model model,
                         @Value("${Globals.fileStoreUriPath}") String filePath,
                         @PathVariable(name = "id") Long id) {

        Course course = courseService.load(id);
        model.addAttribute("form", course);

        CourseItemForm courseItemForm = new CourseItemForm();
        courseItemForm.setCrsPid(id);
        List<CourseItem> courseItems = courseItemService.list(courseItemForm);
        model.addAttribute("courseItems", courseItems);

        CourseMasterRelForm crsMstForm = new CourseMasterRelForm();
        crsMstForm.setCrsPid(course.getId());
        Integer[] snArr = {Constants.priorCrsSn, Constants.fieldCrsSn, Constants.afterCrsSn};
        crsMstForm.setSnArr(snArr);
        List<CourseMasterRel> mstRelList = courseMasterRelService.list(crsMstForm);

        model.addAttribute("masterRelCnt", mstRelList.stream().count());

        model.addAttribute("filePath", filePath + '/' + Constants.FOLDERNAME_COURSE);
        model.addAttribute("imageAccept", StringUtil.join(",", ArrayUtils.addAll(FileUtilHelper.imageExt)));
        model.addAttribute("allAccept", StringUtil.join(",", ArrayUtils.addAll(FileUtilHelper.getAllExt())));

        return "/soulGod/course/detail";
    }

    @GetMapping("/soulGod/course/register")
    public String register(Model model) {

        //Course course = courseService.load(id);
        Course course = new Course();
        model.addAttribute("form", course);

        model.addAttribute("accept", StringUtil.join(",", ArrayUtils.addAll(FileUtilHelper.imageExt)));

        return "/soulGod/course/register";
    }

    @PostMapping(value = "/soulGod/course/register", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String registerProc(Model model,
                               @ModelAttribute CourseForm courseForm,
                               @RequestParam(name = "attachImgFl", required = false) MultipartFile attachImgFl,
                               @CurrentUser Account account,
                               HttpServletResponse response,
                               RedirectAttributes redirect) throws Exception {

        courseForm.setRegDtm(LocalDateTime.now());
        courseForm.setRegPsId(account.getLoginId());
        courseForm.setUpdDtm(LocalDateTime.now());
        courseForm.setUpdPsId(account.getLoginId());
        courseForm.setDelAt("N");

        Course course = courseService.insert(courseForm, attachImgFl);

        //redirect.addAttribute("srchMnGbnCdPid", courseForm.getSrchMnGbnCdPid());
        return "redirect:/soulGod/course/detail/" + course.getId();
    }

    @GetMapping("/soulGod/course/modify/{id}")
    public String modify(Model model,
                         @PathVariable(name = "id") Long id) {

        Course course = courseService.load(id);
        model.addAttribute("form", course);

        CourseMasterRelForm crsMstForm = new CourseMasterRelForm();
        crsMstForm.setCrsPid(course.getId());
        Integer[] snArr = {Constants.priorCrsSn, Constants.fieldCrsSn, Constants.afterCrsSn};
        crsMstForm.setSnArr(snArr);
        List<CourseMasterRel> mstRelList = courseMasterRelService.list(crsMstForm);

        model.addAttribute("masterRelCnt", mstRelList.stream().count());

        model.addAttribute("accept", StringUtil.join(",", ArrayUtils.addAll(FileUtilHelper.imageExt)));

        return "/soulGod/course/modify";
    }

    @PostMapping(value = "/soulGod/course/modify", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String modifyProc(Model model,
                             @ModelAttribute CourseForm courseForm,
                             @RequestParam(name = "attachImgFl", required = false) MultipartFile attachImgFl,
                             @CurrentUser Account account,
                             HttpServletResponse response,
                             RedirectAttributes redirect) {

        courseForm.setUpdDtm(LocalDateTime.now());
        courseForm.setUpdPsId(account.getLoginId());

        boolean result = courseService.update(courseForm, attachImgFl);
        //redirect.addAttribute("srchMnGbnCdPid", courseForm.getSrchMnGbnCdPid());

        return "redirect:/soulGod/course/detail/" + courseForm.getId();
    }

    @PostMapping("/soulGod/course/delete")
    public String delete(Model model,
                         @ModelAttribute CourseForm courseForm,
                         @CurrentUser Account account) throws Exception {

        courseForm.setUpdDtm(LocalDateTime.now());
        courseForm.setUpdPsId(account.getLoginId());
        courseForm.setDelAt("Y");
        courseService.delete(courseForm);

        return "redirect:/soulGod/course/list";
    }

}
