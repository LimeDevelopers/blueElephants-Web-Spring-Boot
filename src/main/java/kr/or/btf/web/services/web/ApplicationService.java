package kr.or.btf.web.services.web;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;
import kr.or.btf.web.common.Constants;
import kr.or.btf.web.domain.web.*;
import kr.or.btf.web.domain.web.QPrevention;
import kr.or.btf.web.domain.web.enums.AppRollType;
import kr.or.btf.web.domain.web.enums.CompleteStatusType;
import kr.or.btf.web.domain.web.enums.FileDvType;
import kr.or.btf.web.domain.web.enums.TableNmType;
import kr.or.btf.web.repository.web.ApplicationRepository;
import kr.or.btf.web.repository.web.FileInfoRepository;
import kr.or.btf.web.repository.web.PreventionMasterRepository;
import kr.or.btf.web.repository.web.PreventionRepository;
import kr.or.btf.web.utils.FileUtilHelper;
import kr.or.btf.web.web.form.ApplicationForm;
import kr.or.btf.web.web.form.PreventionMasterForm;
import kr.or.btf.web.web.form.SearchForm;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = false)
@RequiredArgsConstructor
public class ApplicationService extends _BaseService {
    private final PreventionRepository preventionRepository;
    private final PreventionMasterRepository preventionMasterRepository;
    private final ApplicationRepository applicationRepository;
    private final FileInfoRepository fileInfoRepository;
    private final ModelMapper modelMapper;
    private final JPAQueryFactory queryFactory;

    public PreventionMaster getPreEduMst(Long prePid, Long mberPid) {
        return preventionMasterRepository.findByPrePidAndMberPid(prePid,mberPid);
    }

