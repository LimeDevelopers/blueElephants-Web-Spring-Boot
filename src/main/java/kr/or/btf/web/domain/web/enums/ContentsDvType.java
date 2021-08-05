package kr.or.btf.web.domain.web.enums;

public enum ContentsDvType {
    CDN("CDN"), YOUTUBE("유튜브");

    final private String name;

    public String getName() {
        return name;
    }

    private ContentsDvType(String name){
        this.name = name;
    }
}
