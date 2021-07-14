package marketing.service;

import org.etocrm.database.exception.MyException;
import org.etocrm.marketing.model.discount.DiscountInfoVo;

/**
 * 会员享受资格判断
 * @Author xingxing.xie
 * @Date 2021/3/29 13:40
 */
public interface QualificationChecker {
    /**
     * 会员享受资格判断
     * @param discountInfoVo
     * @return
     */
    Boolean check(DiscountInfoVo discountInfoVo) throws MyException;

    /**
     * 当前 检查器名称
     * @return
     */
    String getName();

}
