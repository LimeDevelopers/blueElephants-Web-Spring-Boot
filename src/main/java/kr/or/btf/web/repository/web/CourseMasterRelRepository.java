package kr.or.btf.web.repository.web;

import kr.or.btf.web.web.form.CourseMasterRelForm;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Repository
public class CourseMasterRelRepository {

    @PersistenceContext
    private EntityManager em;

    /*public void list(Long mberPid) {

        String qry = "select * from TBL_COURSE_MASTER where mber_pid = " + mberPid;
        Query query = em.createNativeQuery(qry);
        query.executeUpdate();

        //em.persist(mngMenuAuth);
    }*/

    public void saveByCrsPid(CourseMasterRelForm form) {

        String qry = "insert into tbl_course_master_rel values (" + form.getCrsMstPid()+ "," + form.getCrsPid() + "," + form.getSn() + ")";
        Query query = em.createNativeQuery(qry);
        query.executeUpdate();

    }

    public void deleteByCrsMstPidAndSn(CourseMasterRelForm form){
        String qry = "delete from tbl_course_master_rel where crs_mst_pid = "+ form.getCrsMstPid() +" and sn = " + form.getSn();
        Query query = em.createNativeQuery(qry);
        query.executeUpdate();
    }

    public Long countByCrsMstPid(Long crsMstPid) {
        String qry = "select count(*) from tbl_course_master_rel where crs_mst_pid = "+ crsMstPid;
        Query query = em.createNativeQuery(qry);
        return ((Number)query.getSingleResult()).longValue();
    }

    /*public void delete(CourseMasterForm form) {

        String qry = "delete from TBL_COURSE_MASTER where crs_mst_pid = " + form.getCrsMstPid();
        Query query = em.createNativeQuery(qry);
        query.executeUpdate();

    }*/

    /*public void deleteByMberPid(MngMenuAuthForm mngMenuAuthForm) {

        String qry = "delete from TBL_COURSE_MASTER where mber_pid=" + mngMenuAuthForm.getMberPid();
        Query query = em.createNativeQuery(qry);
        query.executeUpdate();

    }*/

    /*public void saveToAll(MngMenuAuthForm mngMenuAuthForm) {
        String qry = "insert into TBL_MNG_MENU_AUTH select menu_pid, " + mngMenuAuthForm.getMberPid() + ",'Y' from TBL_MNG_MENU ";
        Query query = em.createNativeQuery(qry);
        query.executeUpdate();
    }*/
}
