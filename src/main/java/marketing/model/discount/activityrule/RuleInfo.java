package marketing.model.discount.activityrule;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.etocrm.marketing.model.discount.ProduceInfo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Author xingxing.xie
 * @Date 2021/3/29 14:59
 */
@ApiModel("活动规则信息")
@Data
public class RuleInfo implements Serializable {
    private static final long serialVersionUID = -8976550728590217742L;

    @ApiModelProperty(value = "机构id")
    private Long orgId;

    @ApiModelProperty(value = "机构id")
    private Long brandId;

    @ApiModelProperty(value = "店铺ID")
    private String shopId;

    @ApiModelProperty(value = "活动id 例：(1,2)，值大的在前")
    private String activityId;

    @ApiModelProperty(value = "规则id，关联赠品信息")
    private String ruleId;

    @ApiModelProperty(value = "规则所属类型，和活动类型一样")
    private Integer ruleType;

    @ApiModelProperty(value = "条件梯级 ：0互斥、1可叠加")
    private Integer level;

    @ApiModelProperty(value = "是否是相同商品 1-是，0-不是")
    private Boolean isIdentical;

    @ApiModelProperty(value = "是否包邮 1-包邮，0-不包邮")
    private Boolean freeExpress;

    /**
     * 满赠活动类型  priceCondition属性在最外层
     */
    @ApiModelProperty(value = "折前、折后价")
    private String priceCondition;

    @ApiModelProperty(value = "优惠门槛值")
    private List<RuleCondition> condition;

    @ApiModelProperty(value = "优惠说明")
    private List<ConditionExplain> conditionExplain;

    @ApiModelProperty(value = "满赠互动：固定赠品")
    private List<ConditionExplain> conditionExplain2;

    @ApiModelProperty(value = "满足当前层级规则的商品列表")
    private List<ProduceInfo> unitProduceList;

    /*********************优惠码相关属性************************************/

    @ApiModelProperty(value = "条件值")
    private BigDecimal value;
    @ApiModelProperty(value = "优惠码值")
    private String couponCode;
    @ApiModelProperty(value = "使用次数(最大被核销次数)")
    private Long totalAmount;

    @ApiModelProperty(value = "优惠值")
    private BigDecimal actValue;

    @ApiModelProperty(value = "reduction:满减/discount:满折/OnePrice:单内参活商品享活动价")
    private String actMethod;
}
