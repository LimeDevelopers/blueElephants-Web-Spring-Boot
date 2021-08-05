package kr.or.btf.web.web.controller;


import kr.or.btf.web.common.annotation.CurrentUser;
import kr.or.btf.web.domain.web.Account;
import kr.or.btf.web.domain.web.BoardMaster;
import kr.or.btf.web.services.web.BoardMasterService;
import kr.or.btf.web.services.web.CommonCodeService;
import kr.or.btf.web.web.form.BoardMasterForm;
import kr.or.btf.web.web.form.SearchForm;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class BoardMasterController extends BaseCont {

    private final BoardMasterService boardMasterService;
    private final CommonCodeService commonCodeService;

    @RequestMapping("/soulGod/boardMaster/list")
    public String list(Model model,
                       @PageableDefault Pageable pageable,
                       @ModelAttribute SearchForm searchForm) {

        model.addAttribute("form", searchForm);

        Page<BoardMaster> boardMasters = boardMasterService.list(pageable, searchForm);
        model.addAttribute("boardMasters", boardMasters);


        model.addAttribute("mc", "boardMaster");
        return "/soulGod/boardMaster/list";
    }

    @GetMapping("/soulGod/boardMaster/detail/{id}")
    public String detail(Model model,
                         @PathVariable(name = "id") Long id) {

        BoardMaster boardMaster = boardMasterService.load(id);
        String a = boardMaster.getSntncHead();
        model.addAttribute("form", boardMaster);

        return "/soulGod/boardMaster/detail";
    }

    @GetMapping("/soulGod/boardMaster/register")
    public String register(Model model) {

        //BoardMaster boardMaster = boardMasterService.load(id);
        BoardMaster boardMaster = new BoardMaster();
        model.addAttribute("form", boardMaster);

        return "/soulGod/boardMaster/register";
    }

    @PostMapping("/soulGod/boardMaster/register")
    public String registerProc(Model model,
                               @ModelAttribute BoardMasterForm boardMasterForm,
                               @CurrentUser Account account,
                               HttpServletResponse response,
                               RedirectAttributes redirect) throws Exception {

        boardMasterForm.setRegDtm(LocalDateTime.now());
        boardMasterForm.setRegPsId(account.getLoginId());
        boardMasterForm.setUpdDtm(LocalDateTime.now());
        boardMasterForm.setUpdPsId(account.getLoginId());
        boardMasterForm.setDelAt("N");

        BoardMaster boardMaster = boardMasterService.insert(boardMasterForm);

        //redirect.addAttribute("srchMnGbnCdPid", boardMasterForm.getSrchMnGbnCdPid());
        return "redirect:/soulGod/boardMaster/list";
    }

    @GetMapping("/soulGod/boardMaster/modify/{id}")
    public String modify(Model model,
                         @PathVariable(name = "id") Long id) {

        BoardMaster boardMaster = boardMasterService.load(id);
        model.addAttribute("form", boardMaster);

        return "/soulGod/boardMaster/modify";
    }

    @PostMapping("/soulGod/boardMaster/modify")
    public String modifyProc(Model model,
                             @ModelAttribute BoardMasterForm boardMasterForm,
                             @CurrentUser Account account,
                             HttpServletResponse response,
                             RedirectAttributes redirect) {

        boardMasterForm.setUpdDtm(LocalDateTime.now());
        boardMasterForm.setUpdPsId(account.getLoginId());

        boolean result = boardMasterService.update(boardMasterForm);
        //redirect.addAttribute("srchMnGbnCdPid", boardMasterForm.getSrchMnGbnCdPid());

        return "redirect:/soulGod/boardMaster/detail/" + boardMasterForm.getId();
    }

    @PostMapping("/soulGod/boardMaster/delete")
    public String delete(Model model,
                         @ModelAttribute BoardMasterForm boardMasterForm,
                         @CurrentUser Account account) throws Exception {

        boardMasterForm.setUpdDtm(LocalDateTime.now());
        boardMasterForm.setUpdPsId(account.getLoginId());
        boardMasterForm.setDelAt("Y");
        boardMasterService.delete(boardMasterForm);

        return "redirect:/soulGod/boardMaster/list";
    }

}
