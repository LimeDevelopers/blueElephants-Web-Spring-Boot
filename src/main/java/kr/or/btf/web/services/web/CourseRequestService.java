package kr.or.btf.web.services.web;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.btf.web.common.Constants;
import kr.or.btf.web.domain.web.*;
import kr.or.btf.web.domain.web.dto.CourseRequestDto;
import kr.or.btf.web.domain.web.enums.CompleteStatusType;
import kr.or.btf.web.repository.web.CourseRequestCompleteRepository;
import kr.or.btf.web.repository.web.CourseRequestRepository;
import kr.or.btf.web.web.form.CourseRequestForm;
import kr.or.btf.web.web.form.SearchForm;
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
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CourseRequestService extends _BaseService {

    private final JPAQueryFactory queryFactory;
    private final CourseRequestRepository courseRequestRepository;
    private final CourseRequestCompleteRepository courseRequestCompleteRepository;
    private final ModelMapper modelMapper;

    public Page<CourseRequest> list(Pageable pageable, SearchForm searchForm) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, Constants.DEFAULT_PAGESIZE); // <- Sort 추가

        QCourseRequest qCourseRequest = QCourseRequest.courseRequest;
        QCourseRequestComplete qCourseRequestComplete1 = new QCourseRequestComplete("courseRequestComplete1");
        QCourseRequestComplete qCourseRequestComplete2 = new QCourseRequestComplete("courseRequestComplete2");
        QCourse qCourse = QCourse.course;
        QAccount qAccount1 = new QAccount("account1");
        QAccount qAccount2 = new QAccount("account2");
        QCourseItem qCourseItem = QCourseItem.courseItem;
        QCourseHis qCourseHis = QCourseHis.courseHis;
        QCourseMaster qCourseMaster = QCourseMaster.courseMaster;

        OrderSpecifier<Long> orderSpecifier = qCourseRequest.id.desc();

        BooleanBuilder builder = new BooleanBuilder();
        if (searchForm.getMngPid() != null) {
            builder.and(qCourseRequest.crsMstPid.eq(searchForm.getMngPid()));
        }
        if (searchForm.getUserPid() != null) {
            builder.and(qCourseRequest.mberPid.eq(searchForm.getUserPid()));
        }
        if (searchForm.getCrsMstPid() != null) {
            builder.and(qCourseRequest.crsMstPid.eq(searchForm.getCrsMstPid()));
        }

        QueryResults<CourseRequest> mngList = queryFactory
                .select(Projections.fields(CourseRequest.class,
                        qCourseRequest.id,
                        qCourseRequest.regDtm,
                        qCourseRequest.confmAt,
                        qCourseRequest.areaNm,
                        qCourseRequest.schlNm,
                        qCourseRequest.grade,
                        qCourseRequest.ban,
                        qCourseRequest.no,
                        qCourseRequest.crsMstPid,
                        qCourseRequest.mberPid,
                        qAccount1.nm.as("mberNm"),
                        qCourseRequestComplete1.id.as("cmplHistPid"),
                        qCourseRequestComplete1.cmplSttTy,
                        qCourseRequestComplete1.cmplOpetrId,
                        qAccount2.nm.as("cmplOpetrNm"),
                        qCourseRequestComplete1.cmplPrsDtm,
                        qCourse.crsNm,
                        qCourseMaster.crsNm.as("masterCrsNm"),
                        ExpressionUtils.as(
                                Expressions.numberTemplate(Integer.class, "fn_getMyAtnlcHour({0},{1})", qCourse.id, qAccount1.id)
                                ,
                                "myHour"),
                        ExpressionUtils.as(
                                JPAExpressions.select(qCourseItem.cntntsLen.sum())
                                        .from(qCourseItem)
                                        .where(qCourseItem.crsPid.eq(qCourse.id)),
                                "totalLen")

                ))
                .from(qCourseRequest)
                .leftJoin(qAccount1).on(qCourseRequest.mberPid.eq(qAccount1.id))
                .leftJoin(qCourseRequestComplete1).on(qCourseRequest.crsMstPid.eq(qCourseRequestComplete1.crsMstPid)
                .and(qCourseRequest.id.eq(qCourseRequestComplete1.atnlcReqPid).and(qCourseRequestComplete1.sn.eq(Constants.fieldCrsSn))))
                .innerJoin(qCourseMaster).on(qCourseRequest.crsMstPid.eq(qCourseMaster.id))
                .leftJoin(qAccount2).on(qAccount2.loginId.eq(qCourseRequestComplete1.cmplOpetrId))
                .leftJoin(qCourse).on(qCourseRequestComplete1.crsPid.eq(qCourse.id))
                .where(builder)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(orderSpecifier)
                .fetchResults();

        return new PageImpl<>(mngList.getResults(), pageable, mngList.getTotal());
    }

    public Page<CourseRequest> listForMasterStatus(Pageable pageable, SearchForm searchForm) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, Constants.DEFAULT_PAGESIZE); // <- Sort 추가

        QCourseRequest qCourseRequest = QCourseRequest.courseRequest;
        QCourseRequestComplete qCourseRequestComplete1 = new QCourseRequestComplete("courseRequestComplete1");
        QCourseRequestComplete qCourseRequestComplete2 = new QCourseRequestComplete("courseRequestComplete2");
        QCourse qCourse = QCourse.course;
        QAccount qAccount1 = new QAccount("account1");
        QAccount qAccount2 = new QAccount("account2");
        QCourseItem qCourseItem = QCourseItem.courseItem;
        QCourseHis qCourseHis = QCourseHis.courseHis;
        QCourseMaster qCourseMaster = QCourseMaster.courseMaster;

        OrderSpecifier<Long> orderSpecifier = qCourseRequest.id.desc();

        BooleanBuilder builder = new BooleanBuilder();
        if (searchForm.getMngPid() != null) {
            builder.and(qCourseRequest.crsMstPid.eq(searchForm.getMngPid()));
        }
        if (searchForm.getUserPid() != null) {
            builder.and(qCourseRequest.mberPid.eq(searchForm.getUserPid()));
        }
        if (searchForm.getCrsMstPid() != null) {
            builder.and(qCourseRequest.crsMstPid.eq(searchForm.getCrsMstPid()));
        }

        QueryResults<CourseRequest> mngList = queryFactory
                .select(Projections.fields(CourseRequest.class,
                        qCourseRequest.id,
                        qCourseRequest.regDtm,
                        qCourseRequest.confmAt,
                        qCourseRequest.areaNm,
                        qCourseRequest.schlNm,
                        qCourseRequest.grade,
                        qCourseRequest.ban,
                        qCourseRequest.no,
                        qCourseRequest.crsMstPid,
                        qCourseRequest.mberPid,
                        qAccount1.nm.as("mberNm"),
                        qCourseRequestComplete1.id.as("cmplHistPid"),
                        qCourseRequestComplete1.cmplSttTy,
                        qCourseRequestComplete1.cmplOpetrId,
                        qAccount2.nm.as("cmplOpetrNm"),
                        qCourseRequestComplete1.cmplPrsDtm,
                        qCourse.crsNm,
                        qCourseMaster.crsNm.as("masterCrsNm"),
                        ExpressionUtils.as(
                                JPAExpressions.select(qCourseRequestComplete2.sn.min())
                                        .from(qCourseRequestComplete2)
                                        .where(qCourseRequestComplete2.crsMstPid.eq(qCourseRequest.crsMstPid)
                                                .and(qCourseRequest.id.eq(qCourseRequestComplete2.atnlcReqPid)
                                                        .and(qCourseRequestComplete2.cmplSttTy.eq(CompleteStatusType.APPLY.name())
                                                        .and(qCourseRequestComplete2.sn.in(Constants.priorSvySn, Constants.priorCrsSn, Constants.fieldCrsSn, Constants.afterCrsSn, Constants.afterSvySn, Constants.satisfSvySn))))),
                                "progressSn"),
                        ExpressionUtils.as(
                                Expressions.numberTemplate(Integer.class, "fn_getMyAtnlcHour({0},{1})", qCourse.id, qAccount1.id)
                                ,
                                "myHour"),
                        ExpressionUtils.as(
                                JPAExpressions.select(qCourseItem.cntntsLen.sum())
                                        .from(qCourseItem)
                                        .where(qCourseItem.crsPid.eq(qCourse.id)),
                                "totalLen")

                ))
                .from(qCourseRequest)
                .leftJoin(qAccount1).on(qCourseRequest.mberPid.eq(qAccount1.id))
                .leftJoin(qCourseRequestComplete1).on(qCourseRequest.crsMstPid.eq(qCourseRequestComplete1.crsMstPid)
                        .and(qCourseRequest.id.eq(qCourseRequestComplete1.atnlcReqPid).and(qCourseRequestComplete1.sn.eq(JPAExpressions.select(qCourseRequestComplete2.sn.min())
                                .from(qCourseRequestComplete2)
                                .where(qCourseRequestComplete2.crsMstPid.eq(qCourseRequest.crsMstPid)
                                        .and(qCourseRequest.id.eq(qCourseRequestComplete2.atnlcReqPid)
                                                .and(qCourseRequestComplete2.cmplSttTy.eq(CompleteStatusType.APPLY.name())
                                                        .and(qCourseRequestComplete2.sn.in(Constants.priorSvySn, Constants.priorCrsSn, Constants.fieldCrsSn, Constants.afterCrsSn, Constants.afterSvySn, Constants.satisfSvySn)))))))))
                .innerJoin(qCourseMaster).on(qCourseRequest.crsMstPid.eq(qCourseMaster.id))
                .leftJoin(qAccount2).on(qAccount2.loginId.eq(qCourseRequestComplete1.cmplOpetrId))
                .leftJoin(qCourse).on(qCourseRequestComplete1.crsPid.eq(qCourse.id))
                .where(builder)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(orderSpecifier)
                .fetchResults();

        return new PageImpl<>(mngList.getResults(), pageable, mngList.getTotal());
    }

    public List<CourseRequest> listByform(CourseRequestForm form) {

        QCourseRequest qCourseRequest = QCourseRequest.courseRequest;
        QAccount qAccount = QAccount.account;
        QCommonCode qCommonCode = QCommonCode.commonCode;

        BooleanBuilder builder = new BooleanBuilder();

        if (form.getCrsMstPid() != null) {
            builder.and(qCourseRequest.crsMstPid.eq(form.getCrsMstPid()));
        }
        if (form.getMberPid() != null) {
            builder.and(qCourseRequest.mberPid.eq(form.getMberPid()));
        }
        if (form.getCrsMstPid() != null) {
            builder.and(qCourseRequest.crsMstPid.eq(form.getCrsMstPid()));
        }
        if (form.getAreaNm() != null) {
            builder.and(qCourseRequest.areaNm.eq(form.getAreaNm()));
        }
        if (form.getSchlNm() != null) {
            builder.and(qCourseRequest.schlNm.eq(form.getSchlNm()));
        }
        if (form.getGrade() != null) {
            builder.and(qCourseRequest.grade.eq(form.getGrade()));
        }

        List<CourseRequest> list = queryFactory
                .select(Projections.fields(CourseRequest.class,
                        qCourseRequest.id,
                        qCourseRequest.regDtm,
                        qCourseRequest.confmAt,
                        qCourseRequest.areaNm,
                        qCourseRequest.schlNm,
                        qCourseRequest.grade,
                        qCourseRequest.ban,
                        qCourseRequest.no,
                        qCourseRequest.crsMstPid,
                        qCourseRequest.mberPid
                ))
                .from(qCourseRequest)
                .where(builder)
                .fetch();

        return list;
    }

    public List<CourseRequestDto> listForParentOrTeacher(CourseRequestForm courseRequestForm) {

        List<Object[]> objects = courseRequestRepository.listForParentOrTeacher(courseRequestForm);

        List<CourseRequestDto> tempList = new ArrayList<>();

        if (objects != null) {
            for (Object[] vo : objects) {
                CourseRequestDto courseRequestDto = new CourseRequestDto();
                courseRequestDto.setCrsNm((String)vo[0]);
                courseRequestDto.setCrsMstPid(((BigInteger)vo[1]).longValue());
                courseRequestDto.setSn2SttTy((String)vo[2]);
                courseRequestDto.setSn2SttTyNm(CompleteStatusType.valueOf((String)vo[2]).getName());
                courseRequestDto.setSn2CrsPid(((BigInteger)vo[3]).longValue());
                courseRequestDto.setSn3SttTy((String)vo[4]);
                courseRequestDto.setSn3SttTyNm(CompleteStatusType.valueOf((String)vo[4]).getName());
                courseRequestDto.setSn3CrsPid(((BigInteger)vo[5]).longValue());
                courseRequestDto.setSn4SttTy((String)vo[6]);
                courseRequestDto.setSn4SttTyNm(CompleteStatusType.valueOf((String)vo[6]).getName());
                courseRequestDto.setSn4CrsPid(((BigInteger)vo[7]).longValue());
                courseRequestDto.setSn5SttTy((String)vo[8]);
                courseRequestDto.setSn5SttTyNm(CompleteStatusType.valueOf((String)vo[8]).getName());
                courseRequestDto.setSn5CrsPid(((BigInteger)vo[9]).longValue());
                courseRequestDto.setSn6SttTy((String)vo[10]);
                courseRequestDto.setSn6SttTyNm(CompleteStatusType.valueOf((String)vo[10]).getName());
                courseRequestDto.setSn6CrsPid(((BigInteger)vo[11]).longValue());

                tempList.add(courseRequestDto);

            }
        }

        return tempList;
    }

    public CourseRequest load(Long id) {
        CourseRequest request = courseRequestRepository.findById(id).orElseGet(CourseRequest::new);

        return request;
    }

    public CourseRequest loadByform(CourseRequestForm form) {

        QCourseRequest qCourseRequest = QCourseRequest.courseRequest;
        QAccount qAccount = QAccount.account;
        QCommonCode qCommonCode = QCommonCode.commonCode;

        BooleanBuilder builder = new BooleanBuilder();

        if (form.getCrsMstPid() != null) {
            builder.and(qCourseRequest.crsMstPid.eq(form.getCrsMstPid()));
        }
        if (form.getMberPid() != null) {
            builder.and(qCourseRequest.mberPid.eq(form.getMberPid()));
        }
        if (form.getCrsMstPid() != null) {
            builder.and(qCourseRequest.crsMstPid.eq(form.getCrsMstPid()));
        }
        if (form.getAreaNm() != null) {
            builder.and(qCourseRequest.areaNm.eq(form.getAreaNm()));
        }
        if (form.getSchlNm() != null) {
            builder.and(qCourseRequest.schlNm.eq(form.getSchlNm()));
        }
        if (form.getGrade() != null) {
            builder.and(qCourseRequest.grade.eq(form.getGrade()));
        }

        List<CourseRequest> list = queryFactory
                .select(Projections.fields(CourseRequest.class,
                        qCourseRequest.id,
                        qCourseRequest.regDtm,
                        qCourseRequest.confmAt,
                        qCourseRequest.areaNm,
                        qCourseRequest.schlNm,
                        qCourseRequest.grade,
                        qCourseRequest.ban,
                        qCourseRequest.no,
                        qCourseRequest.crsMstPid,
                        qCourseRequest.mberPid
                ))
                .from(qCourseRequest)
                .where(builder)
                .fetch();

        return (list != null && list.size() > 0 ? list.get(0) : new CourseRequest());
    }

    @Transactional
    public void delete(CourseRequestForm form) {

        courseRequestRepository.deleteById(form.getId());
    }


    /**
     *
     * @param form
     * @return
     * @throws Exception
     */
    @Transactional
    public boolean insert(CourseRequestForm form, List<CourseMasterRel> courseMasterRelList) {

        try {
            CourseRequest request = modelMapper.map(form, CourseRequest.class);
            courseRequestRepository.save(request);

            if (request != null) {
                for (CourseMasterRel courseMasterRel : courseMasterRelList) {
                    CourseRequestComplete complete = new CourseRequestComplete();
                    complete.setCmplSttTy(CompleteStatusType.APPLY.name());
                    complete.setAtnlcReqPid(request.getId());
                    complete.setCrsMstPid(courseMasterRel.getCrsMstPid());
                    complete.setCrsPid(courseMasterRel.getCrsPid());
                    complete.setSn(courseMasterRel.getSn());
                    courseRequestCompleteRepository.save(complete);
                }
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public boolean update(CourseRequestForm form) {

        try {

            CourseRequest request = courseRequestRepository.findById(form.getId()).orElseGet(CourseRequest::new);
            request.setRegDtm(request.getRegDtm());
            request.setConfmAt(request.getConfmAt());

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Long countByMberPid(Long mberPid) {
        return courseRequestRepository.countByMberPid(mberPid);
    }

    public List<String> groupByCourseRequest(CourseRequestForm form) {
        QCourseRequest qCourseRequest = QCourseRequest.courseRequest;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qCourseRequest.areaNm.isNotNull());
        builder.and(qCourseRequest.areaNm.ne(""));
        if (form.getAreaNm() != null && !"".equals(form.getAreaNm())) {
            builder.and(qCourseRequest.areaNm.eq(form.getAreaNm()));
            builder.and(qCourseRequest.schlNm.isNotNull());
            builder.and(qCourseRequest.schlNm.ne(""));
            if (form.getSchlNm() != null && !"".equals(form.getSchlNm())) {
                builder.and(qCourseRequest.schlNm.eq(form.getSchlNm()));
                builder.and(qCourseRequest.grade.isNotNull());
                if (form.getGrade() != null) {
                    builder.and(qCourseRequest.grade.eq(form.getGrade()));
                    if (form.getBan() != null && !"".equals(form.getBan())) {
                        builder.and(qCourseRequest.ban.eq(form.getBan()));
                    }
                }
            }
        }

        List<String> list = null;
        JPAQuery<String> temp = queryFactory
                .select(qCourseRequest.areaNm)
                .from(qCourseRequest)
                .where(builder)
                .groupBy(qCourseRequest.areaNm);
        if (form.getAreaNm() != null && !"".equals(form.getAreaNm())) {
            temp = queryFactory
                    .select(qCourseRequest.schlNm)
                    .from(qCourseRequest)
                    .where(builder)
                    .groupBy(qCourseRequest.areaNm, qCourseRequest.schlNm);

            if (form.getSchlNm() != null && !"".equals(form.getSchlNm())) {
                temp = queryFactory
                        .select(qCourseRequest.grade.stringValue().as("grade"))
                        .from(qCourseRequest)
                        .where(builder)
                        .groupBy(qCourseRequest.areaNm, qCourseRequest.schlNm, qCourseRequest.grade);

                if (form.getGrade() != null && !"".equals(form.getGrade())) {
                    temp = queryFactory
                            .select(qCourseRequest.ban)
                            .from(qCourseRequest)
                            .where(builder)
                            .groupBy(qCourseRequest.areaNm, qCourseRequest.schlNm, qCourseRequest.grade, qCourseRequest.ban);

                    if (form.getBan() != null && !"".equals(form.getBan())) {
                        temp = queryFactory
                                .select(qCourseRequest.no.stringValue().as("no"))
                                .from(qCourseRequest)
                                .where(builder)
                                .groupBy(qCourseRequest.areaNm, qCourseRequest.schlNm, qCourseRequest.grade, qCourseRequest.ban, qCourseRequest.no);

                    }
                }
            }
        }
        list = temp.fetch();

        return list;
    }

    public Long existByMberPid(Long MberPid){
        Long Result = courseRequestRepository.countByMberPid(MberPid);
        return Result;
    }
}
