package marketing.model.discount;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author xingxing.xie
 * @Date 2021/3/30 14:30
 */
@Data
@ApiModel("计算过程金额信息")
@Accessors(chain = true)
public class ProcessInfo implements Serializable {

    private static final long serialVersionUID = 7965441760278054590L;
    /**
     * 优惠过前的价格
     */
    @ApiModelProperty(value ="优惠前的总 吊牌价")
    private BigDecimal originPriceBefore;

    @ApiModelProperty(value ="优惠前的总 销售价")
    private BigDecimal salePriceBefore;

    @ApiModelProperty(value ="应付价")
    private BigDecimal finalPayPrice;


    @ApiModelProperty(value ="总件数")
    private BigDecimal totalAmount;


}
