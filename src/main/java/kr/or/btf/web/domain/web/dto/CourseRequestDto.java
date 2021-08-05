package kr.or.btf.web.domain.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseRequestDto {
    private String crsNm;
    private Long crsMstPid;

    private String sn2SttTy;
    private String sn2SttTyNm;
    private Long sn2CrsPid;

    private String sn3SttTy;
    private String sn3SttTyNm;
    private Long sn3CrsPid;

    private String sn4SttTy;
    private String sn4SttTyNm;
    private Long sn4CrsPid;

    private String sn5SttTy;
    private String sn5SttTyNm;
    private Long sn5CrsPid;

    private String sn6SttTy;
    private String sn6SttTyNm;
    private Long sn6CrsPid;
}
