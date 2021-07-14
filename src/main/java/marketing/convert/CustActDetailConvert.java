package marketing.convert;

import org.etocrm.marketing.entity.CustActivityDetail;
import org.etocrm.marketing.model.activitydetail.CustActivityDetailVo;
import org.etocrm.marketing.model.discount.DiscountInfoVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * @Author xingxing.xie
 * @Date 2021/3/31 11:36
 */
@Mapper(componentModel = "spring")
public interface CustActDetailConvert {

    /**
     * 转换对应的vo类
     * @param custActivityDetail
     * @return
     */
    CustActivityDetailVo doToVo(CustActivityDetail custActivityDetail);

    /**
     * 转换对应的vo类
     * @param custActivityDetailVo
     * @return
     */
    CustActivityDetail voToDo(CustActivityDetailVo custActivityDetailVo);

    /**
     * 填充 入库属性
     * @param discountInfoVo
     * @return
     */
    @Mappings({
            @Mapping(source = "customerInfo.customerId",target = "customerId"),
            @Mapping(source = "campaignInfo.activityCode",target = "activityId")
    })
    CustActivityDetailVo toVoByDisInfo(DiscountInfoVo discountInfoVo);


}
