package marketing.constant;

import lombok.Getter;

/**
 * @Author xingxing.xie
 * @Date 2021/3/30 10:15
 */
@Getter
public enum ActivityTypeEnum {
    /**
     *  限时折扣
     */
    OTHER_TYPE("OtherType","其他活动",-1),
    /**
     *  限时折扣
     */
    LIMIT_DISCOUNT("LimitDiscount","限时折扣",1),
    /**
     *  满减满折券
     */
    FULL_DISCOUNT("FullDiscount","满减/满折",2),
    /**
     *  满赠
     */
    FULL_GIVEN("FullGive","满赠",3),
    /**
     *  优惠券
     */
    COUPON("Coupon","优惠券",13),
    /**
     *  优惠码
     */
    PROMOTION_CODE("PromotionCode","优惠码",38);

    private String code;
    private String name;
    private Integer value;

     ActivityTypeEnum(String code,String name,Integer value) {
         this.code = code;
         this.name=name;
         this.value = value;

    }

    public static ActivityTypeEnum getEnumByValue(Integer value) {
         if(null==value){
             return OTHER_TYPE;
         }
        for (ActivityTypeEnum typeEnum:ActivityTypeEnum.values()){
            if(typeEnum.value.equals(value)){
                return typeEnum;
            }

        }
        return OTHER_TYPE;

    }

}
