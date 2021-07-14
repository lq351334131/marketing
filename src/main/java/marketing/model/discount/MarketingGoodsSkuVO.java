package marketing.model.discount;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.etocrm.marketing.model.discount.prosub.ProList;
import org.etocrm.marketing.model.discount.prosub.SubList;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Author xingxing.xie
 * @Date 2021/3/31 10:06
 */
@Data
@ApiModel(value = "销售订单基础信息")
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class MarketingGoodsSkuVO implements Serializable {
    private static final long serialVersionUID = -6756271372989830222L;


    @ApiModelProperty(value = "SKU 主键ID")
    private Long skuKeyId;

    @ApiModelProperty(value = "SPU 主键ID")
    private Long spuKeyId;

    @ApiModelProperty(value = "SKU 助记码")
    private String skuMneCode;

    @ApiModelProperty(value = "SPU 助记码" , required = true)
    private String spuMneCode;

    @ApiModelProperty(value = "pos售价")
    private BigDecimal posPrice;

    @ApiModelProperty(value = "pos原价")
    private BigDecimal showPrice;

    @ApiModelProperty(value = "pos应付金额")
    private BigDecimal amountPrice;

    @ApiModelProperty(value = "商品数量" , required = true)
    private BigDecimal goodsCount;

    @ApiModelProperty(value = "辅料价格")
    private BigDecimal subPrice;

    @ApiModelProperty(value = "工艺数组" )
    private List<ProList> proLists;

    @ApiModelProperty(value = "辅料数组" )
    private List<SubList> subLists;

}
