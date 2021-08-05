package kr.or.btf.web.web.controller;


import kr.or.btf.web.common.annotation.CurrentUser;
import kr.or.btf.web.domain.web.Account;
import kr.or.btf.web.domain.web.CourseMaster;
import kr.or.btf.web.domain.web.CourseRequest;
import kr.or.btf.web.domain.web.CourseRequestComplete;
import kr.or.btf.web.domain.web.enums.CompleteStatusType;
import kr.or.btf.web.services.web.CourseMasterService;
import kr.or.btf.web.services.web.CourseRequestCompleteService;
import kr.or.btf.web.services.web.CourseRequestService;
import kr.or.btf.web.web.form.SearchForm;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class FieldEduController extends BaseCont {

    private final CourseMasterService courseMasterService;
    private final CourseRequestService courseRequestService;
    private final CourseRequestCompleteService courseRequestCompleteService;

    @RequestMapping("/soulGod/fieldEdu/list")
    public String list(Model model,
                       @PageableDefault Pageable pageable,
                       @CurrentUser Account account,
                       @ModelAttribute SearchForm searchForm) {

        model.addAttribute("form",searchForm);

        Page<CourseMaster> masters = courseMasterService.list(pageable,searchForm);
        model.addAttribute("masters",masters);

        model.addAttribute("mc", "fieldEdu");
        return "/soulGod/fieldEdu/list";
    }

    @RequestMapping("/soulGod/fieldEdu/requestList/{crsMstPid}")
    public String requestList(Model model,
                              @PathVariable(name = "crsMstPid") Long crsMstPid,
                              @PageableDefault Pageable pageable,
                              @ModelAttribute SearchForm searchForm) {

        model.addAttribute("form", searchForm);
        model.addAttribute("crsMstPid",crsMstPid);

        searchForm.setCrsMstPid(crsMstPid);
        Page<CourseRequest> requests = courseRequestService.list(pageable, searchForm);
        model.addAttribute("requests", requests);

        model.addAttribute("mc", "fieldEdu");
        return "/soulGod/fieldEdu/requestList";
    }

    @PostMapping("/api/fieldEdu/cmplUpdate")
    public String questionDelete(Model model,
                                 @RequestParam(name = "crsMstPid") Long crsMstPid,
                                 @RequestParam(name = "cmplHistPids") Long[] cmplHistPids,
                                 @CurrentUser Account account) throws Exception{

        List<CourseRequestComplete> list = new ArrayList<>();
        for (Long cmplHistPid : cmplHistPids) {
            CourseRequestComplete complete = new CourseRequestComplete();
            complete.setId(cmplHistPid);
            complete.setCmplSttTy(CompleteStatusType.COMPLETE.name());
            complete.setCmplOpetrId(account.getLoginId());
            complete.setCmplPrsDtm(LocalDateTime.now());
            list.add(complete);
        }

        courseRequestCompleteService.update(list);

        return "redirect:/soulGod/fieldEdu/requestList/" + crsMstPid;
    }

}
