package kr.or.btf.web.domain.web.enums;

public enum SurveyDvType {
    SATISFACTION("만족도검사"),
    SELF("자가진단"),
    FACTUAL("실태조사");

    final private String name;

    public String getName() {
        return name;
    }

    private SurveyDvType(String name){
        this.name = name;
    }
}
