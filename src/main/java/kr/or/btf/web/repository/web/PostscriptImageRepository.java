package kr.or.btf.web.repository.web;

import kr.or.btf.web.domain.web.PostscriptImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostscriptImageRepository extends JpaRepository<PostscriptImage, Long> {

    List<PostscriptImage> findAllByPostscriptPid(Long postscriptPid);

    void deleteByFlPid(Long flPid);
}

