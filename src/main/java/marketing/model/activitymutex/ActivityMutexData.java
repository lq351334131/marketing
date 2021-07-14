package marketing.model.activitymutex;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @Author xingxing.xie
 * @Date 2021/4/7 16:09
 */
@ApiModel("活动类型互斥请求参数")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ActivityMutexData implements Serializable {
    private static final long serialVersionUID = -7461655596578546126L;

    @ApiModelProperty(value = "品牌ID",required = true)
    private Long brandId;

    @ApiModelProperty(value = "机构id",required = true)
    private Long organizationId;

    @ApiModelProperty(value = "分发的门店id",required = true)
    private List<String> shopIds;

    @ApiModelProperty(value = "规则ID",required = true)
    private String configId;

    @ApiModelProperty(value = "活动计算顺序",required = true)
    private List<ActivityTypeInfo> paymentRule;

    @ApiModelProperty(value = "营销活动互斥关系",required = true)
    private Map<String,Map<String,Integer>> marketingRelation;


}
