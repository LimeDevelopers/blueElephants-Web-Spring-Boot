package kr.or.btf.web.web.form;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonCodeForm extends SearchForm {

    private Long id;
    private Long prntCodePid;
    private int codeSno;
    private String codeNm;
    private String codeDsc;
    private String codeValue;

}
