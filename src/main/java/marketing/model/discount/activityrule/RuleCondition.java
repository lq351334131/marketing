package marketing.model.discount.activityrule;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author xingxing.xie
 * @Date 2021/3/30 11:35
 */
@ApiModel("优惠门槛值")
@Data
public class RuleCondition implements Serializable {
    private static final long serialVersionUID = -4498355245012425279L;

    /**
     * 条件值
     */
    @ApiModelProperty(value = "条件值",required = true)
    private BigDecimal value;
    /**
     * 优惠计量方式【 1 - 按钱， 2 - 按件】
     */
    @ApiModelProperty(value = "优惠计量方式【 1 - 按钱， 2 - 按件】",required = true)
    private Integer discountType;
    /**
     * 预留固定值“ and”
     */
    @ApiModelProperty(value = "预留固定值“ and”")
    private String compatible;
}
