package kr.or.btf.web.repository.web;

import kr.or.btf.web.domain.web.BoardTarget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardTargetRepository extends JpaRepository<BoardTarget, Long> {

    void deleteAllByDataPid(Long id);

    List<BoardTarget> findByDataPid(Long dataPid);

}
