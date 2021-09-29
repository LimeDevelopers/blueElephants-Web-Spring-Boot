package kr.or.btf.web.domain.web.enums;

public enum InstructorDvTy {
    NO("없음"),
    EDU("교육강사"),
    MOR("모니터링강사");

    final private String name;

    public String getName() {
        return name;
    }

    private InstructorDvTy(String name){
        this.name = name;
    }
}
