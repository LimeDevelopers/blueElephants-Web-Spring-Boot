package kr.or.btf.web.services.web;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.btf.web.domain.web.CertificationHis;
import kr.or.btf.web.repository.web.CertificationHisRepository;
import kr.or.btf.web.web.form.CertificationHisForm;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CertificationHisService extends _BaseService {

    private final JPAQueryFactory queryFactory;
    private final ModelMapper modelMapper;
    private final CertificationHisRepository certificationHisRepository;

    @Transactional
    public boolean insert(CertificationHisForm form) {
        try{
            CertificationHis certificationHis = modelMapper.map(form, CertificationHis.class);
            certificationHisRepository.save(certificationHis);
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
