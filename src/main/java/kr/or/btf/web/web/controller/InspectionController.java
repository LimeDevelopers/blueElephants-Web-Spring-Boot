package kr.or.btf.web.web.controller;

import kr.or.btf.web.common.annotation.CurrentUser;
import kr.or.btf.web.domain.web.Account;
import kr.or.btf.web.domain.web.Inspection;
import kr.or.btf.web.domain.web.InspectionAnswerItem;
import kr.or.btf.web.domain.web.InspectionQuestionItem;
import kr.or.btf.web.domain.web.enums.AnswerType;
import kr.or.btf.web.services.web.*;
import kr.or.btf.web.web.form.InspectionAnswerItemForm;
import kr.or.btf.web.web.form.InspectionForm;
import kr.or.btf.web.web.form.InspectionQuestionItemForm;
import kr.or.btf.web.web.form.SearchForm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
public class InspectionController extends BaseCont {

    private final InspectionService inspectionService;
    private final InspectionQuestionItemService inspectionQuestionItemService;
    private final InspectionAnswerItemService inspectionAnswerItemService;
    private final InspectionResponseService inspectionResponseService;
    private final CommonCodeService commonCodeService;

    @RequestMapping("/soulGod/inspection/list")
    public String list(Model model,
                       @PageableDefault Pageable pageable,
                       @ModelAttribute SearchForm searchForm) {

        model.addAttribute("form", searchForm);

        Page<Inspection> inspections = inspectionService.list(pageable, searchForm);
        model.addAttribute("inspections", inspections);


        model.addAttribute("mc", "inspection");
        return "/soulGod/inspection/list";
    }

    @GetMapping("/soulGod/inspection/register")
    public String register(Model model) {

        //Course course = courseService.load(id);
        Inspection inspection = new Inspection();
        model.addAttribute("form", inspection);

        return "/soulGod/inspection/register";
    }

    @PostMapping("/soulGod/inspection/register")
    public String registerProc(Model model,
                               @ModelAttribute InspectionForm inspectionForm,
                               @CurrentUser Account account,
                               HttpServletResponse response,
                               RedirectAttributes redirect) throws Exception {

        inspectionForm.setRegDtm(LocalDateTime.now());
        inspectionForm.setRegPsId(account.getLoginId());
        inspectionForm.setUpdDtm(LocalDateTime.now());
        inspectionForm.setUpdPsId(account.getLoginId());
        inspectionForm.setDelAt("N");

        Inspection inspection = inspectionService.insert(inspectionForm);

        return "redirect:/soulGod/inspection/list";
    }

