package kr.or.btf.web.web.form;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CertificationHisForm extends SearchForm {

    private Long id;
    private String regPsId;
    private LocalDateTime regDtm;
    private Long ctfhvPid;

}
