package kr.or.btf.web.services.web;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.btf.web.common.Constants;
import kr.or.btf.web.domain.web.*;
import kr.or.btf.web.domain.web.enums.CompleteStatusType;
import kr.or.btf.web.repository.web.CertificationRepository;
import kr.or.btf.web.repository.web.CourseRequestCompleteRepository;
import kr.or.btf.web.repository.web.SurveyResponsePersonRepository;
import kr.or.btf.web.repository.web.SurveyResponseRepository;
import kr.or.btf.web.web.form.CourseRequestCompleteForm;
import kr.or.btf.web.web.form.SearchForm;
import kr.or.btf.web.web.form.SurveyResponsePersonForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SurveyResponsePersonService extends _BaseService {

    private final JPAQueryFactory queryFactory;
    private final SurveyResponsePersonRepository surveyResponsePersonRepository;
    private final ModelMapper modelMapper;
    private final SurveyResponseRepository surveyResponseRepository;
    private final CourseRequestCompleteRepository courseRequestCompleteRepository;
    private final CertificationRepository certificationRepository;

    public Page<SurveyResponsePerson> list(Pageable pageable, SearchForm searchForm) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, Constants.DEFAULT_PAGESIZE); // <- Sort 추가

        QSurveyResponsePerson qSurveyResponsePerson = QSurveyResponsePerson.surveyResponsePerson;
        QAccount qAccount = QAccount.account;

        OrderSpecifier<Long> orderSpecifier = qSurveyResponsePerson.id.desc();

        BooleanBuilder builder = new BooleanBuilder();

        QueryResults<SurveyResponsePerson> mngList = queryFactory
                .select(Projections.fields(SurveyResponsePerson.class,
                        qSurveyResponsePerson.id,
                        qSurveyResponsePerson.loginId,
                        qSurveyResponsePerson.nm,
                        qSurveyResponsePerson.mberDvty,
                        qSurveyResponsePerson.regPsId,
                        qSurveyResponsePerson.regDtm,
                        qSurveyResponsePerson.qustnrPid,
                        qSurveyResponsePerson.atnlcReqPid,
                        ExpressionUtils.as(
                                JPAExpressions.select(qAccount.nm)
                                        .from(qAccount)
                                        .where(qAccount.loginId.eq(qSurveyResponsePerson.regPsId)),
                                "regPsNm")
                ))
                .from(qSurveyResponsePerson)
                .where(builder)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(orderSpecifier)
                .fetchResults();

        return new PageImpl<>(mngList.getResults(), pageable, mngList.getTotal());
    }

    public SurveyResponsePerson load(Long id) {
        SurveyResponsePerson surveyResponsePerson = surveyResponsePersonRepository.findById(id).orElseGet(SurveyResponsePerson::new);

        return surveyResponsePerson;
    }

    public SurveyResponsePerson loadByform(SurveyResponsePersonForm form) {

        QSurveyResponsePerson qSurveyResponsePerson = QSurveyResponsePerson.surveyResponsePerson;
        QAccount qAccount = QAccount.account;
        QCommonCode qCommonCode = QCommonCode.commonCode;

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(qSurveyResponsePerson.delAt.eq("N"));
        if (form.getLoginId() != null) {
            builder.and(qSurveyResponsePerson.loginId.eq(form.getLoginId()));
        }
        if (form.getQustnrPid() != null) {
            builder.and(qSurveyResponsePerson.qustnrPid.eq(form.getQustnrPid()));
        }
        if (form.getAtnlcReqPid() != null) {
            builder.and(qSurveyResponsePerson.atnlcReqPid.eq(form.getAtnlcReqPid()));
        }

        List<SurveyResponsePerson> list = queryFactory
                .select(Projections.fields(SurveyResponsePerson.class,
                        qSurveyResponsePerson.id,
                        qSurveyResponsePerson.loginId,
                        qSurveyResponsePerson.nm,
                        qSurveyResponsePerson.mberDvty,
                        qSurveyResponsePerson.regPsId,
                        qSurveyResponsePerson.regDtm,
                        qSurveyResponsePerson.updPsId,
                        qSurveyResponsePerson.updDtm,
                        qSurveyResponsePerson.delAt,
                        qSurveyResponsePerson.qustnrPid,
                        qSurveyResponsePerson.atnlcReqPid
                ))
                .from(qSurveyResponsePerson)
                .where(builder)
                .fetch();

        return (list != null && list.size() > 0 ? list.get(0) : new SurveyResponsePerson());
    }

    @Transactional
    public void delete(SurveyResponsePersonForm surveyResponsePersonForm) {
        SurveyResponsePerson mng = this.load(surveyResponsePersonForm.getId());

        mng.setUpdDtm(LocalDateTime.now());
        mng.setUpdPsId(surveyResponsePersonForm.getUpdPsId());
        mng.setDelAt(surveyResponsePersonForm.getDelAt());
    }

    /**
     * @param surveyResponsePersonForm
     * @return
     */
    @Transactional
    public boolean proc(SurveyResponsePersonForm surveyResponsePersonForm, List<SurveyResponse> responsesList, CourseRequestCompleteForm completeForm, Account account) {

        try {
            if (completeForm.getAtnlcReqPid() != null) { //만족도 조사일경우
                surveyResponsePersonForm.setAtnlcReqPid(completeForm.getAtnlcReqPid());
                CourseRequestComplete complete = courseRequestCompleteRepository.findByAtnlcReqPidAndCrsMstPidAndCrsPidAndSn(completeForm.getAtnlcReqPid(), completeForm.getCrsMstPid(), completeForm.getCrsPid(), completeForm.getSn());
                complete.setCmplSttTy(CompleteStatusType.COMPLETE.name());
                complete.setCmplPrsDtm(LocalDateTime.now());

                //이수증 insert
                Certification certification = new Certification();
                //이수증에서 오늘날짜 기준으로 발급번호 조회해보고 +1 해서 발급번호 set
                String nowDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MMdd"));
                QCertification qCertification = QCertification.certification;
                int index = 0;
                int length = 1;
                String isuNo = nowDate + "-";

                BooleanBuilder builder = new BooleanBuilder();
                builder.and(qCertification.isuNo.like(nowDate+ "%"));
                OrderSpecifier<Long> orderSpecifier = qCertification.id.desc();
                Certification latestCertiication = queryFactory.select(Projections.fields(Certification.class, qCertification.isuNo)).from(qCertification).where(builder).orderBy(orderSpecifier).fetchFirst();
                if (latestCertiication != null) {
                    index = Integer.parseInt(latestCertiication.getIsuNo().substring(11,14));
                }
                if (index != 0) {
                    length = (int)Math.log10(index)+1;
                }
                if (length < 4) {
                    for (int i = 0; i < 4-length; i++) {
                        isuNo += "0";
                    }
                }

                certification.setIsuNo(isuNo + (index + 1));
                certification.setRegDtm(LocalDateTime.now());
                certification.setMberPid(account.getId());
                certification.setAtnlcReqPid(completeForm.getAtnlcReqPid());
                certificationRepository.save(certification);
            }
            SurveyResponsePerson surveyResponsePerson = surveyResponsePersonRepository.findByLoginIdAndQustnrPid(surveyResponsePersonForm.getLoginId(), surveyResponsePersonForm.getQustnrPid());
            if (surveyResponsePerson == null) {
                surveyResponsePerson = modelMapper.map(surveyResponsePersonForm, SurveyResponsePerson.class);
                SurveyResponsePerson save = surveyResponsePersonRepository.save(surveyResponsePerson);

                if (responsesList != null) {
                    for (SurveyResponse surveyResponse : responsesList) {
                        surveyResponse.setRspPsPid(save.getId());
                        surveyResponseRepository.save(surveyResponse);
                    }
                }
            } else {
                surveyResponsePerson.setUpdDtm(LocalDateTime.now());
                surveyResponsePerson.setUpdPsId(account.getLoginId());
                surveyResponseRepository.deleteByRspPsPid(surveyResponsePerson.getId());
                if (responsesList != null) {
                    for (SurveyResponse surveyResponse : responsesList) {
                        surveyResponse.setRspPsPid(surveyResponsePerson.getId());
                        surveyResponseRepository.save(surveyResponse);
                    }
                }
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public boolean update(SurveyResponsePersonForm surveyResponsePersonForm) {

        try {

            SurveyResponsePerson surveyResponsePerson = surveyResponsePersonRepository.findById(surveyResponsePersonForm.getId()).orElseGet(SurveyResponsePerson::new);
            surveyResponsePerson.setLoginId(surveyResponsePersonForm.getLoginId());
            surveyResponsePerson.setNm(surveyResponsePersonForm.getNm());
            surveyResponsePerson.setMberDvty(surveyResponsePersonForm.getMberDvty());
            surveyResponsePerson.setQustnrPid(surveyResponsePersonForm.getQustnrPid());
            surveyResponsePerson.setUpdPsId(surveyResponsePersonForm.getUpdPsId());
            surveyResponsePerson.setUpdDtm(LocalDateTime.now());

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Long count(SurveyResponsePersonForm surveyResponsePersonForm) {
        QSurvey qSurvey = QSurvey.survey;
        QSurveyResponsePerson qSurveyResponsePerson = QSurveyResponsePerson.surveyResponsePerson;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qSurveyResponsePerson.delAt.eq("N"));
        builder.and(qSurveyResponsePerson.loginId.eq(surveyResponsePersonForm.getLoginId()));

        Long count = queryFactory
                .select(Projections.fields(SurveyResponsePerson.class,
                        qSurveyResponsePerson.id
                ))
                .from(qSurveyResponsePerson)
                .innerJoin(qSurvey).on(qSurvey.id.eq(qSurveyResponsePerson.qustnrPid)
                                    .and(qSurvey.delAt.eq("N"))
                                    .and(qSurvey.dvTy.in(surveyResponsePersonForm.getSurveyDvTypes())))
                .where(builder)
                .fetchCount();

        return count;
    }
}
