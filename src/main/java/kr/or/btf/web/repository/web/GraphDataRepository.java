package kr.or.btf.web.repository.web;

import kr.or.btf.web.domain.web.GraphData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GraphDataRepository extends JpaRepository<GraphData, Long> {

    List<GraphData> findAllByGraphPidOrderByColSno(Long graphPid);
}
