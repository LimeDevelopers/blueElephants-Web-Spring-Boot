package kr.or.btf.web.services.web;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.btf.web.common.Constants;
import kr.or.btf.web.domain.web.Peer;
import kr.or.btf.web.domain.web.QAccount;
import kr.or.btf.web.domain.web.QCommonCode;
import kr.or.btf.web.domain.web.QPeer;
import kr.or.btf.web.repository.web.PeerRepository;
import kr.or.btf.web.web.form.PeerForm;
import kr.or.btf.web.web.form.SearchForm;
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
public class PeerService extends _BaseService {

    private final JPAQueryFactory queryFactory;
    private final PeerRepository peerRepository;
    private final ModelMapper modelMapper;

    public Page<Peer> list(Pageable pageable, SearchForm searchForm) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, Constants.DEFAULT_PAGESIZE); // <- Sort 추가

        QPeer qPeer = QPeer.peer;
        QAccount qAccount = QAccount.account;

        OrderSpecifier<Long> orderSpecifier = qPeer.id.desc();

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qPeer.delAt.eq("N"));

        if (searchForm.getSrchWord() != null && !searchForm.getSrchWord().isEmpty()) {
            builder.and(qPeer.ttl.like("%" + searchForm.getSrchWord() + "%"));
        }

        QueryResults<Peer> mngList = queryFactory
                .select(Projections.fields(Peer.class,
                        qPeer.id,
                        qPeer.ttl,
                        qPeer.cnts,
                        qPeer.opnAt,
                        qPeer.regPsId,
                        qPeer.regDtm,
                        qPeer.updPsId,
                        qPeer.updDtm,
                        qPeer.delAt,
                        ExpressionUtils.as(
                                JPAExpressions.select(qAccount.nm)
                                        .from(qAccount)
                                        .where(qAccount.loginId.eq(qPeer.regPsId)),
                                "regPsNm")
                ))
                .from(qPeer)
                .where(builder)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(orderSpecifier)
                .fetchResults();

        return new PageImpl<>(mngList.getResults(), pageable, mngList.getTotal());
    }

    public Peer firstOne() {

        QPeer qPeer = QPeer.peer;
        QAccount qAccount = QAccount.account;

        OrderSpecifier<Long> orderSpecifier = qPeer.id.desc();

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qPeer.delAt.eq("N"));

        Peer mng = queryFactory
                .select(Projections.fields(Peer.class,
                        qPeer.id,
                        qPeer.ttl,
                        qPeer.cnts,
                        qPeer.opnAt,
                        qPeer.regPsId,
                        qPeer.regDtm,
                        qPeer.updPsId,
                        qPeer.updDtm,
                        qPeer.delAt,
                        ExpressionUtils.as(
                                JPAExpressions.select(qAccount.nm)
                                        .from(qAccount)
                                        .where(qAccount.loginId.eq(qPeer.regPsId)),
                                "regPsNm")
                ))
                .from(qPeer)
                .where(builder)
                .orderBy(orderSpecifier)
                .fetchFirst();

        return mng;
    }

    public Peer load(Long id) {
        Peer peer = peerRepository.findById(id).orElseGet(Peer::new);

        return peer;
    }

    public Peer loadByform(PeerForm form) {

        QPeer qPeer = QPeer.peer;
        QAccount qAccount = QAccount.account;
        QCommonCode qCommonCode = QCommonCode.commonCode;

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(qPeer.delAt.eq("N"));
        if (form.getId() != null) {
            builder.and(qPeer.id.eq(form.getId()));
        }

        List<Peer> list = queryFactory
                .select(Projections.fields(Peer.class,
                        qPeer.id,
                        qPeer.ttl,
                        qPeer.cnts,
                        qPeer.opnAt,
                        qPeer.regPsId,
                        qPeer.regDtm,
                        qPeer.updPsId,
                        qPeer.updDtm,
                        qPeer.delAt
                ))
                .from(qPeer)
                .where(builder)
                .fetch();

        return (list != null && list.size() > 0 ? list.get(0) : new Peer());
    }

    @Transactional
    public void delete(PeerForm peerForm) {
        Peer mng = this.load(peerForm.getId());

        mng.setUpdDtm(LocalDateTime.now());
        mng.setUpdPsId(peerForm.getUpdPsId());
        mng.setDelAt(peerForm.getDelAt());
    }

    /**
     * @param peerForm
     * @return
     */
    @Transactional
    public Peer insert(PeerForm peerForm) throws Exception {

        Peer peer = modelMapper.map(peerForm, Peer.class);
        Peer save = peerRepository.save(peer);

        return save;
    }

    @Transactional
    public boolean update(PeerForm peerForm) {

        try {

            Peer peer = peerRepository.findById(peerForm.getId()).orElseGet(Peer::new);
            peer.setTtl(peerForm.getTtl());
            peer.setCnts(peerForm.getCnts());
            peer.setOpnAt(peerForm.getOpnAt());
            peer.setUpdPsId(peerForm.getUpdPsId());
            peer.setUpdDtm(LocalDateTime.now());

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
