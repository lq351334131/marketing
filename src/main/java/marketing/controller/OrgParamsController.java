package marketing.controller;

import com.alibaba.fastjson.JSON;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.database.exception.MyException;
import org.etocrm.database.util.ResponseVO;
import org.etocrm.marketing.model.orgparams.OrgParamsSelectVo;
import org.etocrm.marketing.model.orgparams.OrgParamsVo;
import org.etocrm.marketing.service.IOrgParamsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * @description: 机构参数配置数据同步接口
 * @author xingxing.xie
 * @date 2021/5/31 15:02
 * @version 1.0
 */
@Api(value = "机构配置参数同步",tags ="机构配置参数同步" )
@RestController
@RequestMapping("/orgParams")
@Slf4j
public class OrgParamsController {
    @Autowired
    private IOrgParamsService orgParamsService;


    @ApiOperation(value = "查询机构参数配置信息")
    @ApiOperationSupport(order = 10)
    @PostMapping("/getOrgParamList")
    public ResponseVO<List<OrgParamsVo>> getOrgParamList(@RequestBody @Valid OrgParamsSelectVo orgParamsSelectVo) {
        return ResponseVO.success(orgParamsService.getOrgParamList(orgParamsSelectVo));
    }

    @ApiOperation(value = "机构参数配置信息同步")
    @ApiOperationSupport(order = 20,ignoreParameters = {"orgParamsVo.id"})
    @PostMapping("/saveOrgParam")
    public ResponseVO<OrgParamsVo> saveOrgParam(@RequestBody @Valid OrgParamsVo orgParamsVo) throws  MyException{
            log.warn("机构参数配置信息同步入参：{}", JSON.toJSONString(orgParamsVo));
            OrgParamsVo save = orgParamsService.save(orgParamsVo);
            return ResponseVO.success(save);
    }

    @ApiOperation(value = "修改")
    @ApiOperationSupport(order = 30)
    @PostMapping("/updateOrgParam")
    public ResponseVO<OrgParamsVo> updateOrgParam(@RequestBody @Valid OrgParamsVo orgParamsVo) throws  MyException{
            OrgParamsVo save = orgParamsService.update(orgParamsVo);
            return ResponseVO.success(save);
    }

    @ApiOperation(value = "删除")
    @ApiOperationSupport(order = 40)
    @PostMapping("/deleteOrgParam")
    public ResponseVO<Boolean> deleteOrgParam(@RequestBody @Valid List<OrgParamsVo> orgParamsVos) throws  MyException{
            return ResponseVO.success(orgParamsService.delete(orgParamsVos));
    }


}
