package kr.or.btf.web.services.web;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.btf.web.domain.web.MemberSchoolLog;
import kr.or.btf.web.domain.web.QMemberSchoolLog;
import kr.or.btf.web.domain.web.dto.MemberSchoolLogDto;
import kr.or.btf.web.repository.web.MemberSchoolLogRepository;
import kr.or.btf.web.web.form.MemberSchoolForm;
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
public class MemberSchoolLogService extends _BaseService {

    private final JPAQueryFactory queryFactory;
    private final MemberSchoolLogRepository memberSchoolLogRepository;
    private final ModelMapper modelMapper;

    public List<MemberSchoolLog> list() {

        QMemberSchoolLog qMemberSchoolLog = QMemberSchoolLog.memberSchoolLog;

        OrderSpecifier<Long> orderSpecifier = qMemberSchoolLog.id.desc();

        BooleanBuilder builder = new BooleanBuilder();
        //builder.and(qMemberSchoolLog.mberPid.eq());

        List<MemberSchoolLog> mngList = queryFactory
                .select(Projections.fields(MemberSchoolLog.class,
                        qMemberSchoolLog.id,
                        qMemberSchoolLog.areaNm,
                        qMemberSchoolLog.schlNm,
                        qMemberSchoolLog.grade,
                        qMemberSchoolLog.ban,
                        qMemberSchoolLog.no,
                        qMemberSchoolLog.teacherNm,
                        qMemberSchoolLog.mberPid
                ))
                .from(qMemberSchoolLog)
                .where(builder)
                .orderBy(orderSpecifier)
                .fetch();

        return mngList;
    }

    public List<MemberSchoolLogDto> childSchlLogList(Long mberPid) {

        List<Object[]> tmpList = memberSchoolLogRepository.childSchlLogList(mberPid);

        List<MemberSchoolLogDto> rtnList = new ArrayList<>();
        if (tmpList != null) {
            for (Object[] objects : tmpList) {
                MemberSchoolLogDto memberSchoolLogDto = new MemberSchoolLogDto();
                memberSchoolLogDto.setNm((String)objects[0]);
                memberSchoolLogDto.setAreaNm((String)objects[1]);
                memberSchoolLogDto.setSchlNm((String)objects[2]);
                memberSchoolLogDto.setGrade((Integer)objects[3]);
                memberSchoolLogDto.setBan((String)objects[4]);
                memberSchoolLogDto.setNo((Integer)objects[5]);
                memberSchoolLogDto.setMberPid(((BigInteger)objects[6]).longValue());
                //memberSchoolLogDto.setRegDtm((LocalDateTime)objects[7].);

                rtnList.add(memberSchoolLogDto);
            }
        }

        return rtnList;
    }

    public List<MemberSchoolLogDto> childLogList(MemberSchoolForm memberSchoolForm) {

        List<Object[]> tmpList = memberSchoolLogRepository.childLogList(memberSchoolForm);

        List<MemberSchoolLogDto> rtnList = new ArrayList<>();
        if (tmpList != null) {
            for (Object[] objects : tmpList) {
                MemberSchoolLogDto memberSchoolLogDto = new MemberSchoolLogDto();
                memberSchoolLogDto.setNm((String)objects[0]);
                memberSchoolLogDto.setAreaNm((String)objects[1]);
                memberSchoolLogDto.setSchlNm((String)objects[2]);
                memberSchoolLogDto.setGrade((Integer)objects[3]);
                memberSchoolLogDto.setBan((String)objects[4]);
                memberSchoolLogDto.setNo((Integer)objects[5]);
                memberSchoolLogDto.setMberPid(((BigInteger)objects[6]).longValue());

                rtnList.add(memberSchoolLogDto);
            }
        }

        return rtnList;
    }

    public MemberSchoolLog load(Long id) {
        MemberSchoolLog memberSchoolLog = memberSchoolLogRepository.findById(id).orElseGet(MemberSchoolLog::new);

        return memberSchoolLog;
    }

/*    @Transactional
    public boolean insert(MemberSchoolLogForm memberSchoolLogForm) {
        try {

            MemberSchoolLog account = modelMapper.map(memberSchoolLogForm, MemberSchoolLog.class);
            memberSchoolLogRepository.save(account);

            return true;
        } catch (Exception e) {
            return false;
        }
    }*/

/*    @Transactional
    public boolean update(MemberSchoolLogForm memberSchoolLogForm) {

        try {
            MemberSchool account = memberSchoolLogRepository.findById(memberSchoolLogForm.getId()).orElseGet(MemberSchoolLog::new);
            return true;
        } catch (Exception e) {
            return false;
        }
    }*/

/*    @Transactional
    public boolean delete(MemberSchoolLogForm memberSchoolLogForm) {

        try {
            memberSchoolLogRepository.deleteById(memberSchoolLogForm.getId());
            return true;
        } catch (Exception e) {
            return false;
        }

    }*/
}
