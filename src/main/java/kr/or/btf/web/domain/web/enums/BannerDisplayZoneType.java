package kr.or.btf.web.domain.web.enums;

public enum BannerDisplayZoneType {
    MAIN("메인");

    final private String name;

    public String getName() {
        return name;
    }

    private BannerDisplayZoneType(String name){
        this.name = name;
    }
}
