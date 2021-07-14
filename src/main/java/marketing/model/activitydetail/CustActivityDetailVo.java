package marketing.model.activitydetail;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @Author xingxing.xie
 * @Date 2021/3/29 9:45
 */
@ApiModel("用户参与活动明细表")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CustActivityDetailVo implements Serializable {

    private static final long serialVersionUID = -3791109375287472789L;
    @ApiModelProperty(value = "主键ID")
    private Long id;

    @ApiModelProperty(value = "品牌ID")
    private Long brandId;

    @ApiModelProperty(value = "机构id",required = true)
    private Long orgId;

    @ApiModelProperty(value = "店铺ID",required = true)
    private String shopId;

    @ApiModelProperty(value = "用户ID",required = true)
    private String customerId;

    @ApiModelProperty(value = "活动ID",required = true)
    private String activityId;

    @ApiModelProperty(value = "记录状态：1.冻结 2.核销 3.完成")
    private Integer recordStatus;

    @ApiModelProperty(value = "优惠码值")
    private String couponCode;
}
