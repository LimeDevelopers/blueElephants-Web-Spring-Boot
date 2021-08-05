package kr.or.btf.web.domain.web.enums;

public enum ResultMessageType {
    SAME_EMAIL("동일계정"), FAIL("실패"), SUCCESS("성공"), NOT_SAME("불일치"), DUPLICATION("중복")
    , OVER("제한초과"), IMAGE_SIZE_OVER("이미지 사이즈 범위초과"), FILE_SIZE_OVER("파일 사이즈 제한 초과"), NOT_ALLOW_EXT("파일 확장자 에러");

    final private String name;

    public String getName() {
        return name;
    }

    private ResultMessageType(String name){
        this.name = name;
    }
}
