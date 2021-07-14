package marketing.constant;

/**
 * @Author xingxing.xie
 * @Date 2021/4/26 16:01
 */
public enum ActivityDiscountErrorEnum {
    /**
     * 该活动无规则数据
     */
    NO_ACT_RULE_DATA(4398, "该活动无规则数据"),
    /**
     * 优惠码操作失败
     */
    COUPON_CODE_ERROR_ENUM(4399, "优惠码操作失败");

    private final Integer code;
    private final String message;

     ActivityDiscountErrorEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }
}
