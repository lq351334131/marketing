package marketing.convert;

import org.etocrm.marketing.entity.ActivityPromotionCode;
import org.etocrm.marketing.model.promotioncode.ActivityPromotionCodeVo;
import org.mapstruct.Mapper;

/**
 * @Author xingxing.xie
 * @Date 2021/3/31 11:36
 */
@Mapper(componentModel = "spring")
public interface ActivityPromotionConvert {

    /**
     * 转换对应的vo类
     * @param promotionCode
     * @return
     */
    ActivityPromotionCodeVo doToVo(ActivityPromotionCode promotionCode);

    /**
     * 转换对应的DO类
     * @param promotionCodeVo
     * @return
     */
    ActivityPromotionCode voToDo(ActivityPromotionCodeVo promotionCodeVo);


}
