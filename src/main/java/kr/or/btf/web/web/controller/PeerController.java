package kr.or.btf.web.web.controller;

import kr.or.btf.web.common.annotation.CurrentUser;
import kr.or.btf.web.domain.web.Account;
import kr.or.btf.web.domain.web.Peer;
import kr.or.btf.web.domain.web.PeerAnswerItem;
import kr.or.btf.web.domain.web.PeerQuestionItem;
import kr.or.btf.web.domain.web.enums.AnswerType;
import kr.or.btf.web.services.web.PeerAnswerItemService;
import kr.or.btf.web.services.web.PeerQuestionItemService;
import kr.or.btf.web.services.web.PeerService;
import kr.or.btf.web.web.form.PeerAnswerItemForm;
import kr.or.btf.web.web.form.PeerForm;
import kr.or.btf.web.web.form.PeerQuestionItemForm;
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
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class PeerController extends BaseCont {

    private final PeerService peerService;
    private final PeerQuestionItemService peerQuestionItemService;
    private final PeerAnswerItemService peerAnswerItemService;

    @RequestMapping("/soulGod/peer/list")
    public String list(Model model,
                       @PageableDefault Pageable pageable,
                       @ModelAttribute SearchForm searchForm) {

        model.addAttribute("form", searchForm);

        Page<Peer> peers = peerService.list(pageable, searchForm);
        model.addAttribute("peers", peers);


        model.addAttribute("mc", "peer");
        return "/soulGod/peer/list";
    }

    @GetMapping("/soulGod/peer/register")
    public String register(Model model) {

        Peer peer = new Peer();
        model.addAttribute("form", peer);

        return "/soulGod/peer/register";
    }

    @PostMapping("/soulGod/peer/register")
    public String registerProc(Model model,
                               @ModelAttribute PeerForm peerForm,
                               @CurrentUser Account account,
                               HttpServletResponse response,
                               RedirectAttributes redirect) throws Exception {

        peerForm.setRegDtm(LocalDateTime.now());
        peerForm.setRegPsId(account.getLoginId());
        peerForm.setUpdDtm(LocalDateTime.now());
        peerForm.setUpdPsId(account.getLoginId());
        peerForm.setDelAt("N");

        Peer peer = peerService.insert(peerForm);

        return "redirect:/soulGod/peer/list";
    }

    @ResponseBody
    @PostMapping("/api/soulGod/peerQuestion/register")
    public String questionRegister(Model model,
                               @ModelAttribute PeerQuestionItemForm peerQuestionItemForm,
                               @RequestParam(name = "answerCnts") String[] answerCnts,
                               @RequestParam(name = "score") Integer[] score,
                               @RequestParam(name = "aswPid") Long[] aswPid,
                               @CurrentUser Account account,
                               HttpServletResponse response,
                               RedirectAttributes redirect) throws Exception {

        peerQuestionItemForm.setRegDtm(LocalDateTime.now());
        peerQuestionItemForm.setRegPsId(account.getLoginId());
        peerQuestionItemForm.setUpdDtm(LocalDateTime.now());
        peerQuestionItemForm.setUpdPsId(account.getLoginId());
        peerQuestionItemForm.setDelAt("N");
        List<PeerAnswerItem> peerAnswerItemList = new ArrayList<>();

        Integer answerCnt = peerQuestionItemForm.getAnswerCnt();

        if (AnswerType.CHOICE.name().equals(peerQuestionItemForm.getAswDvTy())) {
            for (int i = 0; i < answerCnt; i++) {
                PeerAnswerItem peerAnswerItem = new PeerAnswerItem();
                peerAnswerItem.setDelAt("N");
                peerAnswerItem.setRegDtm(LocalDateTime.now());
                peerAnswerItem.setRegPsId(account.getLoginId());
                peerAnswerItem.setUpdDtm(LocalDateTime.now());
                peerAnswerItem.setUpdPsId(account.getLoginId());
                peerAnswerItem.setAnswerCnts(answerCnts[i]);
                peerAnswerItem.setScore(score[i]);
                if (aswPid[i] != null) {
                    peerAnswerItem.setId(aswPid[i]);
                }
                peerAnswerItemList.add(peerAnswerItem);
            }
        } else {
            peerQuestionItemForm.setAnswerCnt(0);
        }

        boolean result = peerQuestionItemForm.getId() == null ?
                peerQuestionItemService.insert(peerQuestionItemForm, peerAnswerItemList) :
                peerQuestionItemService.update(peerQuestionItemForm, peerAnswerItemList);

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

    @GetMapping("/soulGod/peer/detail/{id}")
    public String detail(Model model,
                         @PathVariable(name = "id") Long id) {

        Peer peer = peerService.load(id);
        model.addAttribute("form", peer);

        PeerQuestionItemForm peerQuestionItemForm = new PeerQuestionItemForm();
        peerQuestionItemForm.setPeerPid(id);
        List<PeerQuestionItem> questionItems = peerQuestionItemService.list(peerQuestionItemForm);
        model.addAttribute("questionItems", questionItems);

        return "/soulGod/peer/detail";
    }

    @ResponseBody
    @PostMapping("/api/soulGod/peer/detail")
    public PeerQuestionItem questionDetail(Model model,
                                             @RequestBody PeerQuestionItemForm peerQuestionItemForm,
                                             @CurrentUser Account account,
                                             HttpServletResponse response) {

         PeerQuestionItem load = peerQuestionItemService.load(peerQuestionItemForm.getId());

        return load;
    }

    @ResponseBody
    @PostMapping("/api/soulGod/peerAnswer/answerListLoad")
    public List<PeerAnswerItem> answerItemList(Model model,
                                                 @RequestBody PeerAnswerItemForm peerAnswerItemForm,
                                                 HttpServletResponse response) {

        List<PeerAnswerItem> peerAnswerItemList = peerAnswerItemService.list(peerAnswerItemForm);

        return peerAnswerItemList;
    }

    @PostMapping("/api/peer/questionDelete")
    public String questionDelete(Model model,
                                 @RequestParam(name = "checkBox") Long[] qesitmPids,
                                 @RequestParam(name = "peerPid") Long peerPid,
                                 @CurrentUser Account account) throws Exception{

        List<PeerQuestionItem> list = new ArrayList<>();
        for (Long qesitmPid : qesitmPids) {
            PeerQuestionItem peerQuestionItem = new PeerQuestionItem();
            peerQuestionItem.setId(qesitmPid);
            peerQuestionItem.setUpdDtm(LocalDateTime.now());
            peerQuestionItem.setUpdPsId(account.getLoginId());
            peerQuestionItem.setDelAt("Y");
            list.add(peerQuestionItem);
        }

        peerQuestionItemService.delete(list);

        return "redirect:/soulGod/peer/detail/" + peerPid;
    }

    @GetMapping("/soulGod/peer/modify/{id}")
    public String modify(Model model,
                         @PathVariable(name = "id") Long id) {

        Peer peer = peerService.load(id);
        model.addAttribute("form", peer);

        return "/soulGod/peer/modify";
    }

    @PostMapping("/soulGod/peer/modify")
    public String modifyProc(Model model,
                             @ModelAttribute PeerForm peerForm,
                             @CurrentUser Account account,
                             HttpServletResponse response,
                             RedirectAttributes redirect) {

        peerForm.setUpdDtm(LocalDateTime.now());
        peerForm.setUpdPsId(account.getLoginId());

        boolean result = peerService.update(peerForm);

        return "redirect:/soulGod/peer/detail/" + peerForm.getId();
    }

    @PostMapping("/soulGod/peer/delete")
    public String delete(Model model,
                         @ModelAttribute PeerForm peerForm,
                         @CurrentUser Account account) throws Exception {

        peerForm.setUpdDtm(LocalDateTime.now());
        peerForm.setUpdPsId(account.getLoginId());
        peerForm.setDelAt("Y");
        peerService.delete(peerForm);

        return "redirect:/soulGod/peer/list";
    }

}
