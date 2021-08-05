package kr.or.btf.web.web.controller.pages;

//import NiceID.Check.CPClient;
import kr.or.btf.web.web.controller.BaseCont;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class NiceApiController extends BaseCont {
//
//    private final String ApiNiceCall = "/api/nice/call";
//    private final String ApiNiceSuccess = "/api/nice/success";
//    private final String ApiNiceFail = "/api/nice/fail";
//
//    private final MobileAuthLogService mobileAuthLogService;
//
//    @GetMapping({ApiNiceCall})
//    public String niceCall(Model model,
//                           @Value("${Globals.domain.full}") String domain,
//                           @Value("${nice.api.code}") String sSiteCode,
//                           @Value("${nice.api.password}") String sSitePassword,
//                           HttpSession session,
//                           HttpServletRequest request) {
//        NiceID.Check.CPClient niceCheck = new NiceID.Check.CPClient();
//
//        /*String sSiteCode = "";			// NICE로부터 부여받은 사이트 코드
//        String sSitePassword = "";		// NICE로부터 부여받은 사이트 패스워드*/
//
//        String sRequestNumber = "REQ0000000001";            // 요청 번호, 이는 성공/실패후에 같은 값으로 되돌려주게 되므로
//        // 업체에서 적절하게 변경하여 쓰거나, 아래와 같이 생성한다.
//        sRequestNumber = niceCheck.getRequestNO(sSiteCode);
//        session.setAttribute("REQ_SEQ", sRequestNumber);    // 해킹등의 방지를 위하여 세션을 쓴다면, 세션에 요청번호를 넣는다.
//
//        String sAuthType = "";        // 없으면 기본 선택화면, M: 핸드폰, C: 신용카드, X: 공인인증서
//
//        String popgubun = "N";        //Y : 취소버튼 있음 / N : 취소버튼 없음
//        String customize = "";        //없으면 기본 웹페이지 / Mobile : 모바일페이지
//
//        String sGender = "";            //없으면 기본 선택 값, 0 : 여자, 1 : 남자
//
//        // CheckPlus(본인인증) 처리 후, 결과 데이타를 리턴 받기위해 다음예제와 같이 http부터 입력합니다.
//        //리턴url은 인증 전 인증페이지를 호출하기 전 url과 동일해야 합니다. ex) 인증 전 url : http://www.~ 리턴 url : http://www.~
//        // 도메인 URL 확인후 변경 [2021.04.27 lch 추가]
//        domain = request.getRequestURL().toString();
//        domain = domain.replace(ApiNiceCall, "");
//        /*if(domain.indexOf("www") < 0){
//            domain = domain.replace("https://", "https://www.").replace("http://", "http://www.");
//        }*/
//        String sReturnUrl = domain + ApiNiceSuccess;      // 성공시 이동될 URL
//        String sErrorUrl = domain + ApiNiceFail;          // 실패시 이동될 URL
//
//        // 입력될 plain 데이타를 만든다.
//        String sPlainData = "7:REQ_SEQ" + sRequestNumber.getBytes().length + ":" + sRequestNumber +
//                "8:SITECODE" + sSiteCode.getBytes().length + ":" + sSiteCode +
//                "9:AUTH_TYPE" + sAuthType.getBytes().length + ":" + sAuthType +
//                "7:RTN_URL" + sReturnUrl.getBytes().length + ":" + sReturnUrl +
//                "7:ERR_URL" + sErrorUrl.getBytes().length + ":" + sErrorUrl +
//                "11:POPUP_GUBUN" + popgubun.getBytes().length + ":" + popgubun +
//                "9:CUSTOMIZE" + customize.getBytes().length + ":" + customize +
//                "6:GENDER" + sGender.getBytes().length + ":" + sGender;
//
//        String sMessage = "";
//        String sEncData = "";
//
//        int iReturn = niceCheck.fnEncode(sSiteCode, sSitePassword, sPlainData);
//        if (iReturn == 0) {
//            sEncData = niceCheck.getCipherData();
//        } else if (iReturn == -1) {
//            sMessage = "암호화 시스템 에러입니다.";
//        } else if (iReturn == -2) {
//            sMessage = "암호화 처리오류입니다.";
//        } else if (iReturn == -3) {
//            sMessage = "암호화 데이터 오류입니다.";
//        } else if (iReturn == -9) {
//            sMessage = "입력 데이터 오류입니다.";
//        } else {
//            sMessage = "알수 없는 에러 입니다. iReturn : " + iReturn;
//        }
//
//        model.addAttribute("sEncData", sEncData);
//        model.addAttribute("sMessage", sMessage);
//
//        return "/pages/api/niceApiCall";
//    }
//
//    @RequestMapping({ApiNiceSuccess})
//    public String niceSuccess(Model model,
//                              HttpServletRequest request,
//                              @Value("${nice.api.code}") String sSiteCode,
//                              @Value("${nice.api.password}") String sSitePassword,
//
//                              HttpSession session) {
//        NiceID.Check.CPClient niceCheck = new NiceID.Check.CPClient();
//
//        String sEncodeData = requestReplace(request.getParameter("EncodeData"), "encodeData");
//        //String sSiteCode = "";                // NICE로부터 부여받은 사이트 코드
//        //String sSitePassword = "";            // NICE로부터 부여받은 사이트 패스워드
//
//        String sCipherTime = "";            // 복호화한 시간
//        String sRequestNumber = "";            // 요청 번호
//        String sResponseNumber = "";        // 인증 고유번호
//        String sAuthType = "";                // 인증 수단
//        String sName = "";                    // 성명
//        String sDupInfo = "";                // 중복가입 확인값 (DI_64 byte)
//        String sConnInfo = "";                // 연계정보 확인값 (CI_88 byte)
//        String sBirthDate = "";                // 생년월일(YYYYMMDD)
//        String sGender = "";                // 성별
//        String sNationalInfo = "";            // 내/외국인정보 (개발가이드 참조)
//        String sMobileNo = "";                // 휴대폰번호
//        String sMobileCo = "";                // 통신사
//        String sMessage = null;
//        String sPlainData = "";
//
//        MobileAuthLogForm form = new MobileAuthLogForm();
//
//        int iReturn = niceCheck.fnDecode(sSiteCode, sSitePassword, sEncodeData);
//
//        if (iReturn == 0) {
//            sPlainData = niceCheck.getPlainData();
//            sCipherTime = niceCheck.getCipherDateTime();
//
//            // 데이타를 추출합니다.
//            java.util.HashMap mapresult = niceCheck.fnParse(sPlainData);
//
//            sRequestNumber = (String) mapresult.get("REQ_SEQ");
//            sResponseNumber = (String) mapresult.get("RES_SEQ");
//            sAuthType = (String) mapresult.get("AUTH_TYPE");
//            sName = (String) mapresult.get("NAME");
//            //sName			= (String)mapresult.get("UTF8_NAME"); //charset utf8 사용시 주석 해제 후 사용
//            sBirthDate = (String) mapresult.get("BIRTHDATE");
//            sGender = (String) mapresult.get("GENDER");
//            sNationalInfo = (String) mapresult.get("NATIONALINFO");
//            sDupInfo = (String) mapresult.get("DI");
//            sConnInfo = (String) mapresult.get("CI");
//            sMobileNo = (String) mapresult.get("MOBILE_NO");
//            sMobileCo = (String) mapresult.get("MOBILE_CO");
//
//            String session_sRequestNumber = (String) session.getAttribute("REQ_SEQ");
//            if (!sRequestNumber.equals(session_sRequestNumber)) {
//                sMessage = "세션값 불일치 오류입니다.";
//                sResponseNumber = "";
//                sAuthType = "";
//            }
//        } else if (iReturn == -1) {
//            sMessage = "복호화 시스템 오류입니다.";
//        } else if (iReturn == -4) {
//            sMessage = "복호화 처리 오류입니다.";
//        } else if (iReturn == -5) {
//            sMessage = "복호화 해쉬 오류입니다.";
//        } else if (iReturn == -6) {
//            sMessage = "복호화 데이터 오류입니다.";
//        } else if (iReturn == -9) {
//            sMessage = "입력 데이터 오류입니다.";
//        } else if (iReturn == -12) {
//            sMessage = "사이트 패스워드 오류입니다.";
//        } else {
//            sMessage = "알수 없는 에러 입니다. iReturn : " + iReturn;
//        }
//
//        form.setDecrHr(sCipherTime);
//        form.setDmnNo(sRequestNumber);
//        form.setRspNo(sResponseNumber);
//        form.setAttcMns(sAuthType);
//        form.setNm(sName);
//        form.setDupSbscrbCfmVal(sDupInfo);
//        form.setCnecInfoCfmVal(sConnInfo);
//        form.setBrthdy(sBirthDate);
//        form.setSexdstn(sGender);
//        form.setIseFrerInfo(sNationalInfo);
//        form.setMbtlnum(sMobileNo);
//        form.setTelecom(sMobileCo);
//        form.setFailrCode(null);
//        form.setMssage(sMessage);
//        form.setEncrData(sEncodeData);
//
//        mobileAuthLogService.insert(form);
//
//        model.addAttribute("sCipherTime",sCipherTime);
//        model.addAttribute("sRequestNumber",sRequestNumber);
//        model.addAttribute("sResponseNumber",sResponseNumber);
//        model.addAttribute("sAuthType",sAuthType);
//        model.addAttribute("sName",sName);
//        model.addAttribute("sDupInfo",sDupInfo);
//        model.addAttribute("sConnInfo",sConnInfo);
//        model.addAttribute("sBirthDate",sBirthDate);
//        model.addAttribute("sGender",sGender);
//        model.addAttribute("sNationalInfo",sNationalInfo);
//        model.addAttribute("sMobileNo",sMobileNo);
//        model.addAttribute("sMobileCo",sMobileCo);
//        model.addAttribute("sMessage",sMessage);
//        model.addAttribute("sPlainData",sPlainData);
//
//        return "/pages/api/niceSuccess";
//
//        /*if (sMessage != null) {
//            model.addAttribute("altmsg", sMessage);
//            model.addAttribute("locurl", "selfClose");
//        } else {
//            model.addAttribute("altmsg", "인증되었습니다.");
//            model.addAttribute("locurl", "selfClose");
//        }
//        return "/message";*/
//    }
//
//    @RequestMapping({ApiNiceFail})
//    public String niceFail(Model model,
//                           HttpServletRequest request,
//                           @Value("${nice.api.code}") String sSiteCode,
//                           @Value("${nice.api.password}") String sSitePassword,
//
//                           HttpSession session) {
//        NiceID.Check.CPClient niceCheck = new NiceID.Check.CPClient();
//
//        String sEncodeData = requestReplace(request.getParameter("EncodeData"), "encodeData");
//
//        //String sSiteCode = "";                // NICE로부터 부여받은 사이트 코드
//        //String sSitePassword = "";            // NICE로부터 부여받은 사이트 패스워드
//
//        String sCipherTime = "";            // 복호화한 시간
//        String sRequestNumber = "";            // 요청 번호
//        String sErrorCode = "";                // 인증 결과코드
//        String sAuthType = "";                // 인증 수단
//        String sMessage = null;
//        String sPlainData = "";
//
//        MobileAuthLogForm form = new MobileAuthLogForm();
//
//        int iReturn = niceCheck.fnDecode(sSiteCode, sSitePassword, sEncodeData);
//
//        if (iReturn == 0) {
//            sPlainData = niceCheck.getPlainData();
//            sCipherTime = niceCheck.getCipherDateTime();
//
//            // 데이타를 추출합니다.
//            java.util.HashMap mapresult = niceCheck.fnParse(sPlainData);
//
//            sRequestNumber = (String) mapresult.get("REQ_SEQ");
//            sErrorCode = (String) mapresult.get("ERR_CODE");
//            sAuthType = (String) mapresult.get("AUTH_TYPE");
//        } else if (iReturn == -1) {
//            sMessage = "복호화 시스템 에러입니다.";
//        } else if (iReturn == -4) {
//            sMessage = "복호화 처리오류입니다.";
//        } else if (iReturn == -5) {
//            sMessage = "복호화 해쉬 오류입니다.";
//        } else if (iReturn == -6) {
//            sMessage = "복호화 데이터 오류입니다.";
//        } else if (iReturn == -9) {
//            sMessage = "입력 데이터 오류입니다.";
//        } else if (iReturn == -12) {
//            sMessage = "사이트 패스워드 오류입니다.";
//        } else {
//            sMessage = "알수 없는 에러 입니다. iReturn : " + iReturn;
//        }
//
//        form.setDecrHr(sCipherTime);
//        form.setDmnNo(sRequestNumber);
//        form.setAttcMns(sAuthType);
//        form.setFailrCode(sErrorCode);
//        form.setMssage(sMessage);
//        form.setEncrData(sEncodeData);
//        mobileAuthLogService.insert(form);
//
//        model.addAttribute("sCipherTime", sCipherTime);
//        model.addAttribute("sRequestNumber", sRequestNumber);
//        model.addAttribute("sErrorCode", sErrorCode);
//        model.addAttribute("sAuthType", sAuthType);
//        model.addAttribute("sMessage", sMessage);
//        model.addAttribute("sPlainData", sPlainData);
//
//        return "/pages/api/niceFail";
//
//        /*if (sMessage != null) {
//            model.addAttribute("altmsg", sMessage);
//            model.addAttribute("locurl", "selfClose");
//        } else {
//            model.addAttribute("altmsg", "인증에 실패했습니다.(" + sErrorCode + ")");
//            model.addAttribute("locurl", "selfClose");
//        }
//        return "/message";*/
//    }
//
//    private String requestReplace(String paramValue, String gubun) {
//
//        String result = "";
//
//        if (paramValue != null) {
//
//            paramValue = paramValue.replaceAll("%2B", "+");
//
//            paramValue = paramValue.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
//
//            paramValue = paramValue.replaceAll("\\*", "");
//            paramValue = paramValue.replaceAll("\\?", "");
//            paramValue = paramValue.replaceAll("\\[", "");
//            paramValue = paramValue.replaceAll("\\{", "");
//            paramValue = paramValue.replaceAll("\\(", "");
//            paramValue = paramValue.replaceAll("\\)", "");
//            paramValue = paramValue.replaceAll("\\^", "");
//            paramValue = paramValue.replaceAll("\\$", "");
//            paramValue = paramValue.replaceAll("'", "");
//            paramValue = paramValue.replaceAll("@", "");
//            paramValue = paramValue.replaceAll("%", "");
//            paramValue = paramValue.replaceAll(";", "");
//            paramValue = paramValue.replaceAll(":", "");
//            paramValue = paramValue.replaceAll("-", "");
//            paramValue = paramValue.replaceAll("#", "");
//            paramValue = paramValue.replaceAll("--", "");
//            paramValue = paramValue.replaceAll("-", "");
//            paramValue = paramValue.replaceAll(",", "");
//
//            if (gubun != "encodeData") {
//                paramValue = paramValue.replaceAll("\\+", "");
//                paramValue = paramValue.replaceAll("/", "");
//                paramValue = paramValue.replaceAll("=", "");
//            }
//
//            result = paramValue;
//
//        }
//        return result;
//    }
//
//    private String replaceData(String data) {
//        if (data == null) return null;
//        return data.replaceAll("%2B", "+");
//    }
}
