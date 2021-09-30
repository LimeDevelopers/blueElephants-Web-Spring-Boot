package kr.or.btf.web.services.web;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.btf.web.common.Constants;
import kr.or.btf.web.domain.web.QAccount;
import kr.or.btf.web.domain.web.QCommonCode;
import kr.or.btf.web.domain.web.QSurvey;
import kr.or.btf.web.domain.web.Survey;
import kr.or.btf.web.domain.web.enums.SurveyDvType;
import kr.or.btf.web.repository.web.SurveyRepository;
import kr.or.btf.web.web.form.SearchForm;
import kr.or.btf.web.web.form.SurveyForm;
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
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SurveyService extends _BaseService {

    private final JPAQueryFactory queryFactory;
    private final SurveyRepository surveyRepository;
    private final ModelMapper modelMapper;

    public Page<Survey> list(Pageable pageable, SearchForm searchForm) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, Constants.DEFAULT_PAGESIZE); // <- Sort 추가

        QSurvey qSurvey = QSurvey.survey;
        QAccount qAccount = QAccount.account;

        OrderSpecifier<Long> orderSpecifier = qSurvey.id.desc();

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qSurvey.delAt.eq("N"));
        if (searchForm.getSrchStDt() != null && searchForm.getSrchEdDt() != null) {
            if (!searchForm.getSrchStDt().isEmpty() && !searchForm.getSrchEdDt().isEmpty()) {
                builder
                        .and(qSurvey.stYmd.goe(searchForm.getSrchStDt())
                                .and(qSurvey.stYmd.loe(searchForm.getSrchEdDt())
                                )
                        );
            }
        }

        if (searchForm.getSrchGbn() != null) {
            builder.and(qSurvey.mberDvTy.eq(searchForm.getSrchGbn()));
        }

        if (searchForm.getSrchWord() != null && !searchForm.getSrchWord().isEmpty()) {
            builder.and(qSurvey.ttl.like("%" + searchForm.getSrchWord() + "%"));
        }

        if (searchForm.getSurveyDvType() != null) {
            builder.and(qSurvey.dvTy.eq(searchForm.getSurveyDvType().name()));
        } else {
            builder.and(qSurvey.dvTy.ne(SurveyDvType.SELF.name()));
        }

        QueryResults<Survey> mngList = queryFactory
                .select(Projections.fields(Survey.class,
                        qSurvey.id,
                        qSurvey.dvTy,
                        qSurvey.ttl,
                        qSurvey.stYmd,
                        qSurvey.endYmd,
                        qSurvey.qustnrCn,
                        qSurvey.opnAt,
                        qSurvey.regPsId,
                        qSurvey.regDtm,
                        qSurvey.mberDvTy,
                        ExpressionUtils.as(
                                JPAExpressions.select(qAccount.nm)
                                        .from(qAccount)
                                        .where(qAccount.loginId.eq(qSurvey.regPsId)),
                                "regPsNm")
                ))
                .from(qSurvey)
                .where(builder)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(orderSpecifier)
                .fetchResults();

        return new PageImpl<>(mngList.getResults(), pageable, mngList.getTotal());
    }

    public Survey load(Long id) {
        Survey survey = surveyRepository.findById(id).orElseGet(Survey::new);

        return survey;
    }


    public Survey loadByform(SurveyForm form) {

        QSurvey qSurvey = QSurvey.survey;
        QAccount qAccount = QAccount.account;
        QCommonCode qCommonCode = QCommonCode.commonCode;

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(qSurvey.delAt.eq("N"));
        if (form.getId() != null) {
            builder.and(qSurvey.id.eq(form.getId()));
        }
        if (form.getDvTy() != null) {
            builder.and(qSurvey.dvTy.eq(form.getDvTy()));
        }

        List<Survey> list = queryFactory
                .select(Projections.fields(Survey.class,
                        qSurvey.id,
                        qSurvey.dvTy,
                        qSurvey.ttl,
                        qSurvey.stYmd,
                        qSurvey.endYmd,
                        qSurvey.qustnrCn,
                        qSurvey.opnAt,
                        qSurvey.regPsId,
                        qSurvey.regDtm,
                        qSurvey.mberDvTy
                ))
                .from(qSurvey)
                .where(builder)
                .fetch();

        return (list != null && list.size() > 0 ? list.get(0) : new Survey());
    }

    @Transactional
    public void delete(SurveyForm surveyForm) {
        Survey mng = this.load(surveyForm.getId());

        mng.setUpdDtm(LocalDateTime.now());
        mng.setUpdPsId(surveyForm.getUpdPsId());
        mng.setDelAt(surveyForm.getDelAt());
    }

    /**
     * @param surveyForm
     * @return
     */
    @Transactional
    public Survey insert(SurveyForm surveyForm) throws Exception {

        Survey survey = modelMapper.map(surveyForm, Survey.class);
        Survey save = surveyRepository.save(survey);

        return save;
    }

    @Transactional
    public boolean update(SurveyForm surveyForm) {

        try {

            Survey survey = surveyRepository.findById(surveyForm.getId()).orElseGet(Survey::new);
            survey.setDvTy(surveyForm.getDvTy());
            survey.setTtl(surveyForm.getTtl());
            survey.setStYmd(surveyForm.getStYmd());
            survey.setEndYmd(surveyForm.getEndYmd());
            survey.setQustnrCn(surveyForm.getQustnrCn());
            survey.setMberDvTy(surveyForm.getMberDvTy());
            survey.setOpnAt(surveyForm.getOpnAt());
            survey.setUpdPsId(surveyForm.getUpdPsId());
            survey.setUpdDtm(LocalDateTime.now());

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
