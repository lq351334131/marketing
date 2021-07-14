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
 * @author xingxing.xie
 * @since 2021-03-19
 */
@ApiModel(value = "营销名称关系表")
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
public class ActivityTypeDetail extends BaseDO implements Serializable {

    private static final long serialVersionUID = -8406122175784117608L;
    @ApiModelProperty(value = "主键id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ApiModelProperty(value = "机构id")
    private Long orgId;
    @ApiModelProperty(value = "活动类型ID")
    private Integer activityTypeId;
    @ApiModelProperty(value = "活动类型名称")
    private String activityTypeName;

    @ApiModelProperty(value = "关联商品颗粒度")
    private String productGranularity;
    @ApiModelProperty(value = "商品不互斥时相同时间段是否支持多个(1是、0否)")
    private Boolean isStackable;

}