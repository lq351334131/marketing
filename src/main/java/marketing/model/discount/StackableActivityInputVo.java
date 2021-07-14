package marketing.model.discount;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @Author xingxing.xie
 * @Date 2021/4/13 13:58
 */
@Data
@ApiModel
@Accessors(chain = true)
public class StackableActivityInputVo implements Serializable {

    private static final long serialVersionUID = 4979381206017377637L;

    @ApiModelProperty(value = "机构ID",required = true)
    private Long orgId;
    @ApiModelProperty(value = "门店ID",required = true)
    private String shopId;
    @ApiModelProperty(value = "活动ID")
    private List<String> activityIds;
    @ApiModelProperty(value = "活动ID")
    private List<String> couponIds;

    @ApiModelProperty(value = "会员详情",required = true)
    private MemberDetail memberDetail;
}
