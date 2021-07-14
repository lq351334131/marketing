package marketing.model.discount.prosub;



import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Set;

/**
 * @description: 仅做返回
 * @author xingxing.xie
 * @date 2021/4/23 15:16
 * @version 1.0
 */
@Data
@ApiModel(value = "销售订单基础信息")
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class SubList implements Serializable {

    private static final long serialVersionUID = 6448986972712829391L;
    @ApiModelProperty(value = "辅料模板id" )
    private Long id;

    @ApiModelProperty(value = "辅料模板名称" )
    private String subName;

    @ApiModelProperty(value = "辅料ids" )
    private Set<SubDetailVO> subId;
}
