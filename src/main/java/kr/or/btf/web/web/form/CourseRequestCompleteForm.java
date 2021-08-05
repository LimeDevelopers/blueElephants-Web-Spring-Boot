package kr.or.btf.web.web.form;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CourseRequestCompleteForm extends SearchForm {

    private Long id;
    private String cmplSttTy;
    private String cmplOpetrId;
    private LocalDateTime cmplPrsDtm;
    private Long atnlcReqPid;
    private Long crsMstPid;
    private Long crsPid;
    private Integer sn;
    private Long mberPid;
}
