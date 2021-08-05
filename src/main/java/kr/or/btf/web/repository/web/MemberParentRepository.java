package kr.or.btf.web.repository.web;

import kr.or.btf.web.domain.web.MemberParent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberParentRepository extends JpaRepository<MemberParent, Long> {
    List<MemberParent> findByStdnprntId(String stdnprntId);
    void deleteByStdnprntId(String stdnprntId);
}
