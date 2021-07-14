package marketing.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.etocrm.database.exception.MyException;
import org.etocrm.database.util.ResponseVO;
import org.etocrm.marketing.model.activitydetail.CustActivityDetailVo;
import org.etocrm.marketing.service.ICustActivityDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @Description: 活动规则列表
 * @Author xingxing.xie
 * @Date 2021/3/29 10:28
 */
@Api(value = "用户活动明细表",tags = "用户活动明细表")
@RestController
@RequestMapping("/activityVerification")
public class CustActivityDetailController {
    @Autowired
    private ICustActivityDetailService activityDetailService;


    @ApiOperation(value = "活动次数核销")
    @ApiOperationSupport(order = 10)
    @PostMapping("/success")
    public ResponseVO<CustActivityDetailVo> useSuccess(@RequestBody @Valid CustActivityDetailVo activityDetailVo) throws  MyException{
        return ResponseVO.success(activityDetailService.update(activityDetailVo,true));
    }

    @ApiOperation(value = "活动次数解冻")
    @ApiOperationSupport(order = 20)
    @PostMapping("/fail")
    public ResponseVO<CustActivityDetailVo> useFail(@RequestBody @Valid CustActivityDetailVo activityDetailVo) throws  MyException{
        return ResponseVO.success(activityDetailService.update(activityDetailVo,false));
    }

    @ApiOperation(value = "冻结")
    @ApiOperationSupport(order = 30)
    @PostMapping("/freeze")
    public ResponseVO<Boolean> activityFreeze(@RequestBody @Valid CustActivityDetailVo activityDetailVo) throws MyException {
        return ResponseVO.success(activityDetailService.activityFreeze(activityDetailVo));
    }



}
