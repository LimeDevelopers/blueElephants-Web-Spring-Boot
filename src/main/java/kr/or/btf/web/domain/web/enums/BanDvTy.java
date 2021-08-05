package kr.or.btf.web.domain.web.enums;

public enum BanDvTy {
    MAIN("메인배너"),
    CARD("카드뉴스");

    final private String name;

    public String getName() {
        return name;
    }

    private BanDvTy(String name){
        this.name = name;
    }
}
