package marketing.constant;

/**
 * 营销优惠计算 相关常量
 *
 * @Author xingxing.xie
 * @Date 2021/3/30 11:24
 */
public interface DiscountConstant {
    /**
     * 优惠计量方式 1 - 按钱，
     */
    int DISCOUNT_TYPE_MONEY = 1;
    /**
     * 优惠计量方式 2 - 按件
     */
    int DISCOUNT_TYPE_QUANTITY = 2;
    /**
     * 优惠方式 1-打折，
     */
    int DISCOUNT_MODEL_RATIO = 1;
    /**
     * 优惠方式 2-减价】
     */
    int DISCOUNT_MODEL_REDUCE = 2;
    /**
     * 优惠设置 ：1-阶梯设置
     */
    int DISCOUNT_SET_GRADE = 1;
    /**
     * 优惠设置 ：  2-循环设置
     */
    int DISCOUNT_SET_CYCLIC = 2;
    /**
     * # 折前折后价    after-折后
     */
    String PRICE_CONDITION_AFTER = "after";
    /**
     * # 折前折后价  before-折前
     */
    String PRICE_CONDITION_BEFORE = "before";
    /**
     * # 商品原价
     */
    String PRICE_CONDITION_ORIGIN_PRICE = "before";
    /**
     * # 商品销售价
     */
    String PRICE_CONDITION_SALE_PRICE = "after";

    /**
     * # 数据库字符串 分隔符
     */
    String SPLIT_REGEX = ",";
    /**
     * # 品牌是否可参与该活动 1可参与
     */
    int BRAND_PARTAKE= 1;
    /**
     * # 品牌是否可参与该活动 0不可参与
     */
    int BRAND_NO_PARTAKE = 0;
    /**
     * #   1兼容
     */
    int  ACTIVITY_STACKABLE = 1;
    /**
     * # 0互斥
     */
    int ACTIVITY_MUTEX = 0;

    /**
     * #活动未命中原因  互斥
     */
    String REASON_MUTEX= "REASON_MUTEX";
    /**
     * # 未命中原因  未达标
     */
    String REASON_UN_REACH = "REASON_UN_REACH";
    /**
     * #活动未命中原因  互斥
     */
    int GIFT_FIXED= 0;
    /**
     * # 未命中原因  未达标
     */
    int GIFT_SELECTABLE = 1;
    /****************************优惠码*******************************/

    /**
     * # 优惠码   满减
     */
    String CODE_REDUCTION = "reduction";
    /**
     * # 优惠码   满折
     */
    String CODE_DISCOUNT = "discount";
    /**
     * # 优惠码   一口价
     */
    String CODE_ONE_PRICE = "OnePrice";
    /**
     * # 储蓄卡特定优惠码活动，该情况仅适用当前一种活动
     */
    String USED_ALONE = "USED_ALONE";

    /**
     * # 活动关联全部商品是  商品id 为 -1
     */
    String ALL_PRODUCT_SIGNAL = "-1";

}
