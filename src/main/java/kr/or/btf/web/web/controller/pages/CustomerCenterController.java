package kr.or.btf.web.web.controller.pages;


import kr.or.btf.web.common.Constants;
import kr.or.btf.web.common.annotation.CurrentUser;
import kr.or.btf.web.domain.web.*;
import kr.or.btf.web.domain.web.enums.TableNmType;
import kr.or.btf.web.services.web.BoardDataService;
import kr.or.btf.web.services.web.BoardMasterService;
import kr.or.btf.web.services.web.CommonCommentService;
import kr.or.btf.web.services.web.FileInfoService;
import kr.or.btf.web.utils.FileUtilHelper;
import kr.or.btf.web.utils.StringHelper;
import kr.or.btf.web.web.form.BoardDataForm;
import kr.or.btf.web.web.form.CommonCommentForm;
import kr.or.btf.web.web.form.FileInfoForm;
import kr.or.btf.web.web.form.SearchForm;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CustomerCenterController {

    private final BoardMasterService boardMasterService;
    private final BoardDataService boardDataService;
    private final FileInfoService fileInfoService;
    private final PasswordEncoder passwordEncoder;
    private final CommonCommentService commonCommentService;

    @RequestMapping("/pages/customerCenter/qna")
    public String qna(Model model,
                      @PageableDefault Pageable pageable,
                      @ModelAttribute SearchForm searchForm,
                      @Value("${common.code.qnaCdPid}") Long qnaCdPid) {

        BoardDataForm boardDataForm = new BoardDataForm();
        boardDataForm.setMstPid(qnaCdPid);
        Page<BoardData> boardDatas = boardDataService.list(pageable, searchForm, boardDataForm);
        model.addAttribute("boardDatas", boardDatas);

        BoardMaster boardMaster = boardMasterService.load(qnaCdPid);
        model.addAttribute("boardMaster", boardMaster);

        model.addAttribute("mc", "customerCenter");
        model.addAttribute("pageTitle", "1:1 문의");
        return "/pages/customerCenter/qna";
    }
    @RequestMapping("/pages/customerCenter/qnaDetail/{id}")
    public String qnaDetail(Model model,
                            @ModelAttribute BoardDataForm boardDataForm,
                            @CurrentUser Account account,
                            @Value("${Globals.fileStoreUriPath}") String filePath,
                            HttpSession session) {

        BoardData load = boardDataService.load(boardDataForm);

        if (boardDataForm.getPwd() == null) {
            model.addAttribute("altmsg", "비정상적인 접근방법입니다.");
            model.addAttribute("locurl", "/pages/customerCenter/qna");
            return "/message";
        }
        if (!passwordEncoder.matches(boardDataForm.getPwd(), load.getPwd())) {
            model.addAttribute("altmsg", "비밀번호가 일치하지 않습니다.");
            model.addAttribute("locurl", "/pages/customerCenter/qna?page="+boardDataForm.getPage());
            return "/message";
        }
        session.setAttribute("encodePwd", load.getPwd());

        model.addAttribute("form", load);

        FileInfoForm fileInfoForm = new FileInfoForm();
        fileInfoForm.setDataPid(load.getId());
        fileInfoForm.setTableNm(TableNmType.TBL_BOARD_DATA.name());
        List<FileInfo> fileList = fileInfoService.list(fileInfoForm);

        model.addAttribute("fileList", fileList);
        model.addAttribute("filePath", filePath+"/"+ Constants.FOLDERNAME_BOARDDATA);

        CommonCommentForm commonCommentForm = new CommonCommentForm();
        commonCommentForm.setTableNm(TableNmType.TBL_BOARD_DATA);
        commonCommentForm.setDataPid(load.getId());
        CommonComment qnaAnswer = commonCommentService.loadByForm(commonCommentForm);
        model.addAttribute("qnaAnswer", qnaAnswer);

        if (qnaAnswer.getId() != null) {
            fileInfoForm = new FileInfoForm();
            fileInfoForm.setDataPid(qnaAnswer.getId());
            fileInfoForm.setTableNm(TableNmType.TBL_COMMON_COMMENT.name());
            List<FileInfo> reviewFileList = fileInfoService.list(fileInfoForm);
            model.addAttribute("reviewFileList", reviewFileList);
        }

        model.addAttribute("mc", "customerCenter");
        model.addAttribute("pageTitle", "1:1 문의");
        return "/pages/customerCenter/qnaDetail";
    }
    @RequestMapping({"/pages/customerCenter/qnaRegister","/pages/customerCenter/qnaRegister/{id}"})
    public String qnaRegister(Model model,
                              @ModelAttribute BoardDataForm boardDataForm,
                              @CurrentUser Account account,
                              @Value("${Globals.fileStoreUriPath}") String filePath,
                              @Value("${common.code.qnaCdPid}") Long qnaCdPid,
                              HttpSession session) {

        BoardMaster boardMaster = boardMasterService.load(qnaCdPid);
        model.addAttribute("boardMaster", boardMaster);

        BoardData load = new BoardData();
        if (boardDataForm.getId() != null) {
            load = boardDataService.load(boardDataForm);
            if(!session.getAttribute("encodePwd").toString().equals(load.getPwd())){
                model.addAttribute("altmsg", "비정상적인 접근방법입니다.");
                model.addAttribute("locurl", "/pages/customerCenter/qna");
                return "/message";
            }
        }

        model.addAttribute("form", load);

        if (load.getId() != null) {
            FileInfoForm fileInfoForm = new FileInfoForm();
            fileInfoForm.setDataPid(load.getId());
            fileInfoForm.setTableNm(TableNmType.TBL_BOARD_DATA.name());
            List<FileInfo> fileList = fileInfoService.list(fileInfoForm);
            model.addAttribute("fileList", fileList);

            CommonCommentForm commonCommentForm = new CommonCommentForm();
            commonCommentForm.setTableNm(TableNmType.TBL_BOARD_DATA);
            commonCommentForm.setDataPid(load.getId());
            CommonComment qnaAnswer = commonCommentService.loadByForm(commonCommentForm);
            model.addAttribute("qnaAnswer", qnaAnswer);
        }

        model.addAttribute("filePath", filePath+"/"+ Constants.FOLDERNAME_BOARDDATA);


        model.addAttribute("mstPid", qnaCdPid);
        model.addAttribute("allAccept", StringUtil.join(",", ArrayUtils.addAll(FileUtilHelper.getAllExt())));

        model.addAttribute("mc", "customerCenter");
        model.addAttribute("pageTitle", "1:1 문의");
        return "/pages/customerCenter/qnaRegister";
    }

    @PostMapping("/pages/customerCenter/qnaDelete")
    public String delete(Model model,
                         @ModelAttribute BoardDataForm boardDataForm,
                         @CurrentUser Account account) throws Exception {

        boardDataForm.setUpdDtm(LocalDateTime.now());
        boardDataForm.setUpdPsId(account.getLoginId());
        boardDataForm.setDelAt("Y");
        boolean result = boardDataService.delete(boardDataForm);
        if (!result) {
            model.addAttribute("altmsg", "실패되었습니다 관리자에게 문의 하세요.");
            model.addAttribute("locurl", "/pages/customerCenter/qna");
            return "/message";
        }

        return "redirect:/pages/customerCenter/qna";
    }

    @RequestMapping("/pages/customerCenter/faq")
    public String faq(Model model,
                      @PageableDefault Pageable pageable,
                      @Value("${common.code.faqCdPid}") Long faqCdPid,
                      @ModelAttribute SearchForm searchForm) {

        model.addAttribute("form", searchForm);

        BoardDataForm boardDataForm = new BoardDataForm();
        boardDataForm.setMstPid(faqCdPid);
        Page<BoardData> boardDatas = boardDataService.list(pageable, searchForm, boardDataForm);
        model.addAttribute("boardDatas", boardDatas);

        BoardMaster boardMaster = boardMasterService.load(faqCdPid);
        model.addAttribute("boardMaster", boardMaster);

        model.addAttribute("mc", "customerCenter");
        model.addAttribute("pageTitle", "FAQ");
        return "/pages/customerCenter/faq";
    }

    @RequestMapping("/pages/customerCenter/policy")
    public String policy(Model model) {
        model.addAttribute("mc", "customerCenter");
        model.addAttribute("pageTitle", "운영약관");
        return "/pages/customerCenter/policy";
    }

    @RequestMapping("/pages/customerCenter/help")
    public String help(Model model) {
        model.addAttribute("mc", "customerCenter");
        model.addAttribute("pageTitle", "도움말");
        return "/pages/customerCenter/help";
    }

    @PostMapping("/api/openData/qna/register")
    public String registerProc(Model model,
                               @ModelAttribute BoardDataForm boardDataForm,
                               @RequestParam(name = "targetArr", required = false) String[] targetArr,
                               @RequestParam(name = "thumbFile", required = false) MultipartFile thumbFile,
                               @RequestParam(name = "attachedFile", required = false) MultipartFile[] attachedFile,
                               @CurrentUser Account account,
                               HttpServletResponse response,
                               RedirectAttributes redirect,
                               HttpServletRequest request) {

        boolean result = false;

        boardDataForm.setReadCnt(0);
        boardDataForm.setWrterIp(StringHelper.getClientIP(request));
        boardDataForm.setRegDtm(LocalDateTime.now());
        boardDataForm.setUpdDtm(LocalDateTime.now());
        boardDataForm.setDelAt("N");

        //String[] tags = boardDataForm.getHashTags().split("#");

        if (account != null) {
            boardDataForm.setUpdPsId(account.getLoginId());//
            boardDataForm.setWrterNm(account.getNm());//
            boardDataForm.setRegPsId(account.getLoginId());//
        }

        if (boardDataForm.getId() == null) {
            result = boardDataService.insert(boardDataForm, targetArr, thumbFile, attachedFile, null);
        } else {
            result = boardDataService.update(boardDataForm, targetArr, thumbFile, attachedFile, null);
        }

        if (result) {
            model.addAttribute("altmsg", "저장되었습니다.");

        } else {
            model.addAttribute("altmsg", "실패되었습니다 관리자에게 문의 하세요");
        }
        model.addAttribute("locurl", "/pages/customerCenter/qna");
        return "/message";
    }

    @ResponseBody
    @PostMapping("/api/openData/qna/checkPwd")
    private String resultRegister(Model model,
                                  @RequestBody BoardDataForm boardDataForm){

        BoardData load = boardDataService.load(boardDataForm);

        boolean result = false;

        if (passwordEncoder.matches(boardDataForm.getPwd(), load.getPwd())) {
            result = true;
        }

        String msg = "fail";
        if (result) {
            msg = "ok";
        } else {
            msg = "fail";
        }
        return msg;
    }

}
