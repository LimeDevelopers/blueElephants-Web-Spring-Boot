package kr.or.btf.web.web.form;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class NamaneForm extends SearchForm{
    private Long mberPid;
    private String rotationType;
    private String printText;
    private String encodeStr;
}
