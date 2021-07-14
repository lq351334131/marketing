package marketing.model.activityrule;


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
 * @Date 2021/3/31 10:57
 */
@ApiModel(value = "活动规则列表查询")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ActivityRuleInfoSelectVo implements Serializable {
    private static final long serialVersionUID = 2460767711398502974L;

    @ApiModelProperty(value = "主键id")
    @QueryFileds
    private Long id;

    @ApiModelProperty(value = "活动id")
    @QueryFileds
    private String activityId;
    @ApiModelProperty(value = "活动id")
    @QueryFileds
    private Long orgId;
}
