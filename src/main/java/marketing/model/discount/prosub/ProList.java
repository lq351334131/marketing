package marketing.model.discount.prosub;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

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
public class ProList implements Serializable {

    private static final long serialVersionUID = 4274672002585961251L;
    @ApiModelProperty(value = "工艺模板id" )
    private Long id;

    @ApiModelProperty(value = "工艺模板名称" )
    private String proName;

    @ApiModelProperty(value = "工艺id" )
    private Long proId;

    @ApiModelProperty(value = "工艺名称" )
    private String name;

}
