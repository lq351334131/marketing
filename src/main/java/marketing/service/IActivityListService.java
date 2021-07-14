package marketing.service;

import cn.hutool.core.collection.CollectionUtil;
import org.etocrm.database.exception.MyException;
import org.etocrm.database.util.BasePage;
import org.etocrm.marketing.model.activitylist.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xingxing.xie
 * @version 1.0
 * @date 2021/3/23 14:58
 */
public interface IActivityListService {
    /**
     * @param activityListPageVo
     * @return
     */
    BasePage<ActivityListVo> getActivityListByPage(ActivityListPageVo activityListPageVo);

    /**
     * 根据 活动的 id 查询活动
     * @param activityListSelectVo
     * @return
     */
    List<ActivityListVo> getActivityListVo(ActivityListSelectVo activityListSelectVo);

    /**
     * 根据 活动的 id 查询活动 (批量查询方式)
     * @param activityListBatchSelectVo
     * @return
     */
    List<ActivityListVo> getActivityListByBatchVo(ActivityListBatchSelectVo activityListBatchSelectVo);

    /**
     * 查询当前时间点  门店下所有活动列表
     * @param activityListSelectVo
     * @param currentTimeMillis
     * @return
     */
    List<ActivityListVo> getValidActivityByShopId(ActivityListSelectVo activityListSelectVo, Long currentTimeMillis);

    /**
     * 活动规则数据同步
     * @param activityListVo
     * @return
     * @throws MyException
     */
    ActivityListVo save(ActivityListVo activityListVo) throws MyException;

    /**
     * 更改 活动列表数据
     * @param activityListVo
     * @return
     * @throws MyException
     */
    ActivityListVo update(ActivityListVo activityListVo) throws MyException;

    /**
     * 删除 活动列表数据
     * @param activityListVos
     * @return
     * @throws MyException
     */
    Boolean delete(List<ActivityListVo> activityListVos) throws MyException;

    /**
     *  用于 处理 优惠码的 入参结构 不同问题
     * @param activityCodeListVo
     * @return
     */
    ActivityListVo getCodeActivity(ActivityCodeListVo activityCodeListVo);

    /**
     * 根据当前时间过滤  活动列表
     *
     * @param collect
     * @return
     */
    default List<ActivityListVo> getFilteredListByTime(List<ActivityListVo> collect) {
        //过滤 时间不符合的活动
        long currentTimeMillis = System.currentTimeMillis();

        return collect.stream().filter(t ->
                //是否 上架
                t.getIsEnable()
                        //时间 过滤
                        && t.getActivityStart().getTime() <= currentTimeMillis
                        && currentTimeMillis <= t.getActivityEnd().getTime())
                .collect(Collectors.toList());
    }


      /**
     *  判断 入参 活动 是否  特殊储值卡优惠码活动
     * @param activityCode
     * @param specialCouponCodes
     * @return
     */
      default boolean matchExistSpecial(String activityCode, List<String> specialCouponCodes){
        if(CollectionUtil.isEmpty(specialCouponCodes)){
            return false;
        }
        return specialCouponCodes.stream().anyMatch(t -> t.trim().equals(activityCode.trim()));
    }
}