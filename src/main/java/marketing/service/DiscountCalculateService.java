package marketing.service;

import org.etocrm.database.exception.MyException;
import org.etocrm.marketing.model.activitylist.ActivityListVo;
import org.etocrm.marketing.model.discount.DiscountInfoVo;
import org.etocrm.marketing.model.discount.MarketingDiscountVO;

import java.util.List;

/**
 * @Author xingxing.xie
 * @Date 2021/3/29 11:37
 */
public interface DiscountCalculateService {



    /**
     * 根据 所传参数，透出 优惠信息
     * @return
     * @param marketingDiscountVO 所需入参
     * @throws MyException
     */
    DiscountInfoVo getDiscountInfo(MarketingDiscountVO marketingDiscountVO) throws MyException;

    /**
     *  获得满足条件的 优惠活动
     * @param marketingDiscountVO
     * @return
     * @throws MyException
     */
    List<ActivityListVo> getSatisfyActivity(MarketingDiscountVO marketingDiscountVO)throws MyException;

}
