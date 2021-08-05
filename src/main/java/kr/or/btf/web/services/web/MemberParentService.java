package kr.or.btf.web.services.web;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.btf.web.domain.web.*;
import kr.or.btf.web.repository.web.MemberParentRepository;
import kr.or.btf.web.web.form.MemberParentForm;
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
public class MemberParentService extends _BaseService {

    private final JPAQueryFactory queryFactory;
    private final MemberParentRepository memberParentRepository;
    private final ModelMapper modelMapper;

    public List<MemberParent> list(MemberParentForm memberParentForm) {

        QMemberParent qMemberParent = QMemberParent.memberParent;
        QAccount qAccount = QAccount.account;

        OrderSpecifier<Long> orderSpecifier = qMemberParent.id.desc();

        BooleanBuilder builder = new BooleanBuilder();
        if (memberParentForm.getStdnprntId() != null) {
            builder.and(qMemberParent.stdnprntId.eq(memberParentForm.getStdnprntId()));
        }
        if (memberParentForm.getStdntId() != null) {
            builder.and(qMemberParent.stdntId.eq(memberParentForm.getStdntId()));
        }

        List<MemberParent> mngList = queryFactory
                .select(Projections.fields(MemberParent.class,
                        qMemberParent.id,
                        qMemberParent.stdnprntId,
                        qMemberParent.stdntId,
                        qMemberParent.regDtm,
                        qAccount.id.as("mberPid"),
                        qAccount.nm.as("mberNm"),
                        qAccount.sexPrTy.as("mberSexPrTy"),
                        qAccount.loginId.as("mberLoginId"),
                        qAccount.regDtm.as("mberRegDtm"),
                        qAccount.secsnDtm.as("mberSecsnDtm"),
                        qAccount.moblphon.as("mberMoblphon"),
                        qAccount.email.as("mberEmail")
                ))
                .from(qMemberParent)
                .leftJoin(qAccount).on(qMemberParent.stdnprntId.eq(qAccount.loginId))
                .where(builder)
                .orderBy(orderSpecifier)
                .fetch();

        return mngList;
    }

    public List<MemberParent> listForAdminParent(MemberParentForm memberParentForm) {

        QMemberParent qMemberParent = QMemberParent.memberParent;
        QMemberSchool qMemberSchool = QMemberSchool.memberSchool;
        QMemberTeacher qMemberTeacher = QMemberTeacher.memberTeacher;
        QAccount parent = new QAccount("parent");
        QAccount child = new QAccount("child");
        QAccount teacher = new QAccount("teacher");

        OrderSpecifier<String> orderSpecifierString = child.brthdy.asc();

        BooleanBuilder builder = new BooleanBuilder();
        if (memberParentForm.getStdnprntId() != null) {
            builder.and(qMemberParent.stdnprntId.eq(memberParentForm.getStdnprntId()));
        }
        if (memberParentForm.getStdntId() != null) {
            builder.and(qMemberParent.stdntId.eq(memberParentForm.getStdntId()));
        }

        List<MemberParent> mngList = queryFactory
                .select(Projections.fields(MemberParent.class,
                        qMemberParent.id,
                        qMemberParent.stdnprntId,
                        qMemberParent.stdntId,
                        qMemberParent.regDtm,
                        parent.id.as("mberPid"),
                        /*parent.nm.as("mberNm"),
                        parent.sexPrTy.as("mberSexPrTy"),
                        parent.loginId.as("mberLoginId"),
                        parent.regDtm.as("mberRegDtm"),
                        parent.secsnDtm.as("mberSecsnDtm"),
                        parent.moblphon.as("mberMoblphon"),
                        parent.email.as("mberEmail"),*/
                        child.nm.as("childNm"),
                        child.sexPrTy.as("childSexPrTy"),
                        child.loginId.as("childLoginId"),
                        child.regDtm.as("childRegDtm"),
                        child.secsnDtm.as("childSecsnDtm"),
                        child.moblphon.as("childMoblphon"),
                        child.email.as("childEmail"),
                        qMemberSchool.schlNm.as("childSchlNm"),
                        qMemberSchool.grade.as("childGrade"),
                        qMemberSchool.ban.as("childBan"),
                        qMemberSchool.no.as("childNo"),
                        teacher.nm.as("teacherNm"),
                        teacher.sexPrTy.as("teacherSexPrTy"),
                        teacher.loginId.as("teacherLoginId"),
                        teacher.regDtm.as("teacherRegDtm"),
                        teacher.secsnDtm.as("teacherSecsnDtm"),
                        teacher.moblphon.as("teacherMoblphon"),
                        teacher.email.as("teacherEmail")

                ))
                .from(qMemberParent)
                .innerJoin(parent).on(qMemberParent.stdnprntId.eq(parent.loginId))
                .leftJoin(child).on(qMemberParent.stdntId.eq(child.loginId))
                .innerJoin(qMemberSchool).on(qMemberSchool.mberPid.eq(child.id))
                .leftJoin(qMemberTeacher).on(qMemberSchool.areaNm.eq(qMemberTeacher.areaNm).and(qMemberSchool.schlNm.eq(qMemberTeacher.schlNm).and(qMemberSchool.ban.eq(qMemberTeacher.ban))))
                .leftJoin(teacher).on(qMemberTeacher.mberPid.eq(teacher.id))
                .where(builder)
                .orderBy(orderSpecifierString)
                .fetch();

        return mngList;
    }

    public MemberParent load(Long id) {
        MemberParent memberParent = memberParentRepository.findById(id).orElseGet(MemberParent::new);

        return memberParent;
    }


    /**
     * @param memberParentForm
     * @return
     */
    @Transactional
    public boolean insert(MemberParentForm memberParentForm) {
        try {

            MemberParent account = modelMapper.map(memberParentForm, MemberParent.class);
            memberParentRepository.save(account);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public boolean update(MemberParentForm memberParentForm) {

        try {
            MemberParent account = memberParentRepository.findById(memberParentForm.getId()).orElseGet(MemberParent::new);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public boolean delete(MemberParentForm memberParentForm) {

        try {
            memberParentRepository.deleteById(memberParentForm.getId());
            return true;
        } catch (Exception e) {
            return false;
        }

    }
}
