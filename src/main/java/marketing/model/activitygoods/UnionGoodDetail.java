package marketing.model.activitygoods;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author xingxing.xie
 * @date 2021/4/19 14:53
 * @version 1.0
 */
@ApiModel(value = "关联商品详细信息")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UnionGoodDetail implements Serializable {

    private static final long serialVersionUID = -1880455251083604493L;
    @ApiModelProperty(value = "商品ID",required = true)
    private String productId;
    @ApiModelProperty(value = "SKU",required = true)
    private String systemSku;
    @ApiModelProperty(value = "优惠码一口价信息，json",required = true)
    private String info;

}