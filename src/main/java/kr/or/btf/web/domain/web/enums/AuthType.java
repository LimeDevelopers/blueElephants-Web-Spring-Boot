package kr.or.btf.web.domain.web.enums;

public enum AuthType {
    PHONE_AUTH("휴대폰인증"),
    EMAIL_AUTH("이메일인증"),
    BOTH_AUTH("둘다가능");

    final private String name;

    public String getName() {
        return name;
    }

    private AuthType(String name){
        this.name = name;
    }
}
