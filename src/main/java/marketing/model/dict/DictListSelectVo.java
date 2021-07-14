package marketing.model.dict;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.etocrm.database.annotation.QueryFileds;

import java.io.Serializable;

/**
 * @Author chengrong.yang
 * @Date 2021-03-04 10:56:59
 */
@ApiModel(value = "数据字典项查询")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class DictListSelectVo implements Serializable {

    private static final long serialVersionUID = 7444944103457229594L;

    @ApiModelProperty(value = "主键" )
    @QueryFileds
    private Long id;

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
    private String storeId;

    @ApiModelProperty(value = "名称" )
    @QueryFileds
    private String name;

    @ApiModelProperty(value = "状态" )
    @QueryFileds
    private Boolean status;

}