    @ResponseBody
    @PostMapping("/api/soulGod/inspection/question/register")
    public String questionRegister(Model model,
                               @ModelAttribute InspectionQuestionItemForm inspectionQuestionItemForm,
                               @RequestParam(name = "answerCnts") String[] answerCnts,
                               @RequestParam(name = "apdAnswerAt",required = false) Integer[] apdAnswerAt,
                               @CurrentUser Account account,
                               HttpServletResponse response,
                               RedirectAttributes redirect) throws Exception {

        if (inspectionQuestionItemForm.getLwprtQesitmAt().equals("Y")) {
            inspectionQuestionItemForm.setAnswerCnt(0);
            inspectionQuestionItemForm.setRspnsCnt(0);
        }
        inspectionQuestionItemForm.setRegDtm(LocalDateTime.now());
        inspectionQuestionItemForm.setRegPsId(account.getLoginId());
        inspectionQuestionItemForm.setUpdDtm(LocalDateTime.now());
        inspectionQuestionItemForm.setUpdPsId(account.getLoginId());
        inspectionQuestionItemForm.setDelAt("N");
        List<InspectionAnswerItem> answerItemList = new ArrayList<>();

        Integer answerCnt = inspectionQuestionItemForm.getAnswerCnt();

        if (inspectionQuestionItemForm.getLwprtQesitmAt().equals("N")) {
            if (AnswerType.CHOICE.name().equals(inspectionQuestionItemForm.getAswDvTy())) {
                for (int i = 0; i < answerCnt; i++) {
                    InspectionAnswerItem inspectionAnswerItem = new InspectionAnswerItem();
                    inspectionAnswerItem.setDelAt("N");
                    inspectionAnswerItem.setRegDtm(LocalDateTime.now());
                    inspectionAnswerItem.setRegPsId(account.getLoginId());
                    inspectionAnswerItem.setUpdDtm(LocalDateTime.now());
                    inspectionAnswerItem.setUpdPsId(account.getLoginId());
                    inspectionAnswerItem.setAnswerCnts(answerCnts[i]);
                    inspectionAnswerItem.setSn(i+1);
                    if (apdAnswerAt != null) {
                        for (Integer at : apdAnswerAt) {
                            if (at == i+1) {
                                inspectionAnswerItem.setApdAnswerAt("Y");
                            }
                        }
                    }
                    answerItemList.add(inspectionAnswerItem);
                }
            } else {
                inspectionQuestionItemForm.setAnswerCnt(0);
            }
        }

        boolean result = inspectionQuestionItemForm.getId() == null ?
                inspectionQuestionItemService.insert(inspectionQuestionItemForm, answerItemList) :
                inspectionQuestionItemService.update(inspectionQuestionItemForm, answerItemList);

        String msg = "";
        if (false) {
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
    @PostMapping("/api/soulGod/inspection/lwprtQuestion/register")
    public String lwprtQuestionRegister(Model model,
                                   @ModelAttribute InspectionQuestionItemForm inspectionQuestionItemForm,
                                   @RequestParam(name = "answerCnts") String[] answerCnts,
                                   @RequestParam(name = "apdAnswerAt",required = false) Integer[] apdAnswerAt,
                                   @CurrentUser Account account,
                                   HttpServletResponse response,
                                   RedirectAttributes redirect) throws Exception {

        inspectionQuestionItemForm.setRegDtm(LocalDateTime.now());
        inspectionQuestionItemForm.setRegPsId(account.getLoginId());
        inspectionQuestionItemForm.setUpdDtm(LocalDateTime.now());
        inspectionQuestionItemForm.setUpdPsId(account.getLoginId());
        inspectionQuestionItemForm.setDelAt("N");

        List<InspectionAnswerItem> answerItemList = new ArrayList<>();
        Integer answerCnt = inspectionQuestionItemForm.getAnswerCnt();

        if (AnswerType.CHOICE.name().equals(inspectionQuestionItemForm.getAswDvTy())) {
            for (int i = 0; i < answerCnt; i++) {
                InspectionAnswerItem inspectionAnswerItem = new InspectionAnswerItem();
                inspectionAnswerItem.setDelAt("N");
                inspectionAnswerItem.setRegDtm(LocalDateTime.now());
                inspectionAnswerItem.setRegPsId(account.getLoginId());
                inspectionAnswerItem.setUpdDtm(LocalDateTime.now());
                inspectionAnswerItem.setUpdPsId(account.getLoginId());
                inspectionAnswerItem.setAnswerCnts(answerCnts[i]);
                inspectionAnswerItem.setSn(i+1);
                if (apdAnswerAt != null) {
                    for (Integer at : apdAnswerAt) {
                        if (at == i+1) {
                            inspectionAnswerItem.setApdAnswerAt("Y");
                        }
                    }
                }
                answerItemList.add(inspectionAnswerItem);
            }
        } else {
            inspectionQuestionItemForm.setAnswerCnt(0);
        }

        boolean result = inspectionQuestionItemForm.getId() == null ?
                inspectionQuestionItemService.insert(inspectionQuestionItemForm, answerItemList) :
                inspectionQuestionItemService.update(inspectionQuestionItemForm, answerItemList);

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

    @GetMapping("/soulGod/inspection/detail/{id}")
    public String detail(Model model,
                         @Value("${common.code.inspectDvCodePid}") Long inspectDvCodePid,
                         @PathVariable(name = "id") Long id) {

        Inspection inspection = inspectionService.load(id);
        model.addAttribute("form", inspection);

        InspectionQuestionItemForm inspectionQuestionItemForm = new InspectionQuestionItemForm();
        inspectionQuestionItemForm.setInspctPid(id);
        List<InspectionQuestionItem> questionItems = inspectionQuestionItemService.list(inspectionQuestionItemForm);
        model.addAttribute("questionItems", questionItems);
        model.addAttribute("inspectDvCodePid", inspectDvCodePid);

        return "/soulGod/inspection/detail";
    }

    @ResponseBody
    @PostMapping("/api/soulGod/inspection/detail")
    public InspectionQuestionItem questionDetail(Model model,
                                             @RequestBody InspectionQuestionItemForm inspectionQuestionItemForm,
                                             @CurrentUser Account account,
                                             HttpServletResponse response) {

        InspectionQuestionItem load = inspectionQuestionItemService.loadByForm(inspectionQuestionItemForm);

        return load;
    }

    @ResponseBody
    @PostMapping("/api/soulGod/inspection/answerListLoad")
    public List<InspectionAnswerItem> answerItemList(Model model,
                                                       @RequestBody InspectionAnswerItemForm inspectionAnswerItemForm,
                                                       HttpServletResponse response) {

        List<InspectionAnswerItem> answerItemList = inspectionAnswerItemService.list(inspectionAnswerItemForm, null, null);

        return answerItemList;
    }

    @ResponseBody
    @PostMapping("/api/soulGod/inspection/lwprtListLoad")
    public List<InspectionQuestionItem> lwprtList(Model model,
                                                @RequestBody InspectionQuestionItemForm inspectionQuestionItemForm,
                                                 HttpServletResponse response) {

        List<InspectionQuestionItem> questionItems = inspectionQuestionItemService.list(inspectionQuestionItemForm);

        return questionItems;
    }

    @PostMapping("/api/inspection/questionDelete")
    public String questionDelete(Model model,
                                 @ModelAttribute InspectionQuestionItemForm form,
                                 @CurrentUser Account account) throws Exception{

        form.setUpdDtm(LocalDateTime.now());
        form.setUpdPsId(account.getLoginId());
        form.setDelAt("Y");

        inspectionQuestionItemService.delete(form);

        return "redirect:/soulGod/inspection/detail/" + form.getInspctPid();
    }

    @GetMapping("/soulGod/inspection/modify/{id}")
    public String modify(Model model,
                         @PathVariable(name = "id") Long id) {

        Inspection inspection = inspectionService.load(id);
        model.addAttribute("form", inspection);

        return "/soulGod/inspection/modify";
    }

    @PostMapping("/soulGod/inspection/modify")
    public String modifyProc(Model model,
                             @ModelAttribute InspectionForm inspectionForm,
                             @CurrentUser Account account,
                             HttpServletResponse response,
                             RedirectAttributes redirect) {

        inspectionForm.setUpdDtm(LocalDateTime.now());
        inspectionForm.setUpdPsId(account.getLoginId());

        boolean result = inspectionService.update(inspectionForm);
        //redirect.addAttribute("srchMnGbnCdPid", courseForm.getSrchMnGbnCdPid());

        return "redirect:/soulGod/inspection/detail/" + inspectionForm.getId();
    }

    @PostMapping("/soulGod/inspection/delete")
    public String delete(Model model,
                         @ModelAttribute InspectionForm inspectionForm,
                         @CurrentUser Account account) throws Exception {

        inspectionForm.setUpdDtm(LocalDateTime.now());
        inspectionForm.setUpdPsId(account.getLoginId());
        inspectionForm.setDelAt("Y");
        inspectionService.delete(inspectionForm);

        return "redirect:/soulGod/inspection/list";
    }


    /*private Long[] inspectionQuestionDvCodeList= {70l, 69l, 71l, 59l, 60l};
    @ResponseBody
    @RequestMapping("/page/inspection/question")
    public Map<String, Object> inspectionQuestion(Model model,
                                                  @ModelAttribute InspectionForm inspectionForm,
                                                  @CurrentUser Account account) {

        if (inspectionQuestionDvCodeList.length < inspectionForm.getSectionIndex()) {
            //배열 범위가 아닐 경우 에러 또는 결과페이지로 이동
            return null;
        }

        Long dvCodePid = inspectionQuestionDvCodeList[inspectionForm.getSectionIndex()];
        InspectionDvType inspctDvTy = InspectionDvType.valueOf(inspectionForm.getInspctDvTy());

        InspectionQuestionItemForm inspectionQuestionItemForm = new InspectionQuestionItemForm();
        inspectionQuestionItemForm.setInspctPid(inspectionForm.getId());
        inspectionQuestionItemForm.setDvCodePid(dvCodePid);

        Map<String, Object> rtnMap = new HashMap<>();

        CommonCode commonCode = commonCodeService.findById(dvCodePid);
        rtnMap.put("section", commonCode);

        Inspection inspection = inspectionService.load(inspectionForm.getId());
        rtnMap.put("inspection", inspection);

        List<Map<String, Object>> questionList = new ArrayList<>();
        List<InspectionQuestionItem> list = inspectionQuestionItemService.list(inspectionQuestionItemForm);
        if (list != null && list.size() > 0) {

            List<Long> upperQesitmPid = new ArrayList<>();
            for (InspectionQuestionItem qItem : list) {
                Map<String, Object> questionItem = new HashMap<>();

                if (qItem.getUpperQesitmPid() == null) {        //하위문항 없음
                    questionItem.put("question", qItem);

                    if (AnswerType.CHOICE.name().equals(qItem.getAswDvTy())) {
                        questionItem.put("caseList", getCaseList(qItem, account, inspctDvTy.name()));
                    } else {
                        questionItem.put("answer", getAnswer(qItem, account, inspctDvTy.name()));
                    }

                    questionList.add(questionItem);
                } else {
                    if (!upperQesitmPid.contains(qItem.getUpperQesitmPid())) {
                        upperQesitmPid.add(qItem.getUpperQesitmPid());

                        List<Map<String,Object>> subQuestionList = new ArrayList<>();
                        InspectionQuestionItem load = inspectionQuestionItemService.load(qItem.getUpperQesitmPid());
                        questionItem.put("question", load);
                        for (InspectionQuestionItem subItem : list) {
                            Map<String, Object> subQuestionItem = new HashMap<>();
                            if (subItem.getUpperQesitmPid() != null
                                    && subItem.getUpperQesitmPid().equals(qItem.getUpperQesitmPid())) {
                                subQuestionItem.put("question", subItem);
                                if (AnswerType.CHOICE.name().equals(subItem.getAswDvTy())) {
                                    subQuestionItem.put("caseList", getCaseList(subItem, account, inspctDvTy.name()));
                                } else {
                                    subQuestionItem.put("answer", getAnswer(subItem, account, inspctDvTy.name()));
                                }

                                subQuestionList.add(subQuestionItem);
                            }
                        }
                        questionItem.put("subList", subQuestionList);
                        questionList.add(questionItem);
                    }
                }
            }
        }

        rtnMap.put("questionList", questionList);
        return rtnMap;
    }

    private List<InspectionAnswerItem> getCaseList(InspectionQuestionItem item, Account account, String inspctDvTy) {
        InspectionAnswerItemForm inspectionAnswerItemForm = new InspectionAnswerItemForm();
        inspectionAnswerItemForm.setQesitmPid(item.getId());
        inspectionAnswerItemForm.setInspctPid(item.getInspctPid());
        inspectionAnswerItemForm.setInspctDvTy(inspctDvTy);
        return inspectionAnswerItemService.list(inspectionAnswerItemForm, account);
    }
    private InspectionResponse getAnswer(InspectionQuestionItem item, Account account, String inspctDvTy) {
        InspectionResponseForm inspectionResponseForm = new InspectionResponseForm();
        inspectionResponseForm.setQesitmPid(item.getId());
        inspectionResponseForm.setInspctDvTy(inspctDvTy);
        return inspectionResponseService.load(inspectionResponseForm, account);
    }*/
}
