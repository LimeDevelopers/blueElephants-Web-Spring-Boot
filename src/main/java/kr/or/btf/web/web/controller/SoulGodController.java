package kr.or.btf.web.web.controller;

import kr.or.btf.web.common.annotation.CurrentUser;
import kr.or.btf.web.domain.web.Account;
import kr.or.btf.web.domain.web.CommonCode;
import kr.or.btf.web.domain.web.MemberSchool;
import kr.or.btf.web.domain.web.enums.SurveyDvType;
import kr.or.btf.web.domain.web.enums.UserRollType;
import kr.or.btf.web.services.web.CommonCodeService;
import kr.or.btf.web.services.web.CourseRequestService;
import kr.or.btf.web.services.web.MemberService;
import kr.or.btf.web.utils.DatetimeHelper;
import kr.or.btf.web.web.form.CourseRequestForm;
import kr.or.btf.web.web.form.MemberForm;
import kr.or.btf.web.web.form.SearchForm;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Controller
@RequiredArgsConstructor
public class SoulGodController extends BaseCont {
    private final MemberService memberService;
    private final CommonCodeService commonCodeService;
    private final CourseRequestService courseRequestService;

    @GetMapping({"/soulGod/dashboard"})
    public String dashboard(Model model,
                            HttpSession session,
                            @Value("${common.code.areaCode}") Long areaCode,
                            @CurrentUser Account account) {

        List<JSONObject> memberSexGbn = memberService.sexResult();
        model.addAttribute("memberSexGbn", memberSexGbn);

        List<JSONObject> memberAgeGbn = memberService.ageResult();
        model.addAttribute("memberAgeGbn", memberAgeGbn);

        List<JSONObject> memberTypeGbn = memberService.typeResult();
        model.addAttribute("memberTypeGbn", memberTypeGbn);

        List<CommonCode> areaCodeList = commonCodeService.getCommonCodeParent(areaCode);
        model.addAttribute("areaCodeList", areaCodeList);

        List<String> courseAreaNmList = courseRequestService.groupByCourseRequest(new CourseRequestForm());
        model.addAttribute("courseAreaNmList", courseAreaNmList);

        setNowDate(model);
        return "/soulGod/dashboard";
    }

    @ResponseBody
    @PostMapping("/api/soulGod/dashboard/monthResult")
    private List<Map<String, Object>> monthResult(Model model,
                                                  @RequestBody SearchForm searchForm) {

        String nowYear = searchForm.getNowYear() == null ? DatetimeHelper.format(LocalDate.now(), "yyyy") : searchForm.getNowYear().toString();

        List<Map<String, Object>> resultList = new ArrayList<>();

        Map<String, Object> resultMap = null;
        List<Object[]> list = memberService.monthResult(nowYear);

        for (int i = 0; i < 12; i++) {
            int month = i + 1;
            String yyyyMM = nowYear + "." + (month < 10 ? "0" + month : month);
            Long value = 0l;
            for (Object[] objects : list) {
                String key = String.valueOf(objects[2]);
                if (key.equals(yyyyMM)) {
                    value = Long.valueOf(String.valueOf(objects[0]));
                }
            }
            resultMap = new HashMap<>();
            resultMap.put("category", yyyyMM);
            resultMap.put("first", value);
            resultList.add(resultMap);
        }

        return resultList;

    }

    @ResponseBody
    @PostMapping("/api/soulGod/dashboard/monthConnectResult")
    private List<Map<String, Object>> monthConnectResult(Model model,
                                                         @RequestBody SearchForm searchForm) {

        String nowYear = searchForm.getNowYear() == null ? DatetimeHelper.format(LocalDate.now(), "yyyy") : searchForm.getNowYear().toString();

        List<Map<String, Object>> resultList = new ArrayList<>();

        Map<String, Object> resultMap = null;
        List<Object[]> list = memberService.monthConnectResult(nowYear);

        for (int i = 0; i < 12; i++) {
            int month = i + 1;
            String yyyyMM = nowYear + "-" + (month < 10 ? "0" + month : month);
            Long value = 0l;
            for (Object[] objects : list) {
                String key = String.valueOf(objects[0]);
                if (key.equals(yyyyMM)) {
                    value = Long.valueOf(String.valueOf(objects[1]));
                }
            }
            resultMap = new HashMap<>();
            resultMap.put("category", yyyyMM);
            resultMap.put("first", value);
            resultList.add(resultMap);
        }

        return resultList;

    }

