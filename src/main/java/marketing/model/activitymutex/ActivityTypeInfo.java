package marketing.model.activitymutex;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @Author xingxing.xie
 * @Date 2021/4/7 16:16
 */
@ApiModel("活动类型详情")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ActivityTypeInfo implements Serializable {
    private static final long serialVersionUID = -5600608499988107414L;

    @ApiModelProperty(value = "营销活动类型",required = true)
    private String nid;
    @ApiModelProperty(value = "营销活动名称",required = true)
    private String name;
    @ApiModelProperty(value = "营销活动model",required = true)
    private String model;
    @ApiModelProperty(value = "品牌是否可参与该活动 1可参与 0不可参与",required = true)
    private String partake;
}
