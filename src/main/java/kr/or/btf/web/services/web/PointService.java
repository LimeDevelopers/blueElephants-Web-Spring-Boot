package kr.or.btf.web.services.web;

import kr.or.btf.web.domain.web.Account;
import kr.or.btf.web.domain.web.LoginCnntLogs;
import kr.or.btf.web.domain.web.enums.PointType;
import kr.or.btf.web.web.form.LoginCnntLogsForm;
import kr.or.btf.web.web.form.PointHisForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PointService extends _BaseService{

    private final PointHisService pointHisService;
    private final LoginCnntLogsService loginCnntLogsService;

    @Transactional
    public boolean checkAndAddPoint(Account account, PointType pointType) {
        try {
            switch (pointType) {
                case ATTENDANCE_1DAY:
                case ATTENDANCE_5DAY_CONTINUATION:
                case ATTENDANCE_10DAY_CONTINUATION:
                case ATTENDANCE_1MONTH_AFTER:
                case ATTENDANCE_3MONTH_AFTER:
                    checkAttendance(account);
                    break;
                case ACTIVITY_COMMENT:
                case EDUCATION_DATA_VIEW:
                case EDUCATION_COURSE_COMPLETE:
                case EDUCATION_SURVEY_COMPLETE:
                case EXPERIENCE_ACTIVE:
                case HELP_DATA_VIEW:
                case HELP_REQUEST:
                case SHARE_LINK:
                case CHALLENGE_DATA_VIEW:
                case CHALLENGE_CREW_VIEW:
                case POLICY_DATA_WRITE_VIEW:
                case NOTICE_DATA_WRITE_VIEW:
                    break;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void checkAttendance(Account account) throws Exception {
        LoginCnntLogsForm loginCnntLogsForm = new LoginCnntLogsForm();
        loginCnntLogsForm.setCnctId(account.getLoginId());
        loginCnntLogsForm.setSuccesAt("Y");

        LoginCnntLogs lastLogin = loginCnntLogsService.lastLogin(loginCnntLogsForm);
        if (lastLogin == null) {
            setPoint(account, PointType.ATTENDANCE_1DAY);   //최초 로그인시 출첵 포인트 지급
            return; //최초 로그인시 이전기록 없으므로 리턴
        }
        String lastLoginDate = lastLogin.getCnctDtm().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        LocalDateTime now = LocalDateTime.now();
        if (!lastLoginDate.equals(now.format(DateTimeFormatter.ofPattern("yyyyMMdd")))) {
            //오늘 최초 로그인시 포인트 지급
            setPoint(account, PointType.ATTENDANCE_1DAY);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
        LocalDateTime before1Month = LocalDateTime.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DATE), 0, 0);
        if (lastLogin.getCnctDtm().isBefore(before1Month)) {
            //마지막 로그인이 한달전이면 포인트 지급
            setPoint(account, PointType.ATTENDANCE_1MONTH_AFTER);
        }
        calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 3);
        LocalDateTime before3Month = LocalDateTime.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DATE), 0, 0);
        if (lastLogin.getCnctDtm().isBefore(before3Month)) {
            //마지막 로그인이 세달전이면 포인트 지급
            setPoint(account, PointType.ATTENDANCE_3MONTH_AFTER);
        }

        String[] day5 = new String[4];  //오늘 제외 이전 4일
        String[] day10 = new String[9]; //오늘 제외 이전 9일
        calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        for (int i=0; i<day10.length; i++) {
            calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 1);
            if (i < day5.length) {
                day5[i] = format.format(calendar.getTime());
            }
            day10[i] = format.format(calendar.getTime());
        }

        loginCnntLogsForm.setYyyyMMddArr(day5);
        List<LoginCnntLogs> listForDay5 = loginCnntLogsService.list(loginCnntLogsForm);
        if (listForDay5 != null && listForDay5.size() == day5.length) {
            //연속 5일 로그인시 포인트 지급
            setPoint(account, PointType.ATTENDANCE_5DAY_CONTINUATION);

            //5일을 만족해야 10일 체크
            loginCnntLogsForm.setYyyyMMddArr(day10);
            List<LoginCnntLogs> listForDay10 = loginCnntLogsService.list(loginCnntLogsForm);
            if (listForDay10 != null && listForDay10.size() == day10.length) {
                //연속 10일 로그인시 포인트 지급
                setPoint(account, PointType.ATTENDANCE_10DAY_CONTINUATION);
            }
        }
    }

    @Transactional
    public void setPoint(Account account, PointType pointType) throws Exception {
        PointHisForm form = new PointHisForm();
        form.setMberPid(account.getId());
        form.setObtainPont(pointType.getPoint());
        form.setPontDvTy(pointType);
        form.setYyyyMMdd(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        long count = pointHisService.count(form);
        if (count == 0) {
            //오늘 포인트 지급이 안되었으면 포인트 지급
            pointHisService.insert(form);
        }
    }
}
