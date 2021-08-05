package kr.or.btf.web.domain.web.enums;

public enum StepType {
    PRIOR("사전"), FIELD("현장"), AFTER("사후");

    final private String name;

    public String getName() {
        return name;
    }

    private StepType(String name){
        this.name = name;
    }
}
