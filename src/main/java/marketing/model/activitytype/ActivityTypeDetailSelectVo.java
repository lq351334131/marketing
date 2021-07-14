package marketing.model.activitytype;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.etocrm.database.annotation.QueryFileds;

import java.io.Serializable;

/**
 * @Author xingxing.xie
 * @Date 2021/3/30 18:05
 */
@ApiModel("营销名称关系表查询")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ActivityTypeDetailSelectVo implements Serializable {

    private static final long serialVersionUID = -8881170678502656129L;
    @ApiModelProperty(value = "主键id")
    @QueryFileds
    private Long id;

    @ApiModelProperty(value = "机构id")
    @QueryFileds
    private Long orgId;
    @ApiModelProperty(value = "活动类型ID")
    @QueryFileds
    private Integer activityTypeId;
    @ApiModelProperty(value = "活动类型名称")
    @QueryFileds
    private String activityTypeName;

    @ApiModelProperty(value = "关联商品颗粒度")
    @QueryFileds
    private String productGranularity;
    @ApiModelProperty(value = "商品不互斥时相同时间段是否支持多个(1是、0否)")
    @QueryFileds
    private Boolean isStackable;
}
