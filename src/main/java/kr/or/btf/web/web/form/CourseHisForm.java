package kr.or.btf.web.web.form;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CourseHisForm extends SearchForm {

    private Long id;
    private LocalDateTime atnlcDtm;
    private int atnlcHour;
    private Long cntntsLen;
    private Long mberPid;
    private Long crssqPid;

    private Long crsMstPid;
    private Long crsPid;
    private Integer sn;
}
