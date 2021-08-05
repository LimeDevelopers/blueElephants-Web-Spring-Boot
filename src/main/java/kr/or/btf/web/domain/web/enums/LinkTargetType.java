package kr.or.btf.web.domain.web.enums;

public enum LinkTargetType {
    SELF("_self"), BLANK("_blank");

    final private String name;

    public String getName() {
        return name;
    }

    private LinkTargetType(String name){
        this.name = name;
    }
}
