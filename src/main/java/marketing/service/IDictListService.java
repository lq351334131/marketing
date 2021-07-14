package marketing.service;

import org.etocrm.database.exception.MyException;
import org.etocrm.database.util.BasePage;
import org.etocrm.marketing.model.dict.DictListPageVo;
import org.etocrm.marketing.model.dict.DictListSelectVo;
import org.etocrm.marketing.model.dict.DictListVo;

import java.util.List;

/**
 * @Author chengrong.yang
 * @Date 2021-03-04 10:57:01
 */
public interface IDictListService {

    BasePage<DictListVo> getDictListByPage(DictListPageVo dictListPageVo);

    List<DictListVo> getDictList(DictListSelectVo dictListSelectVo);

    DictListVo save(DictListVo dictListVo) throws MyException;

    DictListVo update(DictListVo dictListVo) throws MyException;

    Boolean delete(DictListVo dictListVo) throws MyException;

    /**
     * 查询 门店下 特殊的优惠码储值卡 活动
     * @param orgId 机构id
     * @param shopId 门店id
     * @return
     */
    List<DictListVo> getSpecialCouponCode(Long orgId, String shopId);
}