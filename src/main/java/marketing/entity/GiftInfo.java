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
@ApiModel(value = "满赠活动赠品表")
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
public class GiftInfo extends BaseDO implements Serializable {

    private static final long serialVersionUID = -6437687704399753586L;
    @ApiModelProperty(value = "主键id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ApiModelProperty(value = "机构id")
    private Long orgId;

    @ApiModelProperty(value = "品牌ID")
    private Long brandId;

    @ApiModelProperty(value = "店铺ID")
    private String shopId;

    @ApiModelProperty(value = "活动标识码")
    private String activityId;

    @ApiModelProperty(value = "规则id")
    private String ruleId;

    @ApiModelProperty(value = "赠品类型：0固定赠品、1可选赠品")
    private Integer  giftType;

    @ApiModelProperty(value = "商品图片路径")
    private String pic;

    @ApiModelProperty(value = "商品id")
    private String productId;

    @ApiModelProperty(value = "商品名称")
    private String productName;

    @ApiModelProperty(value = "赠品价格")
    private BigDecimal price;

    @ApiModelProperty(value = "赠品数量")
    private Integer giftsNum;

    @ApiModelProperty(value = "商品sku（多个）")
    private String systemSkus;

    @ApiModelProperty(value = "商品sku(单个，固定赠品所用字段)")
    private String systemSku;

    @ApiModelProperty(value = "规格项")
    private String platoGroupCn;


}