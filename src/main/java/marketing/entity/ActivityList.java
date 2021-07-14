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
import java.util.Date;

/**
 * @author admin
 * @since 2021-03-19
 */
@ApiModel(value = "活动规则列表 ")
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
public class ActivityList extends BaseDO implements Serializable {

    private static final long serialVersionUID = 1935953059966976028L;

    @ApiModelProperty(value = "主键id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ApiModelProperty(value = "活动标识码")
    private String activityCode;

    @ApiModelProperty(value = "品牌ID")
    private Long brandId;

    @ApiModelProperty(value = "机构id")
    private Long orgId;

    @ApiModelProperty(value = "店铺ID")
    private String shopId;

    @ApiModelProperty(value = "限制人群")
    private String  peopleLimit;

    @ApiModelProperty(value = "活动类型")
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
    private Date activityStart;

    @ApiModelProperty(value = "活动开始时间timestamp")
    private Long activityStartInt;

    @ApiModelProperty(value = "活动结束时间")
    private Date activityEnd;

    @ApiModelProperty(value = "活动结束时间timestamp")
    private Long activityEndInt;

    @ApiModelProperty(value = "适用会员范围")
    private String vipRang;

    @ApiModelProperty(value = "适用渠道 ：1-wx小程序，2-h5，3-app，4-pos")
    private String channel;

    @ApiModelProperty(value = "优惠计量方式 ：1-按前，2-按件，3-组合")
    private Integer discountType;

    @ApiModelProperty(value = "优惠设置 ：1-阶梯设置  2-循环设置")
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

}