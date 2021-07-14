package marketing.service;

import org.etocrm.database.exception.MyException;
import org.etocrm.marketing.model.activitygoods.ActivityGoodsBatchSelectVo;
import org.etocrm.marketing.model.activitygoods.ActivityGoodsSelectVo;
import org.etocrm.marketing.model.activitygoods.ActivityGoodsVo;
import org.etocrm.marketing.model.activitygoods.ActivityUnionGoodVo;

import java.util.List;

/**
 * @author xingxing.xie
 * @version 1.0
 * @date 2021/3/23 14:58
 */
public interface IActivityGoodService {


    /**
     * 活动 关联商品 数据同步
     * @param activityUnionGoodVo
     * @return
     * @throws MyException
     */
    ActivityUnionGoodVo save(ActivityUnionGoodVo activityUnionGoodVo) throws MyException;



    /**
     * 删除 关联商品数据
     * @param activityGoodsVo
     * @return
     * @throws MyException
     */
    Boolean delete(List<ActivityGoodsVo> activityGoodsVo) throws MyException;

    /**
     * 更据条件 查询集合
     * @param activityGoodsSelectVo
     * @return
     */
    List<ActivityGoodsVo> getList(ActivityGoodsSelectVo activityGoodsSelectVo);

    /**
     * 此查询方法 不同点：条件包含对各shopId
     * @param activityGoodsBatchSelectVo
     * @return
     */
    List<ActivityGoodsVo> getListByShopIds(ActivityGoodsBatchSelectVo activityGoodsBatchSelectVo);
}