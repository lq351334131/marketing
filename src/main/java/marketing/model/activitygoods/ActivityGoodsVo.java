package marketing.model.activitygoods;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author admin
 * @since 2021-03-19
 */
@ApiModel(value = "活动与商品关联表")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ActivityGoodsVo implements Serializable {

    private static final long serialVersionUID = 3150212966611509563L;
    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "活动code或ID")
    private String activityCode;

    @ApiModelProperty(value = "活动类型 同activity_list的activity_type字段")
    private Integer activityType;

    @ApiModelProperty(value = "活动名")
    private String activityName;

    @ApiModelProperty(value = "品牌ID")
    private Long brandId;
    @ApiModelProperty(value = "品牌ID")
    private Long orgId;

    @ApiModelProperty(value = "门店ID")
    private String shopId;

    @ApiModelProperty(value = "商品ID")
    private String productId;

    @ApiModelProperty(value = "sku")
    private String shopSku;

    @ApiModelProperty(value = "系统sku")
    private String systemSku;

    @ApiModelProperty(value = "活动价 【部分营销活动直接改价用】")
    private BigDecimal activityPrice;

    @ApiModelProperty(value = "活动开始时间timestamp")
    private Long startTimeInt;

    @ApiModelProperty(value = "活动结束时间timestamp")
    private Long endTimeInt;

    @ApiModelProperty(value = "活动开始时间")
    private Date startTime;

    @ApiModelProperty(value = "活动结束时间")
    private Date endTime;

    @ApiModelProperty(value = "营销商品说明用")
    private String info;

    @ApiModelProperty(value = "是否启用 ：0-关闭   1-启用")
    private Boolean isEnable;

    @ApiModelProperty(value = "营销独立库存量")
    private Integer stock;

    @ApiModelProperty(value = "是否使用当前设置的库存 ：0-不使用   1-使用")
    private Boolean isCurrentStock;



}