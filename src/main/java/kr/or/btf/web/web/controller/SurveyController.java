package kr.or.btf.web.web.controller;

import kr.or.btf.web.common.Constants;
import kr.or.btf.web.common.annotation.CurrentUser;
import kr.or.btf.web.domain.web.*;
import kr.or.btf.web.domain.web.enums.AnswerType;
import kr.or.btf.web.domain.web.enums.SurveyDvType;
import kr.or.btf.web.services.web.*;
import kr.or.btf.web.web.form.*;
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
public class SurveyController extends BaseCont {

    private final SurveyService surveyService;
    private final SurveyQuestionItemService surveyQuestionItemService;
    private final SurveyAnswerItemService surveyAnswerItemService;
    private final CourseMasterRelService courseMasterRelService;
    private final SurveyResponseService surveyResponseService;
    private final CommonCodeService commonCodeService;

    @RequestMapping({"/soulGod/survey/list","/soulGod/survey/list/{dvTy}"})
    public String list(Model model,
                       @PageableDefault Pageable pageable,
                       @PathVariable(name = "dvTy", required = false) SurveyDvType dvTy,
                       @ModelAttribute SearchForm searchForm) {

        searchForm.setSurveyDvType(dvTy);
        model.addAttribute("form", searchForm);

        Page<Survey> surveys = surveyService.list(pageable, searchForm);
        model.addAttribute("surveys", surveys);

        model.addAttribute("mc", "survey");
        return "/soulGod/survey/list";
    }

    @GetMapping({"/soulGod/survey/register","/soulGod/survey/register/{dvTy}"})
    public String register(Model model,
                           @PathVariable(name = "dvTy", required = false) SurveyDvType dvTy) {

        //Course course = courseService.load(id);
        Survey survey = new Survey();
        if (dvTy != null) {
            survey.setDvTy(dvTy.name());
        }
        model.addAttribute("form", survey);

        return "/soulGod/survey/register";
    }

    @PostMapping("/soulGod/survey/register")
    public String registerProc(Model model,
                               @ModelAttribute SurveyForm surveyForm,
                               @CurrentUser Account account,
                               HttpServletResponse response,
                               RedirectAttributes redirect) throws Exception {

        surveyForm.setRegDtm(LocalDateTime.now());
        surveyForm.setRegPsId(account.getLoginId());
        surveyForm.setUpdDtm(LocalDateTime.now());
        surveyForm.setUpdPsId(account.getLoginId());
        surveyForm.setDelAt("N");

        Survey survey = surveyService.insert(surveyForm);

        if (surveyForm.getDvTy().equals(SurveyDvType.SELF.name())) {
            return "redirect:/soulGod/survey/list/" + surveyForm.getDvTy();
        } else {
            return "redirect:/soulGod/survey/list";
        }
    }

