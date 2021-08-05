package kr.or.btf.web.domain.web.enums;


public enum StatusType {
    GOING(" 진행중"), DEADLINE("마감"), TERMINATION("종료");

    final private String name;

    public String getName() {
        return name;
    }

    private StatusType(String name){
        this.name = name;
    }
}
