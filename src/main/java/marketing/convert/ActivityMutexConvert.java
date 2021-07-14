package marketing.convert;

import org.etocrm.marketing.convert.strategy.ListToStringStrategy;
import org.etocrm.marketing.entity.ActivityTypeMutex;
import org.etocrm.marketing.model.activitymutex.ActivityTypeInfo;
import org.etocrm.marketing.model.activitymutex.ActivityTypeMutexVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

import java.util.List;

/**
 * @Author xingxing.xie
 * @Date 2021/3/31 11:36
 */
@Mapper(componentModel = "spring",uses = ListToStringStrategy.class)
public interface ActivityMutexConvert {

    /**
     * 转换对应的vo类
     * @param activityTypeMutex
     * @return
     */
    ActivityTypeMutexVo doToVo(ActivityTypeMutex activityTypeMutex);

    /**
     * 转换对应的vo类
     * @param activityTypeMutexVo
     * @return
     */
    ActivityTypeMutex voToDo(ActivityTypeMutexVo activityTypeMutexVo);

    /**
     * voList  转doList
     * @param activityTypeMutexVo
     * @return
     */
    List<ActivityTypeMutex> voListToDoList(List<ActivityTypeMutexVo> activityTypeMutexVo);

    /**
     *  根据活动类型信息 封装
     * @param activityTypeMutexVo
     * @param activityTypeInfo
     */
    @Mappings({
            @Mapping(source = "nid",target = "activityType"),
            @Mapping(source = "name",target = "activityName"),
            @Mapping(source = "model",target = "model"),
    })
    void boxByActivityTypeInfo(@MappingTarget ActivityTypeMutexVo activityTypeMutexVo, ActivityTypeInfo activityTypeInfo);

}
