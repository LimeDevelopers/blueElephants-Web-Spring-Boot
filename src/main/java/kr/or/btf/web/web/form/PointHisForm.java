package kr.or.btf.web.web.form;

import kr.or.btf.web.domain.web.enums.PointType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PointHisForm extends SearchForm {

    private Long id;

    private Integer obtainPont;

    private PointType pontDvTy;

    private LocalDateTime regDtm;

    private Long mberPid;

    private String yyyyMMdd;
}
