package marketing.enums;

/**
 * @Author: dkx
 * @Date: 13:58 2021/4/14
 * @Desc:
 */
public enum CouponTypeEnum {

    ZERO(0,"只为防止空指针"),

    //优惠券类型 1.折扣券 2.代金券 3.团购券 4.兑换券 5.优惠券 6.加价券 7.满折券 8.第二件折扣券
    COUPON_TYPE_DISCOUNT(1,"折扣卷"),
    COUPON_TYPE_VOUCHER(2,"代金券"),
    COUPON_TYPE_GROUPBUYING(3,"团购券"),
    COUPON_TYPE_EXCHANGE(4,"兑换券"),
    COUPON_TYPE_COUPON(5,"优惠券"),
    COUPON_TYPE_ADD(6,"加价券"),
    COUPON_TYPE_FULLFOLD(7,"满折券"),
    COUPON_TYPE_SECOND(8,"第二件折扣券"),

    ;

    private Integer code;
    private String value;

    CouponTypeEnum(Integer code, String value) {
        this.code = code;
        this.value = value;
    }

    public Integer getCode() {
        return code;
    }


    public String getValue() {
        return value;
    }

    public static CouponTypeEnum getEnumByValue(Integer value) {
        if(null==value){
            return CouponTypeEnum.ZERO;
        }
        for (CouponTypeEnum typeEnum:CouponTypeEnum.values()){
            if(typeEnum.code.equals(value)){
                return typeEnum;
            }
        }
        return CouponTypeEnum.ZERO;

    }
}
