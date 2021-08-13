package kr.or.btf.web.domain.web.enums;

/**
 * AuthType [인증타입 enums]
 * @author : jerry
 * @version : 1.0.0
 * 작성일 : 2021/08/13
**/
public enum AuthType {
    EMAIL_AUTH("이메일"),
    PHONE_AUTH("휴대폰");
    /*BOTH_AUTH("휴대폰+이메일");*/

    final private String name;

    public String getName() {
        return name;
    }

    AuthType(String name){
        this.name = name;
    }
}
