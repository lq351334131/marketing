package marketing.service;

import org.etocrm.database.exception.MyException;
import org.etocrm.marketing.model.promotioncode.ActivityPromotionCodeSelectVo;
import org.etocrm.marketing.model.promotioncode.ActivityPromotionCodeVo;

import java.util.List;

/**
 * @author xingxing.xie
 * @version 1.0
 * @date 2021/4/15 20:58
 */
public interface IActivityPromotionService {

    /**
     * 根据 活动的 id 查询活动
     * @param promotionCodeSelectVo
     * @return
     */
    List<ActivityPromotionCodeVo> getList(ActivityPromotionCodeSelectVo promotionCodeSelectVo);

    /**
     * 优惠码数据同步
     * @param promotionCodeVo
     * @return
     * @throws MyException
     */
    ActivityPromotionCodeVo save(ActivityPromotionCodeVo promotionCodeVo) throws MyException;

    /**
     * 删除 优惠码数据
     * @param promotionCodeVos
     * @return
     * @throws MyException
     */
    Boolean delete(List<ActivityPromotionCodeVo> promotionCodeVos) throws MyException;
}