package kr.or.btf.web.web.controller;


import kr.or.btf.web.common.annotation.CurrentUser;
import kr.or.btf.web.domain.web.Account;
import kr.or.btf.web.domain.web.Course;
import kr.or.btf.web.domain.web.CourseItem;
import kr.or.btf.web.services.web.CommonCodeService;
import kr.or.btf.web.services.web.CourseItemService;
import kr.or.btf.web.web.form.CourseItemForm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequiredArgsConstructor
public class CourseItemController extends BaseCont {

    private final CourseItemService courseItemService;
    private final CommonCodeService commonCodeService;

    @GetMapping("/soulGod/courseItem/detail/{id}")
    public String detail(Model model,
                         @PathVariable(name = "id") Long id) {

        CourseItem courseItem = courseItemService.load(id);
        model.addAttribute("form", courseItem);

        return "/soulGod/courseItem/detail";
    }

    @ResponseBody
    @PostMapping("/api/soulGod/courseItem/load")
    public CourseItem load(Model model,
                           @RequestBody CourseItemForm courseItemForm) {
        CourseItem byId = courseItemService.load(courseItemForm.getId());
        return byId;
    }

    @GetMapping("/soulGod/courseItem/register")
    public String register(Model model) {

        //Course courseItem = courseItemService.load(id);
        Course courseItem = new Course();
        model.addAttribute("form", courseItem);

        return "/soulGod/courseItem/register";
    }

    @PostMapping(value = "/soulGod/courseItem/register", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String registerProc(Model model,
                               @ModelAttribute CourseItemForm courseItemForm,
                               @RequestParam(name = "attachImgFl", required = false) MultipartFile attachImgFl,
                               @RequestParam(name = "attachedFile", required = false) MultipartFile[] attachedFile,
                               @RequestParam(name = "id") Long id,
                               @RequestParam(name = "crssqPid") Long crssqPid,
                               @CurrentUser Account account,
                               HttpServletResponse response,
                               RedirectAttributes redirect) throws Exception {

        courseItemForm.setId(crssqPid);
        courseItemForm.setCrsPid(id);
        courseItemForm.setDelAt("N");

        boolean result = false;

        result = courseItemService.insert(courseItemForm, attachImgFl, attachedFile);

        return "redirect:/soulGod/course/detail/" + id;
    }

    @GetMapping("/soulGod/courseItem/modify/{id}")
    public String modify(Model model,
                         @PathVariable(name = "id") Long id) {

        CourseItem courseItem = courseItemService.load(id);
        model.addAttribute("form", courseItem);

        return "/soulGod/courseItem/modify";
    }

    @PostMapping(value = "/soulGod/courseItem/modify", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String modifyProc(Model model,
                             @ModelAttribute CourseItemForm courseItemForm,
                             @RequestParam(name = "attachImgFl", required = false) MultipartFile attachImgFl,
                             @RequestParam(name = "attachedFile", required = false) MultipartFile[] attachedFile,
                             @RequestParam(name = "id") Long id,
                             @RequestParam(name = "crssqPid") Long crssqPid,
                             @CurrentUser Account account,
                             HttpServletResponse response,
                             RedirectAttributes redirect) {

        courseItemForm.setId(crssqPid);
        courseItemForm.setCrsPid(id);

        boolean result = courseItemService.update(courseItemForm, attachImgFl, attachedFile);
        //redirect.addAttribute("srchMnGbnCdPid", courseItemForm.getSrchMnGbnCdPid());

        return "redirect:/soulGod/course/detail/" + id;
    }

    @PostMapping("/soulGod/courseItem/delete")
    public String delete(Model model,
                         @ModelAttribute CourseItemForm courseItemForm,
                         @RequestParam(name = "crssqPid") Long crssqPid,
                         @CurrentUser Account account) throws Exception {

        Long crsPid = courseItemForm.getId();
        courseItemForm.setCrsPid(crsPid);
        courseItemForm.setId(crssqPid);
        courseItemService.delete(courseItemForm);

        return "redirect:/soulGod/course/detail/" + crsPid;
    }

    @RequestMapping("/courseItem/video/{id}")
    public String video(Model model,
                        HttpServletRequest request,
                        @Value("${Globals.fileStoreUriPath}") String filePath,
                        @PathVariable(name = "id") Long id,
                        @CurrentUser Account account) throws Exception {

        CourseItem courseItem = courseItemService.load(id);

        model.addAttribute("form", courseItem);

        return "/popup/video";
    }
}
