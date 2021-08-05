package kr.or.btf.web.domain.web.enums;

public enum ActionType {
    INNER_LINK("내부링크"),
    EXTRA_LINK("외부링크");

    final private String name;

    public String getName() {
        return name;
    }

    private ActionType(String name){
        this.name = name;
    }
}
