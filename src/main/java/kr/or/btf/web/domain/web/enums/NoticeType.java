package kr.or.btf.web.domain.web.enums;

public enum NoticeType {
    GENERAL("일반"), URGENCY("긴급");

    final private String name;

    public String getName() {
        return name;
    }

    private NoticeType(String name){
        this.name = name;
    }
}
