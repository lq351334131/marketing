package marketing.model.giftinfo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.etocrm.database.annotation.QueryFileds;
import org.etocrm.database.enums.QueryType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Description: 活动规则列表查询
 * @Author xingxing.xie
 * @Date 2021/3/23 15:00
 */
@ApiModel(value = "活动规则列表")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class GiftInfoSelectVo implements Serializable {

    private static final long serialVersionUID = 5982794545399180228L;
    @ApiModelProperty(value = "主键id")
    @QueryFileds
    private Long id;

    @ApiModelProperty(value = "机构id")
    @QueryFileds
    private Long orgId;

    @ApiModelProperty(value = "品牌ID")
    @QueryFileds
    private Long brandId;

    @ApiModelProperty(value = "店铺ID")
    @QueryFileds
    private String shopId;

    @ApiModelProperty(value = "活动id")
    @QueryFileds
    private String activityId;

    @ApiModelProperty(value = "规则id")
    @QueryFileds(type = QueryType.IN)
    private List<String> ruleId;

    @ApiModelProperty(value = "赠品类型：0固定赠品、1可选赠品")
    @QueryFileds
    private Integer  giftType;

    @ApiModelProperty(value = "商品图片路径")
    private String pic;
    @ApiModelProperty(value = "商品图片路径")
    private BigDecimal price;
    @ApiModelProperty(value = "赠品数量")
    private Integer giftsNum;

    @ApiModelProperty(value = "商品ID")
    @QueryFileds
    private Integer productId;

    @ApiModelProperty(value = "多个商品sku")
    private List<String> systemSkus;

    @ApiModelProperty(value = "商品sku")
    private String systemSku;

    @ApiModelProperty(value = "商品名称")
    private String productName;

    @ApiModelProperty(value = "规格项")
    private String platoGroupCn;


}
