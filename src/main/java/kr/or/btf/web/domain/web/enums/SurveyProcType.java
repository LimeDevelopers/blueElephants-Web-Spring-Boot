package kr.or.btf.web.domain.web.enums;


public enum SurveyProcType {
    END(" 종료"), WAIT("진행전"), ING("진행중");

    final private String name;

    public String getName() {
        return name;
    }

    private SurveyProcType(String name){
        this.name = name;
    }
}
