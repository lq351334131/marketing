package marketing.model.promotioncode;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.etocrm.database.annotation.QueryFileds;

import java.io.Serializable;

/**
 * @Description: 活动优惠码关联表查询
 * @Author xingxing.xie
 * @Date 2021/3/23 15:00
 */
@ApiModel(value = "活动优惠码关联表查询")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ActivityPromotionCodeSelectVo implements Serializable {

    private static final long serialVersionUID = -223664283606415772L;
    @ApiModelProperty(value = "主键id")
    @QueryFileds
    private Long id;

    @ApiModelProperty(value = "活动标识码")
    @QueryFileds
    private String activityId;

    @ApiModelProperty(value = "品牌ID")
    @QueryFileds
    private Long brandId;

    @ApiModelProperty(value = "机构id")
    @QueryFileds
    private Long orgId;

    @ApiModelProperty(value = "店铺ID")
    @QueryFileds
    private String shopId;

    @ApiModelProperty(value = "活动类型")
    @QueryFileds
    private Integer activityType;
    @ApiModelProperty(value = "优惠码类型")
    @QueryFileds
    private Integer promotionCodeType;
    @ApiModelProperty(value = "使用次数，最多被核销的次数")
    @QueryFileds
    private Integer totalLimit;

}
