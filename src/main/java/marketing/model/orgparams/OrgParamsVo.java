package marketing.model.orgparams;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @Description: 活动规则列表数据同步类
 * @Author xingxing.xie
 * @Date 2021/3/23 15:00
 */
@ApiModel(value = "机构门店参数")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class OrgParamsVo implements Serializable {

    private static final long serialVersionUID = 6256249636727384133L;
    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "活动标识码",required = true)
    private String activityCode;

    @ApiModelProperty(value = "机构id",required = true)
    private Long orgId;

    @ApiModelProperty(value = "机构名称")
    private String orgName;


}
