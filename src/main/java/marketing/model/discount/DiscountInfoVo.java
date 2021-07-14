package marketing.model.discount;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author xingxing.xie
 * @Date 2021/3/26 10:45
 */
@ApiModel("营销中台优惠信息出入参")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class DiscountInfoVo implements Serializable {

    private static final long serialVersionUID = -6202253134617298186L;
    @ApiModelProperty(value = "品牌id")
    private Long brandId;
    @ApiModelProperty(value = "机构id")
    private Long orgId;
    @ApiModelProperty(value = "店铺id")
    private String shopId;

    @ApiModelProperty(value = "下单渠道")
    private String orderChannel;
    /**
     * 活动基本信息  当前正在计算的活动信息
     */
    @ApiModelProperty(value = "活动基本信息")
    private CampaignInfo campaignInfo;

    /**
     * 会员信息（会员等级）
     */
    @ApiModelProperty(value = "会员信息")
    private CustomerInfo customerInfo;
    /**
     * 所有商品信息（价格）
     */
    @ApiModelProperty(value = "商品信息")
    private List<ProduceInfo> produceInfos;

    /**
     * 命中规则 信息
     * 优惠的值 ：每一次活动 的优惠值
     */
    @ApiModelProperty(value = "享受的 每一次活动 的优惠值")
    private List<AimRulesInfo> discountValueList;

    @ApiModelProperty(value = "未命中得优惠卷")
    private List<MemberCoupons> unMemberCoupons = new ArrayList<>();

    @ApiModelProperty(value = "命中得优惠卷")
    private List<MemberCoupons> memberCoupons = new ArrayList<>();

    @ApiModelProperty(value = "未命中的活动规则信息")
    private List<UnReachCampaignInfo> unReachCampaignInfos;

    @ApiModelProperty(value = "优惠前 应付总金额")
    private BigDecimal priceDiscountBefore;

    @ApiModelProperty(value = "总优惠金额")
    private BigDecimal totalDiscountPrice = BigDecimal.ZERO;

    @ApiModelProperty(value = "优惠后 应付总金额")
    private BigDecimal priceDiscountAfter;

    @ApiModelProperty(value = "初始化优惠券")
    private List<MemberCoupons> couponsList;

    @ApiModelProperty(value = "（满足条件时）是否已计算优惠券")
    private boolean flag;

    @ApiModelProperty(value = "门店是否可用优惠券")
    private boolean couponEnable;

    @ApiModelProperty(value = "优惠券计算顺序")
    private Integer couponDiscountOrder;

    @ApiModelProperty(value = "优惠码活动信息")
    private List<CampaignVO> couponCodeList;


}
