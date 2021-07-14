package marketing.model.discount;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.etocrm.marketing.model.giftinfo.GiftInfoVo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Author xingxing.xie
 * @Date 2021/3/29 15:02
 */
@ApiModel("命中的规则信息")
@Data
public class AimRulesInfo implements Serializable {
    private static final long serialVersionUID = 955136952907789698L;

    @ApiModelProperty(value = "享受的优惠值")
    private BigDecimal discountValue=BigDecimal.ZERO;
    @ApiModelProperty(value = "计算命中规则")
    private CampaignInfo campaignInfo;
    @ApiModelProperty(value = "计算命中规则ID")
    private String campaignId;

    @ApiModelProperty(value = "优惠码命中的商品sku")
    private String systemSku;


    @ApiModelProperty(value = "固定赠品信息")
    private List<GiftInfoVo> fixedGifts;

    @ApiModelProperty(value = "可选赠品信息")
    private List<GiftInfoVo> selectedGifts;
    @ApiModelProperty(value = "可选赠品最多选择次数")
    private Integer selectedGiftNum;



}
