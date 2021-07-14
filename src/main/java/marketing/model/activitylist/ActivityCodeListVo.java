package marketing.model.activitylist;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.etocrm.marketing.model.activityrule.ActivityRuleInfoVo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Description: 优惠码活动规则
 * @Author xingxing.xie
 * @Date 2021/4/16 18:10
 */
@ApiModel(value = "优惠码活动规则")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ActivityCodeListVo implements Serializable {

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "活动标识码",required = true)
    private String activityCode;

    @ApiModelProperty(value = "品牌ID",required = true)
    private Long brandId;

    @ApiModelProperty(value = "机构id",required = true)
    private Long orgId;

    @ApiModelProperty(value = "店铺ID",required = true)
    private List<String> shopIds;

    @ApiModelProperty(value = "限制人群")
    private String  peopleLimit;

    @ApiModelProperty(value = "活动类型",required = true)
    private Integer activityType;

    @ApiModelProperty(value = "活动名称",required = true)
    private String activityName;

    @ApiModelProperty(value = "活动标签")
    private String activityTag;

    @ApiModelProperty(value = "活动备注-用户端不展示")
    private String activityRemark;

    @ApiModelProperty(value = "活动说明")
    private String activityDesc;

    @ApiModelProperty(value = "活动开始时间",required = true)
    private Date activityStart;

    @ApiModelProperty(value = "活动开始时间timestamp")
    private Long activityStartInt;

    @ApiModelProperty(value = "活动结束时间",required = true)
    private Date activityEnd;

    @ApiModelProperty(value = "活动结束时间timestamp")
    private Long activityEndInt;

    @ApiModelProperty(value = "适用会员范围",required = true)
    private List<String> vipRang;

    @ApiModelProperty(value = "适用渠道 ：1-wx小程序，2-h5，3-app，4-pos",required = true)
    private List<String> channel;

    @ApiModelProperty(value = "优惠计量方式 ：1-按钱，2-按件，3-组合",required = true)
    private Integer discountType;

    @ApiModelProperty(value = "优惠设置 ：1-阶梯设置  2-循环设置",required = true)
    private Integer discountForm;

    @ApiModelProperty(value = "优惠规则id")
    private Long ruleId;

    @ApiModelProperty(value = "是否启用 ：1- 已启用 0-终止")
    private Boolean isEnable;

    @ApiModelProperty(value = "预热开始时间")
    private Date showStart;

    @ApiModelProperty(value = "预热结束时间")
    private Date showEnd;

    @ApiModelProperty(value = "预热开始时间timestamp")
    private Long showStartInt;

    @ApiModelProperty(value = "预热结束时间timestamp")
    private Long showEndInt;

    @ApiModelProperty(value = "是否有参与次数的限制条件 ：0-没有  1-有",required = true)
    private Boolean hasLimit;

    @ApiModelProperty(value = "单一用户可参加活动次数",required = true)
    private Integer perLimit;

    @ApiModelProperty(value = "活动总限制次数",required = true)
    private Integer totalLimit;

    @ApiModelProperty(value = "是否有商品购买件数的限制条件 ：0-没有  1-有")
    private Boolean productLimit;

    @ApiModelProperty(value = "单一用户可购买件数")
    private Integer productPerLimit;

    @ApiModelProperty(value = "单一用户可购买总件数")
    private Integer productTotalLimit;

    @ApiModelProperty(value = "活动规则详情，json",required = true)
    private ActivityRuleInfoVo rule;

    @ApiModelProperty(value = "可叠加活动id数组 该字段存放该活动所有可叠加的活动id")
    private String stackableIds;

    @ApiModelProperty(value = "创建时间")
    private Date createdTime;

}
