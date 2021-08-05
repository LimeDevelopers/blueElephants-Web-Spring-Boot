package kr.or.btf.web.services.web;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.btf.web.domain.web.dto.MemberTeacherLogDto;
import kr.or.btf.web.repository.web.MemberTeacherLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberTeacherLogService extends _BaseService {

    private final JPAQueryFactory queryFactory;
    private final MemberTeacherLogRepository memberTeacherLogRepository;
    private final ModelMapper modelMapper;

    public List<MemberTeacherLogDto> schlLogList(Long mberPid) {

        List<Object[]> tmpList = memberTeacherLogRepository.schlLogList(mberPid);

        List<MemberTeacherLogDto> rtnList = new ArrayList<>();

        if (tmpList != null) {
            for (Object[] objects : tmpList) {
                MemberTeacherLogDto memberTeacherLogDto = new MemberTeacherLogDto();
                memberTeacherLogDto.setNm((String)objects[0]);
                memberTeacherLogDto.setMberPid(((BigInteger)objects[1]).longValue());
                memberTeacherLogDto.setAreaNm((String)objects[2]);
                memberTeacherLogDto.setSchlNm((String)objects[3]);
                memberTeacherLogDto.setGrade((Integer)objects[4]);
                memberTeacherLogDto.setBan((String)objects[5]);
                memberTeacherLogDto.setStudentCnt(((BigInteger)objects[7]).intValue());

                rtnList.add(memberTeacherLogDto);
            }
        }

        return rtnList;
    }
}
