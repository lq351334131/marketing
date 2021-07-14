package marketing.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.etocrm.database.exception.MyException;
import org.etocrm.database.util.BasePage;
import org.etocrm.database.util.ResponseVO;
import org.etocrm.marketing.model.activitylist.ActivityCodeListVo;
import org.etocrm.marketing.model.activitylist.ActivityListPageVo;
import org.etocrm.marketing.model.activitylist.ActivityListSelectVo;
import org.etocrm.marketing.model.activitylist.ActivityListVo;
import org.etocrm.marketing.service.IActivityListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @Description: 活动规则列表
 * @Author xingxing.xie
 * @Date 2021/3/23 15:23
 */
@Api(value = "活动规则列表",tags ="活动规则列表" )
@RestController
@RequestMapping("/activity")
public class ActivityListController {
    @Autowired
    private IActivityListService iActivityListService;

    @ApiOperation(value = "分页查询活动列表")
    @ApiOperationSupport(order = 10)
    @GetMapping("/getByPage")
    public ResponseVO<BasePage<ActivityListVo>> getActivityListByPage(@Valid ActivityListPageVo activityListPageVo) {
        return ResponseVO.success(iActivityListService.getActivityListByPage(activityListPageVo));
    }

    @ApiOperation(value = "查询活动列表")
    @ApiOperationSupport(order = 20)
    @GetMapping("/getList")
    public ResponseVO<List<ActivityListVo>> getActivityList(@Valid ActivityListSelectVo activityListSelectVo) {
        return ResponseVO.success(iActivityListService.getActivityListVo(activityListSelectVo));
    }

    @ApiOperation(value = "活动规则数据同步")
    @ApiOperationSupport(order = 30,ignoreParameters = {"activityListVo.id"})
    @PostMapping("/save")
    public ResponseVO<ActivityListVo> saveActivityInfo(@RequestBody @Valid ActivityListVo activityListVo) throws  MyException{
        return ResponseVO.success(iActivityListService.save(activityListVo));
    }

    @ApiOperation(value = "优惠码活动数据同步")
    @ApiOperationSupport(order = 30,ignoreParameters = {"activityListVo.id"})
    @PostMapping("/saveCodeActivityInfo")
    public ResponseVO<ActivityListVo> saveCodeActivityInfo(@RequestBody @Valid ActivityCodeListVo activityCodeListVo) throws  MyException{
        //根据 不同rule  结构，封装相关数据
        ActivityListVo codeActivity = iActivityListService.getCodeActivity(activityCodeListVo);
        return ResponseVO.success(iActivityListService.save(codeActivity));
    }


}