    public Page<PreventionMaster> getPreEduMstList(Pageable pageable, Long id) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, Constants.DEFAULT_PAGESIZE); // <- Sort 추가

        QPreventionMaster qPreventionMaster = QPreventionMaster.preventionMaster;
        OrderSpecifier<Long> orderSpecifier = qPreventionMaster.id.desc();

        QueryResults<PreventionMaster> mngList = queryFactory
                .select(Projections.fields(PreventionMaster.class,
                        qPreventionMaster.id,
                        qPreventionMaster.address,
                        qPreventionMaster.approval,
                        qPreventionMaster.mberPid,
                        qPreventionMaster.classesNum,
                        qPreventionMaster.schlNm,
                        qPreventionMaster.tempSave,
                        qPreventionMaster.personnel,
                        qPreventionMaster.prePid,
                        qPreventionMaster.regDtm,
                        qPreventionMaster.updDtm

                ))
                .from(qPreventionMaster)
                .where(qPreventionMaster.mberPid.eq(id))
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(orderSpecifier)
                .fetchResults();

        return new PageImpl<>(mngList.getResults(), pageable, mngList.getTotal());
    }

    public boolean registerPreEdu(PreventionMasterForm preventionMasterForm) {
        preventionMasterForm.setApproval("N");
        preventionMasterForm.setDelAt("N");
        preventionMasterForm.setRegDtm(LocalDateTime.now());
        PreventionMaster preventionMaster = modelMapper.map(preventionMasterForm, PreventionMaster.class);
        PreventionMaster save = preventionMasterRepository.save(preventionMaster);
        if(save.getId()!=null){
            return true;
        } else {
            return false;
        }
    }

    public boolean updatePreEdu(PreventionMasterForm preventionMasterForm) {
        if(preventionMasterForm.getApproval() == null) {
            preventionMasterForm.setApproval("N");
        }
        if(preventionMasterForm.getDelAt() == null) {
            preventionMasterForm.setApproval("N");
        }
        try {
            PreventionMaster pre = preventionMasterRepository.findById(preventionMasterForm.getPreMstPid()).orElseGet(PreventionMaster::new);
            pre.setAddress(preventionMasterForm.getAddress());
            pre.setClassesNum(preventionMasterForm.getClassesNum());
            pre.setTel(preventionMasterForm.getTel());
            pre.setResultQna1(preventionMasterForm.getResultQna1());
            pre.setResultQna2(preventionMasterForm.getResultQna2());
            pre.setResultQna3(preventionMasterForm.getResultQna3());
            pre.setResultQna4(preventionMasterForm.getResultQna4());
            pre.setResultQna5(preventionMasterForm.getResultQna5());
            pre.setHpSchd1Personnel(preventionMasterForm.getHpSchd1Personnel());
            pre.setHpSchd1Wt(preventionMasterForm.getHpSchd1Wt());
            pre.setHpSchd1Et(preventionMasterForm.getHpSchd1Et());
            pre.setHpSchd2Personnel(preventionMasterForm.getHpSchd2Personnel());
            pre.setHpSchd2Wt(preventionMasterForm.getHpSchd2Wt());
            pre.setHpSchd2Et(preventionMasterForm.getHpSchd2Et());
            pre.setUpdDtm(LocalDateTime.now());
            pre.setApproval(preventionMasterForm.getApproval());
            pre.setDelAt(preventionMasterForm.getDelAt());
            pre.setTempSave(preventionMasterForm.getTempSave());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Page<Prevention> getMyPreEduList(Pageable pageable,
                                          Long id) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, Constants.DEFAULT_PAGESIZE); // <- Sort 추가
        QPrevention qPrevention = QPrevention.prevention;
        QPreventionMaster qPreventionMaster = QPreventionMaster.preventionMaster;
        OrderSpecifier<Long> orderSpecifier = qPrevention.id.desc();
        JPAQuery<Prevention> list = queryFactory
                .select(Projections.fields(Prevention.class,
                        qPrevention.id,
                        qPrevention.schlNm,
                        qPrevention.address,
                        qPrevention.tel,
                        qPrevention.regDtm,
                        qPrevention.delAt,
                        qPrevention.approval,
                        ExpressionUtils.as(
                                JPAExpressions.select(qPreventionMaster.id.count())
                                        .from(qPreventionMaster)
                                        .where(qPreventionMaster.prePid.eq(qPrevention.id)),
                                "mstCnt")
                ))
                .from(qPrevention)
                .where(qPrevention.mberPid.eq(id))
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset());
        list.orderBy(orderSpecifier);
        QueryResults<Prevention> mngList = list.fetchResults();
        return new PageImpl<>(mngList.getResults(), pageable, mngList.getTotal());
    }
    /**
     * 예방교육 리스트
     * @author : jerry
     * @version : 1.0.0
     * 작성일 : 2021/09/15
    **/
    public Page<Prevention> getPreEduList(Pageable pageable,
                                          SearchForm searchForm) {
        if(searchForm.getSrchWord() == null || searchForm.getSrchWord().equals("")){
            searchForm.setSrchWord("");
        }
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, (searchForm.getPageSize() == null ? Constants.DEFAULT_PAGESIZE : searchForm.getPageSize()));
        QPrevention qPrevention = QPrevention.prevention;
        OrderSpecifier<Long> orderSpecifier = qPrevention.id.desc();
        JPAQuery<Prevention> list = queryFactory
                .select(Projections.fields(Prevention.class,
                        qPrevention.id,
                        qPrevention.schlNm,
                        qPrevention.address,
                        qPrevention.tel,
                        qPrevention.regDtm,
                        qPrevention.delAt,
                        qPrevention.approval))
                .from(qPrevention)
                .where(qPrevention.schlNm.contains(searchForm.getSrchWord())
                        .or(qPrevention.address.contains(searchForm.getSrchWord()))
                        .or(qPrevention.tel.contains(searchForm.getSrchWord())))
                .where()
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset());
                list.orderBy(orderSpecifier);
        QueryResults<Prevention> mngList = list.fetchResults();
        return new PageImpl<>(mngList.getResults(), pageable, mngList.getTotal());
    }

    public Prevention getPreEdu(Long id) {
        return preventionRepository.findByIdAndDelAt(id,"N");
    }

    //파트너스 신청
    public boolean partnersRegister(ApplicationForm applicationForm, MultipartFile attachedFile) throws Exception {
        if(applicationForm.getMberPid() == null) {
          applicationForm.setMberPid(0L);
        }

        applicationForm.setApproval("N");
        applicationForm.setDelAt("N");
        applicationForm.setRegDtm(LocalDateTime.now());
        applicationForm.setAppDvTy(AppRollType.PARTNERS);

        //첨부파일이 있을 때
        if (attachedFile != null && !attachedFile.isEmpty()) {
            ActivityApplication application = modelMapper.map(applicationForm, ActivityApplication.class);
            ActivityApplication save = applicationRepository.save(application);
            application.setId(save.getId());
            FileInfo fileInfo = new FileInfo();
            try {
                fileInfo = FileUtilHelper.writeUploadedFile(attachedFile, Constants.FOLDERNAME_LICENSE , FileUtilHelper.imageExt);
            } catch (IOException e) {
                e.setStackTrace(e.getStackTrace());
            }
            fileInfo.setDataPid(save.getId());
            TableNmType tblApplication = TableNmType.TBL_APPLICATION;
            fileInfo.setTableNm(tblApplication.name());
            fileInfo.setDvTy(FileDvType.LICENSE.name());
            FileInfo pid = fileInfoRepository.save(fileInfo);
            applicationRepository.updateFlPid(pid.getId(), save.getId());
            applicationRepository.save(application);
        //첨부파일 없을 때
        } else {
            ActivityApplication application = modelMapper.map(applicationForm, ActivityApplication.class);
            ActivityApplication save = applicationRepository.save(application);
            application.setId(save.getId());

            applicationRepository.save(application);
        }
        return true;
    }

    //지지크루 신청
    public boolean zzcrewRegister(ApplicationForm applicationForm, MultipartFile attachedFile) throws Exception {
        if(applicationForm.getMberPid() == null) {
            applicationForm.setMberPid(0L);
        }

        applicationForm.setApproval("N");
        applicationForm.setDelAt("N");
        applicationForm.setRegDtm(LocalDateTime.now());
        applicationForm.setAppDvTy(AppRollType.CREW);

        //첨부파일이 있을 때
        if (attachedFile != null && !attachedFile.isEmpty()) {
            ActivityApplication application = modelMapper.map(applicationForm, ActivityApplication.class);
            ActivityApplication save = applicationRepository.save(application);
            application.setId(save.getId());
            FileInfo fileInfo = new FileInfo();
            try {
                fileInfo = FileUtilHelper.writeUploadedFile(attachedFile, Constants.FOLDERNAME_LICENSE , FileUtilHelper.imageExt);
            } catch (IOException e) {
                e.setStackTrace(e.getStackTrace());
            }
            fileInfo.setDataPid(save.getId());
            TableNmType tblApplication = TableNmType.TBL_APPLICATION;
            fileInfo.setTableNm(tblApplication.name());
            fileInfo.setDvTy(FileDvType.LICENSE.name());
            FileInfo pid = fileInfoRepository.save(fileInfo);
            applicationRepository.updateFlPid(pid.getId(), save.getId());
            applicationRepository.save(application);
            //첨부파일 없을 때
        } else {
            ActivityApplication application = modelMapper.map(applicationForm, ActivityApplication.class);
            ActivityApplication save = applicationRepository.save(application);
            application.setId(save.getId());

            applicationRepository.save(application);
        }
        return true;
    }
    public boolean zzdeclareRegister(ApplicationForm applicationForm, MultipartFile attachedFile) throws Exception {
        if(applicationForm.getMberPid() == null) {
            applicationForm.setMberPid(0L);
        }
        applicationForm.setApproval("N");
        applicationForm.setDelAt("N");
        applicationForm.setRegDtm(LocalDateTime.now());
        applicationForm.setAppDvTy(AppRollType.DECLARE);

        //첨부파일이 있을 때
        if (attachedFile != null && !attachedFile.isEmpty()) {
            ActivityApplication application = modelMapper.map(applicationForm, ActivityApplication.class);
            ActivityApplication save = applicationRepository.save(application);
            application.setId(save.getId());
            FileInfo fileInfo = new FileInfo();
            try {
                fileInfo = FileUtilHelper.writeUploadedFile(attachedFile, Constants.FOLDERNAME_LICENSE , FileUtilHelper.imageExt);
            } catch (IOException e) {
                e.setStackTrace(e.getStackTrace());
            }
            fileInfo.setDataPid(save.getId());
            TableNmType tblApplication = TableNmType.TBL_APPLICATION;
            fileInfo.setTableNm(tblApplication.name());
            fileInfo.setDvTy(FileDvType.LICENSE.name());
            FileInfo pid = fileInfoRepository.save(fileInfo);
            applicationRepository.updateFlPid(pid.getId(), save.getId());
            applicationRepository.save(application);
            //첨부파일 없을 때
        } else {
            ActivityApplication application = modelMapper.map(applicationForm, ActivityApplication.class);
            ActivityApplication save = applicationRepository.save(application);
            application.setId(save.getId());

            applicationRepository.save(application);
        }
        return true;
    }
    public Boolean updateApproval(String pid) {
        Long chgStr = Long.parseLong(pid);
        int rs = applicationRepository.setApproval(chgStr,"Y");
        if(rs > 0) {
            return true;
        } else {
            return false;
        }
    }
}
