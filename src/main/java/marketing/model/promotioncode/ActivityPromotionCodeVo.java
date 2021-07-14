package marketing.model.promotioncode;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @Description: 活动优惠码关联VO
 * @Author xingxing.xie
 * @Date 2021/3/23 15:00
 */
@ApiModel(value = "活动优惠码关联VO")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ActivityPromotionCodeVo implements Serializable {

    private static final long serialVersionUID = -3214741892149168555L;
    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "活动标识码")
    private String activityId;

    @ApiModelProperty(value = "品牌ID")
    private Long brandId;

    @ApiModelProperty(value = "机构id")
    private Long orgId;

    @ApiModelProperty(value = "店铺ID")
    private String shopId;

    @ApiModelProperty(value = "活动类型")
    private Integer activityType;
    @ApiModelProperty(value = "优惠码类型")
    private Integer promotionCodeType;
    @ApiModelProperty(value = "使用次数，最多被核销的次数")
    private Integer totalLimit;


}
