package kr.or.btf.web.domain.web.enums;

public enum AgreeType {
    SERVICE("서비스이용약관"), PROTECTED("개인정보처리방침"), MARKETING("마케팅정보"),
    EXHIBITION_MARKETING("전시회"),
    EXHIBITION_AGREE("행사주최자약관");

    final private String name;

    public String getName() {
        return name;
    }

    private AgreeType(String name){
        this.name = name;
    }
}
