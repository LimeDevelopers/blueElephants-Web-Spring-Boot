package kr.or.btf.web.domain.web.enums;

public enum MessageSendType {
    SMS("문자"), MAIL("메일"), TACK("카카오톡"), PUSH("푸시");

    final private String name;

    public String getName() {
        return name;
    }

    private MessageSendType(String name){
        this.name = name;
    }
}