    @ResponseBody
    @PostMapping("/api/soulGod/dashboard/dayConnectResult")
    private List<Map<String, Object>> dayConnectResult(Model model,
                                                       @RequestBody SearchForm searchForm) {

        String nowYearMonth = searchForm.getNowYearMonth() == null ? DatetimeHelper.format(LocalDate.now(), "yyyy-MM") : searchForm.getNowYearMonth();

        List<Map<String, Object>> resultList = new ArrayList<>();

        Map<String, Object> resultMap = null;
        List<Object[]> list = memberService.dayConnectResult(nowYearMonth);

        for (int i = 0; i < 31; i++) {
            int day = i + 1;
            String yyyyMMdd = nowYearMonth + "-" + (day < 10 ? "0" + day : day);
            Long value = 0l;
            for (Object[] objects : list) {
                String key = String.valueOf(objects[0]);
                if (key.equals(yyyyMMdd)) {
                    value = Long.valueOf(String.valueOf(objects[1]));
                }
            }
            resultMap = new HashMap<>();
            resultMap.put("category", day < 10 ? "0" + day : day);
            resultMap.put("first", value);
            resultList.add(resultMap);
        }

        return resultList;

    }

    //todo 검색조건 (시작일자~종료일자)
    /**
     * 대상자별 상담 신청 건수
     * @param model
     * @param searchForm
     * @return
     */
    @ResponseBody
    @PostMapping("/api/soulGod/dashboard/memberTypeToAdvice")
    private List<Map<String, Object>> memberTypeToAdvice(Model model,
                                                         @Value("${common.code.areaCode}") Long areaCode,
                                                         @RequestBody SearchForm searchForm) {

        List<Long> areaArr = new ArrayList<>();
        if (searchForm.getSrchCodePid() == null || searchForm.getSrchCodePid() == 0L) {
            List<CommonCode> commonCodeParent = commonCodeService.getCommonCodeParent(areaCode);
            for (CommonCode commonCode : commonCodeParent) {
                areaArr.add(commonCode.getId());
            }
        } else {
            areaArr.add(searchForm.getSrchCodePid());
        }
        searchForm.setSrchCodePidArr(areaArr);

        List<Map<String, Object>> resultList = memberService.memberTypeToAdviceResult(searchForm);

        return resultList;

    }

    //todo 검색조건 (시작일자~종료일자)
    /**
     * 대상자별 교육 신청 건수
     * @param model
     * @param searchForm
     * @return
     */
    @ResponseBody
    @PostMapping("/api/soulGod/dashboard/memberTypeToCourse")
    private List<Map<String, Object>> memberTypeToCourse(Model model,
                                                         @RequestBody SearchForm searchForm) {


        List<String> areaArr = new ArrayList<>();
        if (searchForm.getSrchWord() == null || "".equals(searchForm.getSrchWord())) {
            areaArr = courseRequestService.groupByCourseRequest(new CourseRequestForm());
        } else {
            areaArr.add(searchForm.getSrchWord());
        }
        searchForm.setSrchWordArr(areaArr);

        List<Map<String, Object>> resultList = memberService.memberTypeToCourse(searchForm);

        return resultList;
    }

    @ResponseBody
    @PostMapping("/api/soulGod/dashboard/groupByCourseRequest")
    private List<String> groupByCourseRequest(Model model,
                                              @RequestBody CourseRequestForm form) {
        List<String> resultList = new ArrayList<>();
        if (form.getAreaNm() != null && !"".equals(form.getAreaNm())) {
            resultList = courseRequestService.groupByCourseRequest(form);
        }

        return resultList;
    }

    //todo 검색조건 (시작일자~종료일자, 지역별)
    /**
     * 대상자별 교육 신청 건수
     * @param model
     * @param form
     * @return
     */
    @ResponseBody
    @PostMapping("/api/soulGod/dashboard/courseCompleteStatus")
    private List<Map<String, Object>> courseCompleteStatus(Model model,
                                                           @RequestBody CourseRequestForm form) {

        List<Map<String, Object>> resultList = memberService.courseCompleteStatus(form);

        return resultList;

    }

    @ResponseBody
    @PostMapping("/api/soulGod/dashboard/surveyStatusResult")
    private List<Map<String, Object>> surveyStatusResult(Model model,
                                                         @RequestBody SearchForm form) {

        if (form.getSurveyDvTypeStr() == null || "".equals(form.getSurveyDvTypeStr())) {
            String[] surveyDvTypes = new String[2];
            surveyDvTypes[0] = SurveyDvType.SATISFACTION.name();
            surveyDvTypes[1] = SurveyDvType.SELF.name();
            form.setSurveyDvTypes(surveyDvTypes);
        } else {
            form.setSurveyDvTypes(new String[]{form.getSurveyDvTypeStr()});
        }
        if (form.getUserRollTypeStr() == null || "".equals(form.getUserRollTypeStr())) {
            String[] userRollTypes = new String[6];
            userRollTypes[0] = UserRollType.COUNSELOR.name();
            userRollTypes[1] = UserRollType.LECTURER.name();
            userRollTypes[2] = UserRollType.NORMAL.name();
            userRollTypes[3] = UserRollType.PARENT.name();
            userRollTypes[4] = UserRollType.STUDENT.name();
            userRollTypes[5] = UserRollType.TEACHER.name();
            form.setUserRollTypes(userRollTypes);
        } else {
            form.setUserRollTypes(new String[]{form.getUserRollTypeStr()});
        }
        List<Map<String, Object>> resultList = memberService.surveyResponseResult(form);

        return resultList;

    }

