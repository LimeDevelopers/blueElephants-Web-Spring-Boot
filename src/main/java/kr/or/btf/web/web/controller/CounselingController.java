package kr.or.btf.web.web.controller;

import kr.or.btf.web.common.Constants;
import kr.or.btf.web.common.annotation.CurrentUser;
import kr.or.btf.web.domain.web.*;
import kr.or.btf.web.domain.web.enums.AdviceReservationDvType;
import kr.or.btf.web.domain.web.enums.ProcessType;
import kr.or.btf.web.domain.web.enums.TableNmType;
import kr.or.btf.web.services.web.*;
import kr.or.btf.web.web.form.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CounselingController extends BaseCont {
    private final AdviceRequestService adviceRequestService;
    private final FileInfoService fileInfoService;
    private final AdviceAnswerService adviceAnswerService;
    private final AdviceRequestTypeService adviceRequestTypeService;
    private final AdviceReservationService adviceReservationService;
    private final AdviceReservationTypeService adviceReservationTypeService;
    private final AdviceReservationTimeService adviceReservationTimeService;

    @RequestMapping({"/soulGod/counseling/list/{dvTy}"})
    public String list(Model model,
                               HttpSession session,
                               @PathVariable(name = "dvTy") AdviceReservationDvType dvTy,
                               @PageableDefault Pageable pageable,
                               @ModelAttribute SearchForm searchForm,
                               @CurrentUser Account account) {

        searchForm.setReservationDvTy(dvTy);
        model.addAttribute("dvTy", dvTy);
        Page<AdviceReservation> adviceReservations = adviceReservationService.list(pageable, searchForm);
        model.addAttribute("adviceReservations", adviceReservations);

        return "/soulGod/counseling/list";
    }

    @RequestMapping({"/soulGod/counseling/detail/{id}"})
    public String detail(Model model,
                       HttpSession session,
                       @ModelAttribute AdviceReservationForm adviceReservationForm,
                       @CurrentUser Account account) {

        AdviceReservation load = adviceReservationService.load(adviceReservationForm.getId());
        model.addAttribute("dvTy", load.getDvTy());
        model.addAttribute("form", load);
        List<AdviceReservationTime> loadTimes = adviceReservationTimeService.list(load.getId());
        model.addAttribute("loadTimes", loadTimes);

        List<AdviceReservationType> reservationTypeList = adviceReservationTypeService.list(load.getId());
        model.addAttribute("reservationTypeList", reservationTypeList);

        return "/soulGod/counseling/detail";
    }

    @RequestMapping({"/soulGod/counseling/listForBoard"})
    public String listForBoard(Model model,
                            HttpSession session,
                            @PageableDefault Pageable pageable,
                            @ModelAttribute SearchForm searchForm,
                            @CurrentUser Account account) {

        Page<AdviceRequest> adviceRequests = adviceRequestService.list(pageable, searchForm);
        model.addAttribute("adviceRequests", adviceRequests);

        return "/soulGod/counseling/listForBoard";
    }

    @RequestMapping("/soulGod/counseling/detailForBoard/{id}")
    public String detailForBoard(Model model,
                                    @ModelAttribute AdviceRequestForm adviceRequestForm,
                                    @Value("${Globals.fileStoreUriPath}") String filePath,
                                    @CurrentUser Account account) {

        AdviceRequest loadRequest = adviceRequestService.load(adviceRequestForm.getId());
        model.addAttribute("form", loadRequest);

        List<AdviceRequestType> requestTypeList = adviceRequestTypeService.list(loadRequest.getId());
        model.addAttribute("requestTypeList", requestTypeList);

        FileInfoForm fileInfoForm = new FileInfoForm();
        fileInfoForm.setDataPid(loadRequest.getId());
        fileInfoForm.setTableNm(TableNmType.TBL_ADVICE_REQUEST.name());
        List<FileInfo> fileList = fileInfoService.list(fileInfoForm);

        model.addAttribute("fileList", fileList);
        model.addAttribute("filePath", filePath+"/"+ Constants.FOLDERNAME_ADVICE);

        AdviceAnswer loadAnswer = adviceAnswerService.loadByAdvcReqPid(loadRequest.getId());
        model.addAttribute("loadAnswer", loadAnswer);

        return "/soulGod/counseling/detailForBoard";
    }

    @RequestMapping("/soulGod/counseling/registerForBoard/{id}")
    public String registerForBoard(Model model,
                                    @ModelAttribute AdviceRequestForm adviceRequestForm,
                                    @Value("${Globals.fileStoreUriPath}") String filePath,
                                    @CurrentUser Account account) {

        AdviceRequest loadRequest = adviceRequestService.load(adviceRequestForm.getId());
        model.addAttribute("form", loadRequest);

        List<AdviceRequestType> requestTypeList = adviceRequestTypeService.list(loadRequest.getId());
        model.addAttribute("requestTypeList", requestTypeList);

        FileInfoForm fileInfoForm = new FileInfoForm();
        fileInfoForm.setDataPid(loadRequest.getId());
        fileInfoForm.setTableNm(TableNmType.TBL_ADVICE_REQUEST.name());
        List<FileInfo> fileList = fileInfoService.list(fileInfoForm);

        model.addAttribute("fileList", fileList);
        model.addAttribute("filePath", filePath+"/"+ Constants.FOLDERNAME_ADVICE);

        AdviceAnswer loadAnswer = adviceAnswerService.loadByAdvcReqPid(loadRequest.getId());
        model.addAttribute("loadAnswer", loadAnswer);

        return "/soulGod/counseling/registerForBoard";
    }

    @RequestMapping("/api/counseling/answerProc")
    private String adviceAnswerProc(Model model,
                                    @ModelAttribute AdviceAnswerForm adviceAnswerForm,
                                    @CurrentUser Account account) {

        adviceAnswerForm.setRegPsId(account.getLoginId());
        adviceAnswerForm.setUpdPsId(account.getLoginId());
        adviceAnswerForm.setMberPid(account.getId());
        AdviceAnswer adviceAnswer = adviceAnswerForm.getId() == null ? adviceAnswerService.insert(adviceAnswerForm) : adviceAnswerService.update(adviceAnswerForm);

        if (adviceAnswer == null) {
            model.addAttribute("altmsg", "실패되었습니다 관리자에게 문의 하세요");
            model.addAttribute("locurl", "/soulGod/counseling/listForBoard");
            return "/message";
        } else {
            model.addAttribute("altmsg", "정상처리되었습니다.");
            model.addAttribute("locurl", "/soulGod/counseling/detailForBoard/"+adviceAnswer.getAdvcReqPid());
            return "/message";
        }

    }

    @ResponseBody
    @PostMapping("/api/counseling/answerDelete")
    private String adviceAnswerDelete(Model model,
                                      @RequestBody AdviceAnswerForm adviceAnswerForm,
                                      @CurrentUser Account account) {

        adviceAnswerForm.setUpdPsId(account.getLoginId());

        Boolean result =  adviceAnswerService.delete(adviceAnswerForm);

        String msg = "fail";
        if (result) {
            msg = "ok";
        } else {
            msg = "fail";
        }
        return msg;

    }

    @ResponseBody
    @PostMapping("/api/adviceReservation/updateProcessType")
    private String changeProcessType(Model model,
                                      @RequestBody AdviceReservationForm adviceReservationForm,
                                      @CurrentUser Account account) {

        adviceReservationForm.setProcessTy(ProcessType.COMPLETE);

        Boolean result =  adviceReservationService.updateProcessType(adviceReservationForm);

        String msg = "fail";
        if (result) {
            msg = "ok";
        } else {
            msg = "fail";
        }
        return msg;

    }

}
