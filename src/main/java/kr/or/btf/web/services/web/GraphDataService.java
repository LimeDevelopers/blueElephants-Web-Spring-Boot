package kr.or.btf.web.services.web;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.btf.web.domain.web.GraphData;
import kr.or.btf.web.repository.web.GraphDataRepository;
import kr.or.btf.web.web.form.GraphDataForm;
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
public class GraphDataService extends _BaseService {

    private final JPAQueryFactory queryFactory;
    private final GraphDataRepository graphDataRepository;
    private final ModelMapper modelMapper;

    public List<GraphData> list(Long graphPid) {
        return graphDataRepository.findAllByGraphPidOrderByColSno(graphPid);
    }

    @Transactional
    public boolean save(GraphDataForm graphDataForm) {
        try {
            if (graphDataForm == null) {
                return false;
            }
            Long[] dataPidArr = graphDataForm.getDataPidArr();
            String[] colNmArr = graphDataForm.getColNmArr();
            Float[] colValue1Arr = graphDataForm.getColValue1Arr();
            Float[] colValue2Arr = graphDataForm.getColValue2Arr();
            Float[] colValue3Arr = graphDataForm.getColValue3Arr();
            Integer[] colSnoArr = graphDataForm.getColSnoArr();

            if (dataPidArr == null || colNmArr == null || colValue1Arr == null || colValue2Arr == null || colValue3Arr == null || colSnoArr == null) {
                return false;
            }

            for (int i=0; i<dataPidArr.length; i++) {
                GraphData graphData = new GraphData();
                graphData.setGraphPid(graphDataForm.getGraphPid());
                graphData.setId(dataPidArr[i]);
                graphData.setColNm(colNmArr[i]);
                graphData.setColValue1(colValue1Arr[i]);
                graphData.setColValue2(colValue2Arr[i]);
                graphData.setColValue3(colValue3Arr[i]);
                graphData.setColSno(colSnoArr[i]);
                graphDataRepository.save(graphData);
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
