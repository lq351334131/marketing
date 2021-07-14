package marketing.service.impl.checker;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.etocrm.marketing.model.discount.DiscountInfoVo;
import org.etocrm.marketing.service.QualificationChecker;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @Author xingxing.xie
 * @Date 2021/3/29 16:42
 */
@Component
@Slf4j
public class CustomerLevelChecker implements QualificationChecker {
    @Override
    public Boolean check(DiscountInfoVo discountInfoVo) {
        List<String> vipRang = discountInfoVo.getCampaignInfo().getVipRang();
        if (CollectionUtils.isEmpty(vipRang)) {
            //如果下单 适用渠道为null 则默认 无规则
            return true;
        }
        String customerLevel = discountInfoVo.getCustomerInfo().getCustomerLevelId();
        if (StringUtils.isEmpty(customerLevel)) {
            return false;
        }

        return vipRang.stream().anyMatch(t -> t.trim().equals(customerLevel));

    }

    @Override
    public String getName() {
        return "会员等级";
    }
}
