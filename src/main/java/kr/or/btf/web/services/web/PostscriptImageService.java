package kr.or.btf.web.services.web;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.btf.web.domain.web.PostscriptImage;
import kr.or.btf.web.domain.web.QFileInfo;
import kr.or.btf.web.domain.web.QPostscriptImage;
import kr.or.btf.web.repository.web.PostscriptImageRepository;
import kr.or.btf.web.web.form.PostscriptImageForm;
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
public class PostscriptImageService {
    private final JPAQueryFactory queryFactory;
    private final ModelMapper modelMapper;
    private final PostscriptImageRepository postscriptImageRepository;

    public List<PostscriptImage> list(PostscriptImageForm postscriptImageForm) {

        QPostscriptImage qPostscriptImage = QPostscriptImage.postscriptImage;
        QFileInfo qFileInfo = QFileInfo.fileInfo;

        OrderSpecifier<Integer> orderSpecifier = qPostscriptImage.sn.asc();

        BooleanBuilder builder = new BooleanBuilder();


        if (postscriptImageForm.getPostscriptPid() != null ) {
            builder.and(qPostscriptImage.postscriptPid.eq(postscriptImageForm.getPostscriptPid()));
        }

        List<PostscriptImage> mngList = queryFactory
                .select(Projections.fields(PostscriptImage.class,
                        qPostscriptImage.id,
                        qPostscriptImage.flPid,
                        qPostscriptImage.dsc,
                        qPostscriptImage.sn,
                        qPostscriptImage.postscriptPid,
                        qFileInfo.chgFlNm.as("flNm")
                ))
                .from(qPostscriptImage)
                .innerJoin(qFileInfo).on(qPostscriptImage.flPid.eq(qFileInfo.id))
                .where(builder)
                .orderBy(orderSpecifier)
                .fetch();

        return mngList;
    }

    public PostscriptImage loadByForm(PostscriptImageForm form) {

        QPostscriptImage qPostscriptImage = QPostscriptImage.postscriptImage;

        BooleanBuilder builder = new BooleanBuilder();

        if (form.getPostscriptPid() != null) {
            builder.and(qPostscriptImage.postscriptPid.eq(form.getPostscriptPid()));
        }

        List<PostscriptImage> list = queryFactory
                .select(Projections.fields(PostscriptImage.class,
                        qPostscriptImage.id,
                        qPostscriptImage.flPid,
                        qPostscriptImage.dsc,
                        qPostscriptImage.sn
                ))
                .from(qPostscriptImage)
                .where(builder)
                .fetch();

        return (list != null && list.size() > 0 ? list.get(0) : new PostscriptImage());

    }

    @Transactional
    public boolean deleteByFlPid(PostscriptImageForm form) {
        try {
            postscriptImageRepository.deleteByFlPid(form.getFlPid());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
