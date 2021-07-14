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

/**
 * @Author xingxing.xie
 * @Date 2021/3/29 9:45
 */
@ApiModel("用户参与活动明细表")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@EntityListeners(AuditingEntityListener.class)
@Entity
@DynamicInsert
@DynamicUpdate
@Where(clause = "is_delete = false")
public class CustActivityDetail extends BaseDO implements Serializable {
    private static final long serialVersionUID = 8454105705331289045L;

    @ApiModelProperty(value = "主键ID")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ApiModelProperty(value = "品牌ID")
    private Long brandId;

    @ApiModelProperty(value = "机构id")
    private Long orgId;

    @ApiModelProperty(value = "店铺ID")
    private String shopId;

    @ApiModelProperty(value = "用户ID")
    private String customerId;

    @ApiModelProperty(value = "活动ID")
    private String activityId;

    @ApiModelProperty(value = "记录状态：1.冻结 2.解冻 3.核销")
    private Integer recordStatus;
    @ApiModelProperty(value = "优惠码")
    private String couponCode;
}
