package marketing.service;

import org.etocrm.database.exception.MyException;
import org.etocrm.marketing.model.activitylist.ActivityListVo;
import org.etocrm.marketing.model.activitymutex.ActivityTypeMutexVo;
import org.etocrm.marketing.model.discount.StackableActivityInputVo;

import java.util.List;

/**
 * 校验活动是否互斥
 * @Author xingxing.xie
 * @Date 2021/4/7 10:46
 */
public interface IActivityMutexCheckerService {
    /**
     *  根据 活动id  判断是都互斥活动
     * @param inputVo
     * @return
     * @throws MyException
     */
    List<String> getStackableActivityIds(StackableActivityInputVo inputVo) throws MyException;

    /**
     *  从门店所有活动列表中  筛选出 门店下可用的 活动列表
     * @param originActivityList
     * @param shopMutexTypeList
     * @return
     */
    List<ActivityListVo> pickActivityFromShopType(List<ActivityListVo> originActivityList, List<ActivityTypeMutexVo> shopMutexTypeList);

    /**
     * 获得该 门店下 所有可用的 活动类型
     * @param orgId
     * @param shopId
     * @return
     */
    List<ActivityTypeMutexVo>  getShopActivityType(Long orgId, String shopId);
}
