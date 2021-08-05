package kr.or.btf.web.services.web;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.btf.web.domain.web.*;
import kr.or.btf.web.repository.web.InspectionAnswerItemRepository;
import kr.or.btf.web.web.form.InspectionAnswerItemForm;
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
public class InspectionAnswerItemService extends _BaseService {

    private final JPAQueryFactory queryFactory;
    private final InspectionAnswerItemRepository inspectionAnswerItemRepository;
    private final ModelMapper modelMapper;

    public List<InspectionAnswerItem> list(InspectionAnswerItemForm inspectionAnswerItemForm, Account account, Long atnlcReqPid) {

        QInspectionAnswerItem qInspectionAnswerItem = QInspectionAnswerItem.inspectionAnswerItem;
        QAccount qAccount = QAccount.account;

        QInspectionResponsePerson qInspectionResponsePerson = QInspectionResponsePerson.inspectionResponsePerson;
        QInspectionResponse qInspectionResponse = QInspectionResponse.inspectionResponse;

        OrderSpecifier<Long> orderSpecifier = qInspectionAnswerItem.id.asc();

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qInspectionAnswerItem.delAt.eq("N"));
        builder.and(qInspectionAnswerItem.qesitmPid.eq(inspectionAnswerItemForm.getQesitmPid()));

        QBean<InspectionAnswerItem> selector = null;
        if (account == null && atnlcReqPid == null) {
            selector = Projections.fields(InspectionAnswerItem.class,
                    qInspectionAnswerItem.id,
                    qInspectionAnswerItem.answerCnts,
                    qInspectionAnswerItem.apdAnswerAt,
                    qInspectionAnswerItem.qesitmPid,
                    qInspectionAnswerItem.regPsId,
                    qInspectionAnswerItem.regDtm,
                    ExpressionUtils.as(
                            JPAExpressions.select(qAccount.nm)
                                    .from(qAccount)
                                    .where(qAccount.loginId.eq(qInspectionAnswerItem.regPsId)),
                            "regPsNm")
            );
        } else {
            selector = Projections.fields(InspectionAnswerItem.class,
                    qInspectionAnswerItem.id,
                    qInspectionAnswerItem.answerCnts,
                    qInspectionAnswerItem.apdAnswerAt,
                    qInspectionAnswerItem.qesitmPid,
                    qInspectionAnswerItem.regPsId,
                    qInspectionAnswerItem.regDtm,
                    ExpressionUtils.as(
                            JPAExpressions.select(qAccount.nm)
                                    .from(qAccount)
                                    .where(qAccount.loginId.eq(qInspectionAnswerItem.regPsId)),
                            "regPsNm"),
                    qInspectionResponse.id.as("rspPid"),
                    qInspectionResponse.answerCnts.as("userAnswerCnts")
            );
        }

        JPAQuery<InspectionAnswerItem> tempList = queryFactory
                .select(selector)
                .from(qInspectionAnswerItem)
                .where(builder);
        if (account != null && atnlcReqPid != null) {
            tempList.leftJoin(qInspectionResponsePerson).on(qInspectionResponsePerson.loginId.eq(account.getLoginId())
                                                            .and(qInspectionResponsePerson.inspctDvTy.eq(inspectionAnswerItemForm.getInspctDvTy()))
                                                            .and(qInspectionResponsePerson.inspctPid.eq(inspectionAnswerItemForm.getInspctPid()))
                                                            .and(qInspectionResponsePerson.atnlcReqPid.eq(atnlcReqPid)));
            tempList.leftJoin(qInspectionResponse).on(qInspectionResponsePerson.id.eq(qInspectionResponse.rspPsPid)
                                                    .and(qInspectionAnswerItem.qesitmPid.eq(qInspectionResponse.qesitmPid))
                                                    .and(qInspectionAnswerItem.id.eq(qInspectionResponse.aswPid)));
        } else if (account != null && atnlcReqPid == null) {
            tempList.leftJoin(qInspectionResponsePerson).on(qInspectionResponsePerson.loginId.eq(account.getLoginId())
                    .and(qInspectionResponsePerson.inspctDvTy.eq(inspectionAnswerItemForm.getInspctDvTy()))
                    .and(qInspectionResponsePerson.inspctPid.eq(inspectionAnswerItemForm.getInspctPid())));
            tempList.leftJoin(qInspectionResponse).on(qInspectionResponsePerson.id.eq(qInspectionResponse.rspPsPid)
                    .and(qInspectionAnswerItem.qesitmPid.eq(qInspectionResponse.qesitmPid))
                    .and(qInspectionAnswerItem.id.eq(qInspectionResponse.aswPid)));
        }
        tempList.orderBy(orderSpecifier);

        List<InspectionAnswerItem> answerItemList = tempList.fetch();

        return answerItemList;
    }

    public InspectionAnswerItem load(Long id) {
        InspectionAnswerItem load = inspectionAnswerItemRepository.findById(id).orElseGet(InspectionAnswerItem::new);

        return load;
    }

    @Transactional
    public void delete(InspectionAnswerItemForm form) {
        inspectionAnswerItemRepository.deleteById(form.getId());
    }

    /**
     * @param form
     * @return
     */
    @Transactional
    public InspectionAnswerItem insert(InspectionAnswerItemForm form) throws Exception {

        InspectionAnswerItem answerItem = modelMapper.map(form, InspectionAnswerItem.class);
        answerItem = inspectionAnswerItemRepository.save(answerItem);

        return answerItem;
    }

    @Transactional
    public boolean update(InspectionAnswerItemForm form) {

        try {

            InspectionAnswerItem answerItem = inspectionAnswerItemRepository.findById(form.getId()).orElseGet(InspectionAnswerItem::new);
            answerItem.setAnswerCnts(form.getAnswerCnts());
            answerItem.setApdAnswerAt(form.getApdAnswerAt());
            answerItem.setQesitmPid(form.getQesitmPid());
            answerItem.setUpdPsId(form.getUpdPsId());
            answerItem.setUpdDtm(LocalDateTime.now());

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
