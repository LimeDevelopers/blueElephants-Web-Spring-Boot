package kr.or.btf.web.domain.web.enums;

public enum GenderType {
    MALE("남"),
    FEMALE("여");

    final private String name;

    public String getName() {
        return name;
    }

    private GenderType(String name){
        this.name = name;
    }
}
