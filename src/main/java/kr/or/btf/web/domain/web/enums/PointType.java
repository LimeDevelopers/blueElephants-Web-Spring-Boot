package kr.or.btf.web.domain.web.enums;

public enum PointType {

    ATTENDANCE_1DAY("출석수", 3, "1번 접속", 1),
    ATTENDANCE_5DAY_CONTINUATION("출석수", 8, "5일 연속 접속시 추가점수", 1),
    ATTENDANCE_10DAY_CONTINUATION("출석수", 15, "10일 연속 접속시 추가점수", 1),
    ATTENDANCE_1MONTH_AFTER("출석수", 5, "1달 미접속 후 접속시", 1),
    ATTENDANCE_3MONTH_AFTER("출석수", 10, "3달 미접속 후 접속시", 1),
    ACTIVITY_COMMENT("활동", 1, "댓글, 대댓글, 공감", 3),
    EDUCATION_DATA_VIEW("교육", 1, "자료실 게시물 확인", 2),
    EDUCATION_COURSE_COMPLETE("교육", 5, "강의수강", 1),
    EDUCATION_SURVEY_COMPLETE("교육", 5, "설문실시, 만족도 조사", 1),
    EXPERIENCE_ACTIVE("체험", 1, "사이버폭력 체험, 피해자 목소리, 위로메시지 듣기, 자가진단", 1),
    HELP_DATA_VIEW("도움/상담", 1, "도움요청 확인, 자료실, 상담문의", 1),
    HELP_REQUEST("도움/상담", 2, "게시판 상담", 1),
    SHARE_LINK("나눔", 1, "나눔링크이동", 3),
    CHALLENGE_DATA_VIEW("챌린지", 1, "영상시청(대국민캠페인, 지지선언), 지지서명링크", 3),
    CHALLENGE_CREW_VIEW("챌린지", 2, "업로드(사진/영상/기록), 지지크루 활동기록", 1),
    POLICY_DATA_WRITE_VIEW("정책제안", 1, "제안하기, 목록확인", 1),
    NOTICE_DATA_WRITE_VIEW("소식/행사", 1, "신청하기, 목록확인", 1);


    final private String name;
    final private Integer point;
    final private String desc;
    final private Integer limit;

    public String getName() {
        return name;
    }

    public Integer getPoint() {
        return point;
    }

    public String getDesc() {
        return desc;
    }

    public Integer getLimit() {
        return limit;
    }

    private PointType(String name, Integer point, String desc, Integer limit){
        this.name = name;
        this.point = point;
        this.desc = desc;
        this.limit = limit;
    }
}
