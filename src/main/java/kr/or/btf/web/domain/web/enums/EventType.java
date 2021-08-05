package kr.or.btf.web.domain.web.enums;

public enum EventType {
    EVENT("행사", "basic"),
    EDU("교육", "forum"),
    GROUP("모집", "prevent"),
    ETC("기타", "family");

    final private String name;
    final private String className;

    public String getName() {
        return name;
    }

    public String getClassName() { return className; }

    private EventType(String name, String className){
        this.name = name;
        this.className = className;
    }
}
