package marketing.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.etocrm.database.exception.MyException;
import org.etocrm.database.util.BasePage;
import org.etocrm.database.util.ResponseVO;
import org.etocrm.marketing.model.dict.DictListPageVo;
import org.etocrm.marketing.model.dict.DictListSelectVo;
import org.etocrm.marketing.model.dict.DictListVo;
import org.etocrm.marketing.service.IDictListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @Author chengrong.yang
 * @Date 2021-03-04 10:57:05
 */
@Api(tags = "数据字典项")
@ApiSort(200)
@RestController
@AllArgsConstructor
@RequestMapping("dictList")
public class DictListController {
    @Autowired
    IDictListService dictListService;

    @ApiOperation(value = "分页查询数据字典项")
    @ApiOperationSupport(order = 10)
    @GetMapping("/getDictListByPage")
    public ResponseVO<BasePage<DictListVo>> getDictListByPage(@Valid DictListPageVo dictListPageVo) {
        return ResponseVO.success(dictListService.getDictListByPage(dictListPageVo));
    }

    @ApiOperation(value = "查询数据字典项列表")
    @ApiOperationSupport(order = 20)
    @GetMapping("/getDictListfo")
    public ResponseVO<List<DictListVo>> getDictListfo(@Valid DictListSelectVo dictListSelectVo) {
        return ResponseVO.success(dictListService.getDictList(dictListSelectVo));
    }

    @ApiOperation(value = "添加数据字典项")
    @ApiOperationSupport(order = 30,ignoreParameters = {"dictListVo.id"})
    @PostMapping("/save")
    public ResponseVO<DictListVo> save(@RequestBody @Valid DictListVo dictListVo) throws MyException {
        return ResponseVO.success(dictListService.save(dictListVo));
    }

    @ApiOperation(value = "修改数据字典项")
    @ApiOperationSupport(order = 40)
    @PostMapping("/update")
    public ResponseVO<DictListVo> update(@RequestBody @Valid DictListVo dictListVo) throws MyException {
        return ResponseVO.success(dictListService.update(dictListVo));
    }

    @ApiOperation(value = "删除数据字典项")
    @ApiOperationSupport(order = 50)
    @PostMapping("/delete")
    public ResponseVO<Boolean> delete(@RequestBody @Valid DictListVo dictListVo) throws MyException {
        return ResponseVO.success(dictListService.delete(dictListVo));
    }
}