package marketing.service;

import org.etocrm.database.exception.MyException;
import org.etocrm.marketing.model.discount.DiscountInfoVo;

/**
 * @Author: dkx
 * @Date: 17:08 2021/4/22
 * @Desc:
 */
public interface CouponService {

    /**
     *  优惠券 计算方法
     * @param discountInfoVo
     * @return
     * @throws MyException
     */
    DiscountInfoVo calculateCoupon(DiscountInfoVo discountInfoVo) throws MyException;
}
