package kr.or.btf.web.web.controller.pages;


import kr.or.btf.web.common.Constants;
import kr.or.btf.web.common.annotation.CurrentUser;
import kr.or.btf.web.config.security.direct.UserDetails;
import kr.or.btf.web.config.security.direct.UserDetailsService;
import kr.or.btf.web.domain.web.*;
import kr.or.btf.web.domain.web.dto.CourseRequestCompleteDto;
import kr.or.btf.web.domain.web.dto.CourseRequestDto;
import kr.or.btf.web.domain.web.dto.MemberSchoolLogDto;
import kr.or.btf.web.domain.web.dto.MemberTeacherLogDto;
import kr.or.btf.web.domain.web.enums.*;
import kr.or.btf.web.services.web.*;
import kr.or.btf.web.utils.AESEncryptor;
import kr.or.btf.web.utils.FileUtilHelper;
import kr.or.btf.web.utils.StringHelper;
import kr.or.btf.web.web.controller.BaseCont;
import kr.or.btf.web.web.form.*;
import kr.or.btf.web.web.validator.MemberFormValidator;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.poi.util.StringUtil;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class MyPageController extends BaseCont {
    private final MemberFormValidator memberFormValidator;
    private final MemberService memberService;
    private final MemberSchoolService memberSchoolService;
    private final MemberTeacherService memberTeacherService;
    private final PasswordEncoder passwordEncoder;
    private final LoginCnntLogsService loginCnntLogsService;
    private final CommonCommentService commonCommentService;
    private final ActionLogService actionLogService;
    private final CourseRequestService courseRequestService;
    private final SurveyResponsePersonService surveyResponsePersonService;
    private final PlayLogService playLogService;
    private final ExperienceService experienceService;
    private final AdviceReservationService adviceReservationService;
    private final AdviceRequestService adviceRequestService;
    private final BoardDataService boardDataService;
    private final FileInfoService fileInfoService;
    private final CommonCodeService commonCodeService;
    private final AdviceRequestTypeService adviceRequestTypeService;
    private final AdviceReservationTypeService adviceReservationTypeService;
    private final AdviceAnswerService adviceAnswerService;
    private final MemberParentService memberParentService;
    private final UserDetailsService userDetailsService;
    private final MyBoardDataService myBoardDataService;
    private final AdviceReservationTimeService adviceReservationTimeService;
    private final CourseMasterService courseMasterService;
    private final CourseRequestCompleteService courseRequestCompleteService;
    private final MemberSchoolLogService memberSchoolLogService;
    private final MemberTeacherLogService memberTeacherLogService;
    private final CertificationService certificationService;
    private final CertificationHisService certificationHisService;
    private final PeerService peerService;
    private final PeerQuestionItemService peerQuestionItemService;
    private final PeerResponseService peerResponseService;
    private final PeerResponsePersonService peerResponsePersonService;
    private final PeerAnswerItemService peerAnswerItemService;
    private final InspectionService inspectionService;
    private final CourseMasterRelService courseMasterRelService;
    private final SurveyService surveyService;
    private final SurveyResponseService surveyResponseService;
    private final SurveyQuestionItemService surveyQuestionItemService;
    private final SurveyAnswerItemService surveyAnswerItemService;

    @RequestMapping("/pages/myPage/profile")
    public String profile(Model model,
                          @CurrentUser Account account) {

        Account load = memberService.load(account.getId());
        Account form = new Account();
        form.setMberDvTy(load.getMberDvTy());
        form.setNm(load.getNm());
        form.setLoginId(load.getLoginId());
        form.setBrthdy(load.getBrthdy());
        form.setSexPrTy(load.getSexPrTy());
        form.setEmail(load.getEmail());
        form.setMoblphon(load.getMoblphon());
        form.setNcnm(load.getNcnm());
        model.addAttribute("form", form);

        if (load.getMberDvTy().equals(UserRollType.STUDENT)) {
            MemberSchool memberSchool = memberSchoolService.loadByMber(load.getId());
            model.addAttribute("school", memberSchool);
        } else if (load.getMberDvTy().equals(UserRollType.TEACHER)) {
            MemberTeacher memberTeacher = memberTeacherService.loadByMber(load.getId());
            model.addAttribute("school", memberTeacher);
        } else if (load.getMberDvTy().equals(UserRollType.PARENT)) {
            MemberParentForm memberParentForm = new MemberParentForm();
            memberParentForm.setStdnprntId(account.getLoginId());
            List<MemberParent> memberParentList = memberParentService.list(memberParentForm);
            List<Account> childList = new ArrayList<>();
            for (MemberParent memberParent : memberParentList) {
                if(memberParent.getStdntId() != null){
                    Account child = memberService.findByLoginIdAndDelAt(memberParent.getStdntId(), "N");
                    childList.add(child);
                }
            }
            model.addAttribute("childList", childList);
        }

        model.addAttribute("mc", "myPage");
        model.addAttribute("pageTitle", "계정정보");
        return "/pages/myPage/profile";
    }

    @RequestMapping("/pages/myPage/profileRegister")
    public String profileRegister(Model model,
                                  @ModelAttribute Account inputAccount,
                                  @CurrentUser Account account) {

        if (passwordEncoder.matches(inputAccount.getPwd(), account.getPwd())) {

            Account load = memberService.load(account.getId());

            Account form = new Account();
            form.setId(load.getId());
            form.setMberDvTy(load.getMberDvTy());
            form.setNm(load.getNm());
            form.setLoginId(load.getLoginId());
            form.setBrthdy(load.getBrthdy());
            form.setSexPrTy(load.getSexPrTy());
            form.setEmail(load.getEmail());
            form.setMoblphon(load.getMoblphon());
            form.setNcnm(load.getNcnm());
            model.addAttribute("form", form);

            if (load.getMberDvTy().equals(UserRollType.STUDENT)) {
                MemberSchool memberSchool = memberSchoolService.loadByMber(load.getId());
                model.addAttribute("school", memberSchool);
            } else if (load.getMberDvTy().equals(UserRollType.TEACHER)) {
                MemberTeacher memberTeacher = memberTeacherService.loadByMber(load.getId());
                model.addAttribute("school", memberTeacher);
            } else if (load.getMberDvTy().equals(UserRollType.PARENT)) {
                MemberParentForm memberParentForm = new MemberParentForm();
                memberParentForm.setStdnprntId(account.getLoginId());
                List<MemberParent> memberParentList = memberParentService.list(memberParentForm);
                List<Account> childList = new ArrayList<>();
                for (MemberParent memberParent : memberParentList) {
                    if(memberParent.getStdntId() != null) {
                        Account child = memberService.findByLoginIdAndDelAt(memberParent.getStdntId(), "N");
                        childList.add(child);
                    }
                }
                model.addAttribute("childList", childList);
            }

            model.addAttribute("mc", "myPage");
            model.addAttribute("pageTitle", "계정정보");
            return "/pages/myPage/profileRegister";
        } else {
            model.addAttribute("altmsg", "비밀번호가 일치하지 않습니다.");
            model.addAttribute("locurl", "/pages/myPage/profile");
            return "/message";
        }
    }

    @RequestMapping("/pages/myPage/activity")
    public String activity(Model model,
                           @Value("${common.code.policyProposalCdPid}") Long policyProposalCdPid,
                           @CurrentUser Account account) {

        LoginCnntLogsForm loginCnntLogsForm = new LoginCnntLogsForm();
        loginCnntLogsForm.setCnctId(account.getLoginId());
        Long loginCnt = loginCnntLogsService.count(loginCnntLogsForm);
        model.addAttribute("loginCnt", loginCnt);

        //활동 - 댓글, 대댓글, 공감 수
        Long commontCnt = commonCommentService.countByRegPsId(account.getLoginId());
        model.addAttribute("commontCnt", commontCnt);

        ActionLogForm actionLogForm = new ActionLogForm();
        actionLogForm.setMberPid(account.getId());
        actionLogForm.setActDvTy(ActionType.INNER_LINK);
        actionLogForm.setCnctUrl("/pages/activity/eduDataRoomDetail/%");
        //교육 - 자료실 게시물 확인 수
        Long dataLoadCnt = actionLogService.count(actionLogForm);
        model.addAttribute("dataLoadCnt", dataLoadCnt);

        //교육 - 강의수강 수
        Long courseRequestCnt = courseRequestService.countByMberPid(account.getId());
        model.addAttribute("courseRequestCnt", courseRequestCnt);

        SurveyResponsePersonForm surveyResponsePersonForm = new SurveyResponsePersonForm();
        surveyResponsePersonForm.setLoginId(account.getLoginId());
        surveyResponsePersonForm.setSurveyDvTypes(new String[]{SurveyDvType.SATISFACTION.name()});
        //교육 - 만족도조사 수
        Long surveyCnt = surveyResponsePersonService.count(surveyResponsePersonForm);
        model.addAttribute("surveyCnt", surveyCnt);

        PlayLogForm playLogForm = new PlayLogForm();
        playLogForm.setMberPid(account.getId());
        playLogForm.setCnctUrl("/pages/activity/experienceDetail/%");
        Long expPlayCnt = playLogService.count(playLogForm);
        Long notFileCnt = experienceService.countForNotFileLoad(account.getId());
        surveyResponsePersonForm = new SurveyResponsePersonForm();
        surveyResponsePersonForm.setLoginId(account.getLoginId());
        surveyResponsePersonForm.setSurveyDvTypes(new String[]{SurveyDvType.SELF.name()});
        Long surveySelfCnt = surveyResponsePersonService.count(surveyResponsePersonForm);
        //체험 - 사이버폭력 체럼, 피해자목소리, 위로메시지 듣기, 자가진단
        Long expCnt = expPlayCnt + notFileCnt + surveySelfCnt;
        model.addAttribute("expCnt", expCnt);

        surveyResponsePersonForm = new SurveyResponsePersonForm();
        surveyResponsePersonForm.setLoginId(account.getLoginId());
        surveyResponsePersonForm.setSurveyDvTypes(new String[]{SurveyDvType.FACTUAL.name()});
        //체험 - 실태조사 참여 수
        Long surveyFactualCnt = surveyResponsePersonService.count(surveyResponsePersonForm);
        model.addAttribute("surveyFactualCnt", surveyFactualCnt);

        actionLogForm = new ActionLogForm();
        actionLogForm.setMberPid(account.getId());
        actionLogForm.setActDvTy(ActionType.INNER_LINK);
        actionLogForm.setCnctUrl("/pages/activity/helpDataRoomDetail/%");
        Long adDataCnt = actionLogService.count(actionLogForm);
        AdviceReservationForm adviceReservationForm = new AdviceReservationForm();
        adviceReservationForm.setMberPid(account.getId());
        Long adResCnt = adviceReservationService.count(adviceReservationForm);
        //도움/상담 - 전화,대면 상담요청, 자료실 조회 수
        Long adCnt1 = adDataCnt + adResCnt;
        model.addAttribute("adCnt1", adCnt1);

        AdviceRequestForm adviceRequestForm = new AdviceRequestForm();
        adviceRequestForm.setMberPid(account.getId());
        //도움/상담 - 게시판상담 수
        Long adCnt2 = adviceRequestService.count(adviceRequestForm);
        model.addAttribute("adCnt2", adCnt2);

        actionLogForm = new ActionLogForm();
        actionLogForm.setMberPid(account.getId());
        actionLogForm.setActDvTy(ActionType.EXTRA_LINK);
        actionLogForm.setCnctUrl("https://mrmweb.hsit.co.kr/v2/Member/MemberJoin.aspx?action=join&server=8A0qKbUTSxXKiaFQCm7xBw==&_ga=2.69546768.163200047.1602728354-1602796120.1593159639");
        //나눔정기기부 - 나눔링크이동 수
        Long nanumCnt1 = actionLogService.count(actionLogForm);

        actionLogForm = new ActionLogForm();
        actionLogForm.setMberPid(account.getId());
        actionLogForm.setActDvTy(ActionType.EXTRA_LINK);
        actionLogForm.setCnctUrl("https://mrmweb.hsit.co.kr/v2/Member/SupportOnce.aspx?action=once&server=8A0qKbUTSxXKiaFQCm7xBw==&_ga=2.69546768.163200047.1602728354-1602796120.1593159639");
        //나눔일시기부 - 나눔링크이동 수
        Long nanumCnt2 = actionLogService.count(actionLogForm);
        model.addAttribute("nanumCnt", nanumCnt1 + nanumCnt2);

        actionLogForm = new ActionLogForm();
        actionLogForm.setMberPid(account.getId());
        actionLogForm.setActDvTy(ActionType.EXTRA_LINK);
        actionLogForm.setCnctUrl("http://btf.or.kr/board/board_online_survey/board_form.asp?scrID=0000000169&pageNum=5&subNum=1&ssubNum=1#area_05_sub01");
        Long jijiLinkCnt = actionLogService.count(actionLogForm);
        playLogForm = new PlayLogForm();
        playLogForm.setMberPid(account.getId());
        playLogForm.setCnctUrl("/pages/activity/cultureDetail/%");
        Long videoCnt = playLogService.count(playLogForm);
        //챌린지 - 영상시청, 지지서명링크 수
        Long chgCnt = jijiLinkCnt + videoCnt;
        model.addAttribute("chgCnt", chgCnt);

        actionLogForm = new ActionLogForm();
        actionLogForm.setMberPid(account.getId());
        actionLogForm.setActDvTy(ActionType.INNER_LINK);
        actionLogForm.setCnctUrl("/pages/activity/policyProposal");
        Long policyListCnt = actionLogService.count(actionLogForm);
        BoardDataForm boardDataForm = new BoardDataForm();
        boardDataForm.setRegPsId(account.getLoginId());
        boardDataForm.setMstPid(policyProposalCdPid);
        Long policyWriteCnt = boardDataService.count(boardDataForm);
        //정책제안 - 제안하기, 목록확인 수
        Long policyCnt = policyListCnt + policyWriteCnt;
        model.addAttribute("policyCnt", policyCnt);

        model.addAttribute("mc", "myPage");
        model.addAttribute("pageTitle", "활동내역");
        return "/pages/myPage/activity";
    }

    @RequestMapping("/pages/myPage/counseling")
    public String counseling(Model model,
                             @PageableDefault Pageable pageable,
                             @ModelAttribute SearchForm searchForm,
                             @CurrentUser Account account) {

        searchForm.setUserPid(account.getId());

        Page<AdviceRequest> adviceRequests = adviceRequestService.list(pageable, searchForm);
        model.addAttribute("adviceRequests", adviceRequests);

        searchForm.setReservationDvTy(AdviceReservationDvType.TEL);
        Page<AdviceReservation> adviceTelList = adviceReservationService.list(pageable, searchForm);
        model.addAttribute("adviceTelList", adviceTelList);

        searchForm.setReservationDvTy(AdviceReservationDvType.FACE);
        Page<AdviceReservation> adviceFaceList = adviceReservationService.list(pageable, searchForm);
        model.addAttribute("adviceFaceList", adviceFaceList);

        model.addAttribute("mc", "myPage");
        model.addAttribute("pageTitle", "My 상담");
        return "/pages/myPage/counseling";
    }

    @RequestMapping("/pages/myPage/counselingDetail/{gbn}/{id}")
    public String counselingDetail(Model model,
                                   @PathVariable(name = "id") Long id,
                                   @PathVariable(name = "gbn") String gbn,
                                   @Value("${Globals.fileStoreUriPath}") String filePath,
                                   @Value("${common.code.worryCode}") Long worryCode,
                                   @CurrentUser Account account) {

        model.addAttribute("gbn", gbn);

        if (gbn.equals("board")) {
            AdviceRequest loadRequest = adviceRequestService.load(id);
            model.addAttribute("form", loadRequest);

            List<AdviceRequestType> requestTypeList = adviceRequestTypeService.list(loadRequest.getId());
            model.addAttribute("requestTypeList", requestTypeList);

            FileInfoForm fileInfoForm = new FileInfoForm();
            fileInfoForm.setDataPid(loadRequest.getId());
            fileInfoForm.setTableNm(TableNmType.TBL_ADVICE_REQUEST.name());
            List<FileInfo> fileList = fileInfoService.list(fileInfoForm);
            model.addAttribute("fileList", fileList);

            AdviceAnswer loadAnswer = adviceAnswerService.loadByAdvcReqPid(loadRequest.getId());
            model.addAttribute("loadAnswer", loadAnswer);
        } else {
            AdviceReservation load = adviceReservationService.load(id);
            model.addAttribute("dvTy", load.getDvTy());
            model.addAttribute("form", load);
            List<AdviceReservationTime> loadTimes = adviceReservationTimeService.list(id);
            model.addAttribute("loadTimes", loadTimes);

            List<AdviceReservationType> reservationTypeList = adviceReservationTypeService.list(load.getId());
            model.addAttribute("reservationTypeList", reservationTypeList);
        }

        model.addAttribute("filePath", filePath+"/"+ Constants.FOLDERNAME_ADVICE);

        model.addAttribute("mc", "myPage");
        model.addAttribute("pageTitle", "My 상담");
        return "/pages/myPage/counselingDetail";
    }

    @RequestMapping("/pages/myPage/counselingModify/{gbn}/{id}")
    public String counselingModify(Model model,
                                   @PathVariable(name = "id") Long id,
                                   @PathVariable(name = "gbn") String gbn,
                                   @Value("${Globals.fileStoreUriPath}") String filePath,
                                   @Value("${common.code.worryCode}") Long worryCode,
                                   @Value("${common.code.tenScoreCode}") Long tenScoreCode,
                                   @Value("${common.code.areaCode}") Long areaCode,
                                   @Value("${common.code.hopeTimeCode}") Long hopeTimeCode,
                                   @CurrentUser Account account) {

        model.addAttribute("gbn", gbn);

        if (gbn.equals("board")) {
            AdviceRequest loadRequest = adviceRequestService.load(id);
            model.addAttribute("form", loadRequest);

            List<AdviceRequestType> requestTypeList = adviceRequestTypeService.list(loadRequest.getId());
            model.addAttribute("requestTypeList", requestTypeList);

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


            List<CommonCode> tenScoreCodes = commonCodeService.getCommonCodeParent(tenScoreCode);
            model.addAttribute("tenScoreCodes", tenScoreCodes);

            List<CommonCode> areaCodes = commonCodeService.getCommonCodeParent(areaCode);
            model.addAttribute("areaCodes", areaCodes);

            List<CommonCode> hopeTimeCodes = commonCodeService.getCommonCodeParent(hopeTimeCode);
            model.addAttribute("hopeTimeCodes", hopeTimeCodes);

            FileInfoForm fileInfoForm = new FileInfoForm();
            fileInfoForm.setDataPid(loadRequest.getId());
            fileInfoForm.setTableNm(TableNmType.TBL_ADVICE_REQUEST.name());
            List<FileInfo> fileList = fileInfoService.list(fileInfoForm);
            model.addAttribute("fileList", fileList);

            AdviceAnswer loadAnswer = adviceAnswerService.loadByAdvcReqPid(loadRequest.getId());
            model.addAttribute("loadAnswer", loadAnswer);
        } else {
            AdviceReservation load = adviceReservationService.load(id);
            model.addAttribute("dvTy", load.getDvTy());
            model.addAttribute("form", load);

            List<AdviceReservationType> reservationTypeList = adviceReservationTypeService.list(load.getId());

            List<Map<String, Object>> maps = new ArrayList<>();
            Map<String, Object> map = null;

            List<CommonCode> worryCodes = commonCodeService.getCommonCodeParent(worryCode);
            for (CommonCode code : worryCodes) {
                map = new HashMap<>();
                map.put("id",code.getId());
                map.put("codeNm",code.getCodeNm());
                boolean result = false;
                for (AdviceReservationType reservationType : reservationTypeList) {
                    if (code.getId().equals(reservationType.getCodePid())) {
                        result = true;
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

            List<AdviceReservationTime> loadTimes = adviceReservationTimeService.list(load.getId());
            model.addAttribute("loadTimes", loadTimes);

            List<Map<String, Object>> timeMaps = new ArrayList<>();
            Map<String, Object> timeMap = null;

            List<CommonCode> hopeTimeCodes = commonCodeService.getCommonCodeParent(hopeTimeCode);
            for (CommonCode code : hopeTimeCodes) {
                timeMap = new HashMap<>();
                timeMap.put("id",code.getId());
                timeMap.put("codeNm",code.getCodeNm());
                boolean result = false;
                for (AdviceReservationTime loadTime : loadTimes) {
                    if (code.getId().equals(loadTime.getHopeTimeCodeId())) {
                        result = true;
                    }
                }
                if (result == true) {
                    timeMap.put("codeChk",result);
                } else {
                    timeMap.put("codeChk",result);
                }

                timeMaps.add(timeMap);
            }
            model.addAttribute("timeChkCodes", timeMaps);

            List<CommonCode> areaCodes = commonCodeService.getCommonCodeParent(areaCode);
            model.addAttribute("areaCodes", areaCodes);
        }

        model.addAttribute("filePath", filePath+"/"+ Constants.FOLDERNAME_ADVICE);

        model.addAttribute("mc", "myPage");
        model.addAttribute("pageTitle", "My 상담");
        return "/pages/myPage/counselingModify";
    }

    @PostMapping("/api/myPage/profileModify")
    private String profileModify(Model model,
                                 @CurrentUser Account account,
                                 @ModelAttribute MemberForm memberForm,
                                 @ModelAttribute MemberSchoolForm memberSchoolForm,
                                 @ModelAttribute MemberTeacherForm memberTeacherForm,
                                 @RequestParam(name = "childIdArr", required = false) String[] childIdArr) {
        boolean result = false;

        memberForm.setId(account.getId());

        result = memberService.updateForMyPage(memberForm, memberSchoolForm, memberTeacherForm, childIdArr);

        UserDetails userDetails = userDetailsService.loadUserByUsername(account.getLoginId());
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()));

        if (result) {
            model.addAttribute("altmsg", "수정이 완료되었습니다.");
            model.addAttribute("locurl", "/pages/myPage/profile");
            return "/message";
        } else {
            model.addAttribute("altmsg", "실패되었습니다. 관리자에게 문의하세요.");
            model.addAttribute("locurl", "/pages/myPage/profile");
            return "/message";
        }


    }

    @ResponseBody
    @PostMapping("/api/myPage/isExistsByEmailAndAuth")
    public ResponseEntity isExistsByEmail(Model model,
                                          @CurrentUser Account account,
                                          @ModelAttribute MemberForm memberForm,
                                          BindingResult bindingResult) {

        memberFormValidator.validate(memberForm, bindingResult);
        if(bindingResult.hasFieldErrors("email")){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bindingResult.getFieldError("email").getDefaultMessage());
        }else{
            boolean result = memberService.sendEmailChangeMail(account, memberForm);
            if (result) {
                return ResponseEntity.ok(memberForm);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("인증메일 발송에 실패했습니다.");
            }
        }
    }

    @GetMapping("/api/myPage/mailAuth")
    public String mailAuth(Model model,
                           @CurrentUser Account account,
                           @RequestParam(name="authKey") String authKey) throws Exception {
        boolean result = false;

        AESEncryptor aesEncryptor = AESEncryptor.getInstance(Constants.AESEncryptKey);
        String authKeyStr = aesEncryptor.decrypt(authKey);

        if (authKeyStr != null) {

            String[] authSplit = authKeyStr.split("\\|");
            if (authSplit != null && authSplit.length > 2) {
                String loginId = authSplit[0];
                String email = authSplit[1];
                LocalDateTime emailAttcDtm = LocalDateTime.parse(authSplit[2]);

                MemberForm memberForm = new MemberForm();
                memberForm.setLoginId(loginId);
                memberForm.setEmail(email);
                memberForm.setEmailAttcDtm(emailAttcDtm);
                if (memberForm.getLoginId() != null && memberForm.getEmailAttcDtm() != null) {
                    Account load = memberService.loadByLoginId(memberForm.getLoginId());
                    if (load != null) {
                        if (load.getEmail().equals(memberForm.getEmail())) {
                            model.addAttribute("altmsg", "이미 인증처리 된 아이디 입니다.");
                            model.addAttribute("locurl", "/");
                            return "/message";
                        } else {
                            LocalDateTime limitLdt = LocalDateTime.now();
                            LocalDateTime reqLdt = memberForm.getEmailAttcDtm().plusHours(1L);
                            if (limitLdt.isBefore(reqLdt)) { // 인증 가능 1시간 체크
                                memberForm.setEmailAttcAt("Y");
                                result = memberService.updateEmailAttc(memberForm);
                                if (account != null && account.getLoginId().equals(load.getLoginId())) {
                                    UserDetails userDetails = userDetailsService.loadUserByUsername(load.getLoginId());
                                    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()));
                                }
                            } else {
                                model.addAttribute("altmsg", "인증 가능 시간(1시간) 초과로, 인증 실패했습니다. 인증 메일을 재발송 해주세요.");
                                model.addAttribute("locurl", "/");
                                return "/message";
                            }
                        }
                    }
                }
            }
        }
        if(result){
            model.addAttribute("altmsg","인증 처리됐습니다. 변경된 이메일로 적용됩니다.");
        }else{
            model.addAttribute("altmsg","인증 실패했습니다. 관리자에게 문의해주세요.");
        }
        model.addAttribute("locurl","/login");
        return "/message";
    }

    @PostMapping("/api/myPage/adviceRequest/update")
    private String adviceRequestUpdate(Model model,
                                         @ModelAttribute AdviceRequestForm adviceRequestForm,
                                         @CurrentUser Account account,
                                         @RequestParam(name = "attachedFile", required = false) MultipartFile attachedFile,
                                         @RequestParam(name = "worry")Long[] worryArr ) {

        adviceRequestForm.setUpdPsId(account.getLoginId());
        adviceRequestForm.setUpdDtm(LocalDateTime.now());
        adviceRequestForm.setDelAt("N");
        boolean result = adviceRequestService.update(adviceRequestForm, worryArr, attachedFile);
        if (result == false) {
            model.addAttribute("altmsg", "실패되었습니다 관리자에게 문의 하세요");
            model.addAttribute("locurl", "/pages/myPage/counseling");
            return "/message";
        } else {
            model.addAttribute("altmsg", "상담원에게 내용이 전달됩니다.\n신속한 답변 드리도록 하겠습니다.\n감사합니다.");
            model.addAttribute("locurl", "/pages/myPage/counseling");
            return "/message";
        }
    }

    @PostMapping("/api/myPage/adviceReservation/update")
    private String adviceReservationUpdate(Model model,
                                             @ModelAttribute AdviceReservationForm adviceReservationForm,
                                             @CurrentUser Account account,
                                             @RequestParam(name = "attachedFile", required = false) MultipartFile attachedFile,
                                             @RequestParam(name = "worry")Long[] worryArr,
                                             @RequestParam(name = "hopeTimeCodeId") Long[] hopeTimeCodeIdArr) {

        adviceReservationForm.setUpdPsId(account.getLoginId());
        adviceReservationForm.setUpdDtm(LocalDateTime.now());
        adviceReservationForm.setDelAt("N");
        boolean result = adviceReservationService.update(adviceReservationForm, worryArr, hopeTimeCodeIdArr);
        if (result == false) {
            model.addAttribute("altmsg", "실패되었습니다 관리자에게 문의 하세요");
            model.addAttribute("locurl", "/pages/activity/counseling");
            return "/message";
        } else {
            model.addAttribute("altmsg", "상담원에게 내용이 전달됩니다.\n신속한 답변 드리도록 하겠습니다.\n감사합니다.");
            model.addAttribute("locurl", "/pages/myPage/counseling");
            return "/message";
        }
    }

    @RequestMapping("/pages/myPage/learningStatus")
    public String learningStatus(Model model,
                                 @CurrentUser Account account) {

        if (!account.getMberDvTy().equals(UserRollType.TEACHER)) {
            model.addAttribute("altmsg", "접근권한이 없습니다.");
            model.addAttribute("locurl", "/pages/myPage/profile");
            return "/message";
        }

        List<MemberTeacherLogDto> memberTeacherLogDtoList = memberTeacherLogService.schlLogList(account.getId());
        model.addAttribute("memberTeacherLogDtoList", memberTeacherLogDtoList);

        model.addAttribute("mc", "myPage");
        model.addAttribute("pageTitle", "학습현황");
        return "/pages/myPage/learningStatus";
    }

    @RequestMapping("/pages/myPage/learningStatusDetail")
    public String learningStatusDetail(Model model,
                                       @CurrentUser Account account,
                                       @ModelAttribute MemberSchoolForm memberSchoolForm) {

        List<MemberSchoolLogDto> childrenSchlLogList = new ArrayList<>();

        if (account.getMberDvTy() == UserRollType.PARENT) {
            MemberParentForm memberParentForm = new MemberParentForm();
            memberParentForm.setStdnprntId(account.getLoginId());
            List<MemberParent> childrenList = memberParentService.list(memberParentForm);
            for(MemberParent child : childrenList) {
                List<MemberSchoolLogDto> tmpList = memberSchoolLogService.childSchlLogList(child.getMberPid());
                for (MemberSchoolLogDto m : tmpList) {
                    childrenSchlLogList.add(m);
                }
            }
        } else if(account.getMberDvTy().equals(UserRollType.TEACHER)) {
            childrenSchlLogList = memberSchoolLogService.childLogList(memberSchoolForm);

            model.addAttribute("areaNm", memberSchoolForm.getAreaNm());
            model.addAttribute("schlNm", memberSchoolForm.getSchlNm());
            model.addAttribute("grade", memberSchoolForm.getGrade());
            model.addAttribute("ban", memberSchoolForm.getBan());
        } else {
            model.addAttribute("altmsg", "접근권한이 없습니다.");
            model.addAttribute("locurl", "/pages/myPage/counseling");
            return "/message";
        }
        model.addAttribute("childrenSchlLogList", childrenSchlLogList);

        model.addAttribute("mc", "myPage");
        model.addAttribute("pageTitle", "학습현황");
        return "/pages/myPage/learningStatusDetail";
    }

    @ResponseBody
    @PostMapping("/api/myPage/learningStatusDetail/eduStatuLoad")
    public List<CourseRequestDto> eduStatuLoad(Model model,
                               @CurrentUser Account account,
                               @RequestBody CourseRequestForm courseRequestForm) {

        List<CourseRequestDto> courseRequestDtoList = courseRequestService.listForParentOrTeacher(courseRequestForm);

        return courseRequestDtoList;
    }

    @RequestMapping("/pages/myPage/classroom")
    public String classroom(Model model,
                            @CurrentUser Account account,
                            @PageableDefault Pageable pageable) {

        Page<CourseMaster> courseMasters = courseMasterService.listForMyPage(pageable,account.getId());
        model.addAttribute("courseMasters", courseMasters);

        model.addAttribute("mc", "myPage");
        model.addAttribute("pageTitle", "My 강의실");
        return "/pages/myPage/classroom";
    }

    @RequestMapping("/pages/myPage/classroomDetail/{id}")
    public String classroomDetail(Model model,
                            @PageableDefault Pageable pageable,
                            @PathVariable(name = "id") Long atnlcReqPid,
                            @CurrentUser Account account) {

        Page<CourseRequestCompleteDto> courseRequestCompletes = courseRequestCompleteService.listForMyPage(pageable, atnlcReqPid ,account.getId());
        model.addAttribute("courseRequestCompletes", courseRequestCompletes);
        model.addAttribute("atnlcReqPid", atnlcReqPid);

        model.addAttribute("mc", "myPage");
        model.addAttribute("pageTitle", "My 강의실");
        return "/pages/myPage/classroomDetail";
    }

    @RequestMapping("/pages/myPage/eduDataRoom")
    public String eduDataRoom(Model model,
                              @CurrentUser Account account,
                              @Value("${Globals.fileStoreUriPath}") String filePath,
                              @ModelAttribute SearchForm searchForm,
                              @PageableDefault Pageable pageable,
                              @Value("${common.code.eduDataCdPid}") Long eduDataCdPid) {

        BoardDataForm boardDataForm = new BoardDataForm();
        boardDataForm.setMberPid(account.getId());
        boardDataForm.setMstPid(eduDataCdPid);

        searchForm.setMyBoard("Y");

        Page<BoardData> myBoardDatas = boardDataService.list(pageable, searchForm, boardDataForm);
        model.addAttribute("myBoardDatas", myBoardDatas);

        model.addAttribute("filePath", filePath + "/" + Constants.FOLDERNAME_BOARDDATA);

        model.addAttribute("mc", "myPage");
        model.addAttribute("pageTitle", "My 강의실");
        return "/pages/myPage/eduDataRoom";
    }

    @RequestMapping("/pages/myPage/certificate")
    public String certificate(Model model,
                              @CurrentUser Account account,
                              @PageableDefault Pageable pageable) {

        Page<CourseMaster> courseMasters = courseMasterService.listForMyPage(pageable,account.getId());
        model.addAttribute("courseMasters", courseMasters);

        model.addAttribute("mc", "myPage");
        model.addAttribute("pageTitle", "My 강의실");
        return "/pages/myPage/certificate";
    }

    @RequestMapping("/pages/myPage/proposal")
    public String proposal(Model model,
                           @CurrentUser Account account,
                           @PageableDefault Pageable pageable,
                           @ModelAttribute SearchForm searchForm,
                           @Value("${common.code.policyProposalCdPid}") Long policyProposalCdPid) {

        BoardDataForm boardDataForm = new BoardDataForm();
        boardDataForm.setMstPid(policyProposalCdPid);
        searchForm.setLoginId(account.getLoginId());
        boardDataForm.setFixingAt("N");
        Page<BoardData> boardDatas = boardDataService.listForFront(pageable, searchForm, boardDataForm);
        model.addAttribute("boardDatas", boardDatas);

        model.addAttribute("mc", "myPage");
        model.addAttribute("pageTitle", "My 제안");
        return "/pages/myPage/proposal";
    }

    @RequestMapping("/pages/myPage/proposalDetail/{id}")
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

        model.addAttribute("mc", "myPage");
        model.addAttribute("pageTitle", "My 제안");
        return "/pages/myPage/proposalDetail";
    }

    @RequestMapping("/pages/myPage/proposalRegister/{id}")
    public String policyProposalRegister(Model model,
                                         @PathVariable(name = "id") Long id,
                                         @Value("${common.code.policyProposalCdPid}") Long policyProposalCdPid) {

        BoardDataForm boardDataForm = new BoardDataForm();

        boardDataForm.setId(id);
        BoardData boardData = boardDataService.load(boardDataForm);
        model.addAttribute("form", boardData);

        FileInfoForm fileInfoForm = new FileInfoForm();
        fileInfoForm.setDataPid(boardData.getId());
        fileInfoForm.setTableNm(TableNmType.TBL_BOARD_DATA.name());
        List<FileInfo> fileList = fileInfoService.list(fileInfoForm);
        model.addAttribute("fileList", fileList);


        model.addAttribute("mstPid", policyProposalCdPid);
        model.addAttribute("allAccept", StringUtil.join(",", ArrayUtils.addAll(FileUtilHelper.getAllExt())));

        model.addAttribute("mc", "myPage");
        model.addAttribute("pageTitle", "My 제안");
        return "/pages/myPage/proposalRegister";
    }

    @PostMapping("/api/myPage/proposal/register")
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

        String[] tags = boardDataForm.getHashTags().split("#");

        if (boardDataForm.getId() == null) {
            result = boardDataService.insert(boardDataForm, targetArr, thumbFile, attachedFile, tags);
        } else {
            result = boardDataService.update(boardDataForm, targetArr, thumbFile, attachedFile, tags);
        }

        if (result) {
            model.addAttribute("altmsg", "제안주셔서 감사드립니다.");
            model.addAttribute("locurl", "/pages/myPage/proposal");
            return "/message";
        } else {
            model.addAttribute("altmsg", "실패되었습니다 관리자에게 문의 하세요");
            model.addAttribute("locurl", "/pages/myPage/proposal");
            return "/message";
        }
    }

    @ResponseBody
    @PostMapping("/api/profile/checkPwd")
    public String checkPwd(Model model,
                           @RequestBody Account inputAccount,
                           @CurrentUser Account account) {

        boolean result = false;
        result = memberService.checkPwd(account.getId(), inputAccount.getPwd());

        String msg = "";
        if (result) {
            msg = "ok";
        } else {
            msg = "fail";
        }

        return msg;
    }

    @ResponseBody
    @PostMapping("/api/myPage/srchChild")
    public MemberSchool srchChild(Model model,
                             @RequestBody MemberSchoolForm memberSchoolForm ) {
        MemberSchool srch = memberSchoolService.loadByForm(memberSchoolForm);

        return srch;
    }

    @ResponseBody
    @PostMapping("/api/myPage/srchTeacher")
    public MemberTeacher srchTeacher(Model model,
                                  @RequestBody MemberTeacherForm memberTeacherForm ) {
        MemberTeacher srch = memberTeacherService.loadByForm(memberTeacherForm);

        return srch;
    }

    @ResponseBody
    @PostMapping("/api/myPage/eduDataDelete")
    public String eduDataDelete(Model model,
                                @RequestBody MyBoardDataForm myBoardDataForm,
                                @CurrentUser Account account) {

        myBoardDataForm.setMberPid(account.getId());

        boolean result = false;
        result = myBoardDataService.deleteByMberPidAndDataPid(myBoardDataForm);

        String msg = "";
        if (result) {
            msg = "ok";
        } else {
            msg = "fail";
        }

        return msg;
    }

    @PostMapping("/api/myPage/memberRemove")
    public String memberRemove(Model model,
                               @CurrentUser Account account) {

        boolean result = false;

        result = memberService.delete(account.getId());

        if (result) {
            model.addAttribute("altmsg", "탈퇴되었습니다.");
            model.addAttribute("locurl", "/logout");
            return "/message";
        } else {
            model.addAttribute("altmsg", "실패하였습니다. 관리자에게 문의하세요.");
            model.addAttribute("locurl", "/pages/myPage/profile");
            return "/message";
        }
    }

    @ResponseBody
    @PostMapping("/api/myPage/certificationLoad")
    public Certification certificationLoad(Model model,
                                           @CurrentUser Account account,
                                           @RequestBody Certification certification) {

        certification.setMberPid(account.getId());
        certification = certificationService.loadCertification(certification);

        return certification;
    }

    @ResponseBody
    @PostMapping("/api/myPage/certificationDownload")
    public String certificationDownload(Model model,
                                        @CurrentUser Account account,
                                        @RequestBody CertificationHisForm certificationHisForm) {

        certificationHisForm.setRegPsId(account.getLoginId());
        certificationHisForm.setRegDtm(LocalDateTime.now());
        boolean result = certificationHisService.insert(certificationHisForm);

        String msg = "fail";
        if (result) {
            msg = "ok";
        } else {
            msg = "fail";
        }
        return msg;
    }

    @RequestMapping("/pages/myPage/peer")
    public String peer(Model model,
                       @CurrentUser Account account) {

        MemberSchool memberSchool = memberSchoolService.loadByMber(account.getId());
        model.addAttribute("memberSchool", memberSchool);

        Peer peer = peerService.firstOne();

        //또래지명 등록하기전일경우
        if (peer == null) {
            model.addAttribute("altmsg", "서비스 준비중입니다.");
            model.addAttribute("locurl", "/pages/myPage/profile");
            return "/message";
        }

        PeerQuestionItemForm peerQuestionItemForm = new PeerQuestionItemForm();
        peerQuestionItemForm.setPeerPid(peer.getId());
        List<PeerQuestionItem> questionItems = peerQuestionItemService.listForFront(peerQuestionItemForm, memberSchool);
        model.addAttribute("questionItems", questionItems);

        model.addAttribute("mc", "myPage");
        model.addAttribute("pageTitle", "또래지명");
        return "/pages/myPage/peer";
    }

    @ResponseBody
    @PostMapping("/api/peer/peerResponse/Load")
    public Map<String, Object> responseLoad(Model model,
                                           @CurrentUser Account account,
                                           @RequestBody PeerQuestionItem peerQuestionItem) {

        MemberSchool memberSchool = memberSchoolService.loadByMber(account.getId());
        Peer peer = peerService.firstOne();

        PeerResponsePersonForm peerResponsePersonForm = new PeerResponsePersonForm();
        peerResponsePersonForm.setAreaNm(memberSchool.getAreaNm());
        peerResponsePersonForm.setSchlNm(memberSchool.getSchlNm());
        peerResponsePersonForm.setGrade(memberSchool.getGrade());
        peerResponsePersonForm.setBan(memberSchool.getBan());
        peerResponsePersonForm.setNo(memberSchool.getNo());
        peerResponsePersonForm.setTeacherNm(memberSchool.getTeacherNm());
        peerResponsePersonForm.setPeerPid(peer.getId());
        peerResponsePersonForm.setMberPid(account.getId());
        PeerResponsePerson person = peerResponsePersonService.loadByForm(peerResponsePersonForm);

        List<PeerResponse> peerResponseList = peerResponseService.list(peerQuestionItem.getId(), memberSchool, person);

        PeerAnswerItemForm peerAnswerItemForm = new PeerAnswerItemForm();
        peerAnswerItemForm.setQesitmPid(peerQuestionItem.getId());
        List<PeerAnswerItem> peerAnswerItems = peerAnswerItemService.list(peerAnswerItemForm);

        Map<String, Object> rtn = new HashMap<>();
        rtn.put("peerResponseList", peerResponseList);
        rtn.put("peerAnswerItems", peerAnswerItems);

        return rtn;
    }

    @ResponseBody
    @PostMapping("/api/peer/register")
    public String peerProc (Model model,
                            @CurrentUser Account account,
                            @ModelAttribute PeerResponse peerResponse,
                            @RequestParam(name = "tgtMberPid", required = false) Long[] tgtMberPidArr,
                            HttpServletRequest request) throws ServletRequestBindingException {

        Peer peer = peerService.firstOne();

        MemberSchool memberSchool = memberSchoolService.loadByMber(account.getId());

        PeerResponsePersonForm peerResponsePersonForm = new PeerResponsePersonForm();
        peerResponsePersonForm.setAreaNm(memberSchool.getAreaNm());
        peerResponsePersonForm.setSchlNm(memberSchool.getSchlNm());
        peerResponsePersonForm.setGrade(memberSchool.getGrade());
        peerResponsePersonForm.setBan(memberSchool.getBan());
        peerResponsePersonForm.setNo(memberSchool.getNo());
        peerResponsePersonForm.setTeacherNm(memberSchool.getTeacherNm());
        peerResponsePersonForm.setRegPsId(account.getLoginId());
        peerResponsePersonForm.setRegDtm(LocalDateTime.now());
        peerResponsePersonForm.setUpdPsId(account.getLoginId());
        peerResponsePersonForm.setUpdDtm(LocalDateTime.now());
        peerResponsePersonForm.setDelAt("N");
        peerResponsePersonForm.setPeerPid(peer.getId());
        peerResponsePersonForm.setMberPid(account.getId());

        List<PeerResponseForm> peerResponseFormList = new ArrayList<>();
        for (Long tgtMberPid : tgtMberPidArr) {
            PeerResponseForm peerResponseForm = new PeerResponseForm();
            peerResponseForm.setAswPid(ServletRequestUtils.getLongParameter(request, "score" + tgtMberPid));
            peerResponseForm.setQesitmPid(peerResponse.getQesitmPid());
            peerResponseForm.setAreaNm(memberSchool.getAreaNm());
            peerResponseForm.setSchlNm(memberSchool.getSchlNm());
            peerResponseForm.setGrade(memberSchool.getGrade());
            peerResponseForm.setBan(memberSchool.getBan());
            peerResponseForm.setNo(memberSchool.getNo());
            peerResponseForm.setTeacherNm(memberSchool.getTeacherNm());
            peerResponseForm.setTgtMberPid(tgtMberPid);
            peerResponseFormList.add(peerResponseForm);
        }

        boolean result = false;
        result = peerResponsePersonService.proc(peerResponsePersonForm, peerResponseFormList);

        String msg = "";
        if (result) {
            msg = "ok";
        } else {
            msg = "fail";
        }

        return msg;

    }

    @RequestMapping("/pages/myPage/peerStatus")
    public String peerStatus(Model model,
                             @CurrentUser Account account) {

        if (!account.getMberDvTy().equals(UserRollType.TEACHER)) {
            model.addAttribute("altmsg", "접근권한이 없습니다.");
            model.addAttribute("locurl", "/pages/myPage/profile");
            return "/message";
        }

        List<MemberTeacherLogDto> memberTeacherLogDtoList = memberTeacherLogService.schlLogList(account.getId());
        model.addAttribute("memberTeacherLogDtoList", memberTeacherLogDtoList);

        model.addAttribute("mc", "myPage");
        model.addAttribute("pageTitle", "또래지명");
        return "/pages/myPage/peerStatus";
    }

    @RequestMapping("/pages/myPage/peerStatusDetail")
    public String peerStatusDetail(Model model,
                                       @CurrentUser Account account,
                                       @ModelAttribute MemberSchoolForm memberSchoolForm) {

        //List<MemberSchoolLogDto> childrenSchlLogList = new ArrayList<>();
        Peer peer = peerService.firstOne();

        if(account.getMberDvTy().equals(UserRollType.TEACHER)) {
            /*childrenSchlLogList = memberSchoolLogService.childLogList(memberSchoolForm);

            model.addAttribute("areaNm", memberSchoolForm.getAreaNm());
            model.addAttribute("schlNm", memberSchoolForm.getSchlNm());
            model.addAttribute("grade", memberSchoolForm.getGrade());
            model.addAttribute("ban", memberSchoolForm.getBan());*/
            PeerQuestionItemForm peerQuestionItemForm = new PeerQuestionItemForm();
            peerQuestionItemForm.setPeerPid(peer.getId());
            List<PeerQuestionItem> questionItems = peerQuestionItemService.list(peerQuestionItemForm);
            model.addAttribute("questionItems", questionItems);
        } else {
            model.addAttribute("altmsg", "접근권한이 없습니다.");
            model.addAttribute("locurl", "/pages/myPage/profile");
            return "/message";
        }
        //model.addAttribute("childrenSchlLogList", childrenSchlLogList);

        model.addAttribute("mc", "myPage");
        model.addAttribute("pageTitle", "또래지명");
        return "/pages/myPage/peerStatusDetail";
    }

    @RequestMapping({"/pages/myPage/popup/result"})
    public String result(Model model,
                               @ModelAttribute MemberSchoolForm memberSchoolForm,
                               HttpSession session) throws CloneNotSupportedException {

        Account target = memberService.load(memberSchoolForm.getMberPid());
        model.addAttribute("targetNm", target.getNm());

        Long atnlcReqPid = checkCourseRequest(memberSchoolForm.getMberPid(), memberSchoolForm.getCrsMstPid());

        CourseRequestCompleteForm courseRequestCompleteForm = new CourseRequestCompleteForm();
        courseRequestCompleteForm.setCrsMstPid(memberSchoolForm.getCrsMstPid());
        courseRequestCompleteForm.setSn(memberSchoolForm.getSn());
        courseRequestCompleteForm.setAtnlcReqPid(atnlcReqPid);
        CourseRequestComplete complete = courseRequestCompleteService.loadByform(courseRequestCompleteForm);
        model.addAttribute("cmplPrsDtm",complete.getCmplPrsDtm());

        CourseMasterRelForm courseMasterRelForm = new CourseMasterRelForm();
        courseMasterRelForm.setCrsMstPid(memberSchoolForm.getCrsMstPid());
        courseMasterRelForm.setSn(memberSchoolForm.getSn());
        CourseMasterRel courseMasterRel = courseMasterRelService.loadByform(courseMasterRelForm);

        InspectionForm commonForm = new InspectionForm();
        InspectionForm form = null;
        //공통
        commonForm.setAreaNm(memberSchoolForm.getAreaNm());
        commonForm.setSchlNm(memberSchoolForm.getSchlNm());
        commonForm.setGrade(memberSchoolForm.getGrade());
        commonForm.setBan(memberSchoolForm.getBan());
        commonForm.setCrsMstPid(memberSchoolForm.getCrsMstPid());
        commonForm.setMberPid(memberSchoolForm.getMberPid());
        commonForm.setId(courseMasterRel.getCrsPid());//유효성검사 id
        //사전/사후
        if (memberSchoolForm.getSn().equals(2)) {
            commonForm.setInspctDvTy(InspectionDvType.BEFORE.name());
        } else if (memberSchoolForm.getSn().equals(6)) {
            commonForm.setInspctDvTy(InspectionDvType.AFTER.name());
        }
        commonForm.setSn(memberSchoolForm.getSn());  //사전 : 2, 사후 : 6

        form = commonForm.copy();
        //가해경험 코드
        form.setDvValue1(72l, 3, 5);
        form.setDvValue2(73l, 2, 4);
        form.setDvValue3(74l, 2, 3);
        form.setDvValue4(75l, 1, 1);

        //가해경험(개인) - 언어폭력~성폭행
        JSONArray myResult1_1 = inspectionService.myInspectionResult1_1(form);
        model.addAttribute("myResult1_1", myResult1_1);

        form = commonForm.copy();
        //가해경험 > 가해정보 코드
        form.setDvCodePid1(76l);
        //가해경험(개인) - 가해정보(3문항)
        JSONArray[] myResult1_2 = inspectionService.myInspectionResult1_2(form, 3);
        model.addAttribute("myResult1_2", myResult1_2);

        form = commonForm.copy();
        //피해경험 코드/pages/activity/eduClass/{crsMstPid}/{id}/{sn}
        form.setDvValue1(77l, 3, 5);
        form.setDvValue2(78l, 2, 4);
        form.setDvValue3(79l, 2, 3);
        form.setDvValue4(80l, 1, 1);
        //피해경험(개인) - 언어폭력~성폭행
        JSONArray myResult2_1 = inspectionService.myInspectionResult1_1(form);
        model.addAttribute("myResult2_1", myResult2_1);

        form = commonForm.copy();
        //피해경험 > 가해정보 코드
        form.setDvCodePid1(81l);
        //피해경험(개인) - 피해정보(3문항)
        JSONArray[] myResult2_2 = inspectionService.myInspectionResult1_2(form, 3);
        model.addAttribute("myResult2_2", myResult2_2);

        form = commonForm.copy();
        //목적경험 코드
        form.setDvValue1(82l, 1, 1);
        form.setDvValue2(83l, 2, 3);
        form.setDvValue3(84l, 3, 5);
        form.setDvValue4(85l, 2, 4);
        //목적경험(개인) - 목격경혐여부~동조자
        JSONArray myResult3 = inspectionService.myInspectionResult1_1(form);
        model.addAttribute("myResult3", myResult3);

        form = commonForm.copy();
        //사이버폭력에 대한 부정 태도 코드
        form.setDvValue1(86l, 0, 9);
        //사이버폭력에 대한 부정 태도(개인)
        JSONArray myResultBar1 = inspectionService.myInspectionResultBar(form);
        model.addAttribute("myResultBar1", myResultBar1);


        form = commonForm.copy();
        //사이버폭력에 대한 대처효능감(개인수준) 코드
        form.setDvValue1(87l, 0, 6);
        //사이버폭력에 대한 대처효능감(개인)
        JSONArray myResultBar2 = inspectionService.myInspectionResultBar(form);
        model.addAttribute("myResultBar2", myResultBar2);

        form = commonForm.copy();
        //공격성, 공감, 자아존중감 코드
        form.setDvValue1(88l, 0, 6);
        form.setDvValue2(89l, 0, 6);
        form.setDvValue3(90l, 0, 4);
        //공격성, 공감, 자아존중감(개인)
        JSONArray myResultBar4 = inspectionService.myInspectionResultBarMulti(form);
        model.addAttribute("myResultBar4", myResultBar4);

        form = commonForm.copy();
        //정직, 약속, 용서, 책임, 배려, 소유 코드
        form.setDvValue1(91l, 0, 3);
        form.setDvValue2(92l, 0, 3);
        form.setDvValue3(93l, 0, 3);
        form.setDvValue4(94l, 0, 3);
        form.setDvValue5(95l, 0, 3);
        form.setDvValue6(96l, 0, 3);
        //정직, 약속, 용서, 책임, 배려, 소유(개인)
        JSONArray myResultBar5 = inspectionService.myInspectionResultBarMulti(form);
        model.addAttribute("myResultBar5", myResultBar5);

        form = commonForm.copy();
        //온라인 실태조사 코드
        form.setDvCodePid1(97l);
        //온라인 실태조사(개인) - 4문항
        JSONArray[] myResult4 = inspectionService.myInspectionResult1_2(form, 4);
        model.addAttribute("myResult4", myResult4);

        form = commonForm.copy();
        //사회적 바람직성 코드
        form.setDvCodePid1(99l);
        //사회적 바람직성(개인)
        JSONArray myResult5 = inspectionService.myInspectionResultAvg(form);
        model.addAttribute("myResult5", myResult5);

        model.addAttribute("mc", "myPage");
        model.addAttribute("pageTitle", "검사 결과");
        return "/pages/myPage/popup/result";
    }

    @RequestMapping({"/pages/myPage/popup/resultBan"})
    public String resultBan(Model model,
                         @ModelAttribute MemberSchoolForm memberSchoolForm,
                         HttpSession session) throws CloneNotSupportedException {

        Account target = memberService.load(memberSchoolForm.getMberPid());
        model.addAttribute("targetNm", target.getNm());

        Long atnlcReqPid = checkCourseRequest(memberSchoolForm.getMberPid(), memberSchoolForm.getCrsMstPid());

        CourseRequestCompleteForm courseRequestCompleteForm = new CourseRequestCompleteForm();
        courseRequestCompleteForm.setCrsMstPid(memberSchoolForm.getCrsMstPid());
        courseRequestCompleteForm.setSn(memberSchoolForm.getSn());
        courseRequestCompleteForm.setAtnlcReqPid(atnlcReqPid);
        CourseRequestComplete complete = courseRequestCompleteService.loadByform(courseRequestCompleteForm);
        model.addAttribute("cmplPrsDtm",complete.getCmplPrsDtm());

        CourseMasterRelForm courseMasterRelForm = new CourseMasterRelForm();
        courseMasterRelForm.setCrsMstPid(memberSchoolForm.getCrsMstPid());
        courseMasterRelForm.setSn(memberSchoolForm.getSn());
        CourseMasterRel courseMasterRel = courseMasterRelService.loadByform(courseMasterRelForm);

        InspectionForm commonForm = new InspectionForm();
        InspectionForm form = null;
        //공통
        commonForm.setAreaNm(memberSchoolForm.getAreaNm());
        commonForm.setSchlNm(memberSchoolForm.getSchlNm());
        commonForm.setGrade(memberSchoolForm.getGrade());
        commonForm.setBan(memberSchoolForm.getBan());
        commonForm.setCrsMstPid(memberSchoolForm.getCrsMstPid());
        commonForm.setMberPid(memberSchoolForm.getMberPid());
        commonForm.setId(courseMasterRel.getCrsPid());//유효성검사 id
        //사전/사후
        if (memberSchoolForm.getSn().equals(2)) {
            commonForm.setInspctDvTy(InspectionDvType.BEFORE.name());
        } else if (memberSchoolForm.getSn().equals(6)) {
            commonForm.setInspctDvTy(InspectionDvType.AFTER.name());
        }
        commonForm.setSn(memberSchoolForm.getSn());  //사전 : 2, 사후 : 6
        model.addAttribute("commonForm", commonForm);

        form = commonForm.copy();
        //가해경험 > 가해정보 코드
        form.setDvCodePid1(76l);
        //가해경험(학급) - 가해정보(3문항)
        JSONArray[] clResult1_2 = inspectionService.classCourseInspectionResult1_2(form, 3);
        model.addAttribute("clResult1_2", clResult1_2);
        model.addAttribute("clResult1_2_1", clResult1_2[0]);
        model.addAttribute("clResult1_2_2", clResult1_2[1]);
        model.addAttribute("clResult1_2_3", clResult1_2[2]);

        form = commonForm.copy();
        //피해경험 > 가해정보 코드
        form.setDvCodePid1(81l);
        //피해경험(학급) - 피해정보(3문항)
        JSONArray[] clResult2_2 = inspectionService.classCourseInspectionResult1_2(form, 3);
        model.addAttribute("clResult2_2", clResult2_2);
        model.addAttribute("clResult2_2_1", clResult2_2[0]);
        model.addAttribute("clResult2_2_2", clResult2_2[1]);
        model.addAttribute("clResult2_2_3", clResult2_2[2]);

        form = commonForm.copy();
        //온라인 실태조사 코드
        form.setDvCodePid1(97l);
        //온라인 실태조사(학급) - 4문항
        JSONArray[] clResult4 = inspectionService.classCourseInspectionResult1_2(form, 4);
        model.addAttribute("clResult4", clResult4);
        model.addAttribute("clResult4_1", clResult4[0]);
        model.addAttribute("clResult4_2", clResult4[1]);
        model.addAttribute("clResult4_3", clResult4[2]);
        model.addAttribute("clResult4_4", clResult4[3]);

        model.addAttribute("mc", "myPage");
        model.addAttribute("pageTitle", "검사 결과");
        return "/pages/myPage/popup/resultBan";
    }

    @ResponseBody
    @PostMapping("/api/myPage/popup/clResult")
    public String clResult(Model model,
                                @RequestBody InspectionForm commonForm) {

        if ("clResult1_1".equals(commonForm.getChartId())) {
            //가해경험 코드
            commonForm.setDvValue1(72l, 3, 5);
            commonForm.setDvValue2(73l, 2, 4);
            commonForm.setDvValue3(74l, 2, 3);
            commonForm.setDvValue4(75l, 1, 1);
            //가해경험(학급) - 언어폭력~성폭행
            JSONArray clResult1_1 = inspectionService.classCourseInspectionResult1_1(commonForm);
            return clResult1_1.toString();
        } else if ("clResult2_1".equals(commonForm.getChartId())) {
            //피해경험 코드/pages/activity/eduClass/{crsMstPid}/{id}/{sn}
            commonForm.setDvValue1(77l, 3, 5);
            commonForm.setDvValue2(78l, 2, 4);
            commonForm.setDvValue3(79l, 2, 3);
            commonForm.setDvValue4(80l, 1, 1);
            //피해경험(학급) - 언어폭력~성폭행
            JSONArray clResult2_1 = inspectionService.classCourseInspectionResult1_1(commonForm);
            return clResult2_1.toString();
        } else if ("clResult3".equals(commonForm.getChartId())) {
            //목적경험 코드
            commonForm.setDvValue1(82l, 1, 1);
            commonForm.setDvValue2(83l, 2, 3);
            commonForm.setDvValue3(84l, 3, 5);
            commonForm.setDvValue4(85l, 2, 4);
            //목적경험(학급) - 목격경혐여부~동조자
            JSONArray clResult3 = inspectionService.classCourseInspectionResult1_1(commonForm);
            return clResult3.toString();
        } else if ("clResultBar1".equals(commonForm.getChartId())) {
            //사이버폭력에 대한 부정 태도 코드
            commonForm.setDvValue1(86l, 0, 9);
            //사이버폭력에 대한 부정 태도(학급)
            JSONArray clResultBar1 = inspectionService.classCourseInspectionResultBar(commonForm);
            return clResultBar1.toString();
        } else if ("clResultBar2".equals(commonForm.getChartId())) {
            //사이버폭력에 대한 대처효능감(개인수준) 코드
            commonForm.setDvValue1(87l, 0, 6);
            //사이버폭력에 대한 대처효능감(학급)
            JSONArray clResultBar2 = inspectionService.classCourseInspectionResultBar(commonForm);
            return clResultBar2.toString();
        } else if ("clResultBar3".equals(commonForm.getChartId())) {
            //사이버폭력에 대한 대처효능감(학급수준) 코드
            commonForm.setDvValue1(137l, 0, 6);
            //사이버폭력에 대한 대처효능감(학급)
            JSONArray clResultBar3 = inspectionService.classCourseInspectionResultBar(commonForm);
            return clResultBar3.toString();
        } else if ("clResultBar4".equals(commonForm.getChartId())) {
            //공격성, 공감, 자아존중감 코드
            commonForm.setDvValue1(88l, 0, 6);
            commonForm.setDvValue2(89l, 0, 6);
            commonForm.setDvValue3(90l, 0, 4);
            //공격성, 공감, 자아존중감(학급)
            JSONArray clResultBar4 = inspectionService.classCourseInspectionResultBarMulti(commonForm);
            return clResultBar4.toString();
        } else if ("clResultBar5".equals(commonForm.getChartId())) {
            //정직, 약속, 용서, 책임, 배려, 소유 코드
            commonForm.setDvValue1(91l, 0, 3);
            commonForm.setDvValue2(92l, 0, 3);
            commonForm.setDvValue3(93l, 0, 3);
            commonForm.setDvValue4(94l, 0, 3);
            commonForm.setDvValue5(95l, 0, 3);
            commonForm.setDvValue6(96l, 0, 3);
            //정직, 약속, 용서, 책임, 배려, 소유(학급)
            JSONArray clResultBar5 = inspectionService.classCourseInspectionResultBarMulti(commonForm);
            return clResultBar5.toString();
        }

        return null;
    }

    @ResponseBody
    @PostMapping("/api/myPage/peerStatusDetail/ourClassGraph")
    public List<Map<String,String>> ourClassGraph(Model model,
                                     @RequestBody PeerQuestionItemForm peerQuestionItemForm) {

        List<Map<String,String>> rtnList = new ArrayList<>();


        List<PeerResponse> peerResponseList = peerResponseService.listForGraph(peerQuestionItemForm.getId());
        Map<String, String> map = new HashMap<>();
        for (PeerResponse v : peerResponseList) {

            map = new HashMap<>();
            map.put("source", v.getMberNo() + "." +v.getMberNm());
            map.put("target", v.getTargetNo() + "." +v.getTargetNm());
            map.put("score", v.getScore().toString());
            rtnList.add(map);
        }

        return rtnList;
    }

    @RequestMapping("/pages/myPage/selfTestResult")
    public String myPageSelfTestResult(Model model,
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
            model.addAttribute("locurl", "/pages/myPage/profile");
            return "/message";
        }

        try {
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
            model.addAttribute("resultUpdateDtm", person.getUpdDtm());
        } catch (Exception e) {
            model.addAttribute("altmsg", "자가진단 문항이 변경되었습니다.\n다시 진단해주세요.");
            model.addAttribute("locurl", "/pages/myPage/profile");
            return "/message";
        }

        model.addAttribute("mc", "myPage");
        model.addAttribute("pageTitle", "자가진단결과");
        return "/pages/myPage/selfTestResult";
    }
}
