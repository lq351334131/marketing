package marketing.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.etocrm.database.exception.MyException;
import org.etocrm.database.util.ResponseVO;
import org.etocrm.marketing.model.activitymutex.ActivityMutexData;
import org.etocrm.marketing.model.activitymutex.ActivityTypeMutexVo;
import org.etocrm.marketing.service.IActivityTypeMutexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @Description: 活动规则列表
 * @Author xingxing.xie
 * @Date 2021/3/23 15:23
 */
@Api(value = "活动类型互斥数据同步",tags = "活动类型互斥数据同步")
@RestController
@RequestMapping("/activityTypeMutex")
public class ActivityTypeMutexController {
    @Autowired
    private IActivityTypeMutexService activityTypeMutexService;


    @ApiOperation(value = "活动类型互斥数据同步")
    @ApiOperationSupport(order = 30,ignoreParameters = {"activityTypeMutexVo.id"})
    @PostMapping("/save")
    public ResponseVO<ActivityTypeMutexVo> save(@RequestBody @Valid ActivityMutexData activityMutexData) throws  MyException{
        return ResponseVO.success(activityTypeMutexService.save(activityMutexData));
    }

    @ApiOperation(value = "修改数据")
    @ApiOperationSupport(order = 40)
    @PostMapping("/update")
    public ResponseVO<ActivityTypeMutexVo> update(@RequestBody @Valid ActivityTypeMutexVo activityTypeMutexVo) throws MyException {
        return ResponseVO.success(activityTypeMutexService.update(activityTypeMutexVo));
    }

    @ApiOperation(value = "删除数据")
    @ApiOperationSupport(order = 50)
    @PostMapping("/delete")
    public ResponseVO<Boolean> delete(@RequestBody @Valid ActivityTypeMutexVo activityTypeMutexVo) throws MyException {
        return ResponseVO.success(activityTypeMutexService.delete(activityTypeMutexVo));
    }


}
