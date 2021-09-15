package kr.or.btf.web.domain.web.enums;

public enum MberDvType {
    STUDENT("청소년"),
    NORMAL("일반"),
    TEACHER("교원"),
    PARENT("부모"),
    LECTURER("푸코강사"),
    INSTRUCTOR("예방강사");

    final private String name;

    public String getName() {
        return name;
    }

    private MberDvType(String name){
        this.name = name;
    }
}
