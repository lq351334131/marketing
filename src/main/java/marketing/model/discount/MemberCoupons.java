package marketing.model.discount;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Author xingxing.xie
 * @Date 2021/3/31 10:08
 */
@Data
public class MemberCoupons implements Serializable {
    private static final long serialVersionUID = 439722079896538170L;

    private Integer id;
    private Integer memberId;
    private String userName;
    private String openid;
    private Integer brandId;
    private String appid;
    private Integer couponId;
    private Integer vipLevelId;
    private String vipLevel;
    private String mobile;
    private String couponName;
    private String cardId;
    private String cardCode;
    private Integer couponType;
    private Integer type;
    private String source;
    private String receiveTime;
    private String startTime;
    private String endTime;
    private String consumeTime;
    private String showEnd;
    private String consumeStore;
    private Integer deliveryId;
    private Integer status;
    private String createdAt;
    private String updatedAt;
    private String orderNo;
    private String couponUseChannel;
    private Integer isReceiveWechatCard;
    private String getCouponSourceId;
    private String title;
    private String subTitle;
    private String channel;
    private String notice;
    private String servicePhone;
    private String defaultDetail;
    private Integer quantity;
    private Integer getLimit;
    private Integer isAllStore;
    private Integer isAllCompany;
    private Integer isAllGoods;
    private BigDecimal reduceCost;
    private BigDecimal leastCost;
    private String gift;
    private BigDecimal discount;
    private BigDecimal discountCost;
    private String description;
    private Integer isOver;
    private Integer isPoint;
    private Integer isRank;
    private BigDecimal goodsNum;
    private BigDecimal fareIncrease;
    private List<Integer> usedType;
    private Integer useCondition;
    private Integer staff;
    private Integer isAllStoreEto;
    private Integer companyStoreEto;
    private Integer isAllCompanyEto;
    private String officalCouponId;
    private Integer isActivityOver;
    private Integer isMemberDiscount;
    private Integer isNonPositive;
    private Integer isDiscount;
    private Integer canGiveFriend;
    private Integer expireDay;
    private List<Map<String,String>> goods;
    private List<String> stores;
    private String company;
    private String storesEto;
    private String companyEto;
    private String reason;
    private BigDecimal couponsPrice;
    private String message;
}
