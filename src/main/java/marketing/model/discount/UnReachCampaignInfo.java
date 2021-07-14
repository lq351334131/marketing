package marketing.model.discount;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author xingxing.xie
 * @Date 2021/3/29 15:02
 */
@ApiModel("未命中的规则信息")
@Data
public class UnReachCampaignInfo implements Serializable {

    private static final long serialVersionUID = 5684380959767479318L;

    @ApiModelProperty(value = "未命中的活动规则id")
    private String campaignId;
    @ApiModelProperty(value = "未命中的活动规则")
    private CampaignInfo unReachCampaign;
    @ApiModelProperty(value = "未命中原因")
    private String unReachReason;

    @ApiModelProperty(value = "未命中的优惠卷")
    private MemberCoupons unMemberCoupons;

    @ApiModelProperty(value = "未命中的优惠卷id")
    private Integer unMemberCouponsId;






}
