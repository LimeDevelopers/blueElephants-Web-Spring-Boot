package kr.or.btf.web.repository.web;

import kr.or.btf.web.domain.web.MngMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MngMenuRepository extends JpaRepository<MngMenu, Long> {
    List<MngMenu> findAllByDelAt(String delAt);
}
