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

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Author chengrong.yang
 * @Date 2021-03-04 10:55:40
 */
@ApiModel(value = "数据字典分页查询")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class DictInfoPageVo extends PageVO implements Serializable {

    private static final long serialVersionUID = 6274866721447704245L;

    @ApiModelProperty(value = "字典项ID", required = true )
    @NotNull(message = "字典项ID不能为空")
    @QueryFileds
    private Long typeId;

    @ApiModelProperty(value = "编码" )
    @QueryFileds
    private String code;

    @ApiModelProperty(value = "名称" )
    @QueryFileds
    private String name;

    @ApiModelProperty(value = "值" )
    @QueryFileds
    private String value;

    @ApiModelProperty(value = "状态" )
    @QueryFileds
    private Boolean status;
}