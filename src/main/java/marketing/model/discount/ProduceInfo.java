package marketing.model.discount;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.etocrm.marketing.model.discount.prosub.ProList;
import org.etocrm.marketing.model.discount.prosub.SubList;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author xingxing.xie
 * @Date 2021/3/29 15:01
 */
@ApiModel("商品信息")
@Data
public class ProduceInfo implements Serializable {

    private static final long serialVersionUID = 4179136153255813189L;
    @ApiModelProperty(value = "SKU id" , required = true)
    private String skuId;

    @ApiModelProperty(value = "pos售价")
    private BigDecimal posPrice;

    @ApiModelProperty(value = "pos原价")
    private BigDecimal showPrice;

    @ApiModelProperty(value = "pos应付金额")
    private BigDecimal amountPrice;

    @ApiModelProperty(value = "该数量商品总优惠的价（单位元）")
    private BigDecimal discountedPrice=BigDecimal.ZERO;

    @ApiModelProperty(value = "SPU id" , required = true)
    private String spuId;

    @ApiModelProperty(value = "商品数量" , required = true)
    private BigDecimal goodsCount;

    @ApiModelProperty(value = "已享受过优惠活动类型")
    private List<String> processedDiscountTypes=new ArrayList<>();

    @ApiModelProperty(value = "已享受过优惠卷id")
    private List<String> couponIds=new ArrayList<>();

    @ApiModelProperty(value = "已享受过优惠活动信息")
    private List<CampaignInfo> processedDiscountAct=new ArrayList<>();


    @ApiModelProperty(value = "当前 享受过活动类型 如：满减、团购")
    private String currentActivityType;

    /************************优惠码相关字段**************************/
    /**
     * 系统 sku
     */
    private String systemSku;
    /**
     * 一口价  json 字符串
     */
    private String info;

    /***********************************仅作 返回 不做其他操作******************************************/
    @ApiModelProperty(value = "SKU 主键ID")
    private Long skuKeyId;

    @ApiModelProperty(value = "SPU 主键ID")
    private Long spuKeyId;

    @ApiModelProperty(value = "辅料价格")
    private BigDecimal subPrice;

    @ApiModelProperty(value = "工艺数组" )
    private List<ProList> proLists;

    @ApiModelProperty(value = "辅料数组" )
    private List<SubList> subLists;
}
