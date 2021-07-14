package marketing.model.activitymutex;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.etocrm.database.annotation.QueryFileds;
import org.etocrm.database.enums.QueryType;

import java.io.Serializable;
import java.util.List;

/**
 * @Author xingxing.xie
 * @Date 2021/3/30 18:05
 */
@ApiModel("活动互斥关系查询")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ActivityTypeMutexSelectVo implements Serializable {
    private static final long serialVersionUID = -1062556875114579196L;

    @ApiModelProperty(value = "主键id")
    @QueryFileds
    private Long id;

    @ApiModelProperty(value = "活动id")
    @QueryFileds(type = QueryType.IN)
    private List<Integer> activityType;

    @ApiModelProperty(value = "品牌ID")
    @QueryFileds
    private Long brandId;

    @ApiModelProperty(value = "机构id")
    @QueryFileds
    private Long orgId;

    @ApiModelProperty(value = "店铺ID")
    @QueryFileds
    private String shopId;

    @ApiModelProperty(value = "规则ID")
    @QueryFileds
    private String configId;

    @ApiModelProperty(value = "活动名称")
    @QueryFileds(type=QueryType.LIKE)
    private String activityName;

    @ApiModelProperty(value = "活动类型标识码")
    @QueryFileds
    private String model;

    @ApiModelProperty(value = "活动计算顺序")
    @QueryFileds
    private Integer activityOrder;

    @ApiModelProperty(value = "可叠加活动id数组 该字段存放该活动所有可叠加的活动id")
    @QueryFileds
    private String stackableIds;

    @ApiModelProperty(value = "互斥的活动id数组")
    @QueryFileds
    private String mutexIds;
}
