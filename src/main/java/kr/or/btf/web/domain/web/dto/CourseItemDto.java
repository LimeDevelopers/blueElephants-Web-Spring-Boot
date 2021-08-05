package kr.or.btf.web.domain.web.dto;

import kr.or.btf.web.domain.web.enums.ContentsDvType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseItemDto {

    private Long id;
    private String ttl;
    private String cn;
    private String imgFl;
    private ContentsDvType cntntsDvTy;
    private String cntntsUrl;
    private Long cntntsLen;
    private int sno;
    private Long crsPid;
    private String procNm;
}
