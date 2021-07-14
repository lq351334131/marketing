package marketing.model.activitymutex;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @Author xingxing.xie
 * @Date 2021/3/26 14:46
 */
@ApiModel(value = "活动类型互斥列表")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ActivityTypeMutexVo implements Serializable {
    private static final long serialVersionUID = -3655828074583095508L;

    @ApiModelProperty(value = "主键id")
    private Long id;


    @ApiModelProperty(value = "活动类型")
    private Integer activityType;

    @ApiModelProperty(value = "品牌ID")
    private Long brandId;

    @ApiModelProperty(value = "机构id")
    private Long orgId;

    @ApiModelProperty(value = "店铺ID")
    private String shopId;

    @ApiModelProperty(value = "规则ID")
    private String configId;

    @ApiModelProperty(value = "活动类型标识码")
    private String model;

    @ApiModelProperty(value = "活动名称")
    private String activityName;

    @ApiModelProperty(value = "活动计算顺序")
    private Integer activityOrder;

    @ApiModelProperty(value = "可叠加活动id数组 该字段存放该活动所有可叠加的活动id")
    private List<String> stackableIds;

    @ApiModelProperty(value = "互斥的活动id数组")
    private List<String> mutexIds;

    @ApiModelProperty(value = "该门店是否能参与：1是/0否")
    private Integer partake;
}
