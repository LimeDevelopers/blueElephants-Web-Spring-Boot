package kr.or.btf.web.web.form;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter @Setter
public class PostscriptImageForm {

    private Long id;
    private Long flPid;
    private String dsc;
    private Integer sn;
    private Long postscriptPid;

}
