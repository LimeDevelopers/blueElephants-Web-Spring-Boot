package kr.or.btf.web.services.web;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.btf.web.domain.web.BoardTarget;
import kr.or.btf.web.repository.web.BoardTargetRepository;
import kr.or.btf.web.web.form.BoardTargetForm;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BoardTargetService extends _BaseService {

    private final JPAQueryFactory queryFactory;
    private final ModelMapper modelMapper;
    /*private final BoardDataRepository boardRepository;
    private final FileInfoRepository fileInfoRepository;*/
    private final BoardTargetRepository boardTargetRepository;

    public List<BoardTarget> listByDataPid (BoardTargetForm form) {
        List<BoardTarget> list = boardTargetRepository.findByDataPid(form.getDataPid());

        return list;
    }

    public BoardTarget load(Long id) {
        BoardTarget boardTarget = boardTargetRepository.findById(id).orElseGet(BoardTarget::new);

        return boardTarget;
    }

    @Transactional
    public void delete(BoardTargetForm boardTargetForm) {
        boardTargetRepository.deleteById(boardTargetForm.getId());
    }

    @Transactional
    public boolean insert(BoardTargetForm form) {
        try{
            BoardTarget boardTarget = modelMapper.map(form, BoardTarget.class);
            BoardTarget save = boardTargetRepository.save(boardTarget);

            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

/*    @Transactional
    public boolean update(BoardTargetForm form) {
        try{
            BoardTarget boardTarget = boardTargetRepository.findById(form.getId()).get();

            return true;
        }catch(Exception e){
            return false;
        }

    }*/
}
