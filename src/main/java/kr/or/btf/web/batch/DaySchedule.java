package kr.or.btf.web.batch;

import kr.or.btf.web.common.Base;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
public class DaySchedule extends Base {

	/* 속성
	*           *　　　　　　*　　　　　　*　　　　　　*　　　　　　*
	초(0-59)   분(0-59)　　시간(0-23)　　일(1-31)　　월(1-12)　　요일(0-7)
	cron = "1 * * * * *"
	initialDelay 초기 대기
	fixedDelay 이전작업이 끝난후 작업
	fixedRate 지정된 시간단위로 작업
	@Scheduled(fixedRateString = "5", initialDelay = 3000)
	*/

    /**
     * 사용승인 + 시작~종료일자가 포함된 사용자 승인
     */
    @Scheduled(fixedDelay = 1000 * 5)
    public void JobByEnableUser() {
		/*List<AuthMember> authMembers = authMemberService.enableUser(LocalDateTime.now());
		for (AuthMember authMember : authMembers) {
			scheduleService.enableUser(authMember);
		}*/
    }

    /**
     * 종료일자가 지난 사용자 제외
     */
    @Scheduled(fixedDelay = 1000 * 5)
    public void JobByDisableUser() {
		/*List<AuthMember> authMembers = authMemberService.enableUser(LocalDateTime.now());
		for (AuthMember authMember : authMembers) {
			scheduleService.enableUser(authMember);
		}*/
    }

}
