package marketing.convert;

import org.etocrm.marketing.entity.ActivityUnionGoods;
import org.etocrm.marketing.model.activitygoods.ActivityGoodsVo;
import org.etocrm.marketing.model.activitylist.ActivityListVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

/**
 * @Author xingxing.xie
 * @Date 2021/3/31 11:36
 */
@Mapper(componentModel = "spring")
public interface ActivityGoodsConvert {

    /**
     * 转换对应的vo类
     * @param activityListVo
     * @return
     */
    @Mappings({
            @Mapping(source = "activityStart",target = "startTime"),
            @Mapping(source = "activityEnd",target = "endTime"),
            @Mapping(target = "createdTime",ignore = true),
            @Mapping(target = "id",ignore = true),
    })
    ActivityUnionGoods voToDo(ActivityListVo activityListVo);

    /**
     *  do  转换成Vo
     * @param activityUnionGoods
     * @return
     */
    ActivityGoodsVo doToVo(ActivityUnionGoods activityUnionGoods);

    /**
     * list 转换
     * @param activityGoodsVos
     * @return
     */
    List<ActivityUnionGoods> voListToDoList(List<ActivityGoodsVo> activityGoodsVos);

    /**
     *  do to  do
     * @param activityUnionGoods
     * @return
     */
    ActivityUnionGoods doToSelf(ActivityUnionGoods activityUnionGoods);

}
