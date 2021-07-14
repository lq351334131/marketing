package marketing.model.activitydetail;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.etocrm.database.annotation.QueryFileds;

import java.io.Serializable;

/**
 * @Description: 用户活动明细表查询
 * @Author xingxing.xie
 * @Date 2021/3/29 17:50
 */
@ApiModel(value = "活动规则列表查询")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class CustActDetailSelectVo implements Serializable {

    private static final long serialVersionUID = 8317756011510659691L;

    @ApiModelProperty(value = "主键id")
    @QueryFileds
    private Long id;

    @ApiModelProperty(value = "品牌ID")
    @QueryFileds
    private Long brandId;

    @ApiModelProperty(value = "机构id")
    @QueryFileds
    private Long orgId;

    @ApiModelProperty(value = "店铺ID")
    @QueryFileds
    private String shopId;

    @ApiModelProperty(value = "用户ID")
    @QueryFileds
    private String customerId;

    @ApiModelProperty(value = "活动ID")
    @QueryFileds
    private String activityId;

    @ApiModelProperty(value = "记录状态：1.冻结 2.核销 3.完成")
    @QueryFileds
    private Integer recordStatus;

}
