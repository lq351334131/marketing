package marketing.model.giftinfo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 用于礼品信息入库
 * @Author xingxing.xie
 * @Date 2021/3/30 11:42
 */
@ApiModel("赠品信息")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class GiftInfoVo implements Serializable {

    private static final long serialVersionUID = 5762908322338542595L;

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "机构id")
    private Long orgId;

    @ApiModelProperty(value = "品牌ID")
    private Long brandId;

    @ApiModelProperty(value = "店铺ID")
    private String shopId;

    @ApiModelProperty(value = "活动id")
    private String activityId;

    @ApiModelProperty(value = "规则id")
    private String ruleId;

    @ApiModelProperty(value = "赠品类型：0固定赠品、1可选赠品")
    private Integer giftType;

    @ApiModelProperty(value = "商品图片路径")
    private String pic;
    @ApiModelProperty(value = "赠品价格")
    private BigDecimal price;
    @ApiModelProperty(value = "赠品数量")
    private Integer giftsNum;

    @ApiModelProperty(value = "商品ID")
    private String productId;

    @ApiModelProperty(value = "多个商品sku")
    private List<String> systemSkus;

    @ApiModelProperty(value = "商品sku")
    private String systemSku;

    @ApiModelProperty(value = "商品名称")
    private String productName;

    @ApiModelProperty(value = "规格项")
    private String platoGroupCn;

}
