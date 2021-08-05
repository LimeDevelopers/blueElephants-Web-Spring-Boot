package kr.or.btf.web.services.web;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.btf.web.domain.web.*;
import kr.or.btf.web.repository.web.InspectionAnswerItemRepository;
import kr.or.btf.web.repository.web.InspectionQuestionItemRepository;
import kr.or.btf.web.web.form.InspectionQuestionItemForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InspectionQuestionItemService extends _BaseService {

    private final JPAQueryFactory queryFactory;
    private final InspectionQuestionItemRepository inspectionQuestionItemRepository;
    private final InspectionAnswerItemRepository inspectionAnswerItemRepository;
    private final ModelMapper modelMapper;

    @Value("${common.code.inspectDvCodePid}") Long inspectDvCodePid;

    public List<InspectionQuestionItem> list(InspectionQuestionItemForm inspectionQuestionItemForm) {

        QInspectionQuestionItem qInspectionQuestionItem = QInspectionQuestionItem.inspectionQuestionItem;
        QAccount qAccount = QAccount.account;
        QCommonCode qCommonCode = QCommonCode.commonCode;

        OrderSpecifier<Long> orderSpecifier = qInspectionQuestionItem.id.asc();

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qInspectionQuestionItem.delAt.eq("N"));
        if (inspectionQuestionItemForm.getInspctPid() != null) {
            builder.and(qInspectionQuestionItem.inspctPid.eq(inspectionQuestionItemForm.getInspctPid()));
        }
        if (inspectionQuestionItemForm.getDvCodePid() != null) {
            builder.and(qInspectionQuestionItem.dvCodePid.in(JPAExpressions.select(qCommonCode.id)
                    .from(qCommonCode)
                    .where(qCommonCode.prntCodePid.eq(inspectionQuestionItemForm.getDvCodePid()))));
        } else {
            if (inspectionQuestionItemForm.getUpperQesitmPid() != null) {
                builder.and(qInspectionQuestionItem.upperQesitmPid.eq(inspectionQuestionItemForm.getUpperQesitmPid()));
            } else {
                builder.and(qInspectionQuestionItem.upperQesitmPid.isNull());
            }
        }

        List<InspectionQuestionItem> questionItemList = queryFactory
                .select(Projections.fields(InspectionQuestionItem.class,
                        qInspectionQuestionItem.id,
                        qInspectionQuestionItem.dvCodePid,
                        qInspectionQuestionItem.aswDvTy,
                        qInspectionQuestionItem.qestnQesitm,
                        qInspectionQuestionItem.answerCnt,
                        qInspectionQuestionItem.rspnsCnt,
                        qInspectionQuestionItem.lwprtQesitmAt,
                        qInspectionQuestionItem.inspctPid,
                        qInspectionQuestionItem.upperQesitmPid,
                        qInspectionQuestionItem.regPsId,
                        qInspectionQuestionItem.regDtm,
                        ExpressionUtils.as(
                                JPAExpressions.select(qAccount.nm)
                                        .from(qAccount)
                                        .where(qAccount.loginId.eq(qInspectionQuestionItem.regPsId)),
                                "regPsNm"),
                        ExpressionUtils.as(
                                JPAExpressions.select(qCommonCode.codeNm)
                                        .from(qCommonCode)
                                        .where(qCommonCode.id.eq(qInspectionQuestionItem.dvCodePid)),
                                "dvCodeNm")
                ))
                .from(qInspectionQuestionItem)
                .where(builder)
                .orderBy(orderSpecifier)
                .fetch();

        return questionItemList;
    }

    public InspectionQuestionItem load(Long id) {
        InspectionQuestionItem load = inspectionQuestionItemRepository.findById(id).orElseGet(InspectionQuestionItem::new);

        return load;
    }

    public InspectionQuestionItem loadByForm(InspectionQuestionItemForm form) {

        QInspectionQuestionItem qInspectionQuestionItem = QInspectionQuestionItem.inspectionQuestionItem;
        QAccount qAccount = QAccount.account;
        QCommonCode qCommonCode = QCommonCode.commonCode;

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(qInspectionQuestionItem.id.eq(form.getId()));

        List<InspectionQuestionItem> list = queryFactory
                .select(Projections.fields(InspectionQuestionItem.class,
                        qInspectionQuestionItem.id,
                        qInspectionQuestionItem.dvCodePid,
                        qInspectionQuestionItem.aswDvTy,
                        qInspectionQuestionItem.qestnQesitm,
                        qInspectionQuestionItem.answerCnt,
                        qInspectionQuestionItem.rspnsCnt,
                        qInspectionQuestionItem.lwprtQesitmAt,
                        qInspectionQuestionItem.inspctPid,
                        qInspectionQuestionItem.upperQesitmPid,
                        qInspectionQuestionItem.regPsId,
                        qInspectionQuestionItem.regDtm,
                        ExpressionUtils.as(
                                Expressions.stringTemplate("fn_getParentCodeList({0},{1})", inspectDvCodePid, qInspectionQuestionItem.dvCodePid)
                                ,
                                "dvCodeFull")
                ))
                .from(qInspectionQuestionItem)
                .where(builder)
                .fetch();

        return (list != null && list.size() > 0 ? list.get(0) : new InspectionQuestionItem());
    }

    @Transactional
    public void delete(InspectionQuestionItemForm form) {
        InspectionQuestionItem questionItem = inspectionQuestionItemRepository.findById(form.getId()).orElseGet(InspectionQuestionItem::new);

        questionItem.setUpdDtm(LocalDateTime.now());
        questionItem.setUpdPsId(form.getUpdPsId());
        questionItem.setDelAt(form.getDelAt());

    }

    /**
     * @param form
     * @return
     */ 
    @Transactional
    public boolean insert(InspectionQuestionItemForm form, List<InspectionAnswerItem> answerItems) throws Exception {

        try {
            InspectionQuestionItem questionItem = modelMapper.map(form, InspectionQuestionItem.class);
            questionItem = inspectionQuestionItemRepository.save(questionItem);

            if (answerItems != null) {
                for (InspectionAnswerItem answerItem : answerItems) {
                    answerItem.setQesitmPid(questionItem.getId());

                    inspectionAnswerItemRepository.save(answerItem);
                }
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public boolean update(InspectionQuestionItemForm form, List<InspectionAnswerItem> answerItems) {

        try {

            InspectionQuestionItem questionItem = inspectionQuestionItemRepository.findById(form.getId()).orElseGet(InspectionQuestionItem::new);
            questionItem.setDvCodePid(form.getDvCodePid());
            questionItem.setAswDvTy(form.getAswDvTy());
            questionItem.setQestnQesitm(form.getQestnQesitm());
            questionItem.setAnswerCnt(form.getAnswerCnt());
            questionItem.setRspnsCnt(form.getRspnsCnt());
            questionItem.setLwprtQesitmAt(form.getLwprtQesitmAt());
            questionItem.setInspctPid(form.getInspctPid());
            questionItem.setUpperQesitmPid(form.getUpperQesitmPid());
            questionItem.setUpdPsId(form.getUpdPsId());
            questionItem.setUpdDtm(LocalDateTime.now());

            inspectionAnswerItemRepository.deleteAllByQesitmPid(form.getId());

            for (InspectionAnswerItem answerItem : answerItems) {
                answerItem.setQesitmPid(questionItem.getId());

                inspectionAnswerItemRepository.save(answerItem);
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
