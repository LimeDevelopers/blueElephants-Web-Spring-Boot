package kr.or.btf.web.services.web;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.btf.web.domain.web.QAccount;
import kr.or.btf.web.domain.web.QSurveyAnswerItem;
import kr.or.btf.web.domain.web.QSurveyResponse;
import kr.or.btf.web.domain.web.SurveyAnswerItem;
import kr.or.btf.web.repository.web.SurveyAnswerItemRepository;
import kr.or.btf.web.web.form.SurveyAnswerItemForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SurveyAnswerItemService extends _BaseService {

    private final JPAQueryFactory queryFactory;
    private final SurveyAnswerItemRepository surveyAnswerItemRepository;
    private final ModelMapper modelMapper;

    public List<SurveyAnswerItem> list(SurveyAnswerItemForm surveyAnswerItemForm, Long rspPsPid) {

        QSurveyAnswerItem qSurveyAnswerItem = QSurveyAnswerItem.surveyAnswerItem;
        QAccount qAccount = QAccount.account;
        QSurveyResponse qSurveyResponse = QSurveyResponse.surveyResponse;

        OrderSpecifier<Long> orderSpecifier = qSurveyAnswerItem.id.asc();

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qSurveyAnswerItem.delAt.eq("N"));
        builder.and(qSurveyAnswerItem.qesitmPid.eq(surveyAnswerItemForm.getQesitmPid()));

        JPAQuery<SurveyAnswerItem> tempList;
        if (rspPsPid == null) {
             tempList = queryFactory
                    .select(Projections.fields(SurveyAnswerItem.class,
                            qSurveyAnswerItem.id,
                            qSurveyAnswerItem.answerCnts,
                            qSurveyAnswerItem.qesitmPid,
                            qSurveyAnswerItem.regPsId,
                            qSurveyAnswerItem.regDtm,
                            ExpressionUtils.as(
                                    JPAExpressions.select(qAccount.nm)
                                            .from(qAccount)
                                            .where(qAccount.loginId.eq(qSurveyAnswerItem.regPsId)),
                                    "regPsNm")
                    ))
                    .from(qSurveyAnswerItem)
                    .where(builder)
                    .orderBy(orderSpecifier);
        } else {
            tempList = queryFactory
                    .select(Projections.fields(SurveyAnswerItem.class,
                            qSurveyAnswerItem.id,
                            qSurveyAnswerItem.answerCnts,
                            qSurveyAnswerItem.qesitmPid,
                            qSurveyAnswerItem.regPsId,
                            qSurveyAnswerItem.regDtm,
                            qSurveyResponse.answerCnts.as("responseAnswerCnts"),
                            qSurveyResponse.aswPid.as("responseAswPid"),
                            ExpressionUtils.as(
                                    JPAExpressions.select(qAccount.nm)
                                            .from(qAccount)
                                            .where(qAccount.loginId.eq(qSurveyAnswerItem.regPsId)),
                                    "regPsNm")
                    ))
                    .from(qSurveyAnswerItem)
                    .leftJoin(qSurveyResponse).on(qSurveyResponse.rspPsPid.eq(rspPsPid).and(qSurveyResponse.aswPid.eq(qSurveyAnswerItem.id).and(qSurveyResponse.qesitmPid.eq(qSurveyAnswerItem.qesitmPid))))
                    .where(builder)
                    .orderBy(orderSpecifier);
        }


        List<SurveyAnswerItem> answerItemList = tempList.fetch();

        return answerItemList;
    }

    public SurveyAnswerItem load(Long id) {
        SurveyAnswerItem survey = surveyAnswerItemRepository.findById(id).orElseGet(SurveyAnswerItem::new);

        return survey;
    }

    @Transactional
    public void delete(SurveyAnswerItemForm surveyAnswerItemForm) {
        surveyAnswerItemRepository.deleteById(surveyAnswerItemForm.getId());
    }

    /**
     * @param surveyAnswerItemForm
     * @return
     */
    @Transactional
    public SurveyAnswerItem insert(SurveyAnswerItemForm surveyAnswerItemForm) throws Exception {

        SurveyAnswerItem surveyAnswerItem = modelMapper.map(surveyAnswerItemForm, SurveyAnswerItem.class);
        surveyAnswerItem = surveyAnswerItemRepository.save(surveyAnswerItem);

        return surveyAnswerItem;
    }

    @Transactional
    public boolean update(SurveyAnswerItemForm surveyAnswerItemForm) {

        try {

            SurveyAnswerItem surveyAnswerItem = surveyAnswerItemRepository.findById(surveyAnswerItemForm.getId()).orElseGet(SurveyAnswerItem::new);
            surveyAnswerItem.setAnswerCnts(surveyAnswerItem.getAnswerCnts());
            surveyAnswerItem.setQesitmPid(surveyAnswerItem.getQesitmPid());
            surveyAnswerItem.setUpdPsId(surveyAnswerItem.getUpdPsId());
            surveyAnswerItem.setUpdDtm(LocalDateTime.now());

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
