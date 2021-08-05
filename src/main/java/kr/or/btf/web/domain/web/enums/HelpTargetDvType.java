package kr.or.btf.web.domain.web.enums;

public enum HelpTargetDvType {
    STUDENT("청소년"),
    NORMAL("일반"),
    TEACHER("교원"),
    PARENT("부모"),
    ECT("기타");

    final private String name;

    public String getName() {
        return name;
    }

    private HelpTargetDvType(String name){
        this.name = name;
    }
}
