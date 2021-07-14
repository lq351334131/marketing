package marketing.model.dict;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.etocrm.database.annotation.QueryFileds;
import org.etocrm.database.util.PageVO;

import java.io.Serializable;

/**
 * @Author chengrong.yang
 * @Date 2021-03-04 10:56:59
 */
@ApiModel(value = "数据字典项分页查询")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class DictListPageVo extends PageVO implements Serializable {

    private static final long serialVersionUID = -6887858999053944918L;

    @ApiModelProperty(value = "门店ID" )
    @QueryFileds
    private Long brandsId;

    @ApiModelProperty(value = "公司ID" )
    @QueryFileds
    private Long corpId;

    @ApiModelProperty(value = "机构ID" )
    @QueryFileds
    private Long orgId;

    @ApiModelProperty(value = "门店ID" )
    @QueryFileds
    private Long storeId;

    @ApiModelProperty(value = "名称" )
    @QueryFileds
    private String name;

    @ApiModelProperty(value = "状态" )
    @QueryFileds
    private Boolean status;
}