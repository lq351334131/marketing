package marketing.model.discount.activityrule;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.etocrm.marketing.model.giftinfo.GiftInfoVo;

import java.io.Serializable;
import java.util.List;

/**
 * @Author xingxing.xie
 * @Date 2021/3/30 11:38
 */
@ApiModel("优惠门槛值")
@Data
public class ConditionExplain implements Serializable {
    private static final long serialVersionUID = 7447436158697039390L;

    /**
     * 优惠值
     */
    @ApiModelProperty(value = "优惠值",required = true)
    private Integer discountVal;
    /**
     * 优惠方式【 1-打折，2-减价】
     */
    @ApiModelProperty(value = "优惠方式【 1-打折，2-减价】",required = true)
    private Integer discountModel;
    /**
     * 折前折后价  before-折前  after-折后
     */
    @ApiModelProperty(value = "折前、折后价",required = true)
    private String priceCondition;

    /****************************满赠情况***************************************/
    @ApiModelProperty(value = "赠品信息")
    private List<GiftInfoVo> gifts;

    @ApiModelProperty(value = "冗余字段")
    private String score;

    @ApiModelProperty(value = "是否可选赠品")
    private Integer isGiven;
    @ApiModelProperty(value = "最多赠送几件")
    private Integer giftsNum;

    @ApiModelProperty(value = "赠品类型：固定、可选")
    private String giftType;
}
