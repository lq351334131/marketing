package marketing.model.activitygoods;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.etocrm.database.annotation.QueryFileds;
import org.etocrm.database.enums.QueryType;

import java.io.Serializable;
import java.util.List;

/**
 * @Description: 活动关联商品查询
 * @Author xingxing.xie
 * @Date 2021/3/29 19:20
 */
@ApiModel(value = "活动关联商品查询")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ActivityGoodsSelectVo implements Serializable {

    private static final long serialVersionUID = 718684805017566414L;
    @ApiModelProperty(value = "主键id")
    @QueryFileds
    private Long id;

    @ApiModelProperty(value = "商品ID")
    @QueryFileds(type = QueryType.IN)
    private List<String> productId;

    @ApiModelProperty(value = "活动code或ID")
    @QueryFileds
    private String activityCode;

    @ApiModelProperty(value = "品牌ID")
    @QueryFileds
    private Long brandId;
    @ApiModelProperty(value = "品牌ID")
    @QueryFileds
    private Long orgId;

    @ApiModelProperty(value = "门店ID")
    @QueryFileds
    private String shopId;
}
