package kr.or.btf.web.repository.web;

import kr.or.btf.web.domain.web.MemberCrew;
import kr.or.btf.web.domain.web.MemberGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * MemberCrewRepository [크루 회원 repo]
 * @author : jerry
 * @version : 1.0.0
 * 작성일 : 2021/08/24
**/
@Repository
public interface MemberCrewRepository extends JpaRepository<MemberCrew, Long> {

    List<MemberCrew> findByCrewNmContains(String keywored);


}
