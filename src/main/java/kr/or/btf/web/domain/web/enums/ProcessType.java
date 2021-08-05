package kr.or.btf.web.domain.web.enums;

public enum ProcessType {
    REQUEST("상담대기"), CHECKED("확인"), COMPLETE("상담완료");

    final private String name;

    public String getName() {
        return name;
    }

    private ProcessType(String name){
        this.name = name;
    }
}
