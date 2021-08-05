package kr.or.btf.web.domain.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberTeacherLogDto {
    private String nm;
    private Long mberPid;
    private String areaNm;
    private String schlNm;
    private Integer grade;
    private String ban;
    private LocalDateTime regDtm;
    private Integer studentCnt;
}
