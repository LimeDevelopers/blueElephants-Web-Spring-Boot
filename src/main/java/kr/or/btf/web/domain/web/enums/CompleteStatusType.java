package kr.or.btf.web.domain.web.enums;


public enum CompleteStatusType {
    APPLY("진행중"), COMPLETE("완료");

    final private String name;

    public String getName() {
        return name;
    }

    private CompleteStatusType(String name){
        this.name = name;
    }
}
