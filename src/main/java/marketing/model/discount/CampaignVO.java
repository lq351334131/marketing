package marketing.model.discount;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author xingxing.xie
 * @Date 2021/5/28 14:57
 */
@ApiModel("活动对应优惠码信息")
@Data
public class CampaignVO implements Serializable {

    private static final long serialVersionUID = -9143052379311581617L;
    @ApiModelProperty(value = "活动标识码")
    private String activityCode;

    @ApiModelProperty(value = "特殊标记：USED_ALONE")
    private String specialSign;

    @ApiModelProperty(value = "优惠码")
    private String couponCode;
}