    @ResponseBody
    @PostMapping("/api/soulGod/question/register")
    public String questionRegister(Model model,
                               @ModelAttribute SurveyQuestionItemForm surveyQuestionItemForm,
                               @RequestParam(name = "answerCnts") String[] answerCnts,
                               @CurrentUser Account account,
                               HttpServletResponse response,
                               RedirectAttributes redirect) throws Exception {

        //자가진단일경우 해당문항에 답변자가 있는지 검사
        if (surveyQuestionItemForm.getDvTy().equals(SurveyDvType.SELF) && surveyQuestionItemForm.getId() != null) {
            if(!surveyResponseService.answerPsersonCtnCheck(surveyQuestionItemForm)) {
                String msg = "exist";
                return msg;
            }
        }

        surveyQuestionItemForm.setRegDtm(LocalDateTime.now());
        surveyQuestionItemForm.setRegPsId(account.getLoginId());
        surveyQuestionItemForm.setUpdDtm(LocalDateTime.now());
        surveyQuestionItemForm.setUpdPsId(account.getLoginId());
        surveyQuestionItemForm.setDelAt("N");
        List<SurveyAnswerItem> surveyAnswerItemList = new ArrayList<>();

        Integer answerCnt = surveyQuestionItemForm.getAnswerCnt();

        if (AnswerType.CHOICE.name().equals(surveyQuestionItemForm.getAswDvTy())) {
            for (int i = 0; i < answerCnt; i++) {
                SurveyAnswerItem surveyAnswerItem = new SurveyAnswerItem();
                surveyAnswerItem.setDelAt("N");
                surveyAnswerItem.setRegDtm(LocalDateTime.now());
                surveyAnswerItem.setRegPsId(account.getLoginId());
                surveyAnswerItem.setUpdDtm(LocalDateTime.now());
                surveyAnswerItem.setUpdPsId(account.getLoginId());
                surveyAnswerItem.setAnswerCnts(answerCnts[i]);
                surveyAnswerItemList.add(surveyAnswerItem);
            }
        } else {
            surveyQuestionItemForm.setAnswerCnt(0);
        }

        boolean result = surveyQuestionItemForm.getId() == null ?
                surveyQuestionItemService.insert(surveyQuestionItemForm, surveyAnswerItemList) :
                surveyQuestionItemService.update(surveyQuestionItemForm, surveyAnswerItemList);

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

    @GetMapping("/soulGod/survey/detail/{id}")
    public String detail(Model model,
                         @PathVariable(name = "id") Long id) {

        Survey survey = surveyService.load(id);
        model.addAttribute("form", survey);

        SurveyQuestionItemForm surveyQuestionItemForm = new SurveyQuestionItemForm();
        surveyQuestionItemForm.setQustnrPid(id);
        List<SurveyQuestionItem> questionItems = surveyQuestionItemService.list(surveyQuestionItemForm);
        model.addAttribute("questionItems", questionItems);

        return "/soulGod/survey/detail";
    }

    @ResponseBody
    @PostMapping("/api/soulGod/survey/detail")
    public SurveyQuestionItem questionDetail(Model model,
                                             @RequestBody SurveyQuestionItemForm surveyQuestionItemForm,
                                             @CurrentUser Account account,
                                             HttpServletResponse response) {

         SurveyQuestionItem load = surveyQuestionItemService.load(surveyQuestionItemForm.getId());

        return load;
    }

    @ResponseBody
    @PostMapping("/api/soulGod/answer/answerListLoad")
    public List<SurveyAnswerItem> answerItemList(Model model,
                                                 @RequestBody SurveyAnswerItemForm surveyAnswerItemForm,
                                                 HttpServletResponse response) {

        List<SurveyAnswerItem> surveyAnswerItemList = surveyAnswerItemService.list(surveyAnswerItemForm, null);

        return surveyAnswerItemList;
    }

    @PostMapping("/api/survey/questionDelete")
    public String questionDelete(Model model,
                                 @RequestParam(name = "checkBox") Long[] qesitmPids,
                                 @RequestParam(name = "qustnrPid") Long qustnrPid,
                                 @CurrentUser Account account) throws Exception{

        List<SurveyQuestionItem> list = new ArrayList<>();
        for (Long qesitmPid : qesitmPids) {
            SurveyQuestionItem surveyQuestionItem = new SurveyQuestionItem();
            surveyQuestionItem.setId(qesitmPid);
            surveyQuestionItem.setUpdDtm(LocalDateTime.now());
            surveyQuestionItem.setUpdPsId(account.getLoginId());
            surveyQuestionItem.setDelAt("Y");
            list.add(surveyQuestionItem);
        }

        surveyQuestionItemService.delete(list);

        return "redirect:/soulGod/survey/detail/" + qustnrPid;
    }

    @GetMapping("/soulGod/survey/modify/{id}")
    public String modify(Model model,
                         @PathVariable(name = "id") Long id) {

        Survey survey = surveyService.load(id);
        model.addAttribute("form", survey);

        CourseMasterRelForm crsMstForm = new CourseMasterRelForm();
        crsMstForm.setCrsPid(survey.getId());
        Integer[] snArr = {Constants.satisfSvySn};
        crsMstForm.setSnArr(snArr);
        List<CourseMasterRel> mstRelList = courseMasterRelService.list(crsMstForm);

        model.addAttribute("masterRelCnt", mstRelList.stream().count());

        return "/soulGod/survey/modify";
    }

    @PostMapping("/soulGod/survey/modify")
    public String modifyProc(Model model,
                             @ModelAttribute SurveyForm surveyForm,
                             @CurrentUser Account account,
                             HttpServletResponse response,
                             RedirectAttributes redirect) {

        surveyForm.setUpdDtm(LocalDateTime.now());
        surveyForm.setUpdPsId(account.getLoginId());

        boolean result = surveyService.update(surveyForm);
        //redirect.addAttribute("srchMnGbnCdPid", courseForm.getSrchMnGbnCdPid());

        return "redirect:/soulGod/survey/detail/" + surveyForm.getId();
    }

    @PostMapping("/soulGod/survey/delete")
    public String delete(Model model,
                         @ModelAttribute SurveyForm surveyForm,
                         @CurrentUser Account account) throws Exception {

        surveyForm.setUpdDtm(LocalDateTime.now());
        surveyForm.setUpdPsId(account.getLoginId());
        surveyForm.setDelAt("Y");
        surveyService.delete(surveyForm);

        if (surveyForm.getDvTy().equals(SurveyDvType.SELF.name())) {
            return "redirect:/soulGod/survey/list/" + surveyForm.getDvTy();
        } else {
            return "redirect:/soulGod/survey/list";
        }
    }

}
