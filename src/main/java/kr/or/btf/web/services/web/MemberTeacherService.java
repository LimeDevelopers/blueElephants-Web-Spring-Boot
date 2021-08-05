package kr.or.btf.web.services.web;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.btf.web.domain.web.MemberTeacher;
import kr.or.btf.web.domain.web.QAccount;
import kr.or.btf.web.domain.web.QMemberTeacher;
import kr.or.btf.web.repository.web.MemberTeacherRepository;
import kr.or.btf.web.web.form.MemberTeacherForm;
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
public class MemberTeacherService extends _BaseService {

    private final JPAQueryFactory queryFactory;
    private final MemberTeacherRepository memberTeacherRepository;
    private final ModelMapper modelMapper;

    public List<MemberTeacher> list(MemberTeacherForm memberTeacherForm) {

        QMemberTeacher qMemberTeacher = QMemberTeacher.memberTeacher;

        OrderSpecifier<Long> orderSpecifier = qMemberTeacher.id.desc();

        BooleanBuilder builder = new BooleanBuilder();
        if (memberTeacherForm.getMberPid() != null) {
            builder.and(qMemberTeacher.mberPid.eq(memberTeacherForm.getMberPid()));
        }
        if (memberTeacherForm.getSchlNm() != null) {
            builder.and(qMemberTeacher.schlNm.eq(memberTeacherForm.getSchlNm()));
        }
        if (memberTeacherForm.getGrade() != null) {
            builder.and(qMemberTeacher.grade.eq(memberTeacherForm.getGrade()));
        }
        if (memberTeacherForm.getBan() != null) {
            builder.and(qMemberTeacher.ban.eq(memberTeacherForm.getBan()));
        }

        List<MemberTeacher> mngList = queryFactory
                .select(Projections.fields(MemberTeacher.class,
                        qMemberTeacher.id,
                        qMemberTeacher.areaNm,
                        qMemberTeacher.schlNm,
                        qMemberTeacher.grade,
                        qMemberTeacher.ban,
                        qMemberTeacher.mberPid
                ))
                .from(qMemberTeacher)
                .where(builder)
                .orderBy(orderSpecifier)
                .fetch();

        return mngList;
    }

    public MemberTeacher load(Long id) {
        MemberTeacher memberTeacher = memberTeacherRepository.findById(id).orElseGet(MemberTeacher::new);

        return memberTeacher;
    }

    public MemberTeacher loadByMber(Long mberPid) {
        MemberTeacher memberTeacher = memberTeacherRepository.findByMberPid(mberPid);

        return memberTeacher;
    }

    public MemberTeacher loadByForm(MemberTeacherForm form) {

        QMemberTeacher qMemberTeacher = QMemberTeacher.memberTeacher;
        QAccount qAccount = QAccount.account;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qMemberTeacher.areaNm.eq(form.getAreaNm()));
        builder.and(qMemberTeacher.schlNm.eq(form.getSchlNm()));
        builder.and(qMemberTeacher.grade.eq(form.getGrade()));
        builder.and(qMemberTeacher.ban.eq(form.getBan()));

        MemberTeacher memberTeacher = queryFactory
                .select(Projections.fields(MemberTeacher.class,
                        qMemberTeacher.id,
                        qMemberTeacher.areaNm,
                        qMemberTeacher.schlNm,
                        qMemberTeacher.grade,
                        qMemberTeacher.ban,
                        qMemberTeacher.mberPid,
                        qAccount.nm.as("mberNm"),
                        qAccount.loginId.as("mberLoginId"),
                        qAccount.sexPrTy.as("mberSexPrTy"),
                        qAccount.regDtm.as("mberRegDtm"),
                        qAccount.secsnDtm.as("mberSecsnDtm"),
                        qAccount.moblphon.as("mberMoblphon"),
                        qAccount.email.as("mberEmail")
                ))
                .from(qMemberTeacher)
                .leftJoin(qAccount).on(qMemberTeacher.mberPid.eq(qAccount.id))
                .where(builder)
                .fetchFirst();

        return memberTeacher;
    }


    /**
     * @param memberTeacherForm
     * @return
     */
    @Transactional
    public boolean insert(MemberTeacherForm memberTeacherForm) {
        try {

            MemberTeacher memberTeacher = modelMapper.map(memberTeacherForm, MemberTeacher.class);
            memberTeacherRepository.save(memberTeacher);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public boolean update(MemberTeacherForm memberTeacherForm) {

        try {
            MemberTeacher memberTeacher = memberTeacherRepository.findById(memberTeacherForm.getId()).orElseGet(MemberTeacher::new);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public boolean delete(MemberTeacherForm memberTeacherForm) {

        try {
            memberTeacherRepository.deleteById(memberTeacherForm.getId());
            return true;
        } catch (Exception e) {
            return false;
        }

    }
}
