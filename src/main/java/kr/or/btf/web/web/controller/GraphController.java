package kr.or.btf.web.web.controller;

import kr.or.btf.web.domain.web.GraphData;
import kr.or.btf.web.domain.web.GraphMaster;
import kr.or.btf.web.domain.web.enums.GraphDvType;
import kr.or.btf.web.services.web.GraphDataService;
import kr.or.btf.web.services.web.GraphMasterService;
import kr.or.btf.web.web.form.GraphDataForm;
import kr.or.btf.web.web.form.GraphMasterForm;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class GraphController extends BaseCont {

    private final GraphMasterService graphMasterService;
    private final GraphDataService graphDataService;

    @RequestMapping("/soulGod/graph/detail")
    public String list(Model model) {

        GraphMaster graphMaster = graphMasterService.loadByGraphDvTy(GraphDvType.MAIN_CYBER_BAR_CHART);
        model.addAttribute("form", graphMaster);

        List<GraphData> graphDataList = new ArrayList<>();
        if (graphMaster != null && graphMaster.getId() != null) {
            graphDataList = graphDataService.list(graphMaster.getId());
        }
        model.addAttribute("graphDataList", graphDataList);

        model.addAttribute("mc", "graph");
        return "/soulGod/graph/detail";
    }

    @RequestMapping("/soulGod/graph/modify")
    public String modify(Model model) {

        GraphMaster graphMaster = graphMasterService.loadByGraphDvTy(GraphDvType.MAIN_CYBER_BAR_CHART);
        model.addAttribute("form", graphMaster);

        List<GraphData> graphDataList = new ArrayList<>();
        if (graphMaster != null && graphMaster.getId() != null) {
            graphDataList = graphDataService.list(graphMaster.getId());
        }
        model.addAttribute("graphDataList", graphDataList);

        model.addAttribute("mc", "graph");
        return "/soulGod/graph/modify";
    }

    @PostMapping("/soulGod/graph/save")
    public String save(Model model,
                       @ModelAttribute GraphMasterForm graphMasterForm,
                       @ModelAttribute GraphDataForm graphDataForm) {

        graphDataForm.setGraphPid(graphMasterForm.getId());
        boolean save = graphDataService.save(graphDataForm);

        if (save) {
            model.addAttribute("mc", "graph");
            return "redirect:/soulGod/graph/detail";
        } else {
            model.addAttribute("altmsg", "실패했습니다.");
            model.addAttribute("locurl", "/soulGod/graph/modify");
            return "/message";
        }
    }

    @ResponseBody
    @PostMapping("/api/openData/graph/load/{id}")
    public String apiLoad(Model model,
                          @PathVariable(name = "id") Long id) throws JSONException {
        GraphMaster load = graphMasterService.load(id);
        if (load != null) {
            JSONObject rtnObj = new JSONObject();
            rtnObj.put("id", load.getId());
            rtnObj.put("nm", load.getGraphNm());
            rtnObj.put("ty", load.getGraphDvTy().name());

            JSONArray array = new JSONArray();
            List<GraphData> list = graphDataService.list(id);
            for (GraphData graphData : list) {
                if (graphData.getColNm() != null && !"".equals(graphData.getColNm())) {
                    JSONObject item = new JSONObject();
                    item.put("category", graphData.getColNm());
                    item.put("col1", graphData.getColValue1());
                    item.put("col2", graphData.getColValue2());
                    item.put("col3", graphData.getColValue3());
                    array.put(item);
                }
            }
            rtnObj.put("list", array);

            return rtnObj.toString();
        }
        return "{}";
    }
}
