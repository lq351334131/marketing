package marketing.convert;

import org.etocrm.marketing.entity.ActivityTypeDetail;
import org.etocrm.marketing.model.activitymutex.ActivityTypeMutexVo;
import org.etocrm.marketing.model.activitytype.ActivityTypeDetailVo;
import org.etocrm.marketing.model.discount.ShopActivityTypeDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * @Author xingxing.xie
 * @Date 2021/3/31 11:36
 */
@Mapper(componentModel = "spring")
public interface ActivityTypeDetailConvert {

    /**
     * 转换对应的vo类
     * @param activityTypeDetail
     * @return
     */
    ActivityTypeDetailVo doToVo(ActivityTypeDetail activityTypeDetail);

    /**
     * 转换对应的vo类
     * @param activityTypeDetailVo
     * @return
     */
    ActivityTypeDetail voToDo(ActivityTypeDetailVo activityTypeDetailVo);

    /**
     *  封装 门店下 各营销活动可用 情况
     * @param activityTypeMutexVo
     * @param enableFlag
     * @return
     */
    @Mappings({
            @Mapping(source = "activityTypeMutexVo.activityType",target = "activityTypeId"),
            @Mapping(source = "activityTypeMutexVo.activityName",target = "activityTypeName"),
    })
    ShopActivityTypeDetail boxShopActivityType(ActivityTypeMutexVo activityTypeMutexVo, Boolean enableFlag);

}
