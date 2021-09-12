package kr.or.btf.web.web.controller;

import kr.or.btf.web.common.annotation.CurrentUser;
import kr.or.btf.web.domain.web.Account;
import kr.or.btf.web.domain.web.ActivityApplication;
import kr.or.btf.web.domain.web.enums.UserRollType;
import kr.or.btf.web.services.web.AppManagementService;
import kr.or.btf.web.web.form.SearchForm;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
@RequiredArgsConstructor
public class AppManagementController extends BaseCont  {
    private final AppManagementService appManagementService;

    @GetMapping("/soulGod/application/partners")
    public String partnersPage(Model model,
                               @PageableDefault Pageable pageable,
                               @ModelAttribute SearchForm searchForm,
                               @CurrentUser Account account) {
        searchForm.setGroupDv("N");
        model.addAttribute("form", searchForm);

        searchForm.setUserRollType(account.getMberDvTy());
        Page<ActivityApplication> applications = appManagementService.list(pageable, searchForm);
        model.addAttribute("partners", applications);
        model.addAttribute("totCnt", applications.isEmpty() ? 0 : applications.getContent().size());

        model.addAttribute("mc", "application");
        model.addAttribute("dv", "application");
        return "soulGod/application/partners";
    }
}