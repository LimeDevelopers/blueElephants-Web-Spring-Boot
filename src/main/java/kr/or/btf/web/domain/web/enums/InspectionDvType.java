package kr.or.btf.web.domain.web.enums;

public enum InspectionDvType {
    BEFORE("사전검사"),
    AFTER("사후검사");

    final private String name;

    public String getName() {
        return name;
    }

    private InspectionDvType(String name){
        this.name = name;
    }
}
