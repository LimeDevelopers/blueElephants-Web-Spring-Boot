package kr.or.btf.web.web.controller;


import kr.or.btf.web.common.Constants;
import kr.or.btf.web.common.annotation.CurrentUser;
import kr.or.btf.web.domain.web.*;
import kr.or.btf.web.domain.web.enums.TableNmType;
import kr.or.btf.web.domain.web.enums.UserRollType;
import kr.or.btf.web.services.web.*;
import kr.or.btf.web.utils.FileUtilHelper;
import kr.or.btf.web.utils.StringHelper;
import kr.or.btf.web.web.form.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class BoardController extends BaseCont{

    private final BoardDataService boardDataService;
    private final CommonCodeService commonCodeService;
    private final BoardMasterService boardMasterService;
    private final FileInfoService fileInfoService;
    private final BoardTargetService boardTargetService;
    private final CommonCommentService commonCommentService;
    private final HashTagService hashTagService;

    @RequestMapping("/soulGod/board/list/{mstPid}")
    public String list(Model model,
                       @PageableDefault Pageable pageable,
                       @PathVariable(name = "mstPid") Long mstPid,
                       @ModelAttribute SearchForm searchForm,
                       @Value("${common.code.eduDataCdPid}") Long eduDataCdPid) {

        model.addAttribute("form", searchForm);
        model.addAttribute("mstPid", mstPid);
        model.addAttribute("eduData", eduDataCdPid);

        BoardMaster boardMaster = boardMasterService.load(mstPid);
        model.addAttribute("boardMaster", boardMaster);

        BoardDataForm boardDataForm = new BoardDataForm();
        boardDataForm.setMstPid(mstPid);
        Page<BoardData> boardDatas = boardDataService.list(pageable, searchForm, boardDataForm);
        model.addAttribute("boardDatas", boardDatas);


        model.addAttribute("mc", "board");

        return "/soulGod/board/list";
    }

    @RequestMapping("/soulGod/board/faqList/{mstPid}")
    public String faqList(Model model,
                          @PageableDefault Pageable pageable,
                          @PathVariable(name = "mstPid") Long mstPid,
                          @ModelAttribute SearchForm searchForm) {

        model.addAttribute("form", searchForm);
        model.addAttribute("mstPid", mstPid);

        BoardDataForm boardDataForm = new BoardDataForm();
        boardDataForm.setMstPid(mstPid);
        Page<BoardData> boardDatas = boardDataService.list(pageable, searchForm, boardDataForm);
        model.addAttribute("boardDatas", boardDatas);

        model.addAttribute("mc", "board");

        return "/soulGod/board/faqList";
    }

    @RequestMapping("/soulGod/board/qnaList/{mstPid}")
    public String qnaList(Model model,
                          @PageableDefault Pageable pageable,
                          @PathVariable(name = "mstPid") Long mstPid,
                          @ModelAttribute SearchForm searchForm) {

        model.addAttribute("form", searchForm);
        model.addAttribute("mstPid", mstPid);

        BoardDataForm boardDataForm = new BoardDataForm();
        boardDataForm.setMstPid(mstPid);
        Page<BoardData> boardDatas = boardDataService.list(pageable, searchForm, boardDataForm);
        model.addAttribute("boardDatas", boardDatas);

        model.addAttribute("mc", "board");

        return "/soulGod/board/qnaList";
    }

    @PostMapping("/api/board/qna/delete")
    public String qnaDelete(Model model,
                         @ModelAttribute BoardDataForm boardDataForm,
                         @RequestParam(name = "qnaQuestionId") Long qnaQuestionId,
                         @CurrentUser Account account) throws Exception {

        boardDataForm.setId(qnaQuestionId);
        boardDataForm.setUpdDtm(LocalDateTime.now());
        boardDataForm.setUpdPsId(account.getLoginId());
        boardDataForm.setDelAt("Y");
        boolean result = boardDataService.delete(boardDataForm);

        if (!result) {
            model.addAttribute("altmsg", "실패되었습니다 관리자에게 문의 하세요.");
            model.addAttribute("locurl", "/soulGod/board/qnaList/" + boardDataForm.getMstPid());
            return "/message";
        }

        return "redirect:/soulGod/board/qnaList/" + boardDataForm.getMstPid();
    }

    @GetMapping("/soulGod/board/register/{mstPid}")
    public String register(Model model,
                           @PathVariable(name = "mstPid") Long mstPid,
                           @Value("${common.code.eduDataCdPid}") Long eduDataCdPid) {

        model.addAttribute("eduData", eduDataCdPid);

        BoardMaster boardMaster = boardMasterService.load(mstPid);
        model.addAttribute("boardMaster", boardMaster);

        BoardDataForm boardDataForm = new BoardDataForm();
        model.addAttribute("form", boardDataForm);

        model.addAttribute("imageAccept", StringUtil.join(",", ArrayUtils.addAll(FileUtilHelper.imageExt)));
        model.addAttribute("allAccept", StringUtil.join(",", ArrayUtils.addAll(FileUtilHelper.getAllExt())));

        model.addAttribute("mc", "board");

        return "/soulGod/board/register";
    }

    @GetMapping({"/soulGod/board/faqRegister/{mstPid}","/soulGod/board/faqRegister/{mstPid}/{id}"})
    public String faqRegister(Model model,
                              @PathVariable(name = "id", required = false) Long id,
                              @PathVariable(name = "mstPid", required = false) Long mstPid) {

        BoardMaster boardMaster = boardMasterService.load(mstPid);
        model.addAttribute("boardMaster", boardMaster);

        model.addAttribute("mstPid", mstPid);

        BoardDataForm boardDataForm = new BoardDataForm();

        if (id != null) {
            boardDataForm.setId(id);
            BoardData form = boardDataService.load(boardDataForm);

            model.addAttribute("form", form);
        } else {
            model.addAttribute("form", boardDataForm);
        }

        return "/soulGod/board/faqRegister";
    }

    @GetMapping("/soulGod/board/detail/{id}")
    public String detail(Model model,
                         @Value("${Globals.fileStoreUriPath}") String filepath,
                         @CurrentUser Account account,
                         @PathVariable(name = "id") Long id,
                         @Value("${common.code.eduDataCdPid}") Long eduDataCdPid) {

        model.addAttribute("eduData", eduDataCdPid);

        BoardDataForm boardDataForm = new BoardDataForm();
        boardDataForm.setId(id);
        BoardData boardData = boardDataService.load(boardDataForm);
        model.addAttribute("form", boardData);

        BoardMaster boardMaster = boardMasterService.load(boardData.getMstPid());
        model.addAttribute("boardMaster", boardMaster);

        BoardTargetForm boardTargetForm = new BoardTargetForm();
        boardTargetForm.setDataPid(boardData.getId());
        List<BoardTarget> targetList = boardTargetService.listByDataPid(boardTargetForm);
        model.addAttribute("targetList", targetList);

        FileInfoForm fileInfoForm = new FileInfoForm();
        fileInfoForm.setDataPid(id);
        fileInfoForm.setTableNm(TableNmType.TBL_BOARD_DATA.name());
        List<FileInfo> fileList = fileInfoService.list(fileInfoForm);

        model.addAttribute("fileList", fileList);

        /*boardDataForm.setId(boardData.getId());
        boardDataForm.setReadCnt(boardData.getReadCnt()+1);
        boardDataService.updateByReadCnt(boardDataForm);*/

        model.addAttribute("filePath", filepath+"/"+ Constants.FOLDERNAME_BOARDDATA);

        return "/soulGod/board/detail";
    }

    @GetMapping("/soulGod/board/qnaDetail/{id}")
    public String qnaDetail(Model model,
                         @Value("${Globals.fileStoreUriPath}") String filepath,
                         @CurrentUser Account account,
                         @PathVariable(name = "id") Long id) {

        BoardDataForm boardDataForm = new BoardDataForm();
        boardDataForm.setId(id);
        BoardData boardData = boardDataService.load(boardDataForm);
        model.addAttribute("form", boardData);

        BoardMaster boardMaster = boardMasterService.load(boardData.getMstPid());
        model.addAttribute("boardType", boardMaster.getBbsTy().name());
        model.addAttribute("boardName", boardMaster.getBbsNm());

        FileInfoForm fileInfoForm = new FileInfoForm();
        fileInfoForm.setDataPid(id);
        fileInfoForm.setTableNm(TableNmType.TBL_BOARD_DATA.name());
        List<FileInfo> fileList = fileInfoService.list(fileInfoForm);

        model.addAttribute("fileList", fileList);

        CommonCommentForm commonCommentForm = new CommonCommentForm();
        commonCommentForm.setTableNm(TableNmType.TBL_BOARD_DATA);
        commonCommentForm.setDataPid(boardData.getId());
        CommonComment qnaAnswer = commonCommentService.loadByForm(commonCommentForm);
        model.addAttribute("qnaAnswer", qnaAnswer);

        if (qnaAnswer.getId() != null) {
            fileInfoForm = new FileInfoForm();
            fileInfoForm.setDataPid(qnaAnswer.getId());
            fileInfoForm.setTableNm(TableNmType.TBL_COMMON_COMMENT.name());
            List<FileInfo> reviewFileList = fileInfoService.list(fileInfoForm);
            model.addAttribute("reviewFileList", reviewFileList);
        }

        model.addAttribute("filePath", filepath+"/"+ Constants.FOLDERNAME_BOARDDATA);

        return "/soulGod/board/qnaDetail";
    }

    @GetMapping("/soulGod/board/qnaRegister/{id}")
    public String qnaRegister(Model model,
                            @Value("${Globals.fileStoreUriPath}") String filepath,
                            @CurrentUser Account account,
                            @PathVariable(name = "id") Long id) {

        BoardDataForm boardDataForm = new BoardDataForm();
        boardDataForm.setId(id);
        BoardData boardData = boardDataService.load(boardDataForm);
        model.addAttribute("form", boardData);

        BoardMaster boardMaster = boardMasterService.load(boardData.getMstPid());
        model.addAttribute("boardType", boardMaster.getBbsTy().name());
        model.addAttribute("boardName", boardMaster.getBbsNm());

        FileInfoForm fileInfoForm = new FileInfoForm();
        fileInfoForm.setDataPid(id);
        fileInfoForm.setTableNm(TableNmType.TBL_BOARD_DATA.name());
        List<FileInfo> fileList = fileInfoService.list(fileInfoForm);

        model.addAttribute("fileList", fileList);

        CommonCommentForm commonCommentForm = new CommonCommentForm();
        commonCommentForm.setTableNm(TableNmType.TBL_BOARD_DATA);
        commonCommentForm.setDataPid(boardData.getId());
        CommonComment qnaAnswer = commonCommentService.loadByForm(commonCommentForm);
        if (qnaAnswer.getId() == null) {
            qnaAnswer = new CommonComment();
        } else {
            fileInfoForm = new FileInfoForm();
            fileInfoForm.setDataPid(qnaAnswer.getId());
            fileInfoForm.setTableNm(TableNmType.TBL_COMMON_COMMENT.name());
            List<FileInfo> reviewFileList = fileInfoService.list(fileInfoForm);
            model.addAttribute("reviewFileList", reviewFileList);
        }
        model.addAttribute("qnaAnswer", qnaAnswer);

        model.addAttribute("allAccept", StringUtil.join(",", ArrayUtils.addAll(FileUtilHelper.getAllExt())));
        model.addAttribute("filePath", filepath+"/"+ Constants.FOLDERNAME_BOARDDATA);

        return "/soulGod/board/qnaRegister";
    }

    @GetMapping("/soulGod/board/modify/{id}")
    public String modify(Model model,
                         @PathVariable(name = "id") Long id,
                         @Value("${common.code.eduDataCdPid}") Long eduDataCdPid) {

        model.addAttribute("eduData", eduDataCdPid);

        BoardDataForm boardDataForm = new BoardDataForm();
        boardDataForm.setId(id);
        BoardData boardData = boardDataService.load(boardDataForm);
        model.addAttribute("form", boardData);

        BoardMaster boardMaster = boardMasterService.load(boardData.getMstPid());
        model.addAttribute("boardMaster", boardMaster);

        FileInfoForm fileInfoForm = new FileInfoForm();
        fileInfoForm.setDataPid(id);
        fileInfoForm.setTableNm(TableNmType.TBL_BOARD_DATA.name());
        List<FileInfo> fileList = fileInfoService.list(fileInfoForm);

        BoardTargetForm boardTargetForm = new BoardTargetForm();
        boardTargetForm.setDataPid(boardData.getId());
        List<BoardTarget> boardTargets = boardTargetService.listByDataPid(boardTargetForm);

        List<String> targetList = Lists.newArrayList();
        for (BoardTarget boardTarget : boardTargets) {
            targetList.add(boardTarget.getMberDvTy().name());
        }
        model.addAttribute("targetList", targetList);

        model.addAttribute("imageAccept", StringUtil.join(",", ArrayUtils.addAll(FileUtilHelper.imageExt)));
        model.addAttribute("allAccept", StringUtil.join(",", ArrayUtils.addAll(FileUtilHelper.getAllExt())));

        model.addAttribute("fileList", fileList);

        return "/soulGod/board/modify";
    }

    @PostMapping("/api/board/register")
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
        boardDataForm.setWrterNm(account.getNm());
        boardDataForm.setRegPsId(account.getLoginId());
        boardDataForm.setRegDtm(LocalDateTime.now());
        boardDataForm.setUpdPsId(account.getLoginId());
        boardDataForm.setUpdDtm(LocalDateTime.now());
        boardDataForm.setDelAt("N");

        if (boardDataForm.getHashTags() == null) {
            boardDataForm.setHashTags("#");
        }

        String[] tags = boardDataForm.getHashTags().split("#");
        int tagIdx = 0;
        for (String tag : tags) {
            if (!tag.equals("")) {
                tags[tagIdx] = tag.trim();
            }
            tagIdx++;
        }

        if (boardDataForm.getId().equals(0l)) {
            result = boardDataService.insert(boardDataForm, targetArr, thumbFile, attachedFile, tags);
        } else {
            result = boardDataService.update(boardDataForm, targetArr, thumbFile, attachedFile, tags);
        }

        if (boardDataForm.getMstPid() == 2L) {
            return "redirect:/soulGod/board/faqList/"+boardDataForm.getMstPid();
        }

        return "redirect:/soulGod/board/list/"+boardDataForm.getMstPid();
    }

    @PostMapping("/api/board/qnaAnswerProc")
    public String qnaAnswerProc(Model model,
                               @ModelAttribute CommonCommentForm commonCommentForm,
                                @RequestParam(name = "attachedFile", required = false) MultipartFile[] attachedFile,
                               @CurrentUser Account account,
                               HttpServletResponse response,
                                @Value("${common.code.qnaCdPid}") Long qnaCdPid,
                               HttpServletRequest request) {

        boolean result = false;

        commonCommentForm.setRegPsId(account.getLoginId());
        commonCommentForm.setRegDtm(LocalDateTime.now());
        commonCommentForm.setUpdPsId(account.getLoginId());
        commonCommentForm.setUpdDtm(LocalDateTime.now());
        commonCommentForm.setTableNm(TableNmType.TBL_BOARD_DATA);
        commonCommentForm.setDelAt("N");

        if (commonCommentForm.getId() == null) {
            result = commonCommentService.insert(commonCommentForm, attachedFile);
        } else {
            result = commonCommentService.update(commonCommentForm, attachedFile);
        }

        if (result) {
            model.addAttribute("altmsg", "정상처리되었습니다.");
        } else {
            model.addAttribute("altmsg", "실패되었습니다 관리자에게 문의 하세요.");
        }
        model.addAttribute("locurl", "/soulGod/board/qnaList/" + qnaCdPid);
        return "/message";
    }

    @PostMapping("/api/board/delete")
    public String delete(Model model,
                         @ModelAttribute BoardDataForm boardDataForm,
                         @Value("${common.code.faqCdPid}") Long faqCdPid,
                         @CurrentUser Account account) throws Exception {

        boardDataForm.setUpdDtm(LocalDateTime.now());
        boardDataForm.setUpdPsId(account.getLoginId());
        boardDataForm.setDelAt("Y");
        boolean delete = boardDataService.delete(boardDataForm);

        if (!UserRollType.MASTER.equals(account.getMberDvTy()) || UserRollType.ADMIN.equals(account.getMberDvTy())) {
            return "redirect:/pages/activity/policyProposal";
        }

        if (boardDataForm.getMstPid() == faqCdPid) {
            return "redirect:/soulGod/board/faqList/"+boardDataForm.getMstPid();
        }

        //redirect.addAttribute("srch_menu_group_cd_id", menu.getSrch_menu_group_cd_id());
        return "redirect:/soulGod/board/list/"+boardDataForm.getMstPid();
    }

}
