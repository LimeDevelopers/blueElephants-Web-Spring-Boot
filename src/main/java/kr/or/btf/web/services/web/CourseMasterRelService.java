package kr.or.btf.web.services.web;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.btf.web.domain.web.*;
import kr.or.btf.web.repository.web.CourseMasterRelRepository;
import kr.or.btf.web.web.form.CourseMasterRelForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CourseMasterRelService extends _BaseService {

    private final JPAQueryFactory queryFactory;
    private final CourseMasterRelRepository courseMasterRelRepository;
    private final ModelMapper modelMapper;


    // 수정중 김재일
    public List<CourseMasterRel> eduList(CourseMasterRelForm form) {

        QCourseMasterRel qCourseMasterRel = QCourseMasterRel.courseMasterRel;
        QCourse qCourse = QCourse.course;
        QCourseTaste qCourseTaste = QCourseTaste.courseTaste;
        QSurvey qSurvey = QSurvey.survey;
        QInspection qInspection = QInspection.inspection;
        QCommonCode qCommonCode = QCommonCode.commonCode;
        QAccount qAccount = QAccount.account;

        OrderSpecifier<Integer> orderSpecifier = qCourseMasterRel.sn.asc();

        BooleanBuilder builder = new BooleanBuilder();
        if (form.getCrsMstPid() != null) {
            builder.and(qCourseMasterRel.crsMstPid.eq(form.getCrsMstPid()));
        }
        if (form.getCrsPid() != null) {
            builder.and(qCourseMasterRel.crsPid.eq(form.getCrsPid()));
        }
        if (form.getSnArr() != null) {
            builder.and(qCourseMasterRel.sn.in(form.getSnArr()));
        }
        // sn -> 1 : 맛보기
        // sn -> 2 : 사전검사
        // sn -> 3 : 사전강좌
        // sn -> 4 : 현장강좌
        // sn -> 5 : 사후강좌
        // sn -> 6 : 사후검사
        // sn -> 7 : 만족도조사
        List<CourseMasterRel> mngList = queryFactory
                .select(Projections.fields(CourseMasterRel.class,
                        qCourseMasterRel.crsMstPid,
                        qCourseMasterRel.crsPid,
                        qCourseMasterRel.sn,
                        // CaseBuilder -> CaseWhen 문법 시작
                        // JPAExpressions -> 서브쿼리
                        new CaseBuilder().when(qCourseMasterRel.sn.eq(1))
                                .then(JPAExpressions.select(qCourseTaste.ttl)
                                        .from(qCourseTaste)
                                        .where(qCourseTaste.id.eq(qCourseMasterRel.crsPid)))
                                .when(qCourseMasterRel.sn.eq(2))
                                .then(JPAExpressions.select(qInspection.ttl)
                                        .from(qInspection)
                                        .where(qInspection.id.eq(qCourseMasterRel.crsPid)))
                                .when(qCourseMasterRel.sn.eq(3))
                                .then(JPAExpressions.select(qCourse.crsNm)
                                        .from(qCourse)
                                        .where(qCourse.id.eq(qCourseMasterRel.crsPid)))
                                .when(qCourseMasterRel.sn.eq(4))
                                .then(JPAExpressions.select(qCourse.crsNm)
                                        .from(qCourse)
                                        .where(qCourse.id.eq(qCourseMasterRel.crsPid)))
                                .when(qCourseMasterRel.sn.eq(5))
                                .then(JPAExpressions.select(qCourse.crsNm)
                                        .from(qCourse)
                                        .where(qCourse.id.eq(qCourseMasterRel.crsPid)))
                                .when(qCourseMasterRel.sn.eq(6))
                                .then(JPAExpressions.select(qInspection.ttl)
                                        .from(qInspection)
                                        .where(qInspection.id.eq(qCourseMasterRel.crsPid)))
                                .when(qCourseMasterRel.sn.eq(7))
                                .then(JPAExpressions.select(qSurvey.ttl)
                                        .from(qSurvey)
                                        .where(qSurvey.id.eq(qCourseMasterRel.crsPid)))
                                .otherwise("").as("pidNm"),
                        new CaseBuilder().when(qCourseMasterRel.sn.eq(1))
                                .then(JPAExpressions.select(qCourseTaste.imgFl)
                                        .from(qCourseTaste)
                                        .where(qCourseTaste.id.eq(qCourseMasterRel.crsPid)))
                                .when(qCourseMasterRel.sn.eq(3))
                                .then(JPAExpressions.select(qCourse.imgFl)
                                        .from(qCourse)
                                        .where(qCourse.id.eq(qCourseMasterRel.crsPid)))
                                .when(qCourseMasterRel.sn.eq(4))
                                .then(JPAExpressions.select(qCourse.imgFl)
                                        .from(qCourse)
                                        .where(qCourse.id.eq(qCourseMasterRel.crsPid)))
                                .when(qCourseMasterRel.sn.eq(5))
                                .then(JPAExpressions.select(qCourse.imgFl)
                                        .from(qCourse)
                                        .where(qCourse.id.eq(qCourseMasterRel.crsPid)))
                                .otherwise("").as("imgFl"),
                        new CaseBuilder().when(qCourseMasterRel.sn.eq(3))
                                .then(JPAExpressions.select(qCourse.stepTy)
                                        .from(qCourse)
                                        .where(qCourse.id.eq(qCourseMasterRel.crsPid)))
                                .when(qCourseMasterRel.sn.eq(4))
                                .then(JPAExpressions.select(qCourse.stepTy)
                                        .from(qCourse)
                                        .where(qCourse.id.eq(qCourseMasterRel.crsPid)))
                                .when(qCourseMasterRel.sn.eq(5))
                                .then(JPAExpressions.select(qCourse.stepTy)
                                        .from(qCourse)
                                        .where(qCourse.id.eq(qCourseMasterRel.crsPid)))
                                .otherwise("").as("stepTy")
                ))
                .from(qCourseMasterRel)
                .where(builder)
                .orderBy(orderSpecifier)
                .fetch();

        return mngList;
    }


    // 수정중 김재일
    public List<CourseMasterRel> list(CourseMasterRelForm form) {

        QCourseMasterRel qCourseMasterRel = QCourseMasterRel.courseMasterRel;
        QCourse qCourse = QCourse.course;
        QCourseTaste qCourseTaste = QCourseTaste.courseTaste;
        QSurvey qSurvey = QSurvey.survey;
        QInspection qInspection = QInspection.inspection;
        QCommonCode qCommonCode = QCommonCode.commonCode;
        QAccount qAccount = QAccount.account;

        OrderSpecifier<Integer> orderSpecifier = qCourseMasterRel.sn.asc();

        BooleanBuilder builder = new BooleanBuilder();
        if (form.getCrsMstPid() != null) {
            builder.and(qCourseMasterRel.crsMstPid.eq(form.getCrsMstPid()));
        }
        if (form.getCrsPid() != null) {
            builder.and(qCourseMasterRel.crsPid.eq(form.getCrsPid()));
        }
        if (form.getSnArr() != null) {
            builder.and(qCourseMasterRel.sn.in(form.getSnArr()));
        }

        List<CourseMasterRel> mngList = queryFactory
                .select(Projections.fields(CourseMasterRel.class,
                        qCourseMasterRel.crsMstPid,
                        qCourseMasterRel.crsPid,
                        qCourseMasterRel.sn,
                        new CaseBuilder().when(qCourseMasterRel.sn.eq(1))
                                .then(JPAExpressions.select(qCourseTaste.ttl)
                                        .from(qCourseTaste)
                                        .where(qCourseTaste.id.eq(qCourseMasterRel.crsPid)))
                                .when(qCourseMasterRel.sn.eq(2))
                                .then(JPAExpressions.select(qInspection.ttl)
                                        .from(qInspection)
                                        .where(qInspection.id.eq(qCourseMasterRel.crsPid)))
                                .when(qCourseMasterRel.sn.eq(3))
                                .then(JPAExpressions.select(qCourse.crsNm)
                                        .from(qCourse)
                                        .where(qCourse.id.eq(qCourseMasterRel.crsPid)))
                                .when(qCourseMasterRel.sn.eq(4))
                                .then(JPAExpressions.select(qCourse.crsNm)
                                        .from(qCourse)
                                        .where(qCourse.id.eq(qCourseMasterRel.crsPid)))
                                .when(qCourseMasterRel.sn.eq(5))
                                .then(JPAExpressions.select(qCourse.crsNm)
                                        .from(qCourse)
                                        .where(qCourse.id.eq(qCourseMasterRel.crsPid)))
                                .when(qCourseMasterRel.sn.eq(6))
                                .then(JPAExpressions.select(qInspection.ttl)
                                        .from(qInspection)
                                        .where(qInspection.id.eq(qCourseMasterRel.crsPid)))
                                .when(qCourseMasterRel.sn.eq(7))
                                .then(JPAExpressions.select(qSurvey.ttl)
                                        .from(qSurvey)
                                        .where(qSurvey.id.eq(qCourseMasterRel.crsPid)))
                                .otherwise("").as("pidNm"),
                        new CaseBuilder().when(qCourseMasterRel.sn.eq(1))
                                .then(JPAExpressions.select(qCourseTaste.imgFl)
                                        .from(qCourseTaste)
                                        .where(qCourseTaste.id.eq(qCourseMasterRel.crsPid)))
                                .when(qCourseMasterRel.sn.eq(3))
                                .then(JPAExpressions.select(qCourse.imgFl)
                                        .from(qCourse)
                                        .where(qCourse.id.eq(qCourseMasterRel.crsPid)))
                                .when(qCourseMasterRel.sn.eq(4))
                                .then(JPAExpressions.select(qCourse.imgFl)
                                        .from(qCourse)
                                        .where(qCourse.id.eq(qCourseMasterRel.crsPid)))
                                .when(qCourseMasterRel.sn.eq(5))
                                .then(JPAExpressions.select(qCourse.imgFl)
                                        .from(qCourse)
                                        .where(qCourse.id.eq(qCourseMasterRel.crsPid)))
                                .otherwise("").as("imgFl"),
                        new CaseBuilder().when(qCourseMasterRel.sn.eq(3))
                                .then(JPAExpressions.select(qCourse.stepTy)
                                        .from(qCourse)
                                        .where(qCourse.id.eq(qCourseMasterRel.crsPid)))
                                .when(qCourseMasterRel.sn.eq(4))
                                .then(JPAExpressions.select(qCourse.stepTy)
                                        .from(qCourse)
                                        .where(qCourse.id.eq(qCourseMasterRel.crsPid)))
                                .when(qCourseMasterRel.sn.eq(5))
                                .then(JPAExpressions.select(qCourse.stepTy)
                                        .from(qCourse)
                                        .where(qCourse.id.eq(qCourseMasterRel.crsPid)))
                                .otherwise("").as("stepTy")
                ))
                .from(qCourseMasterRel)
                .where(builder)
                .orderBy(orderSpecifier)
                .fetch();

        return mngList;
    }

    public CourseMasterRel loadByform(CourseMasterRelForm form) {

        QCourseMasterRel qCourseMasterRel = QCourseMasterRel.courseMasterRel;
        QAccount qAccount = QAccount.account;
        QCommonCode qCommonCode = QCommonCode.commonCode;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qCourseMasterRel.crsMstPid.eq(form.getCrsMstPid()));
        if (form.getSn() != null) {
            builder.and(qCourseMasterRel.sn.eq(form.getSn()));
        }
        if (form.getCrsPid() != null) {
            builder.and(qCourseMasterRel.crsPid.eq(form.getCrsPid()));
        }

        List<CourseMasterRel> list = queryFactory
                .select(Projections.fields(CourseMasterRel.class,
                        qCourseMasterRel.crsMstPid,
                        qCourseMasterRel.crsPid,
                        qCourseMasterRel.sn
                ))
                .from(qCourseMasterRel)
                .where(builder)
                .fetch();

        return (list != null && list.size() > 0 ? list.get(0) : new CourseMasterRel());
    }

    /**
     * @param form
     * @return
     */
    @Transactional
    public boolean crsInsert(CourseMasterRelForm form) throws Exception {

        courseMasterRelRepository.saveByCrsPid(form);

        return true;
    }

    @Transactional
    public boolean inspctInsert(CourseMasterRelForm form) throws Exception {

        for(int i=0; i<2; i++) {
            if (i == 0) {
                form.setSn(2);
            }
            if (i == 1) {
                form.setSn(6);
            }
            courseMasterRelRepository.saveByCrsPid(form);
        }

        return true;
    }

    @Transactional
    public boolean inspctDelete(CourseMasterRelForm form) {
        try {

            for(int i=0; i<2; i++) {
                if (i == 0) {
                    form.setSn(2);
                }
                if (i == 1) {
                    form.setSn(6);
                }
                courseMasterRelRepository.deleteByCrsMstPidAndSn(form);
            }

            return true;
        } catch (Exception e) {
            return false;
        }

    }

    @Transactional
    public boolean delete(CourseMasterRelForm form) {
        try {
            courseMasterRelRepository.deleteByCrsMstPidAndSn(form);
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    public Long countByCrsMstPid(Long crsMstPid) {
        return courseMasterRelRepository.countByCrsMstPid(crsMstPid);
    }

    /*@Transactional
    public boolean update(CourseMasterRelForm form) {
        try {
            courseMasterRelRepository.updatByCmpl(form);
            return true;
        } catch (Exception e) {
            return false;
        }
    }*/
}
