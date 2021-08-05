package kr.or.btf.web.domain.web.enums;

public enum AdviceReservationDvType {
    TEL("전화상담"),
    FACE("대면상담");

    final private String name;

    public String getName() {
        return name;
    }

    private AdviceReservationDvType(String name){
        this.name = name;
    }
}
