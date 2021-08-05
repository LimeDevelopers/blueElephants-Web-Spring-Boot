package kr.or.btf.web.web.controller.pages;


import kr.or.btf.web.common.annotation.CurrentUser;
import kr.or.btf.web.domain.web.Account;
import kr.or.btf.web.domain.web.dto.SearchDto;
import kr.or.btf.web.services.web.BoardDataService;
import kr.or.btf.web.services.web.SearchLogService;
import kr.or.btf.web.web.form.SearchForm;
import kr.or.btf.web.web.form.SearchLogForm;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequiredArgsConstructor
public class SearchController {

    private final BoardDataService boardDataService;
    private final SearchLogService searchLogService;

    @PostMapping("/pages/search/searchResult")
    public String list(Model model,
                       HttpServletRequest request,
                       @PageableDefault Pageable pageable,
                       @ModelAttribute SearchForm searchForm,
                       @CurrentUser Account account) {

        if (searchForm.getTotalSrchWord() == null || "".equals(searchForm.getTotalSrchWord()) || searchForm.getTotalSrchWord().length() < 2) {
            String referrer = request.getHeader("Referer");
            model.addAttribute("altmsg", "검색어를 두 글자 이상 입력해주세요..");
            model.addAttribute("locurl", "/index");
            return "/message";
        }
        Page<SearchDto> searchList = boardDataService.totalSearchList(pageable, searchForm);
        model.addAttribute("searchList", searchList);
        model.addAttribute("searchForm", searchForm);

        SearchLogForm searchLogForm = new SearchLogForm();
        searchLogForm.setSrchwrd(searchForm.getTotalSrchWord());
        searchLogForm.setRegPsId(account == null ? null : account.getLoginId());
        searchLogService.insert(searchLogForm);

        model.addAttribute("mc", "searchResult");
        return "/pages/search/searchResult";
    }


}
