package kr.or.btf.web.repository.web;

import kr.or.btf.web.web.form.MngMenuAuthForm;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Repository
public class  MngMenuAuthRepository {

    @PersistenceContext
    private EntityManager em;

    public void list(Long mberPid) {

        String qry = "select * from tbl_mng_menu_auth where mber_pid = " + mberPid;
        Query query = em.createNativeQuery(qry);
        query.executeUpdate();

        //em.persist(mngMenuAuth);
    }
    public void save(MngMenuAuthForm mngMenuAuthForm) {

        String qry = "insert into tbl_mng_menu_auth values (" + mngMenuAuthForm.getMenuPid()+ "," + mngMenuAuthForm.getMberPid() + ",'Y')";
        Query query = em.createNativeQuery(qry);
        query.executeUpdate();

    }
    public void delete(MngMenuAuthForm mngMenuAuthForm) {

        String qry = "delete from tbl_mng_menu_auth where menu_pid = " + mngMenuAuthForm.getMenuPid()+ " and mber_pid=" + mngMenuAuthForm.getMberPid();
        Query query = em.createNativeQuery(qry);
        query.executeUpdate();

    }

    public void deleteByMberPid(MngMenuAuthForm mngMenuAuthForm) {

        String qry = "delete from tbl_mng_menu_auth where mber_pid=" + mngMenuAuthForm.getMberPid();
        Query query = em.createNativeQuery(qry);
        query.executeUpdate();

    }

    public void saveToAll(MngMenuAuthForm mngMenuAuthForm) {
        String qry = "insert into tbl_mng_menu_auth select menu_pid, " + mngMenuAuthForm.getMberPid() + ",'Y' from tbl_mng_menu ";
        Query query = em.createNativeQuery(qry);
        query.executeUpdate();
    }
}
