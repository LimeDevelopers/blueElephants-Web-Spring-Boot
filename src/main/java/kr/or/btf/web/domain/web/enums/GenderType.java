package kr.or.btf.web.domain.web.enums;

public enum GenderType {
    MALE("남자"),
    FEMALE("여자"),
    NONE("단체"),
    BATCH("배치");

    final private String name;

    public String getName() {
        return name;
    }

    private GenderType(String name){
        this.name = name;
    }
}
