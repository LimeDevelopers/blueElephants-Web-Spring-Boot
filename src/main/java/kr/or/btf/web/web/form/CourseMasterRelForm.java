package kr.or.btf.web.web.form;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseMasterRelForm extends SearchForm {

    private Long crsMstPid;
    private Long crsPid;
    private Integer sn;
    private Integer[] snArr;
    private String onlineEdu;

}
