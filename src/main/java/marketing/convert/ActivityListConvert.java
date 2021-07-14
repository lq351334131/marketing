package marketing.convert;

import org.etocrm.marketing.convert.strategy.ListToStringStrategy;
import org.etocrm.marketing.entity.ActivityList;
import org.etocrm.marketing.model.activitylist.ActivityCodeListVo;
import org.etocrm.marketing.model.activitylist.ActivityListVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

/**
 * @Author xingxing.xie
 * @Date 2021/3/31 11:36
 */
@Mapper(componentModel = "spring",uses = ListToStringStrategy.class)
public interface ActivityListConvert {

    /**
     * 转换对应的vo类
     * @param activityList
     * @return
     */
    @Mappings({
            @Mapping(source = "createdTime",target = "createdTime")
    })
    ActivityListVo doToVo(ActivityList activityList);

    /**
     * 转换对应的vo类
     * @param activityListVo
     * @return
     */
    ActivityList voToDo(ActivityListVo activityListVo);

    /**
     * do自身转换
     * @param activityList
     * @return
     */
    ActivityList doToSelf(ActivityList activityList);


    /**
     * 转换对应的 vo list类
     * @param activityListVos vo list类
     * @return
     */
    List<ActivityList> voListToDoList(List<ActivityListVo> activityListVos);


    /**
     * 转换对应的vo类
     * @param codeListVo
     * @return
     */
    @Mappings({
            @Mapping(target = "rule",ignore = true)
    })
    ActivityListVo codeVoToVo(ActivityCodeListVo codeListVo);


}
