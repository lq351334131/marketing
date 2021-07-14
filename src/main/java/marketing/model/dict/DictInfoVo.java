package marketing.model.dict;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Author chengrong.yang
 * @Date 2021-03-04 10:55:40
 */
@ApiModel(value = "数据字典")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class DictInfoVo implements Serializable {

    private static final long serialVersionUID = -78432254857477554L;

    @ApiModelProperty(value = "主键" )
    private Long id;

    @ApiModelProperty(value = "字典项ID", required = true )
    @NotNull(message = "字典项ID不能为空")
    private Long typeId;

    @ApiModelProperty(value = "编码" )
    private String code;

    @ApiModelProperty(value = "名称" )
    private String name;

    @ApiModelProperty(value = "值" )
    private String value;

    @ApiModelProperty(value = "描述" )
    private String desc;

    @ApiModelProperty(value = "状态" )
    private Boolean status;

}