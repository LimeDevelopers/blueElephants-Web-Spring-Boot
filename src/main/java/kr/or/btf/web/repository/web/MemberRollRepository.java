package kr.or.btf.web.repository.web;

import kr.or.btf.web.domain.web.MemberRoll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRollRepository extends JpaRepository<MemberRoll, Long> {

    List<MemberRoll> findAllByMberPid(Long mberPid);
    MemberRoll findByMberPid(Long mberPid);
}
