package marketing.service;

import org.etocrm.database.exception.MyException;
import org.etocrm.marketing.entity.GiftInfo;
import org.etocrm.marketing.model.giftinfo.GiftInfoSelectVo;
import org.etocrm.marketing.model.giftinfo.GiftInfoVo;

import java.util.List;

/**
 * @author xingxing.xie
 * @version 1.0
 * @date 2021/3/23 14:58
 */
public interface IGiftInfoService {

    /**
     *  固定赠品
     */
    int GIFT_TYPE_FIXED = 0;

    /**
     * 可选赠品
     */
    int GIFT_TYPE_SELECTED= 1;

    /**
     * 根据 活动的 id 查询活动
     * @param giftInfoSelectVo
     * @return
     */
    List<GiftInfoVo> getGiftInfoVo(GiftInfoSelectVo giftInfoSelectVo);


    /**
     * 活动规则数据同步
     * @param giftInfoVo
     * @return
     * @throws MyException
     */
    List<GiftInfo> save(List<GiftInfoVo> giftInfoVo) throws MyException;

    /**
     * 删除 活动列表数据
     * @param giftInfoVos
     * @return
     * @throws MyException
     */
    Boolean delete(List<GiftInfoVo> giftInfoVos) throws MyException;

    /**
     *  根据  ruleId   查询赠品信息 满赠活动专属
     * @param orgId
     * @param activityId
     * @param ruleId
     * @return
     */
    List<GiftInfoVo> getGiftInfoByRuleId(Long orgId, String activityId, String ruleId);
}