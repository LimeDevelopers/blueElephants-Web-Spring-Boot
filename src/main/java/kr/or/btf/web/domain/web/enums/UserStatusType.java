package kr.or.btf.web.domain.web.enums;

public enum UserStatusType {
    ENABLE("사용"),
    DISABLE("미사용");

    final private String name;

    public String getName() {
        return name;
    }

    private UserStatusType(String name){
        this.name = name;
    }
}


