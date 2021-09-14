package kr.or.btf.web.services.web;

import kr.or.btf.web.common.Constants;
import kr.or.btf.web.common.annotation.CurrentUser;
import kr.or.btf.web.common.exceptions.ValidCustomException;
import kr.or.btf.web.domain.web.Account;
import kr.or.btf.web.domain.web.ActivityApplication;
import kr.or.btf.web.domain.web.FileInfo;
import kr.or.btf.web.domain.web.enums.AppRollType;
import kr.or.btf.web.domain.web.enums.FileDvType;
import kr.or.btf.web.domain.web.enums.TableNmType;
import kr.or.btf.web.repository.web.ApplicationRepository;
import kr.or.btf.web.repository.web.FileInfoRepository;
import kr.or.btf.web.repository.web.MemberRepository;
import kr.or.btf.web.utils.FileUtilHelper;
import kr.or.btf.web.web.form.ApplicationForm;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.yaml.snakeyaml.scanner.Constant;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@Transactional(readOnly = false)
@RequiredArgsConstructor
public class ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final FileInfoRepository fileInfoRepository;
    private final ModelMapper modelMapper;


    //파트너스 신청
    public boolean partnersRegister(ApplicationForm applicationForm, MultipartFile attachedFile) throws Exception {
        if(applicationForm.getMberPid() == null) {
          applicationForm.setMberPid(0L);
        }

        applicationForm.setApproval("N");
        applicationForm.setDelAt("N");
        applicationForm.setRegDtm(LocalDateTime.now());
        applicationForm.setUpdDtm(LocalDateTime.now());
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
        applicationForm.setUpdDtm(LocalDateTime.now());
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
        applicationForm.setUpdDtm(LocalDateTime.now());
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
