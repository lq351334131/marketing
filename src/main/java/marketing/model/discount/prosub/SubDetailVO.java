package marketing.model.discount.prosub;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @description: 仅做返回
 * @author xingxing.xie
 * @date 2021/4/23 15:16
 * @version 1.0
 */
@Data
@ApiModel(value = "销售订单辅料详细信息")
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class SubDetailVO implements Serializable {

    private static final long serialVersionUID = -3923480786128215094L;
    @ApiModelProperty(value = "辅料id" )
    private Long id;

    @ApiModelProperty(value = "辅料名称" )
    private String name;

    @ApiModelProperty(value = "pos售价")
    private BigDecimal posPrice;

    @ApiModelProperty(value = "pos原价")
    private BigDecimal showPrice;
}
