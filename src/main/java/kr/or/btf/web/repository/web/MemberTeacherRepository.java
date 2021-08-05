package kr.or.btf.web.repository.web;

import kr.or.btf.web.domain.web.MemberTeacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberTeacherRepository extends JpaRepository<MemberTeacher, Long> {

    MemberTeacher findByMberPid(Long mberPid);
}
