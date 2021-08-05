package kr.or.btf.web.services.web;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.btf.web.common.Constants;
import kr.or.btf.web.domain.web.*;
import kr.or.btf.web.domain.web.dto.CourseRequestCompleteDto;
import kr.or.btf.web.domain.web.enums.CompleteStatusType;
import kr.or.btf.web.repository.web.CourseRequestCompleteRepository;
import kr.or.btf.web.web.form.CourseRequestCompleteForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CourseRequestCompleteService extends _BaseService {

    private final JPAQueryFactory queryFactory;
    private final CourseRequestCompleteRepository courseRequestCompleteRepository;
    private final ModelMapper modelMapper;

    public Page<CourseRequestComplete> list(Pageable pageable, Long mberPid) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, Constants.DEFAULT_PAGESIZE); // <- Sort 추가

        QCourseRequestComplete qCourseRequestComplete = QCourseRequestComplete.courseRequestComplete;
        QAccount qAccount = QAccount.account;
        QCourseRequest qCourseRequest = QCourseRequest.courseRequest;
        QCourseItem qCourseItem = QCourseItem.courseItem;

        OrderSpecifier<Long> orderSpecifier = qCourseRequestComplete.id.desc();

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qCourseRequest.mberPid.eq(mberPid));
        builder.and(qCourseRequest.crsMstPid.eq(qCourseRequestComplete.crsMstPid));

        JPAQuery<CourseRequestComplete> tempList = queryFactory
                .select(Projections.fields(CourseRequestComplete.class,
                        qCourseRequestComplete.id,
                        qCourseRequestComplete.cmplSttTy,
                        qCourseRequestComplete.cmplOpetrId,
                        qCourseRequestComplete.cmplPrsDtm,
                        qCourseRequestComplete.atnlcReqPid,
                        qCourseRequestComplete.crsMstPid,
                        qCourseRequestComplete.crsPid,
                        qCourseRequestComplete.sn,
                        ExpressionUtils.as(
                                JPAExpressions.select(qCourseItem.ttl)
                                        .from(qCourseItem)
                                        .where(qCourseItem.crsPid.eq(qCourseRequestComplete.crsPid)),
                                "courseItemTtl")

                ))
                .from(qCourseRequestComplete)
                .leftJoin(qCourseRequest).on(qCourseRequestComplete.atnlcReqPid.eq(qCourseRequest.id))
                .where(builder)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(orderSpecifier);

            QueryResults<CourseRequestComplete> mngList = tempList.fetchResults();

        return new PageImpl<>(mngList.getResults(), pageable, mngList.getTotal());
    }

    public Page<CourseRequestCompleteDto> listForMyPage(Pageable pageable, Long atnlcReqPid,Long mberPid) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, Constants.DEFAULT_PAGESIZE); // <- Sort 추가
        Page<Object[]> objects = courseRequestCompleteRepository.listForMyPage(pageable,atnlcReqPid, mberPid);

        List<CourseRequestCompleteDto> tempList = new ArrayList<>();

        if (objects != null) {
            for (Object[] vo : objects) {
                CourseRequestCompleteDto courseRequestCompleteDto = new CourseRequestCompleteDto();
                courseRequestCompleteDto.setCmplHistPid(((BigInteger)vo[0]).longValue());
                courseRequestCompleteDto.setAtnlcReqPid(((BigInteger)vo[1]).longValue());
                courseRequestCompleteDto.setCrsMstPid(((BigInteger)vo[2]).longValue());
                courseRequestCompleteDto.setCrsPid(((BigInteger)vo[3]).longValue());
                courseRequestCompleteDto.setSn((Integer)vo[4]);
                courseRequestCompleteDto.setCrsNm((String)vo[5]);
                courseRequestCompleteDto.setTtl((String)vo[6]);
                courseRequestCompleteDto.setCrssqPid(((BigInteger)vo[7]).longValue());
                courseRequestCompleteDto.setProcNm((String)vo[8]);
                courseRequestCompleteDto.setProcPer((String)vo[9]);

                tempList.add(courseRequestCompleteDto);

            }
        }

        Page<CourseRequestCompleteDto> rtnList = new PageImpl<>(tempList, objects.getPageable(), objects.getTotalElements());

        return rtnList;
    }

    public CourseRequestComplete load(Long id) {
        CourseRequestComplete complete = courseRequestCompleteRepository.findById(id).orElseGet(CourseRequestComplete::new);

        return complete;
    }

    public CourseRequestComplete loadByform(CourseRequestCompleteForm form) {

        QCourseRequestComplete qCourseRequestComplete = QCourseRequestComplete.courseRequestComplete;
        QAccount qAccount = QAccount.account;
        QCommonCode qCommonCode = QCommonCode.commonCode;

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(qCourseRequestComplete.crsMstPid.eq(form.getCrsMstPid()));
        if (form.getSn() != null) {
            builder.and(qCourseRequestComplete.sn.eq(form.getSn()));
        }
        if (form.getAtnlcReqPid() != null) {
            builder.and(qCourseRequestComplete.atnlcReqPid.eq(form.getAtnlcReqPid()));
        }

        List<CourseRequestComplete> list = queryFactory
                .select(Projections.fields(CourseRequestComplete.class,
                        qCourseRequestComplete.id,
                        qCourseRequestComplete.cmplSttTy,
                        qCourseRequestComplete.cmplOpetrId,
                        qCourseRequestComplete.cmplPrsDtm,
                        qCourseRequestComplete.atnlcReqPid,
                        qCourseRequestComplete.crsMstPid,
                        qCourseRequestComplete.crsPid,
                        qCourseRequestComplete.sn
                ))
                .from(qCourseRequestComplete)
                .where(builder)
                .fetch();

        return (list != null && list.size() > 0 ? list.get(0) : new CourseRequestComplete());
    }

    @Transactional
    public void delete(CourseRequestCompleteForm form) {

        courseRequestCompleteRepository.deleteById(form.getId());
    }



     /**
     * @param form
     * @return
     * @throws Exception
     */

    @Transactional
    public boolean insert(CourseRequestCompleteForm form) {

        try {
            CourseRequestComplete complete = modelMapper.map(form, CourseRequestComplete.class);
            courseRequestCompleteRepository.save(complete);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public boolean update(List<CourseRequestComplete> list) {

        try {

            for (CourseRequestComplete requestComplete : list) {
                CourseRequestComplete complete = courseRequestCompleteRepository.findById(requestComplete.getId()).orElseGet(CourseRequestComplete::new);
                complete.setCmplSttTy(requestComplete.getCmplSttTy());
                complete.setCmplOpetrId(requestComplete.getCmplOpetrId());
                complete.setCmplPrsDtm(requestComplete.getCmplPrsDtm());
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public boolean updateSttTy(CourseRequestCompleteForm form, CompleteStatusType completeStatusType) {
        try {
            CourseRequestComplete complete = courseRequestCompleteRepository.findByAtnlcReqPidAndCrsMstPidAndCrsPidAndSn(form.getAtnlcReqPid(), form.getCrsMstPid(), form.getCrsPid(), form.getSn());
            if (completeStatusType.equals(CompleteStatusType.COMPLETE)) {
                complete.setCmplSttTy(completeStatusType.name());
                complete.setCmplPrsDtm(LocalDateTime.now());
            } else {
                complete.setCmplSttTy(completeStatusType.name());
                complete.setCmplPrsDtm(null);
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public boolean inspectionChk(CourseRequestCompleteForm form) {
        CourseRequestComplete complete = courseRequestCompleteRepository.findByAtnlcReqPidAndCrsMstPidAndCrsPidAndSn(form.getAtnlcReqPid(), form.getCrsMstPid(), form.getCrsPid(), form.getSn());
        if (complete.getCmplSttTy().equals(CompleteStatusType.COMPLETE.name())) {
            return true;
        } else {
            return false;
        }
    }

    public Long countComplete(CourseRequestCompleteForm form) {

        QCourseRequestComplete qCourseRequestComplete = QCourseRequestComplete.courseRequestComplete;
        QCourseRequest qCourseRequest = QCourseRequest.courseRequest;

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(qCourseRequestComplete.sn.eq(form.getSn()));
        builder.and(qCourseRequestComplete.cmplSttTy.eq(CompleteStatusType.COMPLETE.name()));

        long count = queryFactory
                .select(Projections.fields(CourseRequestComplete.class,
                        qCourseRequestComplete.id
                ))
                .from(qCourseRequestComplete)
                .innerJoin(qCourseRequest).on(qCourseRequestComplete.atnlcReqPid.eq(qCourseRequest.id)
                        .and(qCourseRequestComplete.crsMstPid.eq(qCourseRequest.crsMstPid))
                        .and(qCourseRequest.mberPid.eq(form.getMberPid()))
                        .and(qCourseRequest.crsMstPid.eq(form.getCrsMstPid())))
                .where(builder)
                .fetchCount();

        return count;
    }
}
