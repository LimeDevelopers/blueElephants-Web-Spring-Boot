package kr.or.btf.web.services.web;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.btf.web.repository.web.MemberRollRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberRollService extends _BaseService {

    private final JPAQueryFactory queryFactory;
    private final MemberRollRepository memberRollRepository;
    private final ModelMapper modelMapper;

}
