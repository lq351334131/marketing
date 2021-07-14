package marketing.model.discount;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author xingxing.xie
 * @Date 2021/3/31 10:15
 */
@Data
@ApiModel("会员信息")
public class MemberDetail implements Serializable {
    private static final long serialVersionUID = -3291263650776712739L;

    @ApiModelProperty(value = "会员id",required = true)
    private String id;
    @ApiModelProperty(value = "会员等级id",required = true)
    private Integer vipLevelId;
    @ApiModelProperty("会员等级名称")
    private String vipLevel;

}
