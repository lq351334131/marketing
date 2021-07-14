package marketing.model.discount;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.etocrm.marketing.model.discount.activityrule.RuleInfo;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Author xingxing.xie
 * @Date 2021/3/29 14:57
 */
@ApiModel("活动信息")
@Data
public class CampaignInfo implements Serializable {
    private static final long serialVersionUID = 5038712939633405362L;

    @ApiModelProperty(value = "活动标识码")
    @NotNull(message = "活动编码不能为空")
    private String activityCode;

    @ApiModelProperty(value = "活动类型")
    @NotNull(message = "活动类型不能为空")
    private Integer activityType;

    @ApiModelProperty(value = "活动名称")
    private String activityName;

    @ApiModelProperty(value = "活动标签")
    private String activityTag;

    @ApiModelProperty(value = "活动备注-用户端不展示")
    private String activityRemark;

    @ApiModelProperty(value = "活动说明")
    private String activityDesc;

    @ApiModelProperty(value = "活动开始时间")
    @NotNull(message = "活动开始时间不能为空")
    private Date activityStart;

    @ApiModelProperty(value = "活动开始时间timestamp")
    private Long activityStartInt;

    @ApiModelProperty(value = "活动结束时间")
    @NotNull(message = "活动结束时间不能为空")
    private Date activityEnd;

    @ApiModelProperty(value = "活动结束时间timestamp")
    private Long activityEndInt;

    @ApiModelProperty(value = "适用会员范围")
    private List<String> vipRang;

    @ApiModelProperty(value = "适用渠道 ：1-wx小程序，2-h5，3-app，4-pos")
    private List<String> channel;

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

    @ApiModelProperty(value = "是否有参与次数的限制条件 ：0-没有  1-有")
    private Boolean hasLimit;

    @ApiModelProperty(value = "单一用户可参加活动次数")
    private Integer perLimit;

    @ApiModelProperty(value = "活动总限制次数")
    private Integer totalLimit;

    @ApiModelProperty(value = "是否有商品购买件数的限制条件 ：0-没有  1-有")
    private Boolean productLimit;

    @ApiModelProperty(value = "单一用户可购买件数")
    private Integer productPerLimit;

    @ApiModelProperty(value = "单一用户可购买总件数")
    private Integer productTotalLimit;

    @ApiModelProperty(value = "可叠加活动id数组 该字段存放该活动所有可叠加的活动id")
    private String stackableIds;

    @ApiModelProperty(value = "优惠计量方式 ：1-按前，2-按件，3-组合")
    private Integer discountType;

    @ApiModelProperty(value = "优惠设置 ：1-阶梯设置  2-循环设置")
    private Integer discountForm;

    /**
     * 规则信息（活动下的）
     */
    @ApiModelProperty(value = "活动下的规则详情")
    private List<RuleInfo> rule;
    @ApiModelProperty(value = "特殊标记：USED_ALONE")
    private String specialSign;
}
