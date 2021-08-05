package kr.or.btf.web.services.web;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.btf.web.common.Constants;
import kr.or.btf.web.domain.web.QAccount;
import kr.or.btf.web.domain.web.QSurveyResponse;
import kr.or.btf.web.domain.web.QSurveyResponsePerson;
import kr.or.btf.web.domain.web.SurveyResponse;
import kr.or.btf.web.repository.web.SurveyResponseRepository;
import kr.or.btf.web.web.form.SearchForm;
import kr.or.btf.web.web.form.SurveyQuestionItemForm;
import kr.or.btf.web.web.form.SurveyResponseForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SurveyResponseService extends _BaseService {

    private final JPAQueryFactory queryFactory;
    private final SurveyResponseRepository surveyResponseRepository;
    private final ModelMapper modelMapper;

    public Page<SurveyResponse> list(Pageable pageable, SearchForm searchForm) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, Constants.DEFAULT_PAGESIZE); // <- Sort 추가

        QSurveyResponse qSurveyResponse = QSurveyResponse.surveyResponse;
        QAccount qAccount = QAccount.account;

        OrderSpecifier<Long> orderSpecifier = qSurveyResponse.id.desc();

        BooleanBuilder builder = new BooleanBuilder();

        QueryResults<SurveyResponse> mngList = queryFactory
                .select(Projections.fields(SurveyResponse.class,
                        qSurveyResponse.id,
                        qSurveyResponse.answerCnts,
                        qSurveyResponse.rspPsPid,
                        qSurveyResponse.aswPid,
                        qSurveyResponse.qesitmPid
                ))
                .from(qSurveyResponse)
                .where(builder)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(orderSpecifier)
                .fetchResults();

        return new PageImpl<>(mngList.getResults(), pageable, mngList.getTotal());
    }

    public SurveyResponse load(Long id) {
        SurveyResponse surveyResponse = surveyResponseRepository.findById(id).orElseGet(SurveyResponse::new);

        return surveyResponse;
    }

    public SurveyResponse loadByForm(SurveyResponseForm form) {
        QSurveyResponse qSurveyResponse = QSurveyResponse.surveyResponse;
        QSurveyResponsePerson qSurveyResponsePerson = QSurveyResponsePerson.surveyResponsePerson;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qSurveyResponse.qesitmPid.eq(form.getQesitmPid()));

        SurveyResponse surveyResponse = queryFactory
                .select(Projections.fields(SurveyResponse.class,
                        qSurveyResponse.id,
                        qSurveyResponse.answerCnts,
                        qSurveyResponse.rspPsPid,
                        qSurveyResponse.aswPid,
                        qSurveyResponse.qesitmPid))
                .from(qSurveyResponse)
                .innerJoin(qSurveyResponsePerson).on(qSurveyResponse.rspPsPid.eq(qSurveyResponsePerson.id)
                                                    .and(qSurveyResponsePerson.loginId.eq(form.getLoginId())
                                                    .and(qSurveyResponsePerson.qustnrPid.eq(form.getQustnrPid()))))
                .where(builder)
                .fetchOne();

        return surveyResponse;
    }

    @Transactional
    public void delete(SurveyResponseForm surveyResponseForm) {
        surveyResponseRepository.deleteById(surveyResponseForm.getId());
    }

    /**
     * @param surveyResponseForm
     * @return
     */
    @Transactional
    public SurveyResponse insert(SurveyResponseForm surveyResponseForm) throws Exception {

        SurveyResponse surveyResponse = modelMapper.map(surveyResponseForm, SurveyResponse.class);
        surveyResponse = surveyResponseRepository.save(surveyResponse);

        return surveyResponse;
    }

    @Transactional
    public boolean update(SurveyResponseForm surveyResponseForm) {

        try {

            SurveyResponse surveyResponse = surveyResponseRepository.findById(surveyResponseForm.getId()).orElseGet(SurveyResponse::new);
            surveyResponse.setAnswerCnts(surveyResponseForm.getAnswerCnts());
            surveyResponse.setAswPid(surveyResponseForm.getAswPid());
            surveyResponse.setQesitmPid(surveyResponseForm.getQesitmPid());
            surveyResponse.setRspPsPid(surveyResponseForm.getRspPsPid());

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public boolean answerPsersonCtnCheck(SurveyQuestionItemForm surveyQuestionItemForm) {

        QSurveyResponse qSurveyResponse = QSurveyResponse.surveyResponse;

        OrderSpecifier<Long> orderSpecifier = qSurveyResponse.id.desc();

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qSurveyResponse.qesitmPid.eq(surveyQuestionItemForm.getId()));

        List<SurveyResponse> mngList = queryFactory
                .select(Projections.fields(SurveyResponse.class,
                        qSurveyResponse.id,
                        qSurveyResponse.answerCnts,
                        qSurveyResponse.rspPsPid,
                        qSurveyResponse.aswPid,
                        qSurveyResponse.qesitmPid
                ))
                .from(qSurveyResponse)
                .where(builder)
                .orderBy(orderSpecifier)
                .fetch();

        if (mngList.size() == 0) {
            return true;
        } else {
            return false;
        }
    }
}
