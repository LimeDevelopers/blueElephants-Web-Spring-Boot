package kr.or.btf.web.services.web;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.btf.web.domain.web.*;
import kr.or.btf.web.repository.web.PeerAnswerItemRepository;
import kr.or.btf.web.repository.web.PeerQuestionItemRepository;
import kr.or.btf.web.web.form.PeerQuestionItemForm;
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
public class PeerQuestionItemService extends _BaseService {

    private final JPAQueryFactory queryFactory;
    private final PeerQuestionItemRepository peerQuestionItemRepository;
    private final PeerAnswerItemRepository peerAnswerItemRepository;
    private final ModelMapper modelMapper;

    public List<PeerQuestionItem> list(PeerQuestionItemForm peerQuestionItemForm) {

        QPeerQuestionItem qPeerQuestionItem = QPeerQuestionItem.peerQuestionItem;
        QAccount qAccount = QAccount.account;

        OrderSpecifier<Long> orderSpecifier = qPeerQuestionItem.id.asc();

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qPeerQuestionItem.delAt.eq("N"));
        builder.and(qPeerQuestionItem.peerPid.eq(peerQuestionItemForm.getPeerPid()));

        List<PeerQuestionItem> questionItemList = queryFactory
                .select(Projections.fields(PeerQuestionItem.class,
                        qPeerQuestionItem.id,
                        qPeerQuestionItem.qestnQesitm ,
                        qPeerQuestionItem.aswDvTy,
                        qPeerQuestionItem.answerCnt,
                        qPeerQuestionItem.rspnsCnt,
                        qPeerQuestionItem.peerPid,
                        qPeerQuestionItem.regPsId,
                        qPeerQuestionItem.regDtm,
                        ExpressionUtils.as(
                                JPAExpressions.select(qAccount.nm)
                                        .from(qAccount)
                                        .where(qAccount.loginId.eq(qPeerQuestionItem.regPsId)),
                                "regPsNm")
                ))
                .from(qPeerQuestionItem)
                .where(builder)
                .orderBy(orderSpecifier)
                .fetch();

