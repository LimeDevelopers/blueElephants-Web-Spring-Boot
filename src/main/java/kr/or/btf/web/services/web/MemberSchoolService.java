package kr.or.btf.web.services.web;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.btf.web.domain.web.MemberSchool;
import kr.or.btf.web.domain.web.QAccount;
import kr.or.btf.web.domain.web.QMemberParent;
import kr.or.btf.web.domain.web.QMemberSchool;
import kr.or.btf.web.repository.web.MemberSchoolRepository;
import kr.or.btf.web.web.form.MemberSchoolForm;
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
public class MemberSchoolService extends _BaseService {

    private final JPAQueryFactory queryFactory;
    private final MemberSchoolRepository memberSchoolRepository;
    private final ModelMapper modelMapper;

    public List<MemberSchool> list(MemberSchoolForm memberSchoolForm) {

        QMemberSchool qMemberSchool = QMemberSchool.memberSchool;

        OrderSpecifier<Long> orderSpecifier = qMemberSchool.id.desc();

        BooleanBuilder builder = new BooleanBuilder();
        if (memberSchoolForm.getMberPid() != null) {
            builder.and(qMemberSchool.mberPid.eq(memberSchoolForm.getMberPid()));
        }
        if (memberSchoolForm.getSchlNm() != null) {
            builder.and(qMemberSchool.schlNm.eq(memberSchoolForm.getSchlNm()));
        }
        if (memberSchoolForm.getGrade() != null) {
            builder.and(qMemberSchool.grade.eq(memberSchoolForm.getGrade()));
        }
        if (memberSchoolForm.getBan() != null) {
            builder.and(qMemberSchool.ban.eq(memberSchoolForm.getBan()));
        }

        List<MemberSchool> mngList = queryFactory
                .select(Projections.fields(MemberSchool.class,
                        qMemberSchool.id,
                        qMemberSchool.areaNm,
                        qMemberSchool.schlNm,
                        qMemberSchool.grade,
                        qMemberSchool.ban,
                        qMemberSchool.no,
                        qMemberSchool.mberPid
                ))
                .from(qMemberSchool)
                .where(builder)
                .orderBy(orderSpecifier)
                .fetch();

        return mngList;
    }

    public List<MemberSchool> listForAdminTeacher(MemberSchoolForm memberSchoolForm) {

        QMemberSchool qMemberSchool = QMemberSchool.memberSchool;
        QAccount student = new QAccount("student");
        QMemberParent qMemberParent = QMemberParent.memberParent;
        QAccount parent = new QAccount("parent");

        OrderSpecifier<Integer> orderSpecifierInteger = qMemberSchool.no.asc();

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(qMemberSchool.schlNm.eq(memberSchoolForm.getSchlNm()));
        builder.and(qMemberSchool.grade.eq(memberSchoolForm.getGrade()));
        builder.and(qMemberSchool.ban.eq(memberSchoolForm.getBan()));

        List<MemberSchool> mngList = queryFactory
                .select(Projections.fields(MemberSchool.class,
                        qMemberSchool.id,
                        qMemberSchool.areaNm,
                        qMemberSchool.schlNm,
                        qMemberSchool.grade,
                        qMemberSchool.ban,
                        qMemberSchool.no,
                        qMemberSchool.mberPid,
                        student.nm.as("studentNm"),
                        student.sexPrTy.as("studentSexPrTy"),
                        student.loginId.as("studentLoginId"),
                        student.regDtm.as("studentRegDtm"),
                        student.secsnDtm.as("studentSecsnDtm"),
                        student.moblphon.as("studentMoblphon"),
                        student.email.as("studentEmail"),
                        parent.nm.as("parentNm"),
                        parent.sexPrTy.as("parentSexPrTy"),
                        parent.loginId.as("parentLoginId"),
                        parent.regDtm.as("parentRegDtm"),
                        parent.secsnDtm.as("parentSecsnDtm"),
                        parent.moblphon.as("parentMoblphon"),
                        parent.email.as("parentEmail")
                ))
                .from(qMemberSchool)
                .innerJoin(student).on(qMemberSchool.mberPid.eq(student.id))
                .leftJoin(qMemberParent).on(student.loginId.eq(qMemberParent.stdntId))
                .leftJoin(parent).on(qMemberParent.stdnprntId.eq(parent.loginId))
                .where(builder)
                .orderBy(orderSpecifierInteger)
                .fetch();

        return mngList;
    }

    public MemberSchool load(Long id) {
        MemberSchool school = memberSchoolRepository.findById(id).orElseGet(MemberSchool::new);

        return school;
    }

    public MemberSchool loadByMber(Long mberPid) {
        MemberSchool school = memberSchoolRepository.findByMberPid(mberPid);

        return school;
    }

    public MemberSchool loadByForm(MemberSchoolForm form) {
        /*MemberSchool school = memberSchoolRepository.findByAreaNmAndSchlNmAndGradeAndBanAndNo(form.getAreaNm(), form.getSchlNm(), form.getGrade(), form.getBan(), form.getNo());

        return school;*/

        QMemberSchool qMemberSchool = QMemberSchool.memberSchool;
        QAccount qAccount = QAccount.account;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qMemberSchool.areaNm.eq(form.getAreaNm()));
        builder.and(qMemberSchool.schlNm.eq(form.getSchlNm()));
        builder.and(qMemberSchool.grade.eq(form.getGrade()));
        builder.and(qMemberSchool.ban.eq(form.getBan()));
        builder.and(qMemberSchool.no.eq(form.getNo()));

        MemberSchool memberSchool = queryFactory
                .select(Projections.fields(MemberSchool.class,
                        qMemberSchool.id,
                        qMemberSchool.areaNm,
                        qMemberSchool.schlNm,
                        qMemberSchool.grade,
                        qMemberSchool.ban,
                        qMemberSchool.no,
                        qMemberSchool.mberPid,
                        qAccount.nm.as("mberNm"),
                        qAccount.loginId.as("mberLoginId")
                ))
                .from(qMemberSchool)
                .leftJoin(qAccount).on(qMemberSchool.mberPid.eq(qAccount.id))
                .where(builder)
                .fetchFirst();

        return memberSchool;
    }


    /**
     * @param memberSchoolForm
     * @return
     */
    @Transactional
    public boolean insert(MemberSchoolForm memberSchoolForm) {
        try {

            MemberSchool account = modelMapper.map(memberSchoolForm, MemberSchool.class);
            memberSchoolRepository.save(account);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public boolean update(MemberSchoolForm memberSchoolForm) {

        try {
            MemberSchool account = memberSchoolRepository.findById(memberSchoolForm.getId()).orElseGet(MemberSchool::new);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public boolean delete(MemberSchoolForm memberSchoolForm) {

        try {
            memberSchoolRepository.deleteById(memberSchoolForm.getId());
            return true;
        } catch (Exception e) {
            return false;
        }

    }
}
