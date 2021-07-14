package marketing.model.discount;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author xingxing.xie
 * @Date 2021/3/29 15:00
 */
@ApiModel("用户信息")
@Data
public class CustomerInfo implements Serializable {
    private static final long serialVersionUID = -8503800530236064377L;
    @ApiModelProperty(value = "会员ID")
    private String customerId;

    @ApiModelProperty(value = "会员等级名称")
    private String customerLevelName;

    @ApiModelProperty(value = "等级Id")
    private String customerLevelId;
}
