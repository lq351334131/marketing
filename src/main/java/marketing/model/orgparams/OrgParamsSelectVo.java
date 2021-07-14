package marketing.model.orgparams;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.etocrm.database.annotation.QueryFileds;
import org.etocrm.database.enums.QueryType;

import java.io.Serializable;

/**
 * @description: 机构门店参数查询
 * @author xingxing.xie
 * @date 2021/5/31 13:56
 * @version 1.0
 */
@ApiModel(value = "机构门店参数查询")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class OrgParamsSelectVo implements Serializable {

    private static final long serialVersionUID = 3541581197279747852L;
    @ApiModelProperty(value = "主键id")
    @QueryFileds
    private Long id;

    @ApiModelProperty(value = "活动标识码")
    @QueryFileds
    private String activityCode;

    @ApiModelProperty(value = "机构id")
    @QueryFileds
    private Long orgId;

    @ApiModelProperty(value = "机构名称",required = true)
    @QueryFileds(type = QueryType.LIKE)
    private String orgName;

}
