package marketing.convert;

import org.etocrm.marketing.convert.strategy.ListToStringStrategy;
import org.etocrm.marketing.entity.OrgParams;
import org.etocrm.marketing.model.orgparams.OrgParamsVo;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @description: 机构参数配置 映射器
 * @author xingxing.xie
 * @date 2021/5/31 14:39
 * @version 1.0
 */
@Mapper(componentModel = "spring",uses = ListToStringStrategy.class)
public interface OrgParamsConvert {

    /**
     * 转换对应的vo类
     * @param orgParams
     * @return
     */
    OrgParamsVo doToVo(OrgParams orgParams);

    /**
     * 转换对应的vo类
     * @param orgParamsVo
     * @return
     */
    OrgParams voToDo(OrgParamsVo orgParamsVo);

    /**
     *  list 转换
     * @param list
     * @return
     */
    List<OrgParams> voListToDo(List<OrgParamsVo> list);


}
