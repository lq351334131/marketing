package marketing.convert;

import org.etocrm.marketing.convert.strategy.ListToStringStrategy;
import org.etocrm.marketing.entity.GiftInfo;
import org.etocrm.marketing.model.activityrule.ActivityRuleInfoVo;
import org.etocrm.marketing.model.giftinfo.GiftInfoVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * @Author xingxing.xie
 * @Date 2021/3/31 11:36
 */
@Mapper(componentModel = "spring",uses = ListToStringStrategy.class)
public interface GiftInfoConvert {

    /**
     * 转换对应的vo类
     * @param giftInfo
     * @return
     */
    GiftInfoVo doToVo(GiftInfo giftInfo);

    /**
     * 转换对应的vo类
     * @param giftInfoVo
     * @return
     */
    GiftInfo voToDo(GiftInfoVo giftInfoVo);



    /**
     * 转换对应的 vo list类
     * @param giftInfoVos vo list类
     * @return
     */
    List<GiftInfo> voListToDoList(List<GiftInfoVo> giftInfoVos);


    /**
     * 将机构、ruleId等封装进 giftInfo 对象
     * @param giftInfoVo
     * @param ruleInfoVo
     * @return
     */
    @Mapping(target = "id",ignore = true)
    GiftInfoVo partValueSet(@MappingTarget GiftInfoVo giftInfoVo, ActivityRuleInfoVo ruleInfoVo);

}
