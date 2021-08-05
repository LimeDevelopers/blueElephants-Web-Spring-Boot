package kr.or.btf.web.services.web;

import kr.or.btf.web.domain.web.ExperienceTarget;
import kr.or.btf.web.repository.web.ExperienceTargetRepository;
import kr.or.btf.web.web.form.ExperienceTargetForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ExperienceTargetService extends _BaseService {

    private final ExperienceTargetRepository experienceTargetRepository;
    private final ModelMapper modelMapper;

    public List<ExperienceTarget> list(ExperienceTargetForm experienceTargetForm){
        return experienceTargetRepository.findAllByExprnPid(experienceTargetForm.getExprnPid());
    }

    @Transactional
    public void delete(ExperienceTargetForm experienceTargetForm) {
        experienceTargetRepository.deleteById(experienceTargetForm.getId());
    }

    /**
     * @param experienceTargetForm
     * @return
     */
    @Transactional
    public boolean insert(ExperienceTargetForm experienceTargetForm) {
        try {
            ExperienceTarget experienceTarget = modelMapper.map(experienceTargetForm, ExperienceTarget.class);
            experienceTargetRepository.save(experienceTarget);

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
