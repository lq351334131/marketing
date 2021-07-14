package marketing.convert;

import org.etocrm.marketing.entity.DictList;
import org.etocrm.marketing.model.dict.DictListVo;
import org.mapstruct.Mapper;

/**
 * @Author xingxing.xie
 * @Date 2021/4/30 14:54
 */
@Mapper(componentModel = "spring")
public interface DictListConvert {

    /**
     *  do  转换成Vo
     * @param dictList
     * @return
     */
    DictListVo doToVo(DictList dictList);

    /**
     * 转换对应的vo类
     * @param dictListVo
     * @return
     */
    DictList voToDo(DictListVo dictListVo);

}
