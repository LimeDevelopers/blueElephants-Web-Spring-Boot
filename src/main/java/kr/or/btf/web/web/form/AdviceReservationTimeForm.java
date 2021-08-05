package kr.or.btf.web.web.form;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdviceReservationTimeForm extends SearchForm {

    private Long id;
    private Long hopeTimeCodeId;
    private Long advcRsvPid;
}
