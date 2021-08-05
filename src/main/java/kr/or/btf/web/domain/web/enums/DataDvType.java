package kr.or.btf.web.domain.web.enums;

public enum DataDvType {
    VIDEO("동영상"),
    DOCUMENT("문서"),
    IMAGE("이미지");

    final private String name;

    public String getName() {
        return name;
    }

    private DataDvType(String name){
        this.name = name;
    }
}