    @ResponseBody
    @PostMapping("/api/soulGod/dashboard/menuStatusResult")
    private List<Map<String, Object>> menuStatusResult(Model model,
                                                       @RequestBody SearchForm form) {

        if (form.getUserRollTypeStr() == null || "".equals(form.getUserRollTypeStr())) {
            String[] userRollTypes = new String[6];
            userRollTypes[0] = UserRollType.COUNSELOR.name();
            userRollTypes[1] = UserRollType.LECTURER.name();
            userRollTypes[2] = UserRollType.NORMAL.name();
            userRollTypes[3] = UserRollType.PARENT.name();
            userRollTypes[4] = UserRollType.STUDENT.name();
            userRollTypes[5] = UserRollType.TEACHER.name();
            form.setUserRollTypes(userRollTypes);
        } else {
            form.setUserRollTypes(new String[]{form.getUserRollTypeStr()});
        }
        List<Map<String, Object>> resultList = memberService.menuStatusResult(form);

        return resultList;

    }
    
    //todo 연구현황
    //todo 접속현황 ??

    /**
     * 대쉬보드 기본 시작일 종료일 세팅
     * @param model
     */
    private void setNowDate(Model model) {
        Calendar calendar = Calendar.getInstance();
        TimeZone tz = calendar.getTimeZone();
        ZoneId zid = tz == null ? ZoneId.systemDefault() : tz.toZoneId();
        LocalDate localDate = LocalDateTime.ofInstant(calendar.toInstant(), zid).toLocalDate();
        model.addAttribute("defaultSrchEdDt", DatetimeHelper.format(localDate, "yyyy-MM-dd"));

        calendar.set(Calendar.MONTH, 0);
        calendar.set(Calendar.DATE, 1);
        localDate = LocalDateTime.ofInstant(calendar.toInstant(), zid).toLocalDate();
        model.addAttribute("defaultSrchStDt", DatetimeHelper.format(localDate, "yyyy-MM-dd"));
    }

    @ResponseBody
    @PostMapping(value = "/api/member/isExistByBatchLoginId")
    public ResponseEntity isExistsByBatchloginId(@ModelAttribute MemberForm memberForm,
                                                 BindingResult bindingResult) {

        for (int i = 1; i <= memberForm.getBatchArr(); i++) {
            String loginId = memberForm.getTempLoginId();

            if (i < 10) {
                loginId += "0" + i;
            } else {
                loginId += i;
            }

            if (memberService.existsByBatchLoginId(loginId)) {
                bindingResult.rejectValue("loginId", "invalid ID", new Object[]{memberForm.getTempLoginId()}, "이미 사용 중인 계정 양식 입니다");
            }
            if (bindingResult.hasFieldErrors("loginId")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bindingResult.getFieldError("loginId").getDefaultMessage());
            } else {
                return ResponseEntity.ok(memberForm);
            }
        }
        if (memberService.existsSpace(memberForm.getTempLoginId())) {
            bindingResult.rejectValue("loginId", "invalid ID", new Object[]{memberForm.getTempLoginId()}, "아이디에는 공백을 사용 할 수 없습니다.");
        }
        if (bindingResult.hasFieldErrors("loginId")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bindingResult.getFieldError("loginId").getDefaultMessage());
        } else {
            return ResponseEntity.ok(memberForm);
        }
    }

    @ResponseBody
    @PostMapping(value = "/soulGod/member/batchregister/srchTchrNm")
    public List<MemberSchool> srchTchrNm(Model model, @RequestParam(name = "TeacherNm") String TeacherNm) {

        //System.out.println("아약스결과 조회 : " + TeacherNm);

        //선생이름 , 학교 , 학년 , 반 , mber_pid 순으로 뽑아옴.
        List<MemberSchool> memberSchoolList = memberService.srchTchr(TeacherNm);

        return memberSchoolList;
    }

    @PostMapping(value = "/soulGod/member/batchregister/join")
    public String batchJoin(MemberForm memberForm) {
        String[] val = memberForm.getValues().split(",");


        memberForm.setAreaNm(val[0]);
        memberForm.setSchlNm(val[1]);
        memberForm.setTeacherNm(val[2]);
        memberForm.setGrade(Integer.parseInt(val[3]));
        memberForm.setBan(val[4]);


        memberService.batchRegister(memberForm);
        return "redirect:/soulGod/member/list";
    }

    @GetMapping(value = "/soulGod/member/batchregister")
    public String register() {

        return "/soulGod/member/batchregister";
    }
}
