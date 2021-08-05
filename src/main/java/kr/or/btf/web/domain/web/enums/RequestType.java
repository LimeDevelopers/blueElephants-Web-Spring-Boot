package kr.or.btf.web.domain.web.enums;

public enum RequestType {
    SIGNUP("회원가입"), IDFIND("아이디찾기"), PWFIND("비밀번호찾기"), INFOMODI("정보수정"), PWNEW("비밀번호변경"), HPMODI("핸드폰번호 변경");

    final private String name;

    public String getName() {
        return name;
    }

    private RequestType(String name){
        this.name = name;
    }
}
