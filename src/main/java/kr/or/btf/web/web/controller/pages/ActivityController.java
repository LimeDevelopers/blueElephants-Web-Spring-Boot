package kr.or.btf.web.web.controller.pages;


import kr.or.btf.web.common.Constants;
import kr.or.btf.web.common.annotation.CurrentUser;
import kr.or.btf.web.domain.web.*;
import kr.or.btf.web.domain.web.dto.CourseItemDto;
import kr.or.btf.web.domain.web.enums.*;
import kr.or.btf.web.services.web.*;
import kr.or.btf.web.utils.FileUtilHelper;
import kr.or.btf.web.utils.StringHelper;
import kr.or.btf.web.web.controller.BaseCont;
import kr.or.btf.web.web.form.*;
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
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ActivityController extends BaseCont {

    private final BoardMasterService boardMasterService;
    private final BoardDataService boardDataService;
    private final FileInfoService fileInfoService;
    private final CourseService courseService;
    private final CourseItemService courseItemService;
    private final CourseMasterRelService courseMasterRelService;
    private final PostscriptService postscriptService;
    private final CampaignService campaignService;
    private final CommonCommentService commonCommentService;
    private final SurveyService surveyService;
    private final SurveyQuestionItemService surveyQuestionItemService;
    private final SurveyAnswerItemService surveyAnswerItemService;
    private final SurveyResponsePersonService surveyResponsePersonService;
    private final SurveyResponseService surveyResponseService;
    private final ExperienceService experienceService;
    private final AdviceRequestService adviceRequestService;
    private final AdviceAnswerService adviceAnswerService;
    private final CourseMasterService courseMasterService;
    private final CourseTasteService courseTasteService;
    private final CommonCodeService commonCodeService;
    private final AdviceReservationService adviceReservationService;
    private final PasswordEncoder passwordEncoder;
    private final MyBoardDataService myBoardDataService;
    private final InspectionService inspectionService;
    private final InspectionQuestionItemService inspectionQuestionItemService;
    private final InspectionAnswerItemService inspectionAnswerItemService;
    private final InspectionResponseService inspectionResponseService;
    private final MemberSchoolService memberSchoolService;
    private final AdviceRequestTypeService adviceRequestTypeService;
    private final InspectionResponsePersonService inspectionResponsePersonService;
    private final CourseRequestCompleteService courseRequestCompleteService;
    private final CourseHisService courseHisService;
    private final BannerService bannerService;
    private final MemberService memberService;

    @GetMapping({"/pages/activity/eduIntro"})
    public String eduIntro(Model model,
                                   HttpSession session) {
        model.addAttribute("mc", "activity");
        model.addAttribute("pageTitle", "교육");
        return "/pages/activity/eduIntro";
    }

    private final CourseRequestService courseRequestService;

    @RequestMapping({"/pages/activity/preventionEdu", "/pages/activity/preventionEdu/{userGbn}"})
    public String list(Model model,
                       @Value("${Globals.fileStoreUriPath}") String filePath,
                       @PathVariable(name = "userGbn", required = false) String userGbn,
                       @PageableDefault Pageable pageable,
                       @CurrentUser Account account,
                       @ModelAttribute SearchForm searchForm) {

        if (userGbn != null) {
            searchForm.setMberDvType(MberDvType.valueOf(userGbn));
        }

        searchForm.setPageSize(Constants.DEFAULT_THUMBNAIL_PAGESIZE);
        searchForm.setApplyAble(true);
        if (account != null) {
            searchForm.setUserPid(account.getId());
            searchForm.setUseAt("Y");
            Page<CourseMaster> masterSeqs = courseMasterService.listByRequest(pageable, searchForm);
            model.addAttribute("masterSeqs", masterSeqs);
        } else {
            searchForm.setUseAt("Y");
            Page<CourseMaster> masterSeqs = courseMasterService.list(pageable,searchForm);
            model.addAttribute("masterSeqs", masterSeqs);
        }

        model.addAttribute("filePath", filePath+"/"+ Constants.FOLDERNAME_COURSEMASTERSEQ);

        model.addAttribute("userGbn", userGbn);

        model.addAttribute("mc", "activity");
        model.addAttribute("pageTitle", "교육");
        return "/pages/activity/preventionEdu";
    }

    @ResponseBody
    @RequestMapping("/api/pages/courseRequest/register")
    public String authSave(Model model,
                           HttpServletResponse response,
                           @RequestBody CourseRequestForm courseRequestForm,
                           @CurrentUser Account account) {

         if (account.getMberDvTy() == UserRollType.STUDENT) {
            MemberSchool memberSchool = memberSchoolService.loadByMber(account.getId());
            courseRequestForm.setAreaNm(memberSchool.getAreaNm());
            courseRequestForm.setSchlNm(memberSchool.getSchlNm());
            courseRequestForm.setGrade(memberSchool.getGrade());
            courseRequestForm.setBan(memberSchool.getBan());
            courseRequestForm.setNo(memberSchool.getNo());
        }

        courseRequestForm.setRegDtm(LocalDateTime.now());
        courseRequestForm.setConfmAt("Y");
        courseRequestForm.setMberPid(account.getId());

        CourseMasterRelForm courseMasterRelForm = new CourseMasterRelForm();
        courseMasterRelForm.setCrsMstPid(courseRequestForm.getCrsMstPid());
        List<CourseMasterRel> courseMasterRels = courseMasterRelService.list(courseMasterRelForm);

        boolean result = false;
        result = courseRequestService.insert(courseRequestForm, courseMasterRels);

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

    @RequestMapping({"/pages/activity/eduDataRoom"})
    public String eduDataRoom(Model model,
                              @Value("${Globals.fileStoreUriPath}") String rootPath,
                              @PageableDefault Pageable pageable,
                              @Value("${common.code.eduDataCdPid}") Long eduDataCdPid,
                              HttpSession session,
                              @ModelAttribute SearchForm searchForm) {

        BoardDataForm boardDataForm = new BoardDataForm();
        boardDataForm.setMstPid(eduDataCdPid);
        Page<BoardData> boardDatas = boardDataService.list(pageable, searchForm, boardDataForm);
        model.addAttribute("boardDatas", boardDatas);

        BoardMaster boardMaster = boardMasterService.load(eduDataCdPid);
        model.addAttribute("boardMaster", boardMaster);

        model.addAttribute("mc", "activity");
        model.addAttribute("pageTitle", "교육");
        model.addAttribute("rootPath", rootPath);
        return "/pages/activity/eduDataRoom";
    }

    @GetMapping({"/pages/activity/eduDataRoomDetail/{id}"})
    public String eduDataRoomDetail(Model model,
                              @Value("${Globals.fileStoreUriPath}") String rootPath,
                              @CurrentUser Account account,
                              @PathVariable(name = "id") Long id,
                              HttpSession session) {

        if (account == null) {
            model.addAttribute("altmsg", "로그인이 필요한 서비스입니다.");
            model.addAttribute("locurl", "/login");
            return "/message";
        }

        BoardDataForm boardDataForm = new BoardDataForm();
        boardDataForm.setId(id);
        BoardData load = boardDataService.load(boardDataForm);

        if (account.getMberDvTy() != UserRollType.MASTER && account.getMberDvTy() != UserRollType.ADMIN) {
            boolean chk = false;
            String[] targetArr = load.getTargetList().split(",");
            for (String target : targetArr) {
                if (account.getMberDvTy().name().equals(target)) {
                    chk = true;
                }
            }
            if (!chk) {
                model.addAttribute("altmsg", "해당 대상이 아닙니다.");
                model.addAttribute("locurl", "/pages/activity/eduDataRoom");
                return "/message";
            }
        }

        model.addAttribute("form", load);

        FileInfoForm fileInfoForm = new FileInfoForm();
        fileInfoForm.setDataPid(id);
        fileInfoForm.setTableNm(TableNmType.TBL_BOARD_DATA.name());
        List<FileInfo> fileList = fileInfoService.list(fileInfoForm);
        model.addAttribute("fileList", fileList);
        Long downloadCnt = 0l;
        for(FileInfo file : fileList) {
            downloadCnt += file.getDownloadCnt();
            if(file.getDvTy().equals(FileDvType.THUMB.name())){
                model.addAttribute("thumbFile", file);
            }
        }
        model.addAttribute("downloadCnt", downloadCnt);

        boardDataForm.setId(load.getId());
        boardDataForm.setReadCnt(load.getReadCnt()+1);
        boardDataService.updateByReadCnt(boardDataForm);

        model.addAttribute("mc", "activity");
        model.addAttribute("pageTitle", "교육");
        model.addAttribute("rootPath", rootPath);
        return "/pages/activity/eduDataRoomDetail";
    }

    @RequestMapping({"/pages/activity/postscript"})
    public String postscript(Model model,
                              @Value("${Globals.fileStoreUriPath}") String rootPath,
                              @PageableDefault Pageable pageable,
                              HttpSession session,
                              @ModelAttribute SearchForm searchForm,
                              @Value("${common.code.srtCodePid}") Long srtCodePid) {

        searchForm.setPageSize(9);
        Page<Postscript> postscripts = postscriptService.list(pageable, searchForm);
        model.addAttribute("postscripts", postscripts);

        List<CommonCode> commonCodes = commonCodeService.getCommonCodeParent(srtCodePid);
        model.addAttribute("commonCodes", commonCodes);

        model.addAttribute("filePath", rootPath + "/" + Constants.FOLDERNAME_POSTSCRIPT);

        model.addAttribute("mc", "activity");
        model.addAttribute("pageTitle", "교육");
        model.addAttribute("rootPath", rootPath);
        return "/pages/activity/postscript";
    }

    @RequestMapping({"/pages/activity/postscriptRegister", "/pages/activity/postscriptRegister/{id}"})
    public String postscriptRegister(Model model,
                                     @Value("${common.code.srtCodePid}") Long srtCodePid,
                                     @PathVariable(name = "id", required = false) Long id,
                                     @CurrentUser Account account) {

        Postscript postscript = new Postscript();

        if (id != null) {
            PostscriptForm postscriptForm = new PostscriptForm();
            postscriptForm.setId(id);
            postscript = postscriptService.loadByForm(postscriptForm);
            model.addAttribute("form", postscript);

            FileInfoForm fileInfoForm = new FileInfoForm();
            fileInfoForm.setDataPid(postscript.getId());
            fileInfoForm.setTableNm(TableNmType.TBL_BOARD_DATA.name());
            List<FileInfo> fileList = fileInfoService.list(fileInfoForm);
            model.addAttribute("fileList", fileList);
        } else {
            if (account != null) {
                Account user = memberService.findByIdAndMberDvTy(account.getId(), account.getMberDvTy());
                postscript.setAreaNm(user.getAreaNm());
                postscript.setSchlNm(user.getSchlNm());
                postscript.setGrade(user.getGrade());
                postscript.setBan(user.getBan());
                postscript.setNo(user.getNo());
            }
            model.addAttribute("form", postscript);
            model.addAttribute("fileList", new ArrayList<>());
        }

        List<CommonCode> srtCodes = commonCodeService.getCommonCodeParent(srtCodePid);
        model.addAttribute("srtCodes", srtCodes);

        model.addAttribute("accept", StringUtil.join(",", ArrayUtils.addAll(FileUtilHelper.imageExt)));

        model.addAttribute("mc", "activity");
        model.addAttribute("pageTitle", "교육");
        return "/pages/activity/postscriptRegister";
    }

    @GetMapping("/pages/activity/eduMasterClass/{id}")
    public String eduMasterClass(Model model,
                                 @Value("${Globals.fileStoreUriPath}") String filePath,
                                 @PathVariable(name = "id") Long id,
                                 @CurrentUser Account account,
                                 @PageableDefault Pageable pageable,
                                 HttpSession session) {

        CourseMaster masterSeqLoad = courseMasterService.load(id);
        model.addAttribute("form", masterSeqLoad);


        List<CourseMasterRel> rtnList = new ArrayList<>();
        CourseMasterRelForm masterForm = new CourseMasterRelForm();
        masterForm.setCrsMstPid(masterSeqLoad.getId());
        List<CourseMasterRel> masters = courseMasterRelService.list(masterForm);
       for (CourseMasterRel master : masters) {
            Map<String, Object> item = new HashMap<>();
            if (!master.getSn().equals(2) && !master.getSn().equals(6) && !master.getSn().equals(7)) {
                rtnList.add(master);
            }
        }

        model.addAttribute("masters", rtnList);

        Long atnlcReqPid = null;
        boolean afterInspection = false;
        boolean satisfaction = false;

        if (account != null) {
            atnlcReqPid = checkCourseRequest(account.getId(), masterSeqLoad.getId());
            if (atnlcReqPid != null) {
                afterInspection = checkCourseSn(account.getId(), masterSeqLoad.getId(), 6);
                satisfaction = checkCourseSn(account.getId(), masterSeqLoad.getId(), 7);
            }

        }
        model.addAttribute("afterInspection", afterInspection);
        model.addAttribute("satisfaction", satisfaction);
        model.addAttribute("atnlcReqPid",atnlcReqPid);

        model.addAttribute("filePath", filePath);
        model.addAttribute("courseTestsFolder", Constants.FOLDERNAME_COURSETASTE);
        model.addAttribute("courseFolder", Constants.FOLDERNAME_COURSE);

        model.addAttribute("userGbn", account != null ? account.getMberDvTy() : "");

        model.addAttribute("mc", "activity");
        model.addAttribute("pageTitle", "교육");
        return "/pages/activity/eduMasterClass";
    }

    @GetMapping("/pages/activity/eduClass/{crsMstPid}/{id}/{sn}")
    public String eduClass(Model model,
                           @Value("${Globals.fileStoreUriPath}") String filePath,
                           @PathVariable(name = "id") Long id,
                           @PathVariable(name = "crsMstPid") Long crsMstPid,
                           @PathVariable(name = "sn") Integer sn,
                           @CurrentUser Account account,
                           HttpSession session) {

        Long atnlcReqPid = null;

        if (account != null) {
            atnlcReqPid = checkCourseRequest(account.getId(), crsMstPid); //수강신청했는지 안했는지 검사
            boolean b = checkCourseSn(account.getId(), crsMstPid, sn);
            if (!b && atnlcReqPid != null) {
                if (sn == 3) {
                    model.addAttribute("altmsg","사전교육을 수강하기 전 \n사전검사를 먼저 수행해야 합니다.");
                    model.addAttribute("locurl","/pages/activity/preInspection/"+crsMstPid + "/" + (sn-1));
                } else if (sn == 4) {
                    model.addAttribute("altmsg","현장교육을 진행하기 전 \n사전교육을 먼저 수행해야 합니다.");
                    model.addAttribute("locurl","/pages/activity/eduMasterClass/"+crsMstPid);
                } else if (sn == 5) {
                    model.addAttribute("altmsg","사후교육을 수강하기 전 \n현장교육을 먼저 수행해야 합니다.");
                    model.addAttribute("locurl","/pages/activity/eduMasterClass/"+crsMstPid);
                } else {
                    model.addAttribute("altmsg","이미 진행하셨습니다.");
                    model.addAttribute("locurl","/pages/activity/eduMasterClass/"+crsMstPid);
                }
                return "/message";
            }
        } else {
            if (sn != 1) {
                model.addAttribute("altmsg", "로그인이 필요한 서비스입니다.");
                model.addAttribute("locurl", "/pages/activity/eduMasterClass/"+crsMstPid);
                return "/message";
            }
        }
        if (atnlcReqPid == null) {
            model.addAttribute("altmsg", "수강신청을 해야만 콘텐츠를 볼 수 있습니다.");
            model.addAttribute("locurl", "/pages/activity/eduMasterClass/"+crsMstPid);
            return "/message";
        }
        model.addAttribute("atnlcReqPid", atnlcReqPid);

        model.addAttribute("sn", sn);
        model.addAttribute("crsMstPid", crsMstPid);

        CourseForm courseForm = new CourseForm();
        courseForm.setId(id);
        Course course = courseService.loadByform(courseForm);
        model.addAttribute("form", course);

        CourseItemForm courseItemForm = new CourseItemForm();
        courseItemForm.setCrsPid(id);
        courseItemForm.setSorting("my");
        if (account != null && atnlcReqPid != null) { //로그인을했으며 수강신청도 했는지를 확인
            List<CourseItemDto> courseItems = courseItemService.listForProcNm(courseItemForm.getCrsPid(), account.getId());
             model.addAttribute("courseItems", courseItems);
        } else {
            List<CourseItem> courseItems = courseItemService.list(courseItemForm);
            model.addAttribute("courseItems", courseItems);
        }

        model.addAttribute("prior", StepType.PRIOR.name());
        model.addAttribute("filePath", filePath+"/"+ Constants.FOLDERNAME_COURSEITEM);

        model.addAttribute("mc", "activity");
        model.addAttribute("pageTitle", "교육");
        return "/pages/activity/eduClass";
    }

    @RequestMapping({"/pages/activity/eduClassDetail/{crsMstPid}/{id}/{sn}"})
    public String eduClassDetail(Model model,
                                 @Value("${Globals.fileStoreUriPath}") String filePath,
                                 //@PageableDefault Pageable pageable,
                                 @PathVariable(name = "id") Long id,
                                 @PathVariable(name = "sn") Integer sn,
                                 @PathVariable(name = "crsMstPid") Long crsMstPid,
                                 @CurrentUser Account account,
                                 HttpSession session) {
        Long atnlcReqPid = null;

        boolean completeBool = false;
        if (account != null) {
            atnlcReqPid = checkCourseRequest(account.getId(), crsMstPid); //수강신청했는지 안했는지 검사
            completeBool = checkCourseSn(account.getId(), crsMstPid, sn);
            if (!completeBool && atnlcReqPid != null && atnlcReqPid != 0L) {
                if (sn == 3) {
                    model.addAttribute("altmsg","사전교육을 수강하기 전 \n사전검사를 먼저 수행해야 합니다.");
                    model.addAttribute("locurl","/pages/activity/preInspection/"+crsMstPid + "/" + (sn-1));
                } else if (sn == 4) {
                    model.addAttribute("altmsg","현장교육을 진행하기 전 \n사전교육을 먼저 수행해야 합니다.");
                    model.addAttribute("locurl","/pages/activity/eduMasterClass/"+crsMstPid);
                } else if (sn == 5) {
                    model.addAttribute("altmsg","사후교육을 수강하기 전 \n현장교육을 먼저 수행해야 합니다.");
                    model.addAttribute("locurl","/pages/activity/eduMasterClass/"+crsMstPid);
                } else {
                    model.addAttribute("altmsg","이미 진행하셨습니다.");
                    model.addAttribute("locurl","/pages/activity/eduMasterClass/"+crsMstPid);
                }
                return "/message";
            }
        }
        model.addAttribute("atnlcReqPid", atnlcReqPid);

        model.addAttribute("crsMstPid", crsMstPid);
        model.addAttribute("crssqPid", id);
        model.addAttribute("sn", sn);

        boolean isRestudy = false;
        Long cntntsLen = null;
        CourseItem courseItem = courseItemService.load(id);
        if (account != null && atnlcReqPid != null) { //로그인, 수강신청 했는지 확인
            CourseHis courseHis = courseHisService.findTop1MberPidAndCrssqPidOrderByAtnlcHourDescAtnlcDtmDesc(account.getId(), id);
            if(courseHis != null){
                if (courseHis.getCntntsLen() != null && courseHis.getCntntsLen() > 0) {
                    cntntsLen = courseHis.getCntntsLen();
                }

                Double procPer = courseHis.getAtnlcHour() != 0 && courseHis.getCntntsLen() != null && courseHis.getCntntsLen() > 0L ? (double)courseHis.getAtnlcHour() / courseHis.getCntntsLen() * 100 : 0;
                if (completeBool && procPer >= 100) {
                    isRestudy = true;
                }
                model.addAttribute("procPer", procPer);
                model.addAttribute("prgrssSec", courseHis.getAtnlcHour());
            }
        } else {
            courseItem.setCntntsUrl(null);//수강신청 pid가 없거나 비로그인시 컨텐츠 url, null
        }
        model.addAttribute("crsPid", courseItem.getCrsPid());
        model.addAttribute("form", courseItem);

        if (courseItem.getCntntsLen() != null && courseItem.getCntntsLen() > 0) {
            cntntsLen = courseItem.getCntntsLen();
        }

        model.addAttribute("cntntsLen", cntntsLen);
        model.addAttribute("isRestudy", isRestudy);

        /*CommonCommentForm commonCommentForm = new CommonCommentForm();
        commonCommentForm.setDataPid(courseItem.getId());
        commonCommentForm.setTableNm(TableNmType.TBL_COUSE_ITEM);
        Page<CommonComment> commonComments = commonCommentService.list(pageable, commonCommentForm);
        model.addAttribute("commonComments", commonComments);*/

        FileInfoForm fileInfoForm = new FileInfoForm();
        fileInfoForm.setDataPid(courseItem.getId());
        fileInfoForm.setTableNm(TableNmType.TBL_COUSE_ITEM.name());
        List<FileInfo> fileList = fileInfoService.list(fileInfoForm);
        model.addAttribute("fileList",fileList);

        model.addAttribute("filePath", filePath+"/"+ Constants.FOLDERNAME_COURSEITEM);

        model.addAttribute("mc", "activity");
        model.addAttribute("pageTitle", "교육");
        return "/pages/activity/eduClassDetail";
    }

    @RequestMapping({"/pages/activity/eduTasteDetail/{crsMstPid}/{id}"})
    public String eduTasteDetail(Model model,
                                 @Value("${Globals.fileStoreUriPath}") String filePath,
                                 @PageableDefault Pageable pageable,
                                 @PathVariable(name = "id") Long id,
                                 @PathVariable(name = "crsMstPid") Long crsMstPid,
                                 @CurrentUser Account account,
                                 HttpSession session) {

        model.addAttribute("crsMstPid", crsMstPid);

        CourseTaste courseTaste = courseTasteService.load(id);
        model.addAttribute("form", courseTaste);

        model.addAttribute("filePath", filePath+"/"+ Constants.FOLDERNAME_COURSEITEM);

        model.addAttribute("mc", "activity");
        model.addAttribute("pageTitle", "교육");
        return "/pages/activity/eduTasteDetail";
    }

    @GetMapping({"/pages/activity/popup/videoLecture"})
    public String videoLecture(Model model,
                                 HttpSession session) {
        model.addAttribute("mc", "activity");
        model.addAttribute("pageTitle", "교육");
        return "/pages/activity/popup/videoLecture";
    }

    @RequestMapping("/pages/activity/preInspection/{crsMstPid}/{sn}")
    public String preInspection01(Model model,
                                  @ModelAttribute InspectionForm inspectionForm,
                                  @CurrentUser Account account,
                                  @PathVariable(name = "crsMstPid") Long crsMstPid,
                                  @PathVariable(name = "sn") Integer sn) {

        if (account == null) {
            model.addAttribute("altmsg", "로그인이 필요한 서비스입니다.");
            model.addAttribute("locurl", "/login");
            return "/message";
        }

        //url치고 들어오는것을 방지하기위해 유효성검사 완료상태인지 체크 (이전단계가 완료되었는지를 확인하는 함수여서 sn에 1을 더해줌)
        boolean b = checkCourseSn(account.getId(), crsMstPid, sn + 1);
        if (b) {
            model.addAttribute("altmsg","이미 검사를 진행하셨습니다.\n다음단계를 진행해주세요.");
            model.addAttribute("locurl","/pages/activity/eduMasterClass/"+crsMstPid);
            return "/message";
        }
        if (sn == 2) {
            inspectionForm.setInspctDvTy(InspectionDvType.BEFORE.name());
        } else {
            inspectionForm.setInspctDvTy(InspectionDvType.AFTER.name());
        }

        /*if (inspectionForm.getSectionIndex() == null || Constants.inspectionQuestionDvCodeList.length <= inspectionForm.getSectionIndex()) {
            //배열 범위가 아닐 경우 에러
            model.addAttribute("altmsg", "잘못된 접근입니다.");
            model.addAttribute("locurl", "/pages/activity/eduMasterClass/"+crsMstPid);
            return "/message";
        }*/

        Integer selectionIndex = (inspectionForm.getSectionIndex() == null ? 0 : inspectionForm.getSectionIndex());
        Long dvCodePid = Constants.inspectionQuestionDvCodeList[selectionIndex];
        InspectionDvType inspctDvTy = InspectionDvType.valueOf(inspectionForm.getInspctDvTy());

        model.addAttribute("inspctDvTy",inspctDvTy);

        CourseMasterRelForm courseMasterRelForm = new CourseMasterRelForm();
        courseMasterRelForm.setCrsMstPid(crsMstPid);
        courseMasterRelForm.setSn(sn);
        CourseMasterRel mstLoad = courseMasterRelService.loadByform(courseMasterRelForm);

        model.addAttribute("mstLoad",mstLoad);

        CourseRequestForm courseRequestForm = new CourseRequestForm();
        courseRequestForm.setMberPid(account.getId());
        courseRequestForm.setCrsMstPid(mstLoad.getCrsMstPid());
        CourseRequest courseRequest = courseRequestService.loadByform(courseRequestForm);

        CourseRequestCompleteForm courseRequestCompleteForm = new CourseRequestCompleteForm();
        courseRequestCompleteForm.setAtnlcReqPid(courseRequest.getId());
        courseRequestCompleteForm.setCrsMstPid(mstLoad.getCrsMstPid());
        courseRequestCompleteForm.setCrsPid(mstLoad.getCrsPid());
        courseRequestCompleteForm.setSn(sn);

        model.addAttribute("sectionIndex", selectionIndex);
        model.addAttribute("totalIndex", Constants.inspectionQuestionDvCodeList.length);

        InspectionQuestionItemForm inspectionQuestionItemForm = new InspectionQuestionItemForm();
        inspectionQuestionItemForm.setInspctPid(mstLoad.getCrsPid());
        inspectionQuestionItemForm.setDvCodePid(dvCodePid);

        Map<String, Object> rtnMap = new HashMap<>();

        CommonCode commonCode = commonCodeService.findById(dvCodePid);
        rtnMap.put("section", commonCode);

        Inspection inspection = inspectionService.load(mstLoad.getCrsPid());
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
                        questionItem.put("caseList", getCaseList(qItem, account, inspctDvTy.name(), courseRequest.getId()));
                    } else {
                        questionItem.put("answer", getAnswer(qItem, account, inspctDvTy.name(), courseRequest.getId()));
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
                                    subQuestionItem.put("caseList", getCaseList(subItem, account, inspctDvTy.name(), courseRequest.getId()));
                                } else {
                                    subQuestionItem.put("answer", getAnswer(subItem, account, inspctDvTy.name(), courseRequest.getId()));
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

        model.addAttribute("rtnMap", rtnMap);

        model.addAttribute("mc", "activity");
        model.addAttribute("pageTitle", "교육");
        return "/pages/activity/preInspection01";
    }

    // 사전검사 섹션별로 저장
    @ResponseBody
    @PostMapping("/api/preInspection/register")
    private String preInspectionProc(Model model,
                                  @ModelAttribute InspectionForm inspectionForm,
                                  @ModelAttribute InspectionQuestionItemForm inspectionQuestionItemForm,
                                  @ModelAttribute CourseMasterRelForm courseMasterRelForm,
                                  @CurrentUser Account account,
                                  HttpServletRequest request,
                                  HttpServletResponse response) throws ServletRequestBindingException {

        String msg = "fail";

        CourseMasterRel mstLoad = courseMasterRelService.loadByform(courseMasterRelForm);

        CourseRequestForm courseRequestForm = new CourseRequestForm();
        courseRequestForm.setMberPid(account.getId());
        courseRequestForm.setCrsMstPid(mstLoad.getCrsMstPid());
        CourseRequest courseRequest = courseRequestService.loadByform(courseRequestForm);
        CourseRequestCompleteForm courseRequestCompleteForm = new CourseRequestCompleteForm();
        courseRequestCompleteForm.setAtnlcReqPid(courseRequest.getId());
        courseRequestCompleteForm.setCrsMstPid(mstLoad.getCrsMstPid());
        courseRequestCompleteForm.setCrsPid(mstLoad.getCrsPid());
        courseRequestCompleteForm.setSn(courseMasterRelForm.getSn());

        //유효성검사 완료상태인지 체크
        if (courseRequestCompleteService.inspectionChk(courseRequestCompleteForm)) {
            model.addAttribute("altmsg", "이미 검사를 진행하셨습니다.\n다음단계를 진행해주세요.");
            model.addAttribute("locurl", "/pages/activity/eduMasterClass/"+mstLoad.getCrsMstPid());
            return "/message";
        }

        Long dvCodePid = Constants.inspectionQuestionDvCodeList[inspectionForm.getSectionIndex()];

        inspectionQuestionItemForm.setDvCodePid(dvCodePid);

        List<InspectionQuestionItem> questionItemListList = inspectionQuestionItemService.list(inspectionQuestionItemForm);

        InspectionResponsePersonForm inspectionResponsePersonForm = new InspectionResponsePersonForm();
        inspectionResponsePersonForm.setLoginId(account.getLoginId());
        inspectionResponsePersonForm.setNm(account.getNm());
        inspectionResponsePersonForm.setMberDvty(account.getMberDvTy().name());
        inspectionResponsePersonForm.setInspctDvTy(inspectionForm.getInspctDvTy());
        inspectionResponsePersonForm.setRegPsId(account.getLoginId());
        inspectionResponsePersonForm.setRegDtm(LocalDateTime.now());
        inspectionResponsePersonForm.setUpdPsId(account.getLoginId());
        inspectionResponsePersonForm.setUpdDtm(LocalDateTime.now());
        inspectionResponsePersonForm.setDelAt("N");
        inspectionResponsePersonForm.setInspctPid(inspectionQuestionItemForm.getInspctPid());
        inspectionResponsePersonForm.setAtnlcReqPid(courseRequest.getId());

        List<List<InspectionResponseForm>> responseFormList = new ArrayList<>();

        boolean check = true;
        for (InspectionQuestionItem inspectionQuestionItem : questionItemListList) {
            List<InspectionResponseForm> subList = new ArrayList<>();
            Integer answerCnt = inspectionQuestionItem.getAnswerCnt();
            Integer rspnsCnt = inspectionQuestionItem.getRspnsCnt();

            int cntsCnt = 0;
            if (answerCnt > 0) { //객관식일때
                String[] answer = ServletRequestUtils.getStringParameters(request, "answer" + inspectionQuestionItem.getId());
                if ((rspnsCnt > 0 && answer == null) || (rspnsCnt > 0 && answer.length != rspnsCnt)) {
                    check = false;
                    break;
                }
                if (answer.length != 0) {
                    for (String s : answer) {
                        if (s != null && !"".equals(s)) {
                            InspectionResponseForm inspectionResponseForm = new InspectionResponseForm();
                            //응답자 pid는 service에서 넣어줌
                            inspectionResponseForm.setQesitmPid(inspectionQuestionItem.getId());
                            inspectionResponseForm.setAswPid(Long.parseLong(s));
                            String answerCnts = ServletRequestUtils.getStringParameter(request, "answerCnts" + inspectionQuestionItem.getId() + "_" + Long.parseLong(s));
                            inspectionResponseForm.setAnswerCnts(answerCnts);
                            subList.add(inspectionResponseForm);
                        }
                    }
                } else {
                    InspectionResponseForm inspectionResponseForm = new InspectionResponseForm();
                    inspectionResponseForm.setQesitmPid(inspectionQuestionItem.getId());
                    subList.add(inspectionResponseForm);
                }

            } else { //주관식일때
                String answer = ServletRequestUtils.getStringParameter(request, "answer" + inspectionQuestionItem.getId());
                if (answer == null || (rspnsCnt > 0 && "".equals(answer))) {
                    check = false;
                    break;
                }
                InspectionResponseForm inspectionResponseForm = new InspectionResponseForm();
                inspectionResponseForm.setQesitmPid(inspectionQuestionItem.getId());
                inspectionResponseForm.setAnswerCnts(answer);
                subList.add(inspectionResponseForm);
            }
            responseFormList.add(subList);

        }

        Boolean result = false;
        if (check) {
            result = inspectionResponsePersonService.insert(inspectionResponsePersonForm, responseFormList, inspectionForm.getSectionIndex(), courseRequestCompleteForm);
        }

        if (result && Constants.inspectionQuestionDvCodeList.length > inspectionForm.getSectionIndex() && inspectionForm.getSectionIndex().equals(5)) {
            msg = "end";
            response.setStatus(200);
        } else if (result) {
            msg = "ok";
            response.setStatus(200);
        } else{
            msg = "fail";
            response.setStatus(401);
        }
        return msg;
    }

    private List<InspectionAnswerItem> getCaseList(InspectionQuestionItem item, Account account, String inspctDvTy, Long atnlcReqPid) {
        InspectionAnswerItemForm inspectionAnswerItemForm = new InspectionAnswerItemForm();
        inspectionAnswerItemForm.setQesitmPid(item.getId());
        inspectionAnswerItemForm.setInspctPid(item.getInspctPid());
        inspectionAnswerItemForm.setInspctDvTy(inspctDvTy);
        return inspectionAnswerItemService.list(inspectionAnswerItemForm, account, atnlcReqPid);
    }
    private InspectionResponse getAnswer(InspectionQuestionItem item, Account account, String inspctDvTy, Long atnlcReqPid) {
        InspectionResponseForm inspectionResponseForm = new InspectionResponseForm();
        inspectionResponseForm.setQesitmPid(item.getId());
        inspectionResponseForm.setInspctDvTy(inspctDvTy);
        return inspectionResponseService.load(inspectionResponseForm, account, atnlcReqPid);
    }

    @RequestMapping("/pages/activity/preInspection03/{crsMstPid}/{sn}")
    public String preInspection03(Model model,
                                  @CurrentUser Account account,
                                  @ModelAttribute CourseMasterRelForm courseMasterRelForm) {
        if (account == null) {
            model.addAttribute("altmsg", "로그인이 필요한 서비스입니다.");
            model.addAttribute("locurl", "/login");
            return "/message";
        }

        CourseMasterRel mstLoad = courseMasterRelService.loadByform(courseMasterRelForm);

        CourseRequestForm courseRequestForm = new CourseRequestForm();
        courseRequestForm.setMberPid(account.getId());
        courseRequestForm.setCrsMstPid(mstLoad.getCrsMstPid());
        CourseRequest courseRequest = courseRequestService.loadByform(courseRequestForm);

        model.addAttribute("atnlcReqPid", courseRequest.getId());
        model.addAttribute("crsMstPid", courseMasterRelForm.getCrsMstPid());
        model.addAttribute("sn", courseMasterRelForm.getSn());

        model.addAttribute("mc", "activity");
        model.addAttribute("pageTitle", "교육");
        return "/pages/activity/preInspection03";
    }

    @RequestMapping("/pages/activity/satisfactionTest/{crsMstPid}")
    public String satisfactionTest(Model model,
                                   @CurrentUser Account account,
                                   @PathVariable(name = "crsMstPid") Long crsMstPid) {

        Long atnlcReqPid = null;

        atnlcReqPid = checkCourseRequest(account.getId(), crsMstPid); //수강신청했는지 안했는지 검사

        if (atnlcReqPid == null) {
            model.addAttribute("altmsg", "잘못된 접근입니다.");
            model.addAttribute("locurl", "/login");
            return "/message";
        }

        boolean a = checkCourseSn(account.getId(), crsMstPid, 7);
        if (!a) {
            model.addAttribute("altmsg", "만족도검사를 진행하기 전 \n사후검사를 먼저 수행해야 합니다.");
            model.addAttribute("locurl", "/pages/activity/eduMasterClass/"+crsMstPid);
            return "/message";
        }

        boolean b = checkCourseSn(account.getId(), crsMstPid, 8); //이전과정을 확인하는 함수여서 sn에 8을넣어줌
        if (b) {
            model.addAttribute("altmsg", "이미 만족도검사를 진행하셨습니다.");
            model.addAttribute("locurl", "/pages/activity/eduMasterClass/"+crsMstPid);
            return "/message";
        }

        CourseMasterRelForm courseMasterRelForm = new CourseMasterRelForm();
        courseMasterRelForm.setCrsMstPid(crsMstPid);
        courseMasterRelForm.setSn(Constants.satisfSvySn);
        CourseMasterRel mst = courseMasterRelService.loadByform(courseMasterRelForm);

        List<Map<String, Object>> rtnList = new ArrayList<>();

        SurveyForm survey = new SurveyForm();
        survey.setId(mst.getCrsPid());
        Survey load = surveyService.loadByform(survey);
        model.addAttribute("form",load);

        SurveyQuestionItemForm surveyQuestionItem = new SurveyQuestionItemForm();
        surveyQuestionItem.setQustnrPid(load.getId());
        List<SurveyQuestionItem> questionItemList = surveyQuestionItemService.list(surveyQuestionItem);

        for (SurveyQuestionItem questionItem : questionItemList) {
            Map<String, Object> item = new HashMap<>();
            item.put("question", questionItem);

            SurveyAnswerItemForm surveyAnswerItem = new SurveyAnswerItemForm();
            surveyAnswerItem.setQesitmPid(questionItem.getId());
            List<SurveyAnswerItem> answerItemList = surveyAnswerItemService.list(surveyAnswerItem, null);
            item.put("caseList", answerItemList);

            rtnList.add(item);
        }
        model.addAttribute("list", rtnList);

        model.addAttribute("crsMstPid", crsMstPid);
        model.addAttribute("mc", "activity");
        model.addAttribute("pageTitle", "교육");
        return "/pages/activity/satisfactionTest";
    }

    @RequestMapping("/pages/activity/selfTest")
    public String selfTest(Model model,
                           @CurrentUser Account account) {

        if (account == null) {
            model.addAttribute("altmsg", "로그인이 필요한 서비스입니다.");
            model.addAttribute("locurl", "/login");
            return "/message";
        }

        List<Map<String, Object>> rtnList = new ArrayList<>();

        SurveyForm survey = new SurveyForm();
        survey.setDvTy(SurveyDvType.SELF.name());
        Survey load = surveyService.loadByform(survey);

        SurveyResponsePersonForm surveyResponsePersonForm = new SurveyResponsePersonForm();
        surveyResponsePersonForm.setLoginId(account.getLoginId());
        surveyResponsePersonForm.setQustnrPid(load.getId());
        SurveyResponsePerson person = surveyResponsePersonService.loadByform(surveyResponsePersonForm);

        model.addAttribute("form",load);

        SurveyQuestionItemForm surveyQuestionItem = new SurveyQuestionItemForm();
        surveyQuestionItem.setQustnrPid(load.getId());
        List<SurveyQuestionItem> questionItemList = surveyQuestionItemService.list(surveyQuestionItem);

        for (SurveyQuestionItem questionItem : questionItemList) {
            Map<String, Object> item = new HashMap<>();
            item.put("question", questionItem);

            SurveyAnswerItemForm surveyAnswerItem = new SurveyAnswerItemForm();
            surveyAnswerItem.setQesitmPid(questionItem.getId());
            List<SurveyAnswerItem> answerItemList = surveyAnswerItemService.list(surveyAnswerItem, person.getId());
            item.put("caseList", answerItemList);

            rtnList.add(item);
        }
        model.addAttribute("list", rtnList);

        model.addAttribute("mc", "activity");
        model.addAttribute("pageTitle", "체험");
        return "/pages/activity/selfTest";
    }

    @RequestMapping("/pages/activity/selfTestResult")
    public String selfTestResult(Model model,
                                 @ModelAttribute SurveyForm survey,
                                 @CurrentUser Account account) {

        SurveyForm surveyForm = new SurveyForm();
        surveyForm.setDvTy(SurveyDvType.SELF.name());
        Survey load = surveyService.loadByform(surveyForm);

        if (survey.getId() == null) {
            survey.setId(load.getId());
        }

        SurveyResponsePersonForm surveyResponsePersonForm = new SurveyResponsePersonForm();
        surveyResponsePersonForm.setLoginId(account.getLoginId());
        surveyResponsePersonForm.setQustnrPid(load.getId());
        SurveyResponsePerson person = surveyResponsePersonService.loadByform(surveyResponsePersonForm);
        if (person.getId() == null) {
            model.addAttribute("altmsg", "아직 자가진단을 하지 않으셨습니다.");
            model.addAttribute("locurl", "/pages/activity/experienceList");
            return "/message";
        }

        SurveyQuestionItemForm surveyQuestionItem = new SurveyQuestionItemForm();
        surveyQuestionItem.setQustnrPid(survey.getId());
        List<SurveyQuestionItem> questionItemList = surveyQuestionItemService.list(surveyQuestionItem);

        Float point = 5f;
        Float myPoint = 0f;
        Float totalPoint = questionItemList.size() * point;

        for (SurveyQuestionItem questionItem : questionItemList) {
            SurveyAnswerItemForm surveyAnswerItem = new SurveyAnswerItemForm();
            surveyAnswerItem.setQesitmPid(questionItem.getId());
            List<SurveyAnswerItem> answerItemList = surveyAnswerItemService.list(surveyAnswerItem, null);

            SurveyResponseForm surveyResponseForm = new SurveyResponseForm();
            surveyResponseForm.setLoginId(account.getLoginId());
            surveyResponseForm.setQustnrPid(survey.getId());
            surveyResponseForm.setQesitmPid(questionItem.getId());
            SurveyResponse surveyResponse = surveyResponseService.loadByForm(surveyResponseForm);

            Integer temp = 1;
            for (SurveyAnswerItem answerItem : answerItemList) {
                if (answerItem.getId().equals(surveyResponse.getAswPid())) {
                    myPoint += temp;
                    break;
                }
                temp++;
            }
        }

        Float convert = myPoint / totalPoint * 100;

        Float grade = (convert % 20 == 0 ? convert / 20 : convert / 20 + 1);

        model.addAttribute("grade", grade.intValue());

        model.addAttribute("mc", "activity");
        model.addAttribute("pageTitle", "체험");
        return "/pages/activity/selfTestResult";
    }


    @RequestMapping({"/pages/activity/experienceList","/pages/activity/experienceList/{dvCodePid}"})
    public String experienceList(Model model,
                                 @PageableDefault Pageable pageable,
                                 @Value("${Globals.fileStoreUriPath}") String filePath,
                                 @PathVariable(name = "dvCodePid", required = false) Long dvCodePid,
                                 @Value("${common.code.experienceVideoPid}") Long experienceVideoPid,
                                 @Value("${common.code.experienceVoicePid}") Long experienceVoicePid,
                                 @Value("${common.code.experienceMessagePid}") Long experienceMessagePid,
                                 @ModelAttribute SearchForm searchForm,
                                 HttpSession session) {

        model.addAttribute("form", searchForm);

        ExperienceForm experienceForm = new ExperienceForm();
        experienceForm.setDvCodePid(dvCodePid);
        searchForm.setPageSize(Constants.DEFAULT_THUMBNAIL_PAGESIZE);
        Page<Experience> experiences = experienceService.list(pageable, searchForm, experienceForm);
        model.addAttribute("experiences", experiences);

        model.addAttribute("dvCodePid",dvCodePid);
        model.addAttribute("filePath",filePath+"/"+Constants.FOLDERNAME_EXPERIENCE);

        model.addAttribute("videoPid", experienceVideoPid);
        model.addAttribute("voicePid", experienceVoicePid);
        model.addAttribute("messagePid", experienceMessagePid);

        model.addAttribute("mc", "activity");
        model.addAttribute("pageTitle", "체험");
        return "/pages/activity/experienceList";
    }

    @RequestMapping({"/pages/activity/experienceDetail/{id}"})
    public String experienceDetail(Model model,
                                   @PageableDefault Pageable pageable,
                                   @Value("${Globals.fileStoreUriPath}") String filePath,
                                   @PathVariable(name = "id") Long id,
                                   @Value("${common.code.experienceVideoPid}") Long experienceVideoPid,
                                   @Value("${common.code.experienceVoicePid}") Long experienceVoicePid,
                                   @Value("${common.code.experienceMessagePid}") Long experienceMessagePid,
                                   HttpSession session) {

        ExperienceForm form = new ExperienceForm();
        form.setId(id);
        Experience experience = experienceService.loadByform(form);
        model.addAttribute("form", experience);

        model.addAttribute("filePath",filePath+"/"+Constants.FOLDERNAME_EXPERIENCE);

        model.addAttribute("videoPid", experienceVideoPid);
        model.addAttribute("voicePid", experienceVoicePid);
        model.addAttribute("messagePid", experienceMessagePid);

        CommonCommentForm commonCommentForm = new CommonCommentForm();
        commonCommentForm.setDataPid(experience.getId());
        commonCommentForm.setTableNm(TableNmType.TBL_EXPERIENCE);
        Page<CommonComment> commonComments = commonCommentService.list(pageable, commonCommentForm);
        model.addAttribute("commonComments", commonComments);

        Integer readCnt = experience.getReadCnt() == null ? 0 : experience.getReadCnt();

        ExperienceForm experienceForm = new ExperienceForm();
        experienceForm.setId(experience.getId());
        experienceForm.setReadCnt(readCnt+1);
        experienceService.updateByReadCnt(experienceForm);

        model.addAttribute("mc", "activity");
        model.addAttribute("pageTitle", "체험");
        return "/pages/activity/experienceDetail";
    }

    @RequestMapping("/pages/activity/cultureIntro")
    public String cultureIntro(Model model,
                               @Value("${Globals.fileStoreUriPath}") String filepath,
                               @PageableDefault Pageable pageable,
                               @PathVariable(name = "dvCodePid", required = false) Long dvCodePid,
                               @Value("${common.code.campaignCdPid}") Long campaignCdPid,
                               @Value("${common.code.declarationCdPid}") Long declarationCdPid,
                               @Value("${common.code.crewGalleryCdPid}") Long crewCdPid,
                               HttpSession session,
                               @ModelAttribute SearchForm searchForm) {

        model.addAttribute("campaignCdPid", campaignCdPid); //대국민캠페인
        model.addAttribute("declarationCdPid", declarationCdPid); //지지선언
        model.addAttribute("crewCdPid", crewCdPid); //지지크루
        CampaignForm campaignForm = new CampaignForm();

        if (dvCodePid == null) {
            dvCodePid = campaignCdPid;
        }
        campaignForm.setDvCodePid(dvCodePid);

        Page<Campaign> campaigns = campaignService.list(pageable, searchForm, campaignForm);

        model.addAttribute("campaigns", campaigns);
        model.addAttribute("dvCodePid", dvCodePid);
        model.addAttribute("filePath", filepath+"/"+ Constants.FOLDERNAME_CAMPAIGN);
        model.addAttribute("mc", "activity");
        model.addAttribute("pageTitle", "문화");
        return "/pages/activity/cultureIntro";
    }

    @RequestMapping({"/pages/activity/cultureList","/pages/activity/cultureList/{dvCodePid}"})
    public String cultureList(Model model,
                              @Value("${Globals.fileStoreUriPath}") String filepath,
                              @PageableDefault Pageable pageable,
                              @PathVariable(name = "dvCodePid", required = false) Long dvCodePid,
                              @Value("${common.code.campaignCdPid}") Long campaignCdPid,
                              @Value("${common.code.declarationCdPid}") Long declarationCdPid,
                              @Value("${common.code.crewGalleryCdPid}") Long crewGalleryCdPid,
                              @Value("${common.code.crewBoardCdPid}") Long crewBoardCdPid,
                              HttpSession session,
                              @ModelAttribute SearchForm searchForm) {

        model.addAttribute("form", searchForm);

        model.addAttribute("campaignCdPid", campaignCdPid); //대국민캠페인
        model.addAttribute("declarationCdPid", declarationCdPid); //지지선언
        model.addAttribute("crewGalleryCdPid", crewGalleryCdPid); //지지크루(갤러리)
        model.addAttribute("crewBoardCdPid", crewBoardCdPid); //지지크루(게시판)
        CampaignForm campaignForm = new CampaignForm();

        if (dvCodePid == null) {
            dvCodePid = campaignCdPid;
        }
        campaignForm.setDvCodePid(dvCodePid);

        if (!dvCodePid.equals(crewBoardCdPid)) {
            searchForm.setPageSize(Constants.DEFAULT_THUMBNAIL_PAGESIZE);
        }

        Page<Campaign> campaigns = campaignService.list(pageable, searchForm, campaignForm);

        model.addAttribute("campaigns", campaigns);
        model.addAttribute("dvCodePid", dvCodePid);
        model.addAttribute("filePath", filepath+"/"+ Constants.FOLDERNAME_CAMPAIGN);
        model.addAttribute("mc", "activity");
        model.addAttribute("pageTitle", "문화");
        return "/pages/activity/cultureList";
    }

    @RequestMapping("/pages/activity/cultureDetail/{cmpgnPid}")
    public String cultureDetail(Model model,
                                @PageableDefault Pageable pageable,
                                @PathVariable(name = "cmpgnPid") Long cmpgnPid,
                                @Value("${common.code.campaignCdPid}") Long campaignCdPid,
                                @Value("${common.code.declarationCdPid}") Long declarationCdPid,
                                @Value("${common.code.crewGalleryCdPid}") Long crewGalleryCdPid,
                                @Value("${common.code.crewBoardCdPid}") Long crewBoardCdPid) {

        model.addAttribute("campaignCdPid", campaignCdPid); //대국민캠페인
        model.addAttribute("declarationCdPid", declarationCdPid); //지지선언
        model.addAttribute("crewGalleryCdPid", crewGalleryCdPid); //지지크루(갤러리)
        model.addAttribute("crewBoardCdPid", crewBoardCdPid); //지지크루(게시판)

        Campaign load = campaignService.load(cmpgnPid);
        model.addAttribute("form", load);

        FileInfoForm fileInfoForm = new FileInfoForm();
        fileInfoForm.setDataPid(load.getId());
        fileInfoForm.setTableNm(TableNmType.TBL_CAMPAIGN.name());
        List<FileInfo> fileList = fileInfoService.list(fileInfoForm);
        model.addAttribute("fileList", fileList);

        CommonCommentForm commonCommentForm = new CommonCommentForm();
        commonCommentForm.setDataPid(load.getId());
        commonCommentForm.setTableNm(TableNmType.TBL_CAMPAIGN);
        Page<CommonComment> commonComments = commonCommentService.list(pageable, commonCommentForm);
        model.addAttribute("commonComments", commonComments);

        Integer readCnt = load.getReadCnt() == null ? 0 : load.getReadCnt();

        //조회수업데이트
        CampaignForm campaignForm = new CampaignForm();
        campaignForm.setId(load.getId());
        campaignForm.setReadCnt(readCnt+1);
        campaignService.updateByReadCnt(campaignForm);

        model.addAttribute("mc", "activity");
        model.addAttribute("pageTitle", "문화");
        return "/pages/activity/cultureDetail";
    }

    @RequestMapping("/pages/activity/healIntro")
    public String healIntro(Model model) {


        model.addAttribute("mc", "activity");
        model.addAttribute("pageTitle", "치유");
        return "/pages/activity/healIntro";
    }

    @RequestMapping("/pages/activity/helpCounseling")
    public String helpCounseling(Model model,
                                 @PageableDefault Pageable pageable,
                                 @Value("${Globals.fileStoreUriPath}") String filePath,
                                 @ModelAttribute SearchForm searchForm) {

        BannerForm bannerForm = new BannerForm();
        bannerForm.setBanDvTy(BanDvTy.CARD);
        searchForm.setUseAt("Y");
        searchForm.setPageSize(2);//카드뉴스 상위 2개
        searchForm.setSrchDt(LocalDate.now());

        Page<Banner> cardNewsList = bannerService.list(pageable, searchForm, bannerForm);
        model.addAttribute("cardNewsList", cardNewsList);

        model.addAttribute("bannerFilePath", filePath + "/" + Constants.FOLDERNAME_BANNER);

        model.addAttribute("mc", "activity");
        model.addAttribute("pageTitle", "치유");
        return "/pages/activity/helpCounseling";
    }

    @RequestMapping("/pages/activity/helpRequest")
    public String helpRequest(Model model,
                              @ModelAttribute SearchForm searchForm,
                              @PageableDefault Pageable pageable) {

        model.addAttribute("form", searchForm);
        Page<AdviceRequest> adviceRequests = adviceRequestService.list(pageable, searchForm);
        model.addAttribute("adviceRequests", adviceRequests);


        model.addAttribute("mc", "activity");
        model.addAttribute("pageTitle", "치유");
        return "/pages/activity/helpRequest";
    }

    @RequestMapping("/pages/activity/helpRequestDetail/{id}")
    public String helpRequestDetail(Model model,
                                    @ModelAttribute AdviceRequestForm adviceRequestForm,
                                    @PathVariable(name = "id") Long id,
                                    @Value("${Globals.fileStoreUriPath}") String filePath,
                                    @CurrentUser Account account) {
        AdviceRequest loadRequest = adviceRequestService.load(id);

        if (account == null || !UserRollType.COUNSELOR.equals(account.getMberDvTy())) {
            if (adviceRequestForm.getPwd() == null) {
                model.addAttribute("altmsg", "비정상적인 접근방법입니다.");
                model.addAttribute("locurl", "/pages/activity/helpRequest");
                return "/message";
            }
            if (!passwordEncoder.matches(adviceRequestForm.getPwd(), loadRequest.getPwd())) {
                model.addAttribute("altmsg", "비밀번호가 일치하지 않습니다.");
                model.addAttribute("locurl", "/pages/activity/helpRequest?page="+adviceRequestForm.getPage());
                return "/message";
            }
        }

        model.addAttribute("form", loadRequest);

        FileInfoForm fileInfoForm = new FileInfoForm();
        fileInfoForm.setDataPid(loadRequest.getId());
        fileInfoForm.setTableNm(TableNmType.TBL_ADVICE_REQUEST.name());
        List<FileInfo> fileList = fileInfoService.list(fileInfoForm);

        model.addAttribute("fileList", fileList);
        model.addAttribute("filePath", filePath+"/"+ Constants.FOLDERNAME_ADVICE);

        AdviceAnswer loadAnswer = adviceAnswerService.loadByAdvcReqPid(loadRequest.getId());
        model.addAttribute("loadAnswer", loadAnswer);

        model.addAttribute("mc", "activity");
        model.addAttribute("pageTitle", "치유");
        return "/pages/activity/helpRequestDetail";
    }

    @RequestMapping({"/pages/activity/helpRequestRegister","/pages/activity/helpRequestRegister/{id}"})
    public String helpRequestRegister(Model model,
                                      @CurrentUser Account account,
                                      @PathVariable(name = "id", required = false) Long id,
                                      @Value("${common.code.worryCode}") Long worryCode,
                                      @Value("${common.code.tenScoreCode}") Long tenScoreCode,
                                      @Value("${common.code.areaCode}") Long areaCode,
                                      @Value("${common.code.hopeTimeCode}") Long hopeTimeCode) {

        if (account != null) {
            Account userInfo = new Account();
            userInfo.setNm(account.getNm());
            userInfo.setMoblphon(account.getMoblphon());
            model.addAttribute("userInfo", userInfo);
        }

        AdviceRequest adviceRequest = new AdviceRequest();
        List<FileInfo> fileList = new ArrayList<FileInfo>();
        int cnt = 0;
        if (id != null) {
            adviceRequest = adviceRequestService.load(id);

            FileInfoForm fileInfoForm = new FileInfoForm();
            fileInfoForm.setDataPid(id);
            fileInfoForm.setTableNm(TableNmType.TBL_ADVICE_REQUEST.name());
            fileList = fileInfoService.list(fileInfoForm);

            List<AdviceRequestType> requestTypeList = adviceRequestTypeService.list(adviceRequest.getId());

            List<Map<String, Object>> maps = new ArrayList<>();
            Map<String, Object> map = null;

            List<CommonCode> worryCodes = commonCodeService.getCommonCodeParent(worryCode);

            for (CommonCode code : worryCodes) {
                map = new HashMap<>();
                map.put("id",code.getId());
                map.put("codeNm",code.getCodeNm());
                boolean result = false;

                for (AdviceRequestType requestType : requestTypeList) {
                    if (code.getId().equals(requestType.getCodePid())) {
                        result = true;
                        cnt++;
                    }
                }
                if (result == true) {
                    map.put("codeChk",result);
                } else {
                    map.put("codeChk",result);
                }

                maps.add(map);
            }
            model.addAttribute("worryChkCodes", maps);
        }
        model.addAttribute("cnt", cnt);
        model.addAttribute("fileList", fileList);
        model.addAttribute("form", adviceRequest);

        List<CommonCode> worryCodes = commonCodeService.getCommonCodeParent(worryCode);
        model.addAttribute("worryCodes", worryCodes);

        List<CommonCode> tenScoreCodes = commonCodeService.getCommonCodeParent(tenScoreCode);
        model.addAttribute("tenScoreCodes", tenScoreCodes);

        List<CommonCode> areaCodes = commonCodeService.getCommonCodeParent(areaCode);
        model.addAttribute("areaCodes", areaCodes);

        List<CommonCode> hopeTimeCodes = commonCodeService.getCommonCodeParent(hopeTimeCode);
        model.addAttribute("hopeTimeCodes", hopeTimeCodes);

        model.addAttribute("accept", StringUtil.join(",", ArrayUtils.addAll(FileUtilHelper.getAllExt())));

        model.addAttribute("mc", "activity");
        model.addAttribute("pageTitle", "치유");
        return "/pages/activity/helpRequestRegister";
    }

    @RequestMapping("/pages/activity/helpDataRoom")
    public String helpDataRoom(Model model,
                               @Value("${Globals.fileStoreUriPath}") String rootPath,
                               @PageableDefault Pageable pageable,
                               HttpSession session,
                               @ModelAttribute SearchForm searchForm,
                               @Value("${common.code.helpDataCdPid}") Long helpDataCdPid) {

        BoardDataForm boardDataForm = new BoardDataForm();
        boardDataForm.setMstPid(helpDataCdPid);
        searchForm.setPageSize(Constants.DEFAULT_THUMBNAIL_PAGESIZE);
        Page<BoardData> boardDatas = boardDataService.list(pageable, searchForm, boardDataForm);
        model.addAttribute("boardDatas", boardDatas);

        BoardMaster boardMaster = boardMasterService.load(helpDataCdPid);
        model.addAttribute("boardMaster", boardMaster);

        model.addAttribute("mc", "activity");
        model.addAttribute("pageTitle", "치유");
        model.addAttribute("rootPath", rootPath);
        return "/pages/activity/helpDataRoom";
    }

    @RequestMapping("/pages/activity/helpDataRoomDetail/{id}")
    public String helpDataRoomDetail(Model model,
                                     @Value("${Globals.fileStoreUriPath}") String rootPath,
                                     @CurrentUser Account account,
                                     @PathVariable(name = "id") Long id) {

        BoardDataForm boardDataForm = new BoardDataForm();
        boardDataForm.setId(id);
        BoardData load = boardDataService.load(boardDataForm);
        model.addAttribute("form", load);

        FileInfoForm fileInfoForm = new FileInfoForm();
        fileInfoForm.setDataPid(id);
        fileInfoForm.setTableNm(TableNmType.TBL_BOARD_DATA.name());
        List<FileInfo> fileList = fileInfoService.list(fileInfoForm);
        model.addAttribute("fileList", fileList);
        Long downloadCnt = 0l;
        for(FileInfo file : fileList) {
            downloadCnt += file.getDownloadCnt();
            if(file.getDvTy().equals(FileDvType.THUMB.name())){
                model.addAttribute("thumbFile", file);
            }
        }
        model.addAttribute("downloadCnt", downloadCnt);

        boardDataForm.setId(load.getId());
        boardDataForm.setReadCnt(load.getReadCnt()+1);
        boardDataService.updateByReadCnt(boardDataForm);

        model.addAttribute("mc", "activity");
        model.addAttribute("pageTitle", "치유");
        model.addAttribute("rootPath", rootPath);
        return "/pages/activity/helpDataRoomDetail";
    }

    @RequestMapping("/pages/activity/familyTherapy")
    public String familyTherapy(Model model) {


        model.addAttribute("mc", "activity");
        model.addAttribute("pageTitle", "치유");
        return "/pages/activity/familyTherapy";
    }

    @RequestMapping("/pages/activity/mindSharing")
    public String mindSharing(Model model) {


        model.addAttribute("mc", "activity");
        model.addAttribute("pageTitle", "치유");
        return "/pages/activity/mindSharing";
    }

    @RequestMapping("/pages/activity/factualSurvey")
    public String factualSurvey(Model model) {
        model.addAttribute("mc", "activity");
        model.addAttribute("pageTitle", "연구");
        return "/pages/activity/factualSurvey";
    }

    @RequestMapping("/pages/activity/factualSurveyTest")
    public String factualSurveyTest(Model model) {

        List<Map<String, Object>> rtnList = new ArrayList<>();

        SurveyForm survey = new SurveyForm();
        survey.setDvTy(SurveyDvType.FACTUAL.name());
        Survey load = surveyService.loadByform(survey);
        model.addAttribute("form",load);

        SurveyQuestionItemForm surveyQuestionItem = new SurveyQuestionItemForm();
        surveyQuestionItem.setQustnrPid(load.getId());
        List<SurveyQuestionItem> questionItemList = surveyQuestionItemService.list(surveyQuestionItem);

        for (SurveyQuestionItem questionItem : questionItemList) {
            Map<String, Object> item = new HashMap<>();
            item.put("question", questionItem);

            SurveyAnswerItemForm surveyAnswerItem = new SurveyAnswerItemForm();
            surveyAnswerItem.setQesitmPid(questionItem.getId());
            List<SurveyAnswerItem> answerItemList = surveyAnswerItemService.list(surveyAnswerItem, null);
            item.put("caseList", answerItemList);

            rtnList.add(item);
        }
        model.addAttribute("list", rtnList);

        model.addAttribute("mc", "activity");
        model.addAttribute("pageTitle", "연구");
        return "/pages/activity/factualSurveyTest";
    }

    @RequestMapping("/pages/activity/research")
    public String research(Model model) {
        model.addAttribute("mc", "activity");
        model.addAttribute("pageTitle", "연구");
        return "/pages/activity/research";
    }

    @RequestMapping("/pages/activity/policyProposal")
    public String policyProposal(Model model,
                                 @PageableDefault Pageable pageable,
                                 @ModelAttribute SearchForm searchForm,
                                 @Value("${common.code.policyProposalCdPid}") Long policyProposalCdPid) {

        model.addAttribute("form", searchForm);

        BoardDataForm boardDataForm = new BoardDataForm();
        boardDataForm.setMstPid(policyProposalCdPid);
        boardDataForm.setFixingAt("N");
        Page<BoardData> boardDatas = boardDataService.listForFront(pageable, searchForm, boardDataForm);
        model.addAttribute("boardDatas", boardDatas);

        BoardMaster boardMaster = boardMasterService.load(policyProposalCdPid);
        model.addAttribute("boardMaster", boardMaster);

        model.addAttribute("mc", "activity");
        model.addAttribute("pageTitle", "제안");
        return "/pages/activity/policyProposal";
    }

    @RequestMapping("/pages/activity/policyProposalDetail/{id}")
    public String policyProposalDetail(Model model,
                                       @PageableDefault Pageable pageable,
                                       @Value("${Globals.fileStoreUriPath}") String filepath,
                                       @ModelAttribute BoardDataForm boardDataForm) {

        BoardData load = boardDataService.load(boardDataForm);
        model.addAttribute("form", load);

        FileInfoForm fileInfoForm = new FileInfoForm();
        fileInfoForm.setDataPid(load.getId());
        fileInfoForm.setTableNm(TableNmType.TBL_BOARD_DATA.name());
        List<FileInfo> fileList = fileInfoService.list(fileInfoForm);

        model.addAttribute("fileList", fileList);

        CommonCommentForm commonCommentForm = new CommonCommentForm();
        commonCommentForm.setDataPid(load.getId());
        commonCommentForm.setTableNm(TableNmType.TBL_BOARD_DATA);
        Page<CommonComment> commonComments = commonCommentService.list(pageable, commonCommentForm);
        model.addAttribute("commonComments", commonComments);

        Integer readCnt = load.getReadCnt() == null ? 0 : load.getReadCnt();

        boardDataForm.setReadCnt(readCnt+1);
        boardDataService.updateByReadCnt(boardDataForm);

        model.addAttribute("filePath", filepath+"/"+ Constants.FOLDERNAME_BOARDDATA);

        model.addAttribute("mc", "activity");
        model.addAttribute("pageTitle", "제안");
        return "/pages/activity/policyProposalDetail";
    }

    @RequestMapping({"/pages/activity/policyProposalRegister","/pages/activity/policyProposalRegister/{id}"})
    public String policyProposalRegister(Model model,
                                         @PathVariable(name = "id",required = false) Long id,
                                         @Value("${common.code.policyProposalCdPid}") Long policyProposalCdPid) {

        BoardMaster boardMaster = boardMasterService.load(policyProposalCdPid);
        model.addAttribute("boardMaster", boardMaster);

        BoardData boardData = new BoardData();
        if (id != null) {
            BoardDataForm boardDataForm = new BoardDataForm();
            boardDataForm.setId(id);
            boardData = boardDataService.load(boardDataForm);
            model.addAttribute("form", boardData);

            FileInfoForm fileInfoForm = new FileInfoForm();
            fileInfoForm.setDataPid(boardData.getId());
            fileInfoForm.setTableNm(TableNmType.TBL_BOARD_DATA.name());
            List<FileInfo> fileList = fileInfoService.list(fileInfoForm);
            model.addAttribute("fileList", fileList);
        } else {
            model.addAttribute("form", boardData);
            model.addAttribute("fileList", new ArrayList<>());
        }

        model.addAttribute("mstPid", policyProposalCdPid);
        model.addAttribute("allAccept", StringUtil.join(",", ArrayUtils.addAll(FileUtilHelper.getAllExt())));

        model.addAttribute("mc", "activity");
        model.addAttribute("pageTitle", "제안");
        return "/pages/activity/policyProposalRegister";
    }

    @RequestMapping("/pages/activity/contest")
    public String contest(Model model) {
        model.addAttribute("mc", "activity");
        model.addAttribute("pageTitle", "제안");
        return "/pages/activity/contest";
    }

    @RequestMapping("/pages/activity/networking")
    public String networking(Model model) {
        model.addAttribute("mc", "activity");
        model.addAttribute("pageTitle", "네트워킹");
        return "/pages/activity/networking";
    }

    @ResponseBody
    @PostMapping("/api/comment/save")
    public String commentSave(Model model,
                              @RequestBody CommonCommentForm commonCommentForm,
                              @CurrentUser Account account) {

        commonCommentForm.setRegPsId(account.getLoginId());
        commonCommentForm.setRegDtm(LocalDateTime.now());
        commonCommentForm.setUpdPsId(account.getLoginId());
        commonCommentForm.setUpdDtm(LocalDateTime.now());
        commonCommentForm.setDelAt("N");

        boolean result = commonCommentService.insert(commonCommentForm, null);

        String msg = "fail";
        if (result) {
            msg = "ok";
        } else {
            msg = "fail";
        }
        return msg;
    }

    @ResponseBody
    @PostMapping("/api/comment/save/one")
    public String commentSaveOne(Model model,
                              @RequestBody CommonCommentForm commonCommentForm,
                              @PageableDefault Pageable pageable,
                              @CurrentUser Account account) {

        commonCommentForm.setRegPsId(account.getLoginId());
        commonCommentForm.setRegDtm(LocalDateTime.now());
        commonCommentForm.setUpdPsId(account.getLoginId());
        commonCommentForm.setUpdDtm(LocalDateTime.now());
        commonCommentForm.setDelAt("N");

        BoardDataForm boardDataForm = new BoardDataForm();
        boardDataForm.setId(commonCommentForm.getDataPid());
        BoardData boardData = boardDataService.load(boardDataForm);
        if (boardData.getRegPsId().equals(account.getLoginId())) {
            return "self";
        }

        CommonComment load = commonCommentService.loadByDuplicate(account.getLoginId(), TableNmType.TBL_BOARD_DATA, commonCommentForm.getDataPid(), "N");
        if (load != null && load.getId() != null) {
            return "dup";
        }

        boolean result = commonCommentService.insert(commonCommentForm, null);

        String msg = "fail";
        if (result) {
            msg = "ok";
        } else {
            msg = "fail";
        }
        return msg;
    }

    @ResponseBody
    @PostMapping("/api/comment/delete")
    public String commentDelete(Model model,
                              @RequestBody CommonCommentForm commonCommentForm,
                              @CurrentUser Account account){

        commonCommentForm.setUpdPsId(account.getLoginId());
        commonCommentForm.setDelAt("Y");
        boolean result = commonCommentService.delete(commonCommentForm);

        String msg = "fail";
        if (result) {
            msg = "ok";
        } else {
            msg = "fail";
        }
        return msg;
    }

    @ResponseBody
    @PostMapping("/api/comment/check")
    public String commentCheck(Model model,
                                @RequestBody CommonCommentForm commonCommentForm,
                                @CurrentUser Account account){

        boolean result = commonCommentService.check(commonCommentForm.getId(), commonCommentForm.getDataPid(), account.getLoginId(), "N");

        String msg = "fail";
        if (result) {
            msg = "ok";
        } else {
            msg = "fail";
        }
        return msg;
    }

    @ResponseBody
    @PostMapping("/api/activity/satisfactionTest")
    public SurveyResponsePerson satisfactionTestCheck(Model model,
                                @RequestBody CourseMasterRelForm courseMasterRelForm,
                                @CurrentUser Account account){

        courseMasterRelForm.setSn(Constants.afterCrsSn);
        CourseMasterRel load = courseMasterRelService.loadByform(courseMasterRelForm);

        SurveyResponsePersonForm surveyResponsePersonForm = new SurveyResponsePersonForm();
        surveyResponsePersonForm.setQustnrPid(load.getCrsPid());
        surveyResponsePersonForm.setLoginId(account.getLoginId());
        SurveyResponsePerson person = surveyResponsePersonService.loadByform(surveyResponsePersonForm);

        return person;
    }

    @ResponseBody
    @PostMapping("/api/activity/factualTestCheck")
    public SurveyResponsePerson factualTestCheck(Model model,
                                              @RequestBody SurveyResponsePersonForm surveyResponsePersonForm,
                                              @CurrentUser Account account){

        SurveyForm survey = new SurveyForm();
        survey.setDvTy(SurveyDvType.FACTUAL.name());
        Survey load = surveyService.loadByform(survey);

        surveyResponsePersonForm.setQustnrPid(load.getId());
        surveyResponsePersonForm.setLoginId(account.getLoginId());
        SurveyResponsePerson person = surveyResponsePersonService.loadByform(surveyResponsePersonForm);

        return person;
    }

    @ResponseBody
    @PostMapping("/api/activity/selfTestCheck")
    public SurveyResponsePerson selfTestCheck(Model model,
                                              @RequestBody SurveyResponsePersonForm surveyResponsePersonForm,
                                              @CurrentUser Account account){

        SurveyForm survey = new SurveyForm();
        survey.setDvTy(SurveyDvType.SELF.name());
        Survey load = surveyService.loadByform(survey);

        surveyResponsePersonForm.setQustnrPid(load.getId());
        surveyResponsePersonForm.setLoginId(account.getLoginId());
        SurveyResponsePerson person = surveyResponsePersonService.loadByform(surveyResponsePersonForm);

        return person;
    }


    @ResponseBody
    @PostMapping("/api/survey/resultRegister")
    private String resultRegister(Model model,
                                  @ModelAttribute SurveyForm survey,
                                  @ModelAttribute CourseMasterRelForm courseMasterRelForm,
                                  @CurrentUser Account account,
                                  HttpServletRequest request,
                                  HttpServletResponse response) throws ServletRequestBindingException {

        //만족도 조사일경우 완료처리해주기위해
        Survey load = surveyService.load(survey.getId());
        CourseRequestCompleteForm completeForm = new CourseRequestCompleteForm();
        if (SurveyDvType.SATISFACTION.name().equals(load.getDvTy())) {
            Long atnlcReqPid = checkCourseRequest(account.getId(), courseMasterRelForm.getCrsMstPid());
            completeForm.setAtnlcReqPid(atnlcReqPid);
            completeForm.setCrsMstPid(courseMasterRelForm.getCrsMstPid());
            completeForm.setCrsPid(load.getId());
            completeForm.setSn(7);
        }

        SurveyQuestionItemForm surveyQuestionItem = new SurveyQuestionItemForm();
        surveyQuestionItem.setQustnrPid(survey.getId());
        List<SurveyQuestionItem> questionItemList = surveyQuestionItemService.list(surveyQuestionItem);
        List<SurveyResponse> responsesList = new ArrayList<>();

        SurveyResponsePersonForm surveyResponsePersonForm = new SurveyResponsePersonForm();
        surveyResponsePersonForm.setLoginId(account.getLoginId());
        surveyResponsePersonForm.setNm(account.getNm());
        surveyResponsePersonForm.setMberDvty(account.getMberDvTy().name());
        surveyResponsePersonForm.setRegDtm(LocalDateTime.now());
        surveyResponsePersonForm.setRegPsId(account.getLoginId());
        surveyResponsePersonForm.setUpdDtm(LocalDateTime.now());
        surveyResponsePersonForm.setUpdPsId(account.getLoginId());
        surveyResponsePersonForm.setDelAt("N");
        surveyResponsePersonForm.setQustnrPid(survey.getId());

        boolean check = true;
        for (SurveyQuestionItem questionItem : questionItemList) {
            Integer answer_cnt = questionItem.getAnswerCnt();
            Integer rspns_cnt = questionItem.getRspnsCnt();

            if (answer_cnt > 0) {  //객관식
                String[] answer = ServletRequestUtils.getStringParameters(request, "answer" + questionItem.getId());
                if ((rspns_cnt > 0 && answer == null) || (rspns_cnt > 0 && answer.length != rspns_cnt)) {
                    check = false;
                    break;
                }
                if (answer.length != 0) {
                    for (String s : answer) {
                        if (s != null && !"".equals(s)) {
                            SurveyResponse surveyResponse = new SurveyResponse();
                            surveyResponse.setQesitmPid(questionItem.getId());
                            surveyResponse.setAswPid(Long.parseLong(s));
                            responsesList.add(surveyResponse);
                        }
                    }
                } else {
                    SurveyResponse surveyResponse = new SurveyResponse();
                    surveyResponse.setQesitmPid(questionItem.getId());
                    responsesList.add(surveyResponse);
                }
            } else {            //주관식
                String answer = ServletRequestUtils.getStringParameter(request, "answer" + questionItem.getId());
                if (answer == null || (rspns_cnt > 0 && "".equals(answer))) {
                    check = false;
                    break;
                }
                SurveyResponse surveyResponse = new SurveyResponse();
                surveyResponse.setQesitmPid(questionItem.getId());
                surveyResponse.setAnswerCnts(answer);
                responsesList.add(surveyResponse);
            }
        }
        Boolean result = false;
        if (check) {
            result = surveyResponsePersonService.proc(surveyResponsePersonForm, responsesList, completeForm, account);
        }

        String msg = "fail";
        if (result) {
            msg = "ok";
            response.setStatus(200);
        } else {
            msg = "fail";
            response.setStatus(401);
        }
        return msg;
    }

    @PostMapping("/api/openData/helpRequest/adviceRequest/register")
    private String adviceRequestRegister(Model model,
                                       @ModelAttribute AdviceRequestForm adviceRequestForm,
                                       @CurrentUser Account account,
                                       @RequestParam(name = "attachedFile", required = false) MultipartFile attachedFile,
                                       @RequestParam(name = "worry")Long[] worryArr ) {
        if (account != null) {
            adviceRequestForm.setRegPsId(account.getLoginId());
            adviceRequestForm.setUpdPsId(account.getLoginId());
            adviceRequestForm.setMberPid(account.getId());
        }
        adviceRequestForm.setRegDtm(LocalDateTime.now());
        adviceRequestForm.setUpdDtm(LocalDateTime.now());
        adviceRequestForm.setProcessTy(ProcessType.REQUEST);
        adviceRequestForm.setDelAt("N");
        boolean result = false;
        if (adviceRequestForm.getId() == null) {
            result = adviceRequestService.insert(adviceRequestForm, worryArr, attachedFile);
        } else {
            result = adviceRequestService.update(adviceRequestForm, worryArr, attachedFile);
        }
        if (result) {
            model.addAttribute("altmsg", "상담원에게 내용이 전달됩니다.\n신속한 답변 드리도록 하겠습니다.\n감사합니다.");
            model.addAttribute("locurl", "/pages/activity/helpRequest");
            return "/message";
        } else {
            model.addAttribute("altmsg", "실패되었습니다 관리자에게 문의 하세요");
            model.addAttribute("locurl", "/pages/activity/helpRequest");
            return "/message";
        }
    }

    @PostMapping("/api/openData/helpRequest/adviceReservation/register")
    private String adviceReservationRegister(Model model,
                                       @ModelAttribute AdviceReservationForm adviceReservationForm,
                                       @CurrentUser Account account,
                                       @RequestParam(name = "attachedFile", required = false) MultipartFile attachedFile,
                                       @RequestParam(name = "worry") Long[] worryArr,
                                       @RequestParam(name = "hopeTimeCodeId") Long[] hopeTimeCodeIdArr) {
        if (account != null) {
            adviceReservationForm.setRegPsId(account.getLoginId());
            adviceReservationForm.setUpdPsId(account.getLoginId());
            adviceReservationForm.setMberPid(account.getId());
        }
        adviceReservationForm.setRegDtm(LocalDateTime.now());
        adviceReservationForm.setUpdDtm(LocalDateTime.now());
        adviceReservationForm.setProcessTy(ProcessType.REQUEST);
        adviceReservationForm.setDelAt("N");
        AdviceReservation adviceReservation = adviceReservationService.insert(adviceReservationForm, worryArr, hopeTimeCodeIdArr, attachedFile);
        if (adviceReservation == null) {
            model.addAttribute("altmsg", "실패되었습니다 관리자에게 문의 하세요");
            model.addAttribute("locurl", "/pages/activity/helpRequest");
            return "/message";
        } else {
            model.addAttribute("altmsg", "상담원에게 내용이 전달됩니다.\n신속한 답변 드리도록 하겠습니다.\n감사합니다.");
            model.addAttribute("locurl", "/pages/activity/helpRequest");
            return "/message";
        }
    }

    @GetMapping("/api/myDataRoom/save/{dataPid}")
    private String myDataRoomSave(Model model,
                                  @PathVariable(name = "dataPid") Long dataPid,
                                  @CurrentUser Account account,
                                  HttpServletResponse response) {

        boolean result = false;

        MyBoardDataForm myBoardDataForm = new MyBoardDataForm();
        myBoardDataForm.setDataPid(dataPid);
        myBoardDataForm.setMberPid(account.getId());
        MyBoardData load = myBoardDataService.loadByDataPidAndMberPid(myBoardDataForm);
        if (load == null) {
            result = myBoardDataService.insert(myBoardDataForm);

            if (result) {
                model.addAttribute("altmsg", "저장되었습니다.");
                model.addAttribute("locurl", "/pages/activity/eduDataRoomDetail/" + dataPid);
                return "/message";
            } else {
                model.addAttribute("altmsg", "저장에 실패하였습니다.\n관리자에게 문의하세요.");
                model.addAttribute("locurl", "/pages/activity/eduDataRoomDetail/" + dataPid);
                return "/message";
            }
        } else {
            model.addAttribute("altmsg", "이미 내 교육자료실에 저장되어 있습니다.");
            model.addAttribute("locurl", "/pages/activity/eduDataRoomDetail/" + dataPid);
            return "/message";
        }

    }

    @PostMapping("/api/policyProposal/register")
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

        //String[] tags = boardDataForm.getHashTags().split("#");

        if (boardDataForm.getId() == null) {
            result = boardDataService.insert(boardDataForm, targetArr, thumbFile, attachedFile, null);
        } else {
            result = boardDataService.update(boardDataForm, targetArr, thumbFile, attachedFile, null);
        }

        if (result) {
            model.addAttribute("altmsg", "제안주셔서 감사드립니다.");
            model.addAttribute("locurl", "/pages/activity/policyProposal");
            return "/message";
        } else {
            model.addAttribute("altmsg", "실패되었습니다 관리자에게 문의 하세요");
            model.addAttribute("locurl", "/pages/activity/policyProposal");
            return "/message";
        }
    }


    @ResponseBody
    @PostMapping("/api/openData/adviceRequest/checkPwd")
    private String resultRegister(Model model,
                                  @RequestBody AdviceRequestForm adviceRequestForm){

        AdviceRequest load = adviceRequestService.load(adviceRequestForm.getId());
        /*AdviceRequest load = adviceRequestService.checkPwd(adviceRequestForm);*/

        boolean result = false;

        if (passwordEncoder.matches(adviceRequestForm.getPwd(), load.getPwd())) {
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

    @ResponseBody
    @PostMapping("/api/preInspection/changeStt")
    private String changeStt(Model model,
                             @RequestBody CourseMasterRelForm courseMasterRelForm,
                             @CurrentUser Account account) {

        boolean result = false;

        CourseMasterRel mstLoad = courseMasterRelService.loadByform(courseMasterRelForm);

        CourseRequestForm courseRequestForm = new CourseRequestForm();
        courseRequestForm.setMberPid(account.getId());
        courseRequestForm.setCrsMstPid(mstLoad.getCrsMstPid());
        CourseRequest courseRequest = courseRequestService.loadByform(courseRequestForm);
        CourseRequestCompleteForm courseRequestCompleteForm = new CourseRequestCompleteForm();
        courseRequestCompleteForm.setAtnlcReqPid(courseRequest.getId());
        courseRequestCompleteForm.setCrsMstPid(mstLoad.getCrsMstPid());
        courseRequestCompleteForm.setCrsPid(mstLoad.getCrsPid());
        courseRequestCompleteForm.setSn(courseMasterRelForm.getSn());

        result = courseRequestCompleteService.updateSttTy(courseRequestCompleteForm,CompleteStatusType.APPLY);

        String msg = "fail";
        if (result) {
            msg = "ok";
        } else {
            msg = "fail";
        }
        return msg;
    }

    @ResponseBody
    @PostMapping("/api/eduClassDetail/loadCommonComments")
    private Page<CommonComment> loadCommonComments(Model model,
                                                   @RequestBody CourseItem courseItem,
                                                   @PageableDefault Pageable pageable) {
        CommonCommentForm commonCommentForm = new CommonCommentForm();
        commonCommentForm.setDataPid(courseItem.getId());
        commonCommentForm.setTableNm(TableNmType.TBL_COUSE_ITEM);
        Page<CommonComment> commonComments = commonCommentService.list(pageable, commonCommentForm);
        //model.addAttribute("commonComments", commonComments);

        return commonComments;
    }
}
