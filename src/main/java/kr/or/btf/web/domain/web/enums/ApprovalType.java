package kr.or.btf.web.domain.web.enums;

public enum ApprovalType {
    request("기안"), approval("결재"), agreement("합의");

    final private String name;

    public String getName() {
        return name;
    }

    private ApprovalType(String name){
        this.name = name;
    }
}
