package kr.or.btf.web.services.web;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.btf.web.domain.web.*;
import kr.or.btf.web.repository.web.PeerResponseRepository;
import kr.or.btf.web.web.form.PeerResponseForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PeerResponseService extends _BaseService {

    private final JPAQueryFactory queryFactory;
    private final PeerResponseRepository peerResponseRepository;
    private final ModelMapper modelMapper;

    public List<PeerResponse> list(Long qesitmPid, MemberSchool memberSchool, PeerResponsePerson person) {

        QPeerResponsePerson qPeerResponsePerson = QPeerResponsePerson.peerResponsePerson;
        QPeerResponse qPeerResponse = QPeerResponse.peerResponse;
        QAccount qAccount = QAccount.account;
        QMemberSchool qMemberSchool = QMemberSchool.memberSchool;

        BooleanBuilder builder = new BooleanBuilder();
        if (memberSchool.getAreaNm() != null) {
            builder.and(qMemberSchool.areaNm.eq(memberSchool.getAreaNm()));
        }
        if (memberSchool.getSchlNm() != null) {
            builder.and(qMemberSchool.schlNm.eq(memberSchool.getSchlNm()));
        }
        if (memberSchool.getGrade() != null) {
            builder.and(qMemberSchool.grade.eq(memberSchool.getGrade()));
        }
        if (memberSchool.getBan() != null) {
            builder.and(qMemberSchool.ban.eq(memberSchool.getBan()));
        }
        if (memberSchool.getTeacherNm() != null) {
            builder.and(qMemberSchool.teacherNm.eq(memberSchool.getTeacherNm()));
        }
        if (memberSchool.getMberPid() != null) {
            builder.and(qMemberSchool.mberPid.ne(memberSchool.getMberPid()));
        }
        /*if (person != null) {
            builder.and(qPeerResponse.rspPsPid.eq(person.getId()));
        }*/

        JPAQuery<PeerResponse> tempList = queryFactory
                .select(Projections.fields(PeerResponse.class,
                        //qPeerResponse.id,
                        qPeerResponse.aswPid,
                        //qPeerResponse.qesitmPid,
                        //qPeerResponse.rspPsPid,
                        qMemberSchool.areaNm,
                        qMemberSchool.schlNm,
                        qMemberSchool.grade,
                        qMemberSchool.ban,
                        qMemberSchool.no,
                        qMemberSchool.teacherNm,
                        //qPeerResponse.tgtMberPid,
                        qMemberSchool.mberPid.as("tgtMberPid"),
                        ExpressionUtils.as(
                                JPAExpressions.select(qAccount.nm)
                                        .from(qAccount)
                                        .where(qAccount.id.eq(qMemberSchool.mberPid)),
                                "nm")
                ))
                .from(qMemberSchool);
                //.leftJoin(qPeerResponsePerson).on(qMemberSchool.areaNm.eq(qPeerResponsePerson.areaNm).and(qMemberSchool.schlNm.eq(qPeerResponsePerson.schlNm)
                //       .and(qMemberSchool.grade.eq(qPeerResponsePerson.grade).and(qMemberSchool.ban.eq(qPeerResponsePerson.ban).and(qMemberSchool.teacherNm.eq(qPeerResponsePerson.teacherNm))))))
                if (person != null) {
                    tempList.leftJoin(qPeerResponse).on(qMemberSchool.areaNm.eq(qPeerResponse.areaNm).and(qMemberSchool.schlNm.eq(qPeerResponse.schlNm)
                            .and(qMemberSchool.grade.eq(qPeerResponse.grade).and(qMemberSchool.ban.eq(qPeerResponse.ban).and(qMemberSchool.teacherNm.eq(qPeerResponse.teacherNm)
                                    .and(qPeerResponse.no.eq(memberSchool.getNo()))
                                    .and(qMemberSchool.mberPid.eq(qPeerResponse.tgtMberPid).and(qPeerResponse.qesitmPid.eq(qesitmPid).and(qPeerResponse.rspPsPid.eq(person.getId())))))))));
                } else {
                    tempList.leftJoin(qPeerResponse).on(qMemberSchool.areaNm.eq(qPeerResponse.areaNm).and(qMemberSchool.schlNm.eq(qPeerResponse.schlNm)
                            .and(qMemberSchool.grade.eq(qPeerResponse.grade).and(qMemberSchool.ban.eq(qPeerResponse.ban).and(qMemberSchool.teacherNm.eq(qPeerResponse.teacherNm)
                                    .and(qPeerResponse.no.eq(memberSchool.getNo()))
                                    .and(qMemberSchool.mberPid.eq(qPeerResponse.tgtMberPid).and(qPeerResponse.qesitmPid.eq(qesitmPid))))))));
                }
                tempList.where(builder)
                .orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc()); //랜덤으로 정렬

        List<PeerResponse> mngList = tempList.fetch();

        return mngList;
    }

    public List<PeerResponse> listForGraph(Long qesitmPid) {
        QPeerResponse qPeerResponse = QPeerResponse.peerResponse;
        QPeerAnswerItem qPeerAnswerItem = QPeerAnswerItem.peerAnswerItem;
        QAccount qAccount = new QAccount("qAccount");
        QAccount target = new QAccount("target");
        QMemberSchool qMemberSchool = new QMemberSchool("qMemberSchool");
        QMemberSchool targetSchool = new QMemberSchool("targetSchool");

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qPeerResponse.qesitmPid.eq(qesitmPid));

        List<PeerResponse> mngList = queryFactory
                .select(Projections.fields(PeerResponse.class,
                        qPeerResponse.qesitmPid,
                        qPeerResponse.no.as("mberNo"),
                        qAccount.nm.as("mberNm"),
                        targetSchool.no.as("targetNo"),
                        target.nm.as("targetNm"),
                        qPeerAnswerItem.score
                        ))
                .from(qPeerResponse)
                .innerJoin(qPeerAnswerItem).on(qPeerResponse.qesitmPid.eq(qPeerAnswerItem.qesitmPid).and(qPeerResponse.aswPid.eq(qPeerAnswerItem.id)))
                .innerJoin(qMemberSchool).on(qPeerResponse.areaNm.eq(qMemberSchool.areaNm).and(qPeerResponse.schlNm.eq(qMemberSchool.schlNm).and(qPeerResponse.grade.eq(qMemberSchool.grade).and(qPeerResponse.ban.eq(qMemberSchool.ban).and(qPeerResponse.no.eq(qMemberSchool.no))))))
                .innerJoin(qAccount).on(qAccount.id.eq(qMemberSchool.mberPid))
                .innerJoin(target).on(target.id.eq(qPeerResponse.tgtMberPid))
                .innerJoin(targetSchool).on(targetSchool.mberPid.eq(target.id))
                .where(builder)
                .fetch();

        return mngList;

    }

    public PeerResponse load(Long id) {
        PeerResponse peerResponse = peerResponseRepository.findById(id).orElseGet(PeerResponse::new);

        return peerResponse;
    }

    @Transactional
    public void delete(PeerResponseForm peerResponseForm) {
        peerResponseRepository.deleteById(peerResponseForm.getId());
    }

    /**
     * @param peerResponseForm
     * @return
     */
    @Transactional
    public PeerResponse insert(PeerResponseForm peerResponseForm) throws Exception {

        PeerResponse peerResponse = modelMapper.map(peerResponseForm, PeerResponse.class);
        peerResponse = peerResponseRepository.save(peerResponse);

        return peerResponse;
    }

    @Transactional
    public boolean update(PeerResponseForm peerResponseForm) {

        try {

            PeerResponse peerResponse = peerResponseRepository.findById(peerResponseForm.getId()).orElseGet(PeerResponse::new);
            peerResponse.setAswPid(peerResponseForm.getAswPid());
            peerResponse.setQesitmPid(peerResponseForm.getQesitmPid());
            peerResponse.setRspPsPid(peerResponseForm.getRspPsPid());

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
