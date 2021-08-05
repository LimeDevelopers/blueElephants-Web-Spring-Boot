package kr.or.btf.web.repository.web;

import kr.or.btf.web.domain.web.ExperienceTarget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExperienceTargetRepository extends JpaRepository<ExperienceTarget, Long> {

    List<ExperienceTarget> findAllByExprnPid(Long exprnPid);

    void deleteAllByExprnPid(Long id);

}
