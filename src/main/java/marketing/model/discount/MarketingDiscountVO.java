package marketing.model.discount;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @Author xingxing.xie
 * @Date 2021/3/31 10:05
 */
@ApiModel("营销接口入参")
@Data
public class MarketingDiscountVO implements Serializable {
    private static final long serialVersionUID = 8773604298791289152L;

    @ApiModelProperty(value = "商品id" ,required = true)
    private List<MarketingGoodsSkuVO> goods;

    @ApiModelProperty(value = "门店id" ,required = true)
    @NotBlank
    private String sid;

    @ApiModelProperty(value = "品牌id" )
    private Long brandId;

    @ApiModelProperty(value = "机构id" ,required = true)
    @NotNull
    private Long orgId;

    @ApiModelProperty(value = "营销活动List",required = true )
    private List<String> activityList;

    @ApiModelProperty(value = "优惠券List")
    private List<MemberCoupons> couponsList;


    @ApiModelProperty(value = "会员详情",required = true)
    private MemberDetail memberDetail;

    @ApiModelProperty(value = "优惠码")
    private List<CampaignVO> campaignVos;
}
