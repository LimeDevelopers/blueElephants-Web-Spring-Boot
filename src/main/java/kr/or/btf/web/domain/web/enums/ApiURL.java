package kr.or.btf.web.domain.web.enums;

/**
 * ApiURL API 도메인 라우터 관리 enum
 * @author : jerry
 * @version : 1.0.0
 * 작성일 : 2021/08/25
**/
public enum ApiURL {
    AURORA_DOMAIN("https://api.sphokidz.com"),
    AURORA_TEST_DOMAIN("https://stapi.sphokidz.com"),
    AURORA_URL("/uploadMyPhoto/PUCO");

    final private String name;

    public String getName() {
        return name;
    }

    private ApiURL(String name){
        this.name = name;
    }
}
