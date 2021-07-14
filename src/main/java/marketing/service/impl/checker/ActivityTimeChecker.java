package marketing.service.impl.checker;

import lombok.extern.slf4j.Slf4j;
import org.etocrm.marketing.model.discount.DiscountInfoVo;
import org.etocrm.marketing.service.QualificationChecker;
import org.springframework.stereotype.Component;

/**
 * @Author xingxing.xie
 * @Date 2021/3/29 13:42
 */
@Component
@Slf4j
public class ActivityTimeChecker implements QualificationChecker {
    @Override
    public Boolean check(DiscountInfoVo discountInfoVo) {
        long currentTimeMillis = System.currentTimeMillis();
        //获取活动开始时间
        long starTime = discountInfoVo.getCampaignInfo().getActivityStart().getTime();
        //获取活动结束时间
        long endTime = discountInfoVo.getCampaignInfo().getActivityEnd().getTime();
        //判断当前时间是否满足活动时间
        if (starTime <= currentTimeMillis && currentTimeMillis <= endTime) {
            return true;
        }
        return false;
    }

    @Override
    public String getName(){
        return "活动时间";
    }

}
