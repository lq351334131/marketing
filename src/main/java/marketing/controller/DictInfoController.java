package marketing.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.etocrm.database.exception.MyException;
import org.etocrm.database.util.BasePage;
import org.etocrm.database.util.ResponseVO;
import org.etocrm.marketing.model.dict.DictInfoPageVo;
import org.etocrm.marketing.model.dict.DictInfoSelectVo;
import org.etocrm.marketing.model.dict.DictInfoVo;
import org.etocrm.marketing.service.IDictInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @Author chengrong.yang
 * @Date 2021-03-04 10:55:47
 */
@Api(tags = "数据字典")
@ApiSort(300)
@RestController
@AllArgsConstructor
@RequestMapping("dictInfo")
public class DictInfoController {
    @Autowired
    IDictInfoService dictInfoService;

    @ApiOperation(value = "分页查询数据字典")
    @ApiOperationSupport(order = 10)
    @GetMapping("/getDictInfoByPage")
    public ResponseVO<BasePage<DictInfoVo>> getDictInfoByPage(@Valid DictInfoPageVo dictInfoPageVo) {
        return ResponseVO.success(dictInfoService.getDictInfoByPage(dictInfoPageVo));
    }

    @ApiOperation(value = "查询数据字典列表")
    @ApiOperationSupport(order = 20)
    @GetMapping("/getDictInfofo")
    public ResponseVO<List<DictInfoVo>> getDictInfofo(@Valid DictInfoSelectVo dictInfoSelectVo) {
        return ResponseVO.success(dictInfoService.getDictInfo(dictInfoSelectVo));
    }

    @ApiOperation(value = "添加数据字典")
    @ApiOperationSupport(order = 30,ignoreParameters = {"dictInfoVo.id"})
    @PostMapping("/save")
    public ResponseVO<DictInfoVo> save(@RequestBody @Valid DictInfoVo dictInfoVo) throws MyException {
        return ResponseVO.success(dictInfoService.save(dictInfoVo));
    }

    @ApiOperation(value = "修改数据字典")
    @ApiOperationSupport(order = 40)
    @PostMapping("/update")
    public ResponseVO<DictInfoVo> update(@RequestBody @Valid DictInfoVo dictInfoVo) throws MyException {
        return ResponseVO.success(dictInfoService.update(dictInfoVo));
    }

    @ApiOperation(value = "删除数据字典")
    @ApiOperationSupport(order = 50)
    @PostMapping("/delete")
    public ResponseVO<Boolean> delete(@RequestBody @Valid DictInfoVo dictInfoVo) throws MyException {
        return ResponseVO.success(dictInfoService.delete(dictInfoVo));
    }
}