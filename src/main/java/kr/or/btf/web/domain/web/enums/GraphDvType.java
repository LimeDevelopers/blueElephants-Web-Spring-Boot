package kr.or.btf.web.domain.web.enums;

public enum GraphDvType {
    MAIN_CYBER_BAR_CHART("메인 사이버 폭력 막대차트");

    final private String name;

    public String getName() {
        return name;
    }

    private GraphDvType(String name){
        this.name = name;
    }
}
