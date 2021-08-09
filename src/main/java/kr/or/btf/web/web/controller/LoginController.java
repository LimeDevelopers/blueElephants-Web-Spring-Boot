package kr.or.btf.web.web.controller;


import kr.or.btf.web.common.annotation.CurrentUser;
import kr.or.btf.web.common.service.SystemService;
import kr.or.btf.web.config.security.direct.UserDetailsService;
import kr.or.btf.web.domain.web.Account;
import kr.or.btf.web.domain.web.LoginCnntLogs;
import kr.or.btf.web.domain.web.enums.LoginLogMessage;
import kr.or.btf.web.domain.web.enums.PointType;
import kr.or.btf.web.domain.web.enums.UserRollType;
import kr.or.btf.web.services.web.LoginCnntLogsService;
import kr.or.btf.web.services.web.MemberService;
import kr.or.btf.web.services.web.PointService;
import kr.or.btf.web.utils.RequestUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * LoginController [로그인 처리 컨트롤러]
 * @author : jerry
 * @version : 1.0.0
 * 작성일 : 2021/08/09
**/
@Controller
@RequiredArgsConstructor
public class LoginController extends BaseCont {

    private final MemberService userService;
    private final UserDetailsService userDetailsService;
    //private final AuthenticationManager authenticationManager;
    private final SystemService systemService;
    private final PasswordEncoder passwordEncoder;
    private final LoginCnntLogsService loginCnntLogsService;
    private final PointService pointService;


    //private final ResourceServerTokenServices tokenServices;	//kakao login 2020.03.03  fail

    // 로그인 button submit method
    @RequestMapping("/login")
    public String login(Model model,
                        HttpServletRequest request,
                        @CurrentUser Account account,
                        HttpSession session) throws UnsupportedEncodingException {
        String redirect = "/";
        if (super.isLogined(account)) {
            log.debug("이미로그인됨 메인이동, account:{}", account);
            if(UserRollType.ADMIN.name().equals(account.getMberDvTy().name())
                && UserRollType.LECTURER.name().equals(account.getMberDvTy().name())
                && UserRollType.COUNSELOR.name().equals(account.getMberDvTy().name())) {
                //userRepository.updateLastLogin(user.getId());
                //userService.updateLastLogin(account.getUsrId());
                redirect = "/soulGod/dashboard";
            }
            return "redirect:" + redirect;
        }

        /*Account imsi = new Account();
        imsi.setPwd("admin123$%^");
        imsi.encodingPwd(passwordEncoder);
        System.out.println(imsi.getPwd());*/
        String referrer = request.getHeader("Referer");
        request.getSession().setAttribute("prevPage", referrer);

        model.addAttribute("mc", "memberJoin");
        return "/login";
    }

    @RequestMapping("/pages/session/duplication")
    public String sessionDuplication(Model model) {
        /*model.addAttribute("altmsg","로그인 세션이 종료되었습니다.\n다시 로그인 해주세요");
        model.addAttribute("locurl","/login");
        return "/message";*/
        return "/login";
    }

    @RequestMapping("/loginSuccess")
    public String loginSuccess(@CurrentUser Account account,
                               HttpServletRequest request,
                               HttpServletResponse response) throws IOException {


        //log.debug("==로그인성공 후처리시작");

        String redirect = "/";
        if(UserRollType.ADMIN.name().equals(account.getMberDvTy().name())
            || UserRollType.LECTURER.name().equals(account.getMberDvTy().name())
            || UserRollType.COUNSELOR.name().equals(account.getMberDvTy().name())) {
            //userRepository.updateLastLogin(user.getId());
            //userService.updateLastLogin(account.getUsrId());
            redirect = "/soulGod/dashboard";
        }

        String clientIP = RequestUtil.getClientIp(request);

        //로그인 로그 삽입 전 체크 해야함.
        try {
            pointService.checkAndAddPoint(account, PointType.ATTENDANCE_1DAY);
        } catch (Exception e) {
            e.printStackTrace();
        }

        LoginCnntLogs loginCnntLogs = new LoginCnntLogs();
        loginCnntLogs.setCnctDtm(LocalDateTime.now());
        loginCnntLogs.setCnctId(account.getLoginId());
        loginCnntLogs.setCnctIp(clientIP);
        loginCnntLogs.setFailCnt(0);
        loginCnntLogs.setSuccesAt("Y");
        loginCnntLogs.setRsn(LoginLogMessage.LOGINSUCESS.getValue());

        boolean result = loginCnntLogsService.insert(loginCnntLogs);
        //systemService.loginSuccessAsync(account, request);    //비동기방식으로 성공처리

        return "redirect:" + redirect;

    }

    // 로그인 실패처리 메소드
    @RequestMapping("/loginFailure")
    public String loginFailure(Model model,
                               HttpServletRequest request) {

        //log.debug("==로그인실패 후처리시작");
        String userId = request.getAttribute("userId") != null ? request.getAttribute("userId").toString() : "";
        String msg = request.getAttribute("ERRORMSG") != null ? request.getAttribute("ERRORMSG").toString() : "";
        model.addAttribute("errormsg", msg);

        //rttr.addFlashAttribute("message", "fail");

        //로그인기록을 남김.
        //systemService.loginFailAsync(userId, msg, request);

        Integer failCnt = 0;

        LoginCnntLogs loginCnntLogs = new LoginCnntLogs();

        if (userId != null) {
            loginCnntLogs.setCnctId(userId);
            LoginCnntLogs failInfo = loginCnntLogsService.loginFailCnt(loginCnntLogs);

            if (failInfo != null) {
                failCnt = failInfo.getFailCnt();
            }
        }

        String clientIP = RequestUtil.getClientIp(request);

        loginCnntLogs.setCnctDtm(LocalDateTime.now());
        loginCnntLogs.setCnctId(userId);
        loginCnntLogs.setCnctIp(clientIP);
        loginCnntLogs.setFailCnt(failCnt+1);
        loginCnntLogs.setSuccesAt("N");
        loginCnntLogs.setRsn(msg);

        try {
            boolean result = loginCnntLogsService.insert(loginCnntLogs);
        } catch (Exception e) {
            e.printStackTrace();
        }

        model.addAttribute("mc", "memberJoin");
        return "/login";
    }


    @RequestMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/member/success")
    public String success(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        session.invalidate();
        return "/member/success";
    }

    @GetMapping("/mypage/mypage")
    public String mypage(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        session.invalidate();
        return "/front/mypage/mypage";
    }

    private static long getTime(LocalDateTime dob, LocalDateTime now) {
        LocalDateTime today = LocalDateTime.of(now.getYear(),
                now.getMonthValue(), now.getDayOfMonth(), dob.getHour(), dob.getMinute(), dob.getSecond());
        Duration duration = Duration.between(today, now);

        long seconds = duration.getSeconds();

        return seconds;
    }

}
