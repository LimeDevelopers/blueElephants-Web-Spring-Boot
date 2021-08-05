package kr.or.btf.web.domain.web.enums;

public enum AnswerType {
    CHOICE("객관식"), SUBJECTIVE("주관식");

    final private String name;

    public String getName() {
        return name;
    }

    private AnswerType(String name){
        this.name = name;
    }
}
