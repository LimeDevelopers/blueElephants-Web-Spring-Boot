package kr.or.btf.web.domain.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberSchoolLogDto {
    private String nm;
    private String areaNm;
    private String schlNm;
    private Integer grade;
    private String ban;
    private Integer no;
    private Long mberPid;
    private LocalDateTime regDtm;
}
