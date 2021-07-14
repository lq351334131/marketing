package marketing.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.etocrm.database.exception.MyException;
import org.etocrm.database.util.ResponseVO;
import org.etocrm.marketing.model.activitygoods.ActivityUnionGoodVo;
import org.etocrm.marketing.service.IActivityGoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @Description: 活动规则列表
 * @Author xingxing.xie
 * @Date 2021/3/29 15:46
 */
@Api(value = "活动关联商品表",tags = "活动关联商品表")
@RestController
@RequestMapping("/activityGood")
public class ActivityGoodController {
    @Autowired
    private IActivityGoodService activityGoodService;


    @ApiOperation(value = "活动关联商品，可批量")
    @ApiOperationSupport(order = 30,ignoreParameters = {"activityUnionGoodVo.id"})
    @PostMapping("/save")
    public ResponseVO<ActivityUnionGoodVo> saveActivityGood(@RequestBody @Valid ActivityUnionGoodVo activityUnionGoodVo) throws  MyException{
        return ResponseVO.success(activityGoodService.save(activityUnionGoodVo));
    }

}
