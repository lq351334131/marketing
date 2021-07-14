package marketing.model.activitytype;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @Author xingxing.xie
 * @Date 2021/3/26 14:46
 */
@ApiModel(value = "营销名称关系表")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ActivityTypeDetailVo implements Serializable {

    private static final long serialVersionUID = -774876132763202847L;
    @ApiModelProperty(value = "主键id")
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
