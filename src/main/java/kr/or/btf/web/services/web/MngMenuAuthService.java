package kr.or.btf.web.services.web;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.btf.web.domain.web.MngMenuAuth;
import kr.or.btf.web.domain.web.QMngMenuAuth;
import kr.or.btf.web.repository.web.MngMenuAuthRepository;
import kr.or.btf.web.web.form.MngMenuAuthForm;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MngMenuAuthService extends _BaseService {

    private final JPAQueryFactory queryFactory;
    private final ModelMapper modelMapper;
    private final MngMenuAuthRepository mngMenuAuthRepository;

    public List<MngMenuAuth> list(MngMenuAuthForm mngMenuAuthForm) {
        QMngMenuAuth qMngMenuAuth = QMngMenuAuth.mngMenuAuth;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qMngMenuAuth.mberPid.eq(mngMenuAuthForm.getMberPid()));

        List<MngMenuAuth> list = queryFactory
                .select(Projections.fields(MngMenuAuth.class,
                        qMngMenuAuth.menuPid,
                        qMngMenuAuth.mberPid,
                        qMngMenuAuth.confmAt
                ))
                .from(qMngMenuAuth)
                .where(builder)
                .fetch();

        return list;
    }

    @Transactional
    public boolean insert(MngMenuAuthForm mngMenuAuthForm) {
        try{
            //MngMenuAuth mngMenuAuth = modelMapper.map(mngMenuAuthForm, MngMenuAuth.class);
            mngMenuAuthRepository.save(mngMenuAuthForm);
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Transactional
    public boolean delete(MngMenuAuthForm mngMenuAuthForm) {
        try{
            mngMenuAuthRepository.delete(mngMenuAuthForm);
            return true;
        }catch(Exception e){
            return false;
        }
    }

    @Transactional
    public boolean deleteAll(MngMenuAuthForm mngMenuAuthForm) {
        try{
            mngMenuAuthRepository.deleteByMberPid(mngMenuAuthForm);
            return true;
        }catch(Exception e){
            return false;
        }
    }

    @Transactional
    public boolean InsertToAll(MngMenuAuthForm mngMenuAuthForm) {
        try{
            //MngMenuAuth mngMenuAuth = modelMapper.map(mngMenuAuthForm, MngMenuAuth.class);
            mngMenuAuthRepository.saveToAll(mngMenuAuthForm);
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
