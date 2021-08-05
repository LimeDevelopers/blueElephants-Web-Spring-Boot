package kr.or.btf.web.domain.web.enums;

public enum BoardType {
    LIST("목록"), CARD("카드"), THUMB("썸네일");

    final private String name;

    public String getName() {
        return name;
    }

    private BoardType(String name){
        this.name = name;
    }
}
