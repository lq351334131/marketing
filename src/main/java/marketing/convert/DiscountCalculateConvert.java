package marketing.convert;

import org.etocrm.marketing.model.activitylist.ActivityListVo;
import org.etocrm.marketing.model.discount.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

import java.util.List;

/**
 * 优惠计算相关类 映射器
 * @Author xingxing.xie
 * @Date 2021/4/1 10:35
 */
@Mapper(componentModel = "spring")
public interface DiscountCalculateConvert {

    /**
     * 营销入参转换
     * @param marketingDiscountVO
     * @return
     */
    @Mappings({
            @Mapping(source = "sid",target = "shopId"),
            @Mapping(source = "goods",target = "produceInfos"),
            @Mapping(source = "memberDetail.id",target = "customerInfo.customerId"),
            @Mapping(source = "memberDetail.vipLevel",target = "customerInfo.customerLevelName"),
            @Mapping(source = "memberDetail.vipLevelId",target = "customerInfo.customerLevelId"),
            @Mapping(source = "campaignVos",target = "couponCodeList"),
    })
    DiscountInfoVo toDiscountInfoVo(MarketingDiscountVO marketingDiscountVO);

    /**
     * 入参商品信息转换
     * @param marketingGoodsSkuVO
     * @return
     */
    @Mappings({
          @Mapping(source = "skuMneCode",target = "skuId"),
          @Mapping(source = "spuMneCode",target = "spuId"),
    })
    ProduceInfo toProduceInfo(MarketingGoodsSkuVO marketingGoodsSkuVO);

    /**
     * 填充 活动部分属性值
     * @param discountInfoVo
     * @param activityListVo
     * @return
     */
    DiscountInfoVo toVoByActivity(@MappingTarget DiscountInfoVo discountInfoVo, ActivityListVo activityListVo);

    /**
     * 填充 活动部分属性值
     * @param activityListVo
     * @return
     */
    @Mappings({
            @Mapping(target = "perLimit",defaultValue = "0"),
            @Mapping(target = "totalLimit",defaultValue = "0")
    })
    CampaignInfo toCampaignInfo(ActivityListVo activityListVo);
    /**
     * 填充 活动部分属性值
     * @param activityListVo
     * @return
     */
    List<ActivityListVo>  toCampaignInfoList(List<CampaignInfo> activityListVo);

    /**
     * 转换productInfo
     * @param goods
     * @return
     */
    List<ProduceInfo> toProduceInfo(List<MarketingGoodsSkuVO> goods);


}
