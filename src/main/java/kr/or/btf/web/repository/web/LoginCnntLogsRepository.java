package kr.or.btf.web.repository.web;

import kr.or.btf.web.domain.web.LoginCnntLogs;
import kr.or.btf.web.domain.web.LoginCnntLogsKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginCnntLogsRepository extends JpaRepository<LoginCnntLogs, LoginCnntLogsKey> {
    void deleteByCnctId (String cnctId);
}
