package kr.or.btf.web.services.web;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.btf.web.common.Constants;
import kr.or.btf.web.domain.web.CourseItem;
import kr.or.btf.web.domain.web.FileInfo;
import kr.or.btf.web.domain.web.QCourseItem;
import kr.or.btf.web.domain.web.dto.CourseItemDto;
import kr.or.btf.web.domain.web.enums.ContentsDvType;
import kr.or.btf.web.domain.web.enums.FileDvType;
import kr.or.btf.web.domain.web.enums.TableNmType;
import kr.or.btf.web.repository.web.CourseItemRepository;
import kr.or.btf.web.repository.web.FileInfoRepository;
import kr.or.btf.web.utils.FileUtilHelper;
import kr.or.btf.web.web.form.CourseItemForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CourseItemService extends _BaseService {

    private final JPAQueryFactory queryFactory;
    private final CourseItemRepository courseItemRepository;
    private final FileInfoRepository fileInfoRepository;
    private final ModelMapper modelMapper;

    public List<CourseItem> list(CourseItemForm courseItemForm) {

        QCourseItem qCourseItem = QCourseItem.courseItem;
        OrderSpecifier<Integer> orderSpecifier = qCourseItem.sno.desc();
        if (courseItemForm.getSorting() != null && !courseItemForm.getSorting().isEmpty()) {
            if (courseItemForm.getSorting().equals("my")) {
                orderSpecifier = qCourseItem.sno.asc();
            }
        }
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qCourseItem.crsPid.eq(courseItemForm.getCrsPid()));

        List<CourseItem> courseItemList = queryFactory
                .select(Projections.fields(CourseItem.class,
                        qCourseItem.id,
                        qCourseItem.ttl,
                        qCourseItem.cn,
                        qCourseItem.imgFl,
                        qCourseItem.cntntsUrl,
                        qCourseItem.cntntsLen,
                        qCourseItem.sno,
                        qCourseItem.crsPid
                ))
                .from(qCourseItem)
                .where(builder)
                .orderBy(orderSpecifier)
                .fetch();

        return courseItemList;
    }

    public List<CourseItemDto> listForProcNm(Long crsPid, Long mberPid) {

        List<Object[]> tmpList = courseItemRepository.listForProcNm(crsPid, mberPid);
        List<CourseItemDto> rtnList = new ArrayList<>();
        if (tmpList != null) {
            for (Object[] vo : tmpList) {
                CourseItemDto courseItemDto = new CourseItemDto();
                courseItemDto.setId(((BigInteger)vo[0]).longValue());
                courseItemDto.setTtl((String)vo[1]);
                courseItemDto.setCn((String)vo[2]);
                courseItemDto.setImgFl((String)vo[3]);
                courseItemDto.setCntntsDvTy(ContentsDvType.valueOf((String)vo[4]));
                courseItemDto.setCntntsUrl((String)vo[5]);
                courseItemDto.setCntntsLen(((BigInteger)vo[6]).longValue());
                courseItemDto.setSno((Integer)vo[7]);
                courseItemDto.setCrsPid(((BigInteger)vo[8]).longValue());
                courseItemDto.setProcNm((String)vo[9]);
                rtnList.add(courseItemDto);
            }
        }

        return rtnList;
    }

    public CourseItem load(Long id) {
        CourseItem courseItem = courseItemRepository.findById(id).orElseGet(CourseItem::new);

        return courseItem;
    }

    @Transactional
    public void delete(CourseItemForm courseItemForm) {
        courseItemRepository.deleteById(courseItemForm.getId());
    }

    /**
     * @param courseItemForm
     * @return
     */
    @Transactional
    public boolean insert(CourseItemForm courseItemForm, MultipartFile attachImgFl, MultipartFile[] attachedFile) {
        try {

            if (attachImgFl.isEmpty() == false) {
                FileInfo fileInfo = FileUtilHelper.writeUploadedFile(attachImgFl, Constants.FOLDERNAME_COURSEITEM);
                if (fileInfo.getFlNm() != null) {
                    courseItemForm.setImgFl(fileInfo.getFlNm());
                }
            }

            CourseItem courseItem = modelMapper.map(courseItemForm, CourseItem.class);
            CourseItem save = courseItemRepository.save(courseItem);

            if (attachedFile.length > 0) {
                for (MultipartFile multipartFile : attachedFile) {
                    TableNmType tblCouseItem = TableNmType.TBL_COUSE_ITEM;
                    FileInfo file = FileUtilHelper.writeUploadedFile(multipartFile, Constants.FOLDERNAME_COURSEITEM);
                    if (file != null) {
                        file.setDataPid(save.getId());
                        file.setTableNm(tblCouseItem.name());
                        file.setDvTy(FileDvType.ATTACH.name());
                        fileInfoRepository.save(file);
                    }
                }
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public boolean update(CourseItemForm courseItemForm, MultipartFile attachImgFl, MultipartFile[] attachedFile) {

        try {

            FileInfo fileInfo = new FileInfo();
            if (attachImgFl.isEmpty() == false) {
                fileInfo = FileUtilHelper.writeUploadedFile(attachImgFl, Constants.FOLDERNAME_COURSEITEM);
            }

            CourseItem item = courseItemRepository.findById(courseItemForm.getId()).orElseGet(CourseItem::new);
            item.setTtl(courseItemForm.getTtl());
            item.setCn(courseItemForm.getCn());
            if (fileInfo.getFlNm() != null) {
                item.setImgFl(fileInfo.getFlNm());
            }
            item.setCntntsDvTy(courseItemForm.getCntntsDvTy());
            item.setCntntsUrl(courseItemForm.getCntntsUrl());
            item.setCntntsLen(courseItemForm.getCntntsLen());
            item.setSno(courseItemForm.getSno());
            //course.setCrsPid(courseForm.getCrsPid());

            if (attachedFile.length > 0) {
                for (MultipartFile multipartFile : attachedFile) {
                    if(multipartFile.isEmpty() == false) {
                        TableNmType tblCouseItem = TableNmType.TBL_COUSE_ITEM;
                        FileInfo file = FileUtilHelper.writeUploadedFile(multipartFile, Constants.FOLDERNAME_COURSEITEM);
                        if (file != null) {
                            file.setDataPid(item.getId());
                            file.setTableNm(tblCouseItem.name());
                            file.setDvTy(FileDvType.ATTACH.name());
                            fileInfoRepository.save(file);
                        }
                    }
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
