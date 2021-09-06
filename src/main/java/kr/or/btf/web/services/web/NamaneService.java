package kr.or.btf.web.services.web;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.btf.web.common.aurora.AuroraForm;
import kr.or.btf.web.domain.web.Banner;
import kr.or.btf.web.domain.web.MemberRoll;
import kr.or.btf.web.domain.web.NamaneTemp;
import kr.or.btf.web.repository.web.NamaneTempRepository;
import kr.or.btf.web.web.form.NamaneForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NamaneService extends _BaseService {

    private final JPAQueryFactory queryFactory;
    private final NamaneTempRepository namaneTempRepository;
    private final ModelMapper modelMapper;

    public List<NamaneTemp> get(Long pid) {
        List<NamaneTemp> tempList = namaneTempRepository.findAllByMberPid(pid);
        return tempList;
    }

    public boolean set(AuroraForm auroraForm, Long id) {
        NamaneTemp namaneTemp = modelMapper.map(auroraForm, NamaneTemp.class);
        namaneTemp.setMberPid(id);
        namaneTemp.setRegDtm(LocalDateTime.now());
        namaneTemp.setYn("N");
        NamaneTemp result = namaneTempRepository.save(namaneTemp);
        if(result.getId()!=null) {
            return true;
        } else {
            return false;
        }
    }
}
