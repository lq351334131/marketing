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
public class OrderChannelChecker implements QualificationChecker {
    @Override
    public Boolean check(DiscountInfoVo discountInfoVo) {
        List<String> channel = discountInfoVo.getCampaignInfo().getChannel();
        if (CollectionUtils.isEmpty(channel)) {
            //如果下单 适用渠道为null 则默认 无规则
            return true;
        }
        String orderChannel = discountInfoVo.getOrderChannel();
        if (StringUtils.isEmpty(orderChannel)) {
            //目前暂时不考虑
            return true;
        }

        return channel.stream().anyMatch(t -> t.equals(orderChannel));

    }

    @Override
    public String getName() {
        return "下单渠道";
    }
}
