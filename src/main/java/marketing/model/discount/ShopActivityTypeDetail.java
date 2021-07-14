package marketing.model.discount;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @Author xingxing.xie
 * @Date 2021/4/15 15:54
 */
@ApiModel("门店下活动类型信息")
@Data
@Accessors(chain = true)
public class ShopActivityTypeDetail implements Serializable {

    private static final long serialVersionUID = -2713338411145014525L;

    @ApiModelProperty(value = "机构id")
    private Long orgId;
    @ApiModelProperty(value = "门店id")
    private String shopId;
    @ApiModelProperty(value = "活动类型ID")
    private Integer activityTypeId;
    @ApiModelProperty(value = "活动类型名称")
    private String activityTypeName;
    @ApiModelProperty(value = "是否可用 true可用/false不可用")
    private Boolean enableFlag;
}
