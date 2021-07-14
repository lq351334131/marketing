package marketing.model.activitygoods;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author admin
 * @since 2021-03-19
 */
@ApiModel(value = "活动与商品数据同步入参 ")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ActivityUnionGoodVo  implements Serializable {

    private static final long serialVersionUID = -3089256652705190530L;

    @ApiModelProperty(value = "活动code或ID",required = true)
    private String activityId;
    @ApiModelProperty(value = "机构ID",required = true)
    private Long orgId;
    @ApiModelProperty(value = "店铺ID",required = true)
    private List<String> shopIds;
    @ApiModelProperty(value = "总记录数")
    private Integer count;
    @ApiModelProperty(value = "总记录数")
    private Integer page;
    @ApiModelProperty(value = "当页")
    private Integer size;

    @ApiModelProperty(value = "商品详细信息",required = true)
    private List<UnionGoodDetail> goods;




}