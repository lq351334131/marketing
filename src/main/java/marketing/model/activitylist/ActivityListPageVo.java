package marketing.model.activitylist;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.etocrm.database.annotation.QueryFileds;
import org.etocrm.database.util.PageVO;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 活动规则列表分页查询
 * @Author xingxing.xie
 * @Date 2021/3/23 15:00
 */
@ApiModel(value = "活动规则列表分页查询")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class ActivityListPageVo extends PageVO implements Serializable {

    private static final long serialVersionUID = 8464068376244874488L;

    @ApiModelProperty(value = "主键id")
    @QueryFileds
    private Long id;

    @ApiModelProperty(value = "活动标识码")
    @QueryFileds
    private String activityCode;

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

    @ApiModelProperty(value = "活动名称")
    @QueryFileds
    private String activityName;

    @ApiModelProperty(value = "活动标签")
    @QueryFileds
    private String activityTag;

    @ApiModelProperty(value = "活动备注-用户端不展示")
    @QueryFileds
    private String activityRemark;

    @ApiModelProperty(value = "活动说明")
    @QueryFileds
    private String activityDesc;

    @ApiModelProperty(value = "活动开始时间")
    @QueryFileds
    private Date activityStart;

    @ApiModelProperty(value = "活动开始时间timestamp")
    @QueryFileds
    private Long activityStartInt;

    @ApiModelProperty(value = "活动结束时间")
    @QueryFileds
    private Date activityEnd;

    @ApiModelProperty(value = "活动结束时间timestamp")
    @QueryFileds
    private Long activityEndInt;

    @ApiModelProperty(value = "适用会员范围")
    @QueryFileds
    private String vipRang;

    @ApiModelProperty(value = "适用渠道 ：1-wx小程序，2-h5，3-app，4-pos")
    @QueryFileds
    private Integer channel;

    @ApiModelProperty(value = "优惠计量方式 ：1-按前，2-按件，3-组合")
    @QueryFileds
    private Integer discountType;

    @ApiModelProperty(value = "优惠设置 ：1-阶梯设置  2-循环设置")
    @QueryFileds
    private Integer discountForm;

    @ApiModelProperty(value = "优惠方式 ：1-按整单  2-按件数")
    @QueryFileds
    private Integer formType;

    @ApiModelProperty(value = "优惠规则id")
    @QueryFileds
    private Integer ruleId;

    @ApiModelProperty(value = "是否启用 ：1- 已启用 0-终止")
    @QueryFileds
    private Boolean isEnable;

    @ApiModelProperty(value = "预热开始时间")
    @QueryFileds
    private Date showStart;

    @ApiModelProperty(value = "预热结束时间")
    @QueryFileds
    private Date showEnd;

    @ApiModelProperty(value = "预热开始时间timestamp")
    @QueryFileds
    private Long showStartInt;

    @ApiModelProperty(value = "预热结束时间timestamp")
    @QueryFileds
    private Long showEndInt;

    @ApiModelProperty(value = "是否有参与次数的限制条件 ：0-没有  1-有")
    @QueryFileds
    private Boolean hasLimit;

    @ApiModelProperty(value = "单一用户可参加活动次数")
    @QueryFileds
    private Integer perLimit;

    @ApiModelProperty(value = "活动总限制次数")
    @QueryFileds
    private Integer totalLimit;

    @ApiModelProperty(value = "是否有商品购买件数的限制条件 ：0-没有  1-有")
    @QueryFileds
    private Boolean productLimit;

    @ApiModelProperty(value = "单一用户可购买件数")
    @QueryFileds
    private Integer productPerLimit;

    @ApiModelProperty(value = "单一用户可购买总件数")
    @QueryFileds
    private Integer productTotalLimit;

}
