package kr.or.btf.web.services.web;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.btf.web.domain.web.CourseHis;
import kr.or.btf.web.domain.web.CourseItem;
import kr.or.btf.web.domain.web.CourseRequest;
import kr.or.btf.web.domain.web.dto.CourseItemDto;
import kr.or.btf.web.domain.web.enums.CompleteStatusType;
import kr.or.btf.web.repository.web.CourseHisRepository;
import kr.or.btf.web.repository.web.CourseItemRepository;
import kr.or.btf.web.web.form.CourseHisForm;
import kr.or.btf.web.web.form.CourseItemForm;
import kr.or.btf.web.web.form.CourseRequestCompleteForm;
import kr.or.btf.web.web.form.CourseRequestForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CourseHisService extends _BaseService {

    private final JPAQueryFactory queryFactory;
    private final CourseHisRepository courseHisRepository;
    private final CourseItemRepository courseItemRepository;
    private final CourseRequestService courseRequestService;
    private final CourseRequestCompleteService courseRequestCompleteService;
    private final CourseItemService courseItemService;

    private final ModelMapper modelMapper;

    public CourseHis findTop1MberPidAndCrssqPidOrderByAtnlcHourDescAtnlcDtmDesc(Long mberPid, Long crssqPid) {
        CourseHis courseHis = courseHisRepository.findTopByMberPidAndCrssqPidOrderByAtnlcHourDescAtnlcDtmDesc(mberPid, crssqPid).orElse(null);

        return courseHis;
    }

    @Transactional
    public boolean save(CourseHisForm courseHisForm) {
        try {
            CourseHis courseHis = modelMapper.map(courseHisForm, CourseHis.class);
            courseHisRepository.save(courseHis);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public boolean videoProgressProc(CourseHisForm courseHisForm) {
        try {
            CourseHis courseHis = modelMapper.map(courseHisForm, CourseHis.class);

            CourseHis maxOne = courseHisRepository.findTopByMberPidAndCrssqPidOrderByAtnlcHourDescAtnlcDtmDesc(courseHis.getMberPid(), courseHis.getCrssqPid()).orElse(null);

            CourseHis saved = new CourseHis();
            if (courseHis.getCntntsLen() > 0L) {
                if (maxOne == null) {
                    if (courseHisForm.getAtnlcHour() == 0L) {
                        saved = courseHisRepository.save(courseHis);

                        CourseItem courseItem = courseItemRepository.findById(courseHis.getCrssqPid()).orElse(null);
                        if (courseItem != null && courseItem.getCntntsLen() == null) {
                            courseItem.setCntntsLen(courseHisForm.getCntntsLen());
                        }
                    }
                } else {
                    if (courseHis.getAtnlcHour() > maxOne.getAtnlcHour()) {
                        saved = courseHisRepository.save(courseHis);
                    }
                }

                boolean crssqDone = true;
                Long procPer = saved.getAtnlcHour() != 0 && saved.getCntntsLen() != null && saved.getCntntsLen() > 0L ? saved.getAtnlcHour() / saved.getCntntsLen() * 100 : 0;
                
                CourseRequestForm courseRequestForm = new CourseRequestForm();
                courseRequestForm.setCrsMstPid(courseHisForm.getCrsMstPid());
                courseRequestForm.setMberPid(courseHisForm.getMberPid());
                CourseRequest courseRequest = courseRequestService.loadByform(courseRequestForm);
                    
                //차시 완료 체크
                CourseItemForm courseItemForm = new CourseItemForm();
                courseItemForm.setCrsPid(courseHisForm.getCrsPid());
                log.info("sErVicE ====== 2");
                List<CourseItemDto> courseItems = courseItemService.listForProcNm(courseItemForm.getCrsPid(), courseHisForm.getMberPid());
                for (CourseItemDto courseItem : courseItems) {
                    if ((courseItem.getProcNm() == null) || (courseItem.getProcNm() != null && CompleteStatusType.COMPLETE.equals(courseItem.getProcNm()))) {
                        crssqDone = false;
                        break;
                    }
                }

                if (crssqDone && procPer >= 100) { // 진도율 완료처리
                    if (courseRequest != null && courseRequest.getId() != null) {

                        CourseRequestCompleteForm courseRequestCompleteForm = new CourseRequestCompleteForm();
                        //ReqeustId Get
                        courseRequestCompleteForm.setAtnlcReqPid(courseRequest.getId());
                        //CrsMstPid Get
                        courseRequestCompleteForm.setCrsMstPid(courseHisForm.getCrsMstPid());
                        //CrsPid Get
                        courseRequestCompleteForm.setCrsPid(courseHisForm.getCrsPid());
                        //Sn Get
                        courseRequestCompleteForm.setSn(courseHisForm.getSn());
                        courseRequestCompleteForm.setCmplPrsDtm(LocalDateTime.now());
                        log.info("crssqDone ======= " + crssqDone);
                        log.info("procPer ======= " + procPer);
                        log.info("sErVicE ====== 1");
                        //update ReqComple Form , set CompleStt.Complete
                        courseRequestCompleteService.updateSttTy(courseRequestCompleteForm, CompleteStatusType.COMPLETE);

                    }
                }
            } else {
                return false;
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
