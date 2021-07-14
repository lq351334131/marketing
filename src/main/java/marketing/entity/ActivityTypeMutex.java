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
 * @author admin
 * @since 2021-03-19
 */
@ApiModel(value = "活动类型互斥列表 ")
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
public class ActivityTypeMutex extends BaseDO implements Serializable {

    private static final long serialVersionUID = -3821200374776099648L;

    @ApiModelProperty(value = "主键id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ApiModelProperty(value = "活动类型")
    private Integer activityType;
    @ApiModelProperty(value = "品牌ID")
    private Long brandId;

    @ApiModelProperty(value = "机构id")
    private Long orgId;
    @ApiModelProperty(value = "规则ID")
    private String configId;

    @ApiModelProperty(value = "店铺ID")
    private String shopId;

    @ApiModelProperty(value = "活动类型标识码")
    private String model;

    @ApiModelProperty(value = "活动名称")
    private String activityName;

    @ApiModelProperty(value = "活动计算顺序")
    private Integer activityOrder;

    @ApiModelProperty(value = "可叠加活动id数组 该字段存放该活动所有可叠加的活动id")
    private String stackableIds;

    @ApiModelProperty(value = "互斥的活动id数组")
    private String mutexIds;

    @ApiModelProperty(value = "该门店是否能参与：1是/0否")
    private Integer partake;


}