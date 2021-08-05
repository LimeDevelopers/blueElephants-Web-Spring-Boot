package kr.or.btf.web.repository.web;

import kr.or.btf.web.domain.web.Postscript;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostscriptRepository extends JpaRepository<Postscript, Long> {
}
