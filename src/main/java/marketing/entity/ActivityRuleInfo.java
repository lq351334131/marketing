package marketing.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.etocrm.database.entity.BaseDO;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Where;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author admin
 * @since 2021-03-19
 */
@ApiModel(value = "活动规则信息表 ")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@EntityListeners(AuditingEntityListener.class)
@Entity
@DynamicInsert
@DynamicUpdate
@Where(clause = "is_delete = false ")
public class ActivityRuleInfo extends BaseDO implements Serializable {

    private static final long serialVersionUID = 4702036177868392869L;

    @ApiModelProperty(value = "主键id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ApiModelProperty(value = "机构id")
    private Long orgId;

    @ApiModelProperty(value = "机构id")
    private Long brandId;

    @ApiModelProperty(value = "店铺ID")
    private String shopId;

    @ApiModelProperty(value = "活动id")
    private String activityId;

    @ApiModelProperty(value = "条件梯级")
    private Integer level;

    @ApiModelProperty(value = "规则所属类型，和活动类型一样")
    private Integer ruleType;


    @ApiModelProperty(value = "是否是相同商品 1-是，0-不是")
    private Boolean isIdentical;

    @ApiModelProperty(value = "是否包邮 1-包邮，0-不包邮")
    private Boolean freeExpress;

    /**************************condition 优惠门槛值************/
    /**
     * 条件值
     */
    @ApiModelProperty(value = "条件值")
    private BigDecimal value;
    /**
     * 优惠计量方式【 1 - 按钱， 2 - 按件】
     */
    @ApiModelProperty(value = "优惠计量方式【 1 - 按钱， 2 - 按件】")
    private Integer discountType;
    /**
     * 预留固定值“ and”
     */
    @ApiModelProperty(value = "预留固定值“ and”")
    private String compatible;

    /*******************************conditionExplain 优惠说明************/

    /**
     * 优惠值
     */
    @ApiModelProperty(value = "优惠值")
    private Integer discountVal;
    /**
     * 优惠方式【 1-打折，2-减价】
     */
    @ApiModelProperty(value = "优惠方式【 1-打折，2-减价")
    private Integer discountModel;
    /**
     * 满减满折情况下 为
     * 商品原价：originPrice
     * 商品销售价：salePrice
     */
    @ApiModelProperty(value = "商品原价：originPrice/商品销售价：salePrice")
    private String priceCondition;

    /*******************************满赠相关字段*******************************/

    @ApiModelProperty(value = "规则id，关联赠品信息")
    private String ruleId;

    @ApiModelProperty(value = "可选赠品最多赠送几件")
    private Integer giftsNum;

    /*******************************优惠码相关字段*******************************/
    @ApiModelProperty(value = "优惠值")
    private BigDecimal actValue;

    @ApiModelProperty(value = "reduction:满减/discount:满折/OnePrice:单内参活商品享活动价")
    private String actMethod;

    @ApiModelProperty(value = "优惠码值")
    private String couponCode;
    @ApiModelProperty(value = "使用次数(最大被核销次数)")
    private Long totalAmount;

}