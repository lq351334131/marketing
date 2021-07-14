package marketing.enums;

/**
 * @Author: dkx
 * @Date: 13:58 2021/4/14
 * @Desc:
 */
public enum CouponEnum {

    COUPON(13,"优惠卷"),

    IS_DISCOUNT_FRONT(0,"折前"),
    IS_DISCOUNT_AFTER(1,"折后"),

//    IS_ALL_STORE_ALL(1,"全部门店"),
//    IS_ALL_STORE_APPOINT(0,"指定门店"),
//    IS_ALL_STORE_NOT(2,"无适应门店"),

    IS_ALL_GOODS_ALL(1,"全部商品"),
    IS_ALL_GOODS_APPOINT(0,"指定商品"),
    IS_ALL_GOODS_NOT(2,"反选商品"),

    //是否叠加使用 0.不能叠加 1.可以叠加 coupon_type = 2 时专属
    IS_OVER_OK(1,"可以叠加"),
    IS_OVER_NOT(0,"不能叠加"),

//    //使用渠道 1.官网 2.APP 3.H5 4.微信 5.etoshop 6.线下渠道 7.自助核销
//    USED_TYPE_OFFICIALWEBSITE(1,"官网"),
//    USED_TYPE_APP(2,"APP"),
//    USED_TYPE_H5(3,"H5"),
//    USED_TYPE_WECHAT(4,"微信"),
//    USED_TYPE_ETOSHOP(5,"etoshop"),
//    used_type_offline(6,"线下渠道"),
//    USED_TYPE_SELFHELP(7,"自助核销"),
//
//    //is_activity_over	int	0.不与店铺促销同享 1.与店铺促销同享
//    is_activity_over_not(0,"不与店铺促销同享"),
//    is_activity_over_ok(1,"与店铺促销同享"),
//
//    //is_member_discount	int	0.不与会员折扣同享 1.与会员折扣同享
//    is_member_discount_not(0,"不与会员折扣同享"),
//    is_member_discount_ok(1,"与会员折扣同享"),
//
//    //is_non_positive	int	‘0.不与非正价商品同享 1.与非正价商品同享
//    is_non_positive_not(0,"不与非正价商品同享"),
//    is_non_positive_ok(1,"与非正价商品同享"),


    ;

    private Integer code;
    private String value;

    CouponEnum(Integer code, String value) {
        this.code = code;
        this.value = value;
    }

    public Integer getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

}
