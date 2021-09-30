package kr.or.btf.web.services.web;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.btf.web.domain.web.QAccount;
import kr.or.btf.web.domain.web.QSurveyQuestionItem;
import kr.or.btf.web.domain.web.SurveyAnswerItem;
import kr.or.btf.web.domain.web.SurveyQuestionItem;
import kr.or.btf.web.repository.web.SurveyAnswerItemRepository;
import kr.or.btf.web.repository.web.SurveyQuestionItemRepository;
import kr.or.btf.web.web.form.SurveyQuestionItemForm;
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
public class SurveyQuestionItemService extends _BaseService {

    private final JPAQueryFactory queryFactory;
    private final SurveyQuestionItemRepository surveyQuestionItemRepository;
    private final SurveyAnswerItemRepository surveyAnswerItemRepository;
    private final ModelMapper modelMapper;

    public List<SurveyQuestionItem> selfFactionList(SurveyQuestionItemForm surveyQuestionItemForm) {

        QSurveyQuestionItem qSurveyQuestionItem = QSurveyQuestionItem.surveyQuestionItem;
        QAccount qAccount = QAccount.account;

        OrderSpecifier<Long> orderSpecifier = qSurveyQuestionItem.id.asc();

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qSurveyQuestionItem.delAt.eq("N"));
        builder.and(qSurveyQuestionItem.qustnrPid.eq(surveyQuestionItemForm.getQustnrPid()));

        List<SurveyQuestionItem> questionItemList = queryFactory
                .select(Projections.fields(SurveyQuestionItem.class,
                        qSurveyQuestionItem.id,
                        qSurveyQuestionItem.qestnQesitm ,
                        qSurveyQuestionItem.aswDvTy,
                        qSurveyQuestionItem.answerCnt,
                        qSurveyQuestionItem.rspnsCnt,
                        qSurveyQuestionItem.qustnrPid,
                        qSurveyQuestionItem.regPsId,
                        qSurveyQuestionItem.regDtm,
                        ExpressionUtils.as(
                                JPAExpressions.select(qAccount.nm)
                                        .from(qAccount)
                                        .where(qAccount.loginId.eq(qSurveyQuestionItem.regPsId)),
                                "regPsNm")
                ))
                .from(qSurveyQuestionItem)
                .where(builder)
                .orderBy(orderSpecifier)
                .fetch();

        return questionItemList;
    }

    public List<SurveyQuestionItem> list(SurveyQuestionItemForm surveyQuestionItemForm) {

        QSurveyQuestionItem qSurveyQuestionItem = QSurveyQuestionItem.surveyQuestionItem;
        QAccount qAccount = QAccount.account;

        OrderSpecifier<Long> orderSpecifier = qSurveyQuestionItem.id.asc();

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qSurveyQuestionItem.delAt.eq("N"));
        builder.and(qSurveyQuestionItem.qustnrPid.eq(surveyQuestionItemForm.getQustnrPid()));

        List<SurveyQuestionItem> questionItemList = queryFactory
                .select(Projections.fields(SurveyQuestionItem.class,
                        qSurveyQuestionItem.id,
                        qSurveyQuestionItem.qestnQesitm ,
                        qSurveyQuestionItem.aswDvTy,
                        qSurveyQuestionItem.answerCnt,
                        qSurveyQuestionItem.rspnsCnt,
                        qSurveyQuestionItem.qustnrPid,
                        qSurveyQuestionItem.regPsId,
                        qSurveyQuestionItem.regDtm,
                        ExpressionUtils.as(
                                JPAExpressions.select(qAccount.nm)
                                        .from(qAccount)
                                        .where(qAccount.loginId.eq(qSurveyQuestionItem.regPsId)),
                                "regPsNm")
                ))
                .from(qSurveyQuestionItem)
                .where(builder)
                .orderBy(orderSpecifier)
                .fetch();

        return questionItemList;
    }

    public SurveyQuestionItem load(Long id) {
        SurveyQuestionItem surveyQuestionItem = surveyQuestionItemRepository.findById(id).orElseGet(SurveyQuestionItem::new);

        return surveyQuestionItem;
    }

    @Transactional
    public void delete(List<SurveyQuestionItem> list) {
        for (SurveyQuestionItem surveyQuestionItem : list) {
            SurveyQuestionItemForm surveyQuestionItemForm = new SurveyQuestionItemForm();
            surveyQuestionItemForm.setId(surveyQuestionItem.getId());

            SurveyQuestionItem mng = this.load(surveyQuestionItemForm.getId());

            mng.setUpdDtm(LocalDateTime.now());
            mng.setUpdPsId(surveyQuestionItem.getUpdPsId());
            mng.setDelAt(surveyQuestionItem.getDelAt());
        }
    }

    /**
     * @param surveyQuestionItemForm
     * @return
     */ 
    @Transactional
    public boolean insert(SurveyQuestionItemForm surveyQuestionItemForm, List<SurveyAnswerItem> surveyAnswerItemList) throws Exception {

        try {
            SurveyQuestionItem surveyQuestionItem = modelMapper.map(surveyQuestionItemForm, SurveyQuestionItem.class);
            surveyQuestionItem = surveyQuestionItemRepository.save(surveyQuestionItem);

            for (SurveyAnswerItem surveyAnswerItem : surveyAnswerItemList) {
                surveyAnswerItem.setQesitmPid(surveyQuestionItem.getId());

                surveyAnswerItemRepository.save(surveyAnswerItem);
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public boolean update(SurveyQuestionItemForm surveyQuestionItemForm, List<SurveyAnswerItem> surveyAnswerItemList) {

        try {

            SurveyQuestionItem surveyQuestionItem = surveyQuestionItemRepository.findById(surveyQuestionItemForm.getId()).orElseGet(SurveyQuestionItem::new);
            surveyQuestionItem.setAswDvTy(surveyQuestionItemForm.getAswDvTy());
            surveyQuestionItem.setQestnQesitm(surveyQuestionItemForm.getQestnQesitm());
            surveyQuestionItem.setAnswerCnt(surveyQuestionItemForm.getAnswerCnt());
            surveyQuestionItem.setRspnsCnt(surveyQuestionItemForm.getRspnsCnt());
            surveyQuestionItem.setQustnrPid(surveyQuestionItemForm.getQustnrPid());
            surveyQuestionItem.setUpdPsId(surveyQuestionItemForm.getUpdPsId());
            surveyQuestionItem.setUpdDtm(LocalDateTime.now());

            surveyAnswerItemRepository.deleteAllByQesitmPid(surveyQuestionItemForm.getId());

            for (SurveyAnswerItem surveyAnswerItem : surveyAnswerItemList) {
                surveyAnswerItem.setQesitmPid(surveyQuestionItem.getId());

                surveyAnswerItemRepository.save(surveyAnswerItem);
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
