package kr.or.btf.web.services.web;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.btf.web.domain.web.PeerAnswerItem;
import kr.or.btf.web.domain.web.QAccount;
import kr.or.btf.web.domain.web.QPeerAnswerItem;
import kr.or.btf.web.repository.web.PeerAnswerItemRepository;
import kr.or.btf.web.web.form.PeerAnswerItemForm;
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
public class PeerAnswerItemService extends _BaseService {

    private final JPAQueryFactory queryFactory;
    private final PeerAnswerItemRepository peerAnswerItemRepository;
    private final ModelMapper modelMapper;

    public List<PeerAnswerItem> list(PeerAnswerItemForm peerAnswerItemForm) {

        QPeerAnswerItem qPeerAnswerItem = QPeerAnswerItem.peerAnswerItem;
        QAccount qAccount = QAccount.account;

        OrderSpecifier<Long> orderSpecifier = qPeerAnswerItem.id.asc();

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qPeerAnswerItem.delAt.eq("N"));
        builder.and(qPeerAnswerItem.qesitmPid.eq(peerAnswerItemForm.getQesitmPid()));

        List<PeerAnswerItem> answerItemList = queryFactory
                .select(Projections.fields(PeerAnswerItem.class,
                        qPeerAnswerItem.id,
                        qPeerAnswerItem.answerCnts,
                        qPeerAnswerItem.score,
                        qPeerAnswerItem.regPsId,
                        qPeerAnswerItem.regDtm,
                        qPeerAnswerItem.updPsId,
                        qPeerAnswerItem.updDtm,
                        qPeerAnswerItem.delAt,
                        qPeerAnswerItem.qesitmPid,
                        ExpressionUtils.as(
                                JPAExpressions.select(qAccount.nm)
                                        .from(qAccount)
                                        .where(qAccount.loginId.eq(qPeerAnswerItem.regPsId)),
                                "regPsNm")
                ))
                .from(qPeerAnswerItem)
                .where(builder)
                .orderBy(orderSpecifier)
                .fetch();

        return answerItemList;
    }

    public PeerAnswerItem load(Long id) {
        PeerAnswerItem peer = peerAnswerItemRepository.findById(id).orElseGet(PeerAnswerItem::new);

        return peer;
    }

    @Transactional
    public void delete(PeerAnswerItemForm peerAnswerItemForm) {
        peerAnswerItemRepository.deleteById(peerAnswerItemForm.getId());
    }

    /**
     * @param peerAnswerItemForm
     * @return
     */
    @Transactional
    public PeerAnswerItem insert(PeerAnswerItemForm peerAnswerItemForm) throws Exception {

        PeerAnswerItem peerAnswerItem = modelMapper.map(peerAnswerItemForm, PeerAnswerItem.class);
        peerAnswerItem = peerAnswerItemRepository.save(peerAnswerItem);

        return peerAnswerItem;
    }

    @Transactional
    public boolean update(PeerAnswerItemForm peerAnswerItemForm) {

        try {

            PeerAnswerItem peerAnswerItem = peerAnswerItemRepository.findById(peerAnswerItemForm.getId()).orElseGet(PeerAnswerItem::new);
            peerAnswerItem.setAnswerCnts(peerAnswerItem.getAnswerCnts());
            peerAnswerItem.setQesitmPid(peerAnswerItem.getQesitmPid());
            peerAnswerItem.setUpdPsId(peerAnswerItem.getUpdPsId());
            peerAnswerItem.setUpdDtm(LocalDateTime.now());

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
