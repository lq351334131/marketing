package marketing.model.dict;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @Author chengrong.yang
 * @Date 2021-03-04 10:56:59
 */
@ApiModel(value = "数据字典项")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class DictListVo implements Serializable {

    private static final long serialVersionUID = -19652781474892494L;

    @ApiModelProperty(value = "主键" )
    private Long id;

    @ApiModelProperty(value = "门店ID" )
    private Long brandsId;

    @ApiModelProperty(value = "公司ID" )
    private Long corpId;

    @ApiModelProperty(value = "机构ID" )
    private Long orgId;

    @ApiModelProperty(value = "门店ID" )
    private String storeId;

    @ApiModelProperty(value = "编码" )
    private String code;

    @ApiModelProperty(value = "名称" )
    private String name;

    @ApiModelProperty(value = "状态" )
    private Boolean status;

    @ApiModelProperty(value = "描述" )
    private String desc;

}