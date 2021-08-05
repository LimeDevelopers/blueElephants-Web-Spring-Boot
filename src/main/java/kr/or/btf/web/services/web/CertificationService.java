package kr.or.btf.web.services.web;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.btf.web.common.Constants;
import kr.or.btf.web.domain.web.*;
import kr.or.btf.web.repository.web.CertificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CertificationService extends _BaseService {

    private final JPAQueryFactory queryFactory;
    private final CertificationRepository certificationRepository;
    private final ModelMapper modelMapper;

    public Certification loadCertification(Certification certification) {

        QCertification qCertification = QCertification.certification;
        QAccount qAccount = QAccount.account;
        QCourseRequest qCourseRequest = QCourseRequest.courseRequest;
        QCourseMaster qCourseMaster = QCourseMaster.courseMaster;
        QCourseRequestComplete qCourseRequestComplete = QCourseRequestComplete.courseRequestComplete;
        QCourse qCourse = QCourse.course;
        QCourseItem qCourseItem = QCourseItem.courseItem;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qCertification.mberPid.eq(certification.getMberPid()));
        builder.and(qCertification.atnlcReqPid.eq(certification.getAtnlcReqPid()));
        builder.and(qCourseRequestComplete.sn.in(Constants.priorCrsSn, Constants.fieldCrsSn, Constants.afterCrsSn));

        OrderSpecifier<Long> orderSpecifier = qCertification.id.desc();


        Certification rtn = queryFactory
                .select(Projections.fields(Certification.class,
                        qCertification.id,
                        qCertification.isuNo,
                        qCertification.regDtm,
                        qCertification.mberPid,
                        qCertification.atnlcReqPid,
                        qAccount.nm,
                        qAccount.brthdy,
                        qCourseMaster.crsNm,
                        qCourseItem.cntntsLen.sum().as("eduTm")
                ))
                .from(qCertification)
                .innerJoin(qAccount).on(qCertification.mberPid.eq(qAccount.id))
                .innerJoin(qCourseRequest).on(qCertification.atnlcReqPid.eq(qCourseRequest.id))
                .innerJoin(qCourseMaster).on(qCourseRequest.crsMstPid.eq(qCourseMaster.id))
                .innerJoin(qCourseRequestComplete).on(qCourseRequestComplete.atnlcReqPid.eq(qCourseRequest.id))
                .innerJoin(qCourse).on(qCourse.id.eq(qCourseRequestComplete.crsPid))
                .innerJoin(qCourseItem).on(qCourse.id.eq(qCourseItem.crsPid))
                .where(builder)
                .orderBy(orderSpecifier)
                .fetchFirst();

        return rtn;
    }
}