        return questionItemList;
    }

    public List<PeerQuestionItem> listForFront(PeerQuestionItemForm peerQuestionItemForm, MemberSchool memberSchool) {

        QPeerQuestionItem qPeerQuestionItem = QPeerQuestionItem.peerQuestionItem;
        QAccount qAccount = QAccount.account;
        QPeer qPeer = QPeer.peer;
        QPeerResponsePerson qPeerResponsePerson = QPeerResponsePerson.peerResponsePerson;
        QPeerResponse qPeerResponse = QPeerResponse.peerResponse;
        QMemberSchool qMemberSchool = QMemberSchool.memberSchool;

        OrderSpecifier<Long> orderSpecifier = qPeerQuestionItem.id.asc();

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qPeerQuestionItem.delAt.eq("N"));
        builder.and(qPeerQuestionItem.peerPid.eq(peerQuestionItemForm.getPeerPid()));

        List<PeerQuestionItem> questionItemList = queryFactory
                .select(Projections.fields(PeerQuestionItem.class,
                        qPeerQuestionItem.id,
                        qPeerQuestionItem.qestnQesitm,
                        qPeerQuestionItem.aswDvTy,
                        qPeerQuestionItem.answerCnt,
                        qPeerQuestionItem.rspnsCnt,
                        qPeerQuestionItem.peerPid,
                        qPeerQuestionItem.regPsId,
                        qPeerQuestionItem.regDtm,
                        ExpressionUtils.as(
                                JPAExpressions.select(qMemberSchool.count())
                                        .from(qMemberSchool)
                                        .where(qMemberSchool.areaNm.eq(memberSchool.getAreaNm()).and(qMemberSchool.schlNm.eq(memberSchool.getSchlNm())
                                        .and(qMemberSchool.grade.eq(memberSchool.getGrade()).and(qMemberSchool.ban.eq(memberSchool.getBan())
                                        .and(qMemberSchool.teacherNm.eq(memberSchool.getTeacherNm()).and(qMemberSchool.mberPid.ne(memberSchool.getMberPid()))))))),
                                "studentCnt"),
                        ExpressionUtils.as(
                                JPAExpressions.select(qPeerResponse.count())
                                        .from(qPeerResponse)
                                        .leftJoin(qPeerResponsePerson).on(qPeerResponse.rspPsPid.eq(qPeerResponsePerson.id).and(qPeerResponsePerson.areaNm.eq(memberSchool.getAreaNm())
                                            .and(qPeerResponsePerson.schlNm.eq(memberSchool.getSchlNm()).and(qPeerResponsePerson.grade.eq(memberSchool.getGrade())
                                            .and(qPeerResponsePerson.ban.eq(memberSchool.getBan()).and(qPeerResponsePerson.teacherNm.eq(memberSchool.getTeacherNm())
                                            .and(qPeerResponsePerson.peerPid.eq(peerQuestionItemForm.getPeerPid()))))))))
                                        .where(qPeerResponse.qesitmPid.eq(qPeerQuestionItem.id).and(qPeerResponse.rspPsPid.eq(qPeerResponsePerson.id).and(qPeerResponse.no.eq(memberSchool.getNo())))),
                                "responseCnt")
                ))
                .from(qPeerQuestionItem)
                .where(builder)
                .orderBy(orderSpecifier)
                .fetch();

        return questionItemList;
    }

    public PeerQuestionItem load(Long id) {
        PeerQuestionItem peerQuestionItem = peerQuestionItemRepository.findById(id).orElseGet(PeerQuestionItem::new);

        return peerQuestionItem;
    }

    @Transactional
    public void delete(List<PeerQuestionItem> list) {
        for (PeerQuestionItem peerQuestionItem : list) {
            PeerQuestionItemForm peerQuestionItemForm = new PeerQuestionItemForm();
            peerQuestionItemForm.setId(peerQuestionItem.getId());
            peerQuestionItemForm.setUpdPsId(peerQuestionItem.getUpdPsId());
            peerQuestionItemForm.setUpdDtm(peerQuestionItem.getUpdDtm());
            peerQuestionItemForm.setDelAt(peerQuestionItem.getDelAt());

            PeerQuestionItem mng = this.load(peerQuestionItemForm.getId());

            mng.setUpdDtm(LocalDateTime.now());
            mng.setUpdPsId(peerQuestionItemForm.getUpdPsId());
            mng.setUpdDtm(peerQuestionItemForm.getUpdDtm());
            mng.setDelAt(peerQuestionItemForm.getDelAt());
        }
    }

    /**
     * @param peerQuestionItemForm
     * @return
     */ 
    @Transactional
    public boolean insert(PeerQuestionItemForm peerQuestionItemForm, List<PeerAnswerItem> peerAnswerItemList) throws Exception {

        try {
            PeerQuestionItem peerQuestionItem = modelMapper.map(peerQuestionItemForm, PeerQuestionItem.class);
            peerQuestionItem = peerQuestionItemRepository.save(peerQuestionItem);

            for (PeerAnswerItem peerAnswerItem : peerAnswerItemList) {
                peerAnswerItem.setQesitmPid(peerQuestionItem.getId());

                peerAnswerItemRepository.save(peerAnswerItem);
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public boolean update(PeerQuestionItemForm peerQuestionItemForm, List<PeerAnswerItem> peerAnswerItemList) {

        try {

            PeerQuestionItem peerQuestionItem = peerQuestionItemRepository.findById(peerQuestionItemForm.getId()).orElseGet(PeerQuestionItem::new);
            peerQuestionItem.setAswDvTy(peerQuestionItemForm.getAswDvTy());
            peerQuestionItem.setQestnQesitm(peerQuestionItemForm.getQestnQesitm());
            peerQuestionItem.setAnswerCnt(peerQuestionItemForm.getAnswerCnt());
            peerQuestionItem.setRspnsCnt(peerQuestionItemForm.getRspnsCnt());
            peerQuestionItem.setPeerPid(peerQuestionItemForm.getPeerPid());
            peerQuestionItem.setUpdPsId(peerQuestionItemForm.getUpdPsId());
            peerQuestionItem.setUpdDtm(LocalDateTime.now());

            for (PeerAnswerItem peerAnswerItem : peerAnswerItemList) {
                peerAnswerItem.setQesitmPid(peerQuestionItem.getId());
                if (peerAnswerItem.getId() != null) {
                    PeerAnswerItem loadAnswerItem = peerAnswerItemRepository.findById(peerAnswerItem.getId()).orElseGet(PeerAnswerItem::new);
                    loadAnswerItem.setAnswerCnts(peerAnswerItem.getAnswerCnts());
                    loadAnswerItem.setScore(peerAnswerItem.getScore());
                    loadAnswerItem.setUpdPsId(peerAnswerItem.getUpdPsId());
                    loadAnswerItem.setUpdDtm(peerAnswerItem.getUpdDtm());
                } else {
                    peerAnswerItemRepository.save(peerAnswerItem);
                }
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
