package kr.or.btf.web.repository.web;

import kr.or.btf.web.domain.web.CommonComment;
import kr.or.btf.web.domain.web.enums.TableNmType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommonCommentRepository extends JpaRepository<CommonComment, Long> {

    CommonComment findByParntsComtPidAndDataPidAndRegPsIdAndDelAt(Long id, Long parntsComtPid,String regPsId, String delAt);
    CommonComment findByRegPsIdAndTableNmAndDataPidAndDelAt(String regPsId, TableNmType tableNmType, Long dataPid, String delAt);
    CommonComment findByTableNmAndDataPidAndDelAt(TableNmType tableNmType, Long dataPid, String delAt);

    Long countByRegPsIdAndDelAt(String regPsId, String delAt);
}
