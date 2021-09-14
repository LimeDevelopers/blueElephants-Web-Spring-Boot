package kr.or.btf.web.domain.web.enums;

public enum UserRollType {
    MASTER("마스터"),
    ADMIN("관리자"),
    NORMAL("일반"),
    STUDENT("학생"),
    TEACHER("선생님"),
    PARENT("부모님"),
    LECTURER("푸코강사"),
    COUNSELOR("상담사"),
    GROUP("단체"),
    CREW("지지크루"),
    BATCH("일괄가입"),
    INSTRUCTOR("예방강사");

    final private String name;

    public String getName() {
        return name;
    }

    private UserRollType(String name){
        this.name = name;
    }
}
