package kr.or.btf.web.web.form;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CourseRequestForm extends SearchForm {

    private Long id;
    private LocalDateTime regDtm;
    private String confmAt;
    private String areaNm;
    private String schlNm;
    private Integer grade;
    private String ban;
    private Integer no;
    private Long crsMstPid;
    private Long mberPid;
    private Integer sn;
    private String onlineEdu;
}
