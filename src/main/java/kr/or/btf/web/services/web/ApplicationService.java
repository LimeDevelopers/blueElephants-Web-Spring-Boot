package kr.or.btf.web.services.web;

import kr.or.btf.web.domain.web.ActivityApplication;
import kr.or.btf.web.domain.web.FileInfo;
import kr.or.btf.web.domain.web.enums.AppRollType;
import kr.or.btf.web.repository.web.ApplicationRepository;
import kr.or.btf.web.repository.web.FileInfoRepository;
import kr.or.btf.web.repository.web.MemberRepository;
import kr.or.btf.web.web.form.ApplicationForm;
import kr.or.btf.web.web.form.FileInfoForm;
import kr.or.btf.web.web.form.MemberForm;
import lombok.RequiredArgsConstructor;
import org.hibernate.procedure.spi.ParameterRegistrationImplementor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = false)
@RequiredArgsConstructor
public class ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final FileInfoRepository fileInfoRepository;
    private final MemberRepository memberRepository;

    public void partnersRegister(ApplicationForm applicationForm){

        ActivityApplication activityApplication = new ActivityApplication();
        FileInfoForm fileInfoForm = new FileInfoForm();
        //폼에서 받아온 데이터
        activityApplication.setNm(applicationForm.getNm());//신청자 이름
        activityApplication.setMoblphon(applicationForm.getMoblphon()); //폰번호
        activityApplication.setAffi(applicationForm.getAffi()); //소속명
        //우리가 넣어줘야 하는 데이터

        activityApplication.setFlPid(fileInfoForm.getId()); //file pid
        System.out.println("파일pid : " + fileInfoForm.getId());

        activityApplication.setId(applicationForm.getAppPid());// application pid
        System.out.println("apppid : " + applicationForm.getAppPid());


        activityApplication.setRegDtm(LocalDateTime.now()); //등록일시
        System.out.println("등록일시 : " + LocalDateTime.now());

        activityApplication.setApproval("N"); //승인 안됨

        activityApplication.setAppDvTy(AppRollType.PARTNERS); //*//*

        //activityapplication에 applicationForm의 데이터를 넣어준 다음에
        //applicationRepository에 있는 save(SQL의 insert)를 통해서 activityApplication의 데이터를 넣어주었다

        applicationRepository.save(activityApplication);
    }
}
