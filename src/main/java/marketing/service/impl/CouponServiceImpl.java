package marketing.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.database.enums.ResponseEnum;
import org.etocrm.database.exception.MyException;
import org.etocrm.marketing.constant.ActivityTypeEnum;
import org.etocrm.marketing.enums.CouponEnum;
import org.etocrm.marketing.enums.CouponTypeEnum;
import org.etocrm.marketing.model.discount.DiscountInfoVo;
import org.etocrm.marketing.model.discount.MemberCoupons;
import org.etocrm.marketing.model.discount.ProcessInfo;
import org.etocrm.marketing.model.discount.ProduceInfo;
import org.etocrm.marketing.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: dkx
 * @Date: 17:08 2021/4/22
 * @Desc:
 */
@Service
@Slf4j
public class CouponServiceImpl implements CouponService {

    @Autowired
    DiscountCalculateServiceImpl discountCalculateService;

    @Override
    public DiscountInfoVo calculateCoupon(DiscountInfoVo discountInfoVo) throws MyException {
        // TODO: 2021/4/21 暂时不校验优惠卷得与店铺、会员、非正价问题
        if (CollectionUtil.isEmpty(discountInfoVo.getCouponsList()) ||
                CollectionUtil.isEmpty(discountInfoVo.getProduceInfos())) {
            return discountInfoVo;
        }
        log.info("进入优惠卷计算逻辑");
        //商品
        List<ProduceInfo> produceInfos = discountInfoVo.getProduceInfos();
        List<MemberCoupons> memberCoupons = discountInfoVo.getMemberCoupons();
        List<MemberCoupons> unMemberCoupons = discountInfoVo.getUnMemberCoupons();
        //优惠券 折扣金额
        BigDecimal multiply = BigDecimal.ZERO;
        //商品得编码list
        List<String> collect = produceInfos.stream().map(ProduceInfo::getSkuId).collect(Collectors.toList());
        Boolean flag1 = false;
        for (MemberCoupons item : discountInfoVo.getCouponsList()) {
            switch (CouponTypeEnum.getEnumByValue(item.getCouponType())) {
                case COUPON_TYPE_DISCOUNT:
                    log.info("2021/4/14 折扣卷");
                    multiply = multiply.add(this.getCouponTypeDiscountMap(item, produceInfos, collect, memberCoupons, unMemberCoupons));
                    break;
                case COUPON_TYPE_VOUCHER:
                    log.info("2021/4/14 代金卷");
                    if (!flag1) {
                        multiply = multiply.add(this.getCouponTypeVoucherMap(item, produceInfos, collect, memberCoupons, unMemberCoupons));
                        for (MemberCoupons memberCoupon : memberCoupons) {
                            if (memberCoupon.getId().equals(item.getId())) {
                                if (memberCoupon.getIsOver().equals(CouponEnum.IS_OVER_NOT.getCode())) {
                                    flag1 = true;
                                }
                            }
                        }
                    } else {
                        unMemberCoupons.add(item);
                    }

                    break;
                case COUPON_TYPE_FULLFOLD:
                    log.info("满折卷");
                    multiply = multiply.add(this.getCouponTypeFullFoldMap(item, produceInfos, collect, memberCoupons, unMemberCoupons));
                    break;
                case COUPON_TYPE_SECOND:
                    log.info("2021/4/14 第二件折扣卷");
                    multiply = multiply.add(this.getCouponTypeSecondMap(item, produceInfos, collect, memberCoupons, unMemberCoupons));
                    break;
                default:
                    throw new MyException(ResponseEnum.FAILD.getCode(), "优惠卷异常！");
            }

        }
        multiply = discountCalculateService.validDiscountValue(multiply);
        discountInfoVo.setTotalDiscountPrice(discountInfoVo.getTotalDiscountPrice().add(multiply));
        discountInfoVo.setPriceDiscountAfter(discountInfoVo.getPriceDiscountAfter().subtract(multiply));
        log.info("优惠卷计算后：最终优惠价格=" + discountInfoVo.getTotalDiscountPrice());
        log.info("优惠卷计算后：最终应付价格=" + discountInfoVo.getPriceDiscountAfter());
        return discountInfoVo;
    }

