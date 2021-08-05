package kr.or.btf.web.services.web;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.btf.web.domain.web.GraphMaster;
import kr.or.btf.web.domain.web.enums.GraphDvType;
import kr.or.btf.web.repository.web.GraphMasterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GraphMasterService extends _BaseService {

    private final JPAQueryFactory queryFactory;
    private final GraphMasterRepository graphMasterRepository;
    private final ModelMapper modelMapper;

    public GraphMaster load(Long id) {
        return graphMasterRepository.findById(id).orElseGet(GraphMaster::new);
    }

    public GraphMaster loadByGraphDvTy(GraphDvType graphDvTy) {
        return graphMasterRepository.findByGraphDvTy(graphDvTy);
    }
}
