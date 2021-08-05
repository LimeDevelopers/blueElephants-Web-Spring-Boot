package kr.or.btf.web.repository.web;

import kr.or.btf.web.domain.web.MemberLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberLogRepository extends JpaRepository<MemberLog, Long> {

    MemberLog findByLoginId(String loginId);

    boolean existsByLoginId(String loginId);

    MemberLog findByNmAndEmail(String nm, String email);

    MemberLog findByLoginIdAndEmail(String loginId, String email);

    Optional<MemberLog> findByEmail(String email);
}