    /**
     * 折扣卷
     *
     * @param item
     * @param produceInfos
     * @param collect
     * @return
     */
    private BigDecimal getCouponTypeDiscountMap(MemberCoupons item, List<ProduceInfo> produceInfos,
                                                List<String> collect, List<MemberCoupons> memberCoupons, List<MemberCoupons> unMemberCoupons) {
        BigDecimal multiply = BigDecimal.ZERO;
        //全部商品
        if (item.getIsAllGoods().equals(CouponEnum.IS_ALL_GOODS_ALL.getCode())) {
            ProcessInfo processInfo = discountCalculateService.boxProcessInfo(produceInfos);
            if (processInfo.getTotalAmount().compareTo(item.getGoodsNum()) > -1) {
                //折前
                multiply = getDiscount(item, produceInfos, processInfo, memberCoupons, unMemberCoupons);
            } else {
                item.setMessage("商品数量不满足");
                unMemberCoupons.add(item);
            }
        } else if (item.getIsAllGoods().equals(CouponEnum.IS_ALL_GOODS_APPOINT.getCode())) {
            //指定商品
            List<String> goo = getAppointGoodsNumber(item, collect);
            //满足商品
            List<ProduceInfo> produceInfoList = getProduceInfosEnableAppointGoods(produceInfos, goo);
            ProcessInfo processInfo = discountCalculateService.boxProcessInfo(produceInfoList);
            if (processInfo.getTotalAmount().compareTo(item.getGoodsNum()) > -1) {
                //折前
                multiply = getDiscount(item, produceInfos, processInfo, memberCoupons, unMemberCoupons);
            } else {
                item.setMessage("商品数量不满足");
                unMemberCoupons.add(item);
            }
        } else if (item.getIsAllGoods().equals(CouponEnum.IS_ALL_GOODS_NOT.getCode())) {
            //反选商品
            List<String> goo = getAppointGoodsNumber(item, collect);
            //满足商品
            //List<ProduceInfo> produceInfoList = getProduceInfosGoodsNot(produceInfos, goo);
            List<ProduceInfo> produceInfoList = getProduceInfosEnableAppointGoods(produceInfos, goo);
            ProcessInfo processInfo = discountCalculateService.boxProcessInfo(produceInfoList);
            if (processInfo.getTotalAmount().compareTo(item.getGoodsNum()) > -1) {
                //折前
                multiply = getDiscount(item, produceInfos, processInfo, memberCoupons, unMemberCoupons);
            } else {
                item.setMessage("商品数量不满足");
                unMemberCoupons.add(item);
            }
        }

        return multiply.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    private BigDecimal getDiscount(MemberCoupons item, List<ProduceInfo> produceInfos, ProcessInfo processInfo,
                                   List<MemberCoupons> memberCoupons, List<MemberCoupons> unMemberCoupons) {
        BigDecimal multiply = BigDecimal.ZERO;
        BigDecimal salePriceBefore = processInfo.getSalePriceBefore();
        BigDecimal finalPayPrice = processInfo.getFinalPayPrice();
        Boolean flag = false;
        //启用金额//对比
        if (item.getIsDiscount().equals(CouponEnum.IS_DISCOUNT_FRONT.getCode()) && salePriceBefore.compareTo(item.getLeastCost()) > -1) {
            //折扣额度
            BigDecimal divide = item.getDiscount().divide(BigDecimal.TEN);
            BigDecimal subtract = BigDecimal.ONE.subtract(divide);
            //拿到优惠金额
            multiply = salePriceBefore.multiply(subtract);
            if (multiply.compareTo(finalPayPrice) > 0) {
                multiply = finalPayPrice;
            }
            multiply = getCompare(item, produceInfos, multiply, true);
            //记录 优惠金额
            memberCoupons.add(item);
            setGoodsCouponAndActivityType(item, produceInfos);
            flag = true;
        }
        //折后//启用金额//对比
        if (item.getIsDiscount().equals(CouponEnum.IS_DISCOUNT_AFTER.getCode()) && finalPayPrice.compareTo(item.getLeastCost()) > -1) {
            //折扣额度
            BigDecimal divide = item.getDiscount().divide(BigDecimal.TEN);
            BigDecimal subtract = BigDecimal.ONE.subtract(divide);
            //拿到优惠金额
            multiply = finalPayPrice.multiply(subtract);
            if (multiply.compareTo(finalPayPrice) > 0) {
                multiply = finalPayPrice;
            }
            multiply = getCompare(item, produceInfos, multiply, false);
            item.setCouponsPrice(multiply);
            memberCoupons.add(item);
            setGoodsCouponAndActivityType(item, produceInfos);
            flag = true;
        }
        if (!flag) {
            item.setMessage("启用金额不满足");
            unMemberCoupons.add(item);
        }
        return multiply;
    }

    private BigDecimal getCompare(MemberCoupons item, List<ProduceInfo> produceInfos, BigDecimal multiply, Boolean flag) {
        // 2021/4/14 最高折扣金额
        if (item.getDiscountCost().compareTo(BigDecimal.ZERO) == 0) {
            discountCalculateService.dutchDiscount(produceInfos, multiply, flag);
        } else {
            if (multiply.compareTo(item.getDiscountCost()) > 0) {
                //大于
                multiply = item.getDiscountCost();
                discountCalculateService.dutchDiscount(produceInfos, item.getDiscountCost(), flag);
            } else {
                //小于
                discountCalculateService.dutchDiscount(produceInfos, multiply, flag);
            }
        }
        return multiply;
    }

    private void setGoodsCouponAndActivityType(MemberCoupons item, List<ProduceInfo> produceInfos) {
        for (ProduceInfo produceInfo : produceInfos) {
            produceInfo.getCouponIds().add(item.getId().toString());
            produceInfo.getProcessedDiscountTypes().add(ActivityTypeEnum.COUPON.getValue().toString());
        }
    }

    private List<ProduceInfo> getProduceInfosGoodsNot(List<ProduceInfo> produceInfos, List<String> goo) {
        List<ProduceInfo> produceInfoList = new ArrayList<>();
        for (ProduceInfo produceInfo : produceInfos) {
            //满足商品
            if (!goo.contains(produceInfo.getSkuId())) {
                //满足
                produceInfoList.add(produceInfo);
            }
        }
        return produceInfoList;
    }

    private List<ProduceInfo> getProduceInfosEnableAppointGoods(List<ProduceInfo> produceInfos, List<String> goo) {
        List<ProduceInfo> produceInfoList = new ArrayList<>();
        for (ProduceInfo produceInfo : produceInfos) {
            //满足商品
            if (goo.contains(produceInfo.getSkuId())) {
                produceInfoList.add(produceInfo);
            }
        }
        return produceInfoList;
    }

    private List<String> getAppointGoodsNumber(MemberCoupons item, List<String> collect) {
        List<String> goo = new ArrayList<>();
        List<Map<String, String>> goods = item.getGoods();
        for (Map<String, String> good : goods) {
            String goodsNumber = good.get("goodsNumber");
            if (collect.contains(goodsNumber)) {
                goo.add(goodsNumber);
            }
        }
        return goo;
    }

    /**
     * 代金卷
     *
     * @param item
     * @param produceInfos
     * @param collect
     * @return
     */
    private BigDecimal getCouponTypeVoucherMap(MemberCoupons item, List<ProduceInfo> produceInfos, List<String> collect,
                                               List<MemberCoupons> memberCoupons, List<MemberCoupons> unMemberCoupons) {
        BigDecimal multiply = BigDecimal.ZERO;
        //全部商品
        if (item.getIsAllGoods().equals(CouponEnum.IS_ALL_GOODS_ALL.getCode())) {
            ProcessInfo processInfo = discountCalculateService.boxProcessInfo(produceInfos);
            if (processInfo.getTotalAmount().compareTo(item.getGoodsNum()) > -1) {
                //折前
                multiply = getVoucher(item, produceInfos, processInfo, memberCoupons, unMemberCoupons);
            } else {
                item.setMessage("商品数量不满足");
                unMemberCoupons.add(item);
            }
        } else if (item.getIsAllGoods().equals(CouponEnum.IS_ALL_GOODS_APPOINT.getCode())) {
            //指定商品
            List<String> goo = getAppointGoodsNumber(item, collect);
            //满足商品
            List<ProduceInfo> produceInfoList = getProduceInfosEnableAppointGoods(produceInfos, goo);
            ProcessInfo processInfo = discountCalculateService.boxProcessInfo(produceInfoList);
            if (processInfo.getTotalAmount().compareTo(item.getGoodsNum()) > -1) {
                multiply = getVoucher(item, produceInfos, processInfo, memberCoupons, unMemberCoupons);
            } else {
                item.setMessage("商品数量不满足");
                unMemberCoupons.add(item);
            }
        } else if (item.getIsAllGoods().equals(CouponEnum.IS_ALL_GOODS_NOT.getCode())) {
            //反选商品
            List<String> goo = getAppointGoodsNumber(item, collect);
            //满足商品
            //List<ProduceInfo> produceInfoList = getProduceInfosGoodsNot(produceInfos, goo);
            List<ProduceInfo> produceInfoList = getProduceInfosEnableAppointGoods(produceInfos, goo);
            ProcessInfo processInfo = discountCalculateService.boxProcessInfo(produceInfoList);
            if (processInfo.getTotalAmount().compareTo(item.getGoodsNum()) > -1) {
                //折前
                multiply = getVoucher(item, produceInfos, processInfo, memberCoupons, unMemberCoupons);
            } else {
                item.setMessage("商品数量不满足");
                unMemberCoupons.add(item);
            }
        }

        return multiply.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    private BigDecimal getVoucher(MemberCoupons item, List<ProduceInfo> produceInfos, ProcessInfo processInfo,
                                  List<MemberCoupons> memberCoupons, List<MemberCoupons> unMemberCoupons) {
        BigDecimal divide = BigDecimal.ZERO;
        BigDecimal salePriceBefore = processInfo.getSalePriceBefore();
        BigDecimal finalPayPrice = processInfo.getFinalPayPrice();
        Boolean flag = false;
        //折前 //启用金额//对比
        if (item.getIsDiscount().equals(CouponEnum.IS_DISCOUNT_FRONT.getCode()) && salePriceBefore.compareTo(item.getLeastCost()) > -1) {
            //减免金额
            divide = item.getReduceCost();
            if (divide.compareTo(finalPayPrice) > 0) {
                divide = finalPayPrice;
            }
            discountCalculateService.dutchDiscount(produceInfos, divide, true);
            item.setCouponsPrice(divide);
            memberCoupons.add(item);
            setGoodsCouponAndActivityType(item, produceInfos);
            flag = true;
        }
        if (item.getIsDiscount().equals(CouponEnum.IS_DISCOUNT_AFTER.getCode()) && finalPayPrice.compareTo(item.getLeastCost()) > -1) {
            //减免金额
            divide = item.getReduceCost();
            if (divide.compareTo(finalPayPrice) > 0) {
                divide = finalPayPrice;
            }
            discountCalculateService.dutchDiscount(produceInfos, divide, false);
            item.setCouponsPrice(divide);
            memberCoupons.add(item);
            setGoodsCouponAndActivityType(item, produceInfos);
            flag = true;
        }
        if (!flag) {
            item.setMessage("启用金额不满足");
            unMemberCoupons.add(item);
        }
        return divide;
    }

    /**
     * 满折卷
     *
     * @param item
     * @param produceInfos
     * @param collect
     * @return
     */
    private BigDecimal getCouponTypeFullFoldMap(MemberCoupons item, List<ProduceInfo> produceInfos, List<String> collect,
                                                List<MemberCoupons> memberCoupons, List<MemberCoupons> unMemberCoupons) {
        BigDecimal multiply = BigDecimal.ZERO;
        //全部商品
        if (item.getIsAllGoods().equals(CouponEnum.IS_ALL_GOODS_ALL.getCode())) {
            ProcessInfo processInfo = discountCalculateService.boxProcessInfo(produceInfos);
            if (processInfo.getTotalAmount().compareTo(item.getGoodsNum()) > -1) {
                multiply = getFullFold(item, produceInfos, processInfo, memberCoupons, unMemberCoupons);
            } else {
                item.setMessage("商品数量不满足");
                unMemberCoupons.add(item);
            }

        } else if (item.getIsAllGoods().equals(CouponEnum.IS_ALL_GOODS_APPOINT.getCode())) {
            //指定商品
            List<String> goo = getAppointGoodsNumber(item, collect);
            //满足商品
            List<ProduceInfo> produceInfoList = getProduceInfosEnableAppointGoods(produceInfos, goo);
            ProcessInfo processInfo = discountCalculateService.boxProcessInfo(produceInfoList);
            if (processInfo.getTotalAmount().compareTo(item.getGoodsNum()) > -1) {
                //折前
                multiply = getFullFold(item, produceInfos, processInfo, memberCoupons, unMemberCoupons);
                setGoodsCouponAndActivityType(item, produceInfos);
            } else {
                item.setMessage("商品数量不满足");
                unMemberCoupons.add(item);
            }
        } else if (item.getIsAllGoods().equals(CouponEnum.IS_ALL_GOODS_NOT.getCode())) {
            //反选商品
            List<String> goo = getAppointGoodsNumber(item, collect);
            //满足商品
            //List<ProduceInfo> produceInfoList = getProduceInfosGoodsNot(produceInfos, goo);
            List<ProduceInfo> produceInfoList = getProduceInfosEnableAppointGoods(produceInfos, goo);
            ProcessInfo processInfo = discountCalculateService.boxProcessInfo(produceInfoList);
            if (processInfo.getTotalAmount().compareTo(item.getGoodsNum()) > -1) {
                //折前
                multiply = getFullFold(item, produceInfos, processInfo, memberCoupons, unMemberCoupons);
                setGoodsCouponAndActivityType(item, produceInfos);
            } else {
                item.setMessage("商品数量不满足");
                unMemberCoupons.add(item);
            }
        }
        return multiply.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    private BigDecimal getFullFold(MemberCoupons item, List<ProduceInfo> produceInfos, ProcessInfo processInfo,
                                   List<MemberCoupons> memberCoupons, List<MemberCoupons> unMemberCoupons) {
        BigDecimal multiply = BigDecimal.ZERO;
        BigDecimal salePriceBefore = processInfo.getSalePriceBefore();
        BigDecimal finalPayPrice = processInfo.getFinalPayPrice();
        Boolean flag = false;
        //折前
        if (item.getIsDiscount().equals(CouponEnum.IS_DISCOUNT_FRONT.getCode()) && salePriceBefore.compareTo(item.getLeastCost()) > -1) {
            //启用金额//对比
            //折扣额度
            BigDecimal divide = item.getDiscount().divide(BigDecimal.TEN);
            BigDecimal subtract = BigDecimal.ONE.subtract(divide);
            //拿到优惠金额
            multiply = salePriceBefore.multiply(subtract);
            if (multiply.compareTo(finalPayPrice) > 0) {
                multiply = finalPayPrice;
            }
            discountCalculateService.dutchDiscount(produceInfos, multiply, true);
            item.setCouponsPrice(multiply);
            memberCoupons.add(item);
            setGoodsCouponAndActivityType(item, produceInfos);
            flag = true;
        }
        if (item.getIsDiscount().equals(CouponEnum.IS_DISCOUNT_AFTER.getCode()) && finalPayPrice.compareTo(item.getLeastCost()) > -1) {
            //折扣额度
            BigDecimal divide = item.getDiscount().divide(BigDecimal.TEN);
            BigDecimal subtract = BigDecimal.ONE.subtract(divide);
            //拿到优惠金额
            multiply = finalPayPrice.multiply(subtract);
            if (multiply.compareTo(finalPayPrice) > 0) {
                multiply = finalPayPrice;
            }
            discountCalculateService.dutchDiscount(produceInfos, multiply, false);
            item.setCouponsPrice(multiply);
            memberCoupons.add(item);
            setGoodsCouponAndActivityType(item, produceInfos);
        }
        if (!flag) {
            item.setMessage("启用金额不满足");
            unMemberCoupons.add(item);
        }
        return multiply;
    }


    /**
     * 第二件折扣卷
     *
     * @param item
     * @param produceInfos
     * @param collect
     * @return
     */
    private BigDecimal getCouponTypeSecondMap(MemberCoupons item, List<ProduceInfo> produceInfos, List<String> collect,
                                              List<MemberCoupons> memberCoupons, List<MemberCoupons> unMemberCoupons) {
        BigDecimal multiply = BigDecimal.ZERO;
        //全部商品
        if (item.getIsAllGoods().equals(CouponEnum.IS_ALL_GOODS_ALL.getCode())) {
            multiply = getSecondGoods(item, produceInfos, memberCoupons, unMemberCoupons);
        } else if (item.getIsAllGoods().equals(CouponEnum.IS_ALL_GOODS_APPOINT.getCode())) {
            //指定商品
            List<String> goo = getAppointGoodsNumber(item, collect);
            //满足商品
            List<ProduceInfo> produceInfoList = getProduceInfosEnableAppointGoods(produceInfos, goo);
            multiply = getSecondGoods(item, produceInfoList, memberCoupons, unMemberCoupons);

        } else if (item.getIsAllGoods().equals(CouponEnum.IS_ALL_GOODS_NOT.getCode())) {
            //反选商品
            List<String> goo = getAppointGoodsNumber(item, collect);
            //List<ProduceInfo> produceInfoList = getProduceInfosGoodsNot(produceInfos, goo);
            List<ProduceInfo> produceInfoList = getProduceInfosEnableAppointGoods(produceInfos, goo);
            multiply = getSecondGoods(item, produceInfoList, memberCoupons, unMemberCoupons);
        }
        return multiply.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    private BigDecimal getSecondGoods(MemberCoupons item, List<ProduceInfo> produceInfoList,
                                      List<MemberCoupons> memberCoupons, List<MemberCoupons> unMemberCoupons) {
        BigDecimal multiply = BigDecimal.ZERO;
        List<Integer> jj = new ArrayList<>();
        for (ProduceInfo produceInfo : produceInfoList) {
            BigDecimal goodsCount = produceInfo.getGoodsCount();
            //满足同一个商品第二件
            List<ProduceInfo> arr = new ArrayList<>();
            if (goodsCount.compareTo(BigDecimal.ONE) > 0) {
                //折前
                if (item.getIsDiscount().equals(CouponEnum.IS_DISCOUNT_FRONT.getCode())) {
                    //原价
                    BigDecimal posPrice = produceInfo.getPosPrice();
                    //折扣额度
                    BigDecimal divide = item.getDiscount().divide(BigDecimal.TEN);
                    BigDecimal subtract = BigDecimal.ONE.subtract(divide);
                    //拿到优惠金额
                    multiply = posPrice.multiply(subtract);
                    arr.add(produceInfo);
                    discountCalculateService.dutchDiscount(arr, multiply, true);
                } else {
                    //折后
                    BigDecimal posPrice = produceInfo.getAmountPrice();
                    //折扣额度
                    BigDecimal divide = item.getDiscount().divide(BigDecimal.TEN);
                    BigDecimal subtract = BigDecimal.ONE.subtract(divide);
                    //拿到优惠金额
                    multiply = posPrice.multiply(subtract);
                    arr.add(produceInfo);
                    discountCalculateService.dutchDiscount(arr, multiply, false);
                }
                item.setCouponsPrice(multiply);
                jj.add(item.getId());
                produceInfo.getProcessedDiscountTypes().add(ActivityTypeEnum.COUPON.getValue().toString());
                produceInfo.getCouponIds().add(item.getId().toString());
            } else {
                item.setMessage("商品数量不满足");
                unMemberCoupons.add(item);
            }
        }
        if(jj.contains(item.getId())){
            memberCoupons.add(item);
        }
        return multiply;
    }
}
