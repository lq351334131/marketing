package marketing.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.database.enums.ResponseEnum;
import org.etocrm.database.exception.MyException;
import org.etocrm.marketing.constant.ActivityDiscountErrorEnum;
import org.etocrm.marketing.constant.ActivityTypeEnum;
import org.etocrm.marketing.constant.DiscountConstant;
import org.etocrm.marketing.convert.DiscountCalculateConvert;
import org.etocrm.marketing.model.activitygoods.ActivityGoodsSelectVo;
import org.etocrm.marketing.model.activitygoods.ActivityGoodsVo;
import org.etocrm.marketing.model.activitylist.ActivityListSelectVo;
import org.etocrm.marketing.model.activitylist.ActivityListVo;
import org.etocrm.marketing.model.activitymutex.ActivityTypeMutexSelectVo;
import org.etocrm.marketing.model.activitymutex.ActivityTypeMutexVo;
import org.etocrm.marketing.model.activityrule.ActivityRuleInfoSelectVo;
import org.etocrm.marketing.model.activityrule.ActivityRuleInfoVo;
import org.etocrm.marketing.model.discount.*;
import org.etocrm.marketing.model.discount.activityrule.ConditionExplain;
import org.etocrm.marketing.model.discount.activityrule.RuleCondition;
import org.etocrm.marketing.model.discount.activityrule.RuleInfo;
import org.etocrm.marketing.model.giftinfo.GiftInfoVo;
import org.etocrm.marketing.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author xingxing.xie
 * @Date 2021/3/29 11:44
 */
@Slf4j
@Service
public class DiscountCalculateServiceImpl implements DiscountCalculateService {

    @Autowired
    private List<QualificationChecker> checkerList;
    @Autowired
    private IActivityGoodService activityGoodService;
    @Autowired
    private IActivityTypeMutexService activityTypeMutexService;
    @Autowired
    private IActivityListService activityListService;
    @Autowired
    private CouponService couponService;

    @Autowired
    private IActivityRuleInfoService ruleInfoService;
    @Autowired
    private IActivityMutexCheckerService mutexCheckerService;
    @Autowired
    private DiscountCalculateConvert calculateConvert;
    @Autowired
    private IOrgParamsService orgParamsService;

    /**
     * 营销计算 单个优惠
     *
     * @param discountInfoVo
     * @return
     * @throws MyException
     */
    private void calculate(DiscountInfoVo discountInfoVo, Map<Integer, ActivityTypeMutexVo> map, Map<String, List<ProduceInfo>> actProRelationMap) throws MyException {
        List<ProduceInfo> produceInfoOrigin = discountInfoVo.getProduceInfos();
        //获得 活动ID
        String activityId = discountInfoVo.getCampaignInfo().getActivityCode();
        // 1、活动类型：满减：梯度满减/循环满减     满折：梯度满折 （注意梯度满折 金额排序）
        Integer activityType = discountInfoVo.getCampaignInfo().getActivityType();
        //获得此活动互斥id    (活动互斥信息  可能为空  数据库没数据)
        List<String> mutexIds = null == map.get(activityType) ? Lists.newArrayList() : map.get(activityType).getMutexIds();
        //获得该活动 下命中的 商品
        List<ProduceInfo> produceInfos = actProRelationMap.get(activityId);

        if (CollectionUtils.isEmpty(produceInfos)) {
            //该活动下 未命中商品 此次活动计算结束
            return;
        }
        //从 原始商品 中 筛选出 属于当前活动规则下的商品
        List<ProduceInfo> judgeTemp = getProListFromALLActivityPros(produceInfoOrigin, produceInfos);

        //过滤掉 已享受过活动 且 已享受活动与当前活动 互斥的  商品
        List<ProduceInfo> satisfyProducts = judgeTemp.stream().filter(t -> {
            //获取该商品 已经享受过的 活动 类型？
            List<String> processedDiscountTypes = t.getProcessedDiscountTypes();
            //该商品已享受过的活动类型 与当前活动类型互斥  则被过滤
            return !mutexIds.stream().anyMatch(s -> processedDiscountTypes.stream().anyMatch(p -> s.equals(p)));
        }).collect(Collectors.toList());

        //若过滤后 集合为null，则表示 此活动下的 商品都已经享受了 与之 互斥的且优先级高的 活动
        if (CollectionUtils.isEmpty(satisfyProducts)) {
            //记录 此次活动id，标志为 未命中，原因：互斥
            addUnReachCampaignInfo(discountInfoVo, DiscountConstant.REASON_MUTEX);
            // 此次活动计算结束
            return;
        }
        //如果 还存在 未优惠的 或者  可叠加该活动的商品 则进行优惠计算
        calculateByCondition(discountInfoVo, satisfyProducts);

        //享受 优惠后，将 在 对应商品 对象中 记录 活动id
        for (ProduceInfo origin : produceInfoOrigin) {
            //这里注意  是和  满足条件的 商品信息 配对 （即  基于满足条件的商品 筛选）
            for (ProduceInfo produceInfo : satisfyProducts) {
                if (origin.getSpuId().equals(produceInfo.getSpuId())) {
                    String currentCampaignType = produceInfo.getCurrentActivityType();
                    if (!StringUtils.isEmpty(currentCampaignType)) {
                        //不为空，则满足 此次活动 ,则加入到商品 下对应的  集合中
                        origin.getProcessedDiscountTypes().add(currentCampaignType);
                        origin.setDiscountedPrice(produceInfo.getDiscountedPrice());
                        origin.setAmountPrice(produceInfo.getAmountPrice());
                        //添加 享受优惠的活动信息
                        origin.getProcessedDiscountAct().add(discountInfoVo.getCampaignInfo());
                    }
                }
            }

        }

    }

    /**
     * 记录 当前 无效的活动
     *
     * @param discountInfoVo
     * @param reason
     */
    private void addUnReachCampaignInfo(DiscountInfoVo discountInfoVo, String reason) {
        UnReachCampaignInfo unReachCampaignInfo = new UnReachCampaignInfo();
        unReachCampaignInfo.setCampaignId(discountInfoVo.getCampaignInfo().getActivityCode());
        unReachCampaignInfo.setUnReachCampaign(discountInfoVo.getCampaignInfo());
        unReachCampaignInfo.setUnReachReason(reason);
        discountInfoVo.getUnReachCampaignInfos().add(unReachCampaignInfo);

    }

    /**
     * 从 目标商品集合 中 筛选出 属于当前活动规则下的商品
     *
     * @param targetProduceInfo 目标商品集合
     * @param allProduceInfos   该活动下 所关联的商品
     * @return
     */
    private List<ProduceInfo> getProListFromALLActivityPros(List<ProduceInfo> targetProduceInfo, List<ProduceInfo> allProduceInfos) {
        //找出 DiscountInfoVo 中对应的 商品对象 集合
        List<ProduceInfo> result = new ArrayList<>();
        for (ProduceInfo origin : targetProduceInfo) {
            // 注意  是和  活动下 圈起的 商品信息 配对 （即  基于活动的商品 筛选）
            boolean anyMatch = allProduceInfos.stream()
                    .anyMatch(t -> t.getSpuId().equals(origin.getSpuId()));
            //如果 该商品 存在 活动关联商品中，则添加返回集合
            if (anyMatch) {
                result.add(origin);
            }

        }
        return result;
    }

    /**
     * @param discountInfoVo
     * @param satisfyProducts 该活动下 关联，且未享受 与当前活动互斥的活动  的商品
     * @throws MyException
     */
    public void calculateByCondition(DiscountInfoVo discountInfoVo, List<ProduceInfo> satisfyProducts) throws MyException {
        //活动类型
        ActivityTypeEnum activityType = ActivityTypeEnum.getEnumByValue(discountInfoVo.getCampaignInfo().getActivityType());
        //创建 命中的规则
        List<AimRulesInfo> aimRulesInfos = new ArrayList<>();
        // 用于记录 享受过 优惠的 商品对象
        List<ProduceInfo> aimRuleProList = new ArrayList<>();

        switch (activityType) {
            case FULL_DISCOUNT:
                fullDiscount(discountInfoVo, satisfyProducts, aimRulesInfos, aimRuleProList);
                break;
            case FULL_GIVEN:
                //满赠计算
                fullGiven(discountInfoVo, satisfyProducts, aimRulesInfos, aimRuleProList);
                break;
            case PROMOTION_CODE:
                //优惠码 相关 rule 部分
                promotionCode(discountInfoVo, satisfyProducts, aimRulesInfos, aimRuleProList);
                break;
            default:
                throw new MyException(ResponseEnum.INCORRECT_PARAMS.getCode(), "活动类型异常！");
        }

        recordAimRuleInfo(discountInfoVo, aimRulesInfos, satisfyProducts, aimRuleProList);

    }

    /**
     * 将 命中的规则信息 填充到 DiscountInfoVo 对象中  并记录 对应商品享受的 活动
     *
     * @param discountInfoVo
     * @param aimRulesInfos
     * @param satisfyProducts
     * @param aimRuleProList
     */
    public void recordAimRuleInfo(DiscountInfoVo discountInfoVo, List<AimRulesInfo> aimRulesInfos, List<ProduceInfo> satisfyProducts, List<ProduceInfo> aimRuleProList) {
        // 记录 享受到的优惠
        BigDecimal totalDiscountValue = BigDecimal.ZERO;
        for (AimRulesInfo aimRulesInfo : aimRulesInfos) {
            if (aimRulesInfo.getDiscountValue().compareTo(BigDecimal.ZERO) < 0) {
                //非法 优惠值
                continue;
            }
            aimRulesInfo.setCampaignInfo(discountInfoVo.getCampaignInfo());
            aimRulesInfo.setCampaignId(discountInfoVo.getCampaignInfo().getActivityCode());
            discountInfoVo.getDiscountValueList().add(aimRulesInfo);
            //累计 优惠值
            totalDiscountValue = totalDiscountValue.add(aimRulesInfo.getDiscountValue());

        }

        //获取 优惠后的 金额
        BigDecimal priceDiscountAfter = discountInfoVo.getPriceDiscountAfter();
        //如果该次优惠 金额大于零 才操作
        if (totalDiscountValue.compareTo(BigDecimal.ZERO) > 0) {
            //优惠后总金额=上一次活动 优惠后金额-此次活动优惠总金额
            priceDiscountAfter = priceDiscountAfter.subtract(totalDiscountValue);
            // 更新 优惠总金额
            discountInfoVo.setTotalDiscountPrice(discountInfoVo.getTotalDiscountPrice().add(totalDiscountValue));
            //设置 优惠后 总应付价
            discountInfoVo.setPriceDiscountAfter(validDiscountValue(priceDiscountAfter));


        }

        //记录当前  商品 已享受的  活动id
        for (ProduceInfo produceInfo : satisfyProducts) {

            for (ProduceInfo aimPro : aimRuleProList) {
                if (aimPro.getSpuId().equals(produceInfo.getSpuId())) {
                    produceInfo.setCurrentActivityType(discountInfoVo.getCampaignInfo().getActivityType().toString());
                    produceInfo.setAmountPrice(aimPro.getAmountPrice());
                    produceInfo.setDiscountedPrice(aimPro.getDiscountedPrice());
                }

            }

        }

    }

    /**
     * 满减 循环计算 情况
     *
     * @param ruleInfo
     * @param satisfyProducts
     * @param aimRuleProList
     * @return true  表示满足此活动
     * @throws MyException
     */
    private Boolean circleReduceRuleProcess(RuleInfo ruleInfo, List<ProduceInfo> satisfyProducts, List<AimRulesInfo> aimRulesInfos, List<ProduceInfo> aimRuleProList) throws MyException {
        //默认不满足
        Boolean isSatisfy = false;
        //循环
        List<RuleCondition> ruleConditions = ruleInfo.getCondition();
        List<ConditionExplain> conditionExplains = ruleInfo.getConditionExplain();
        if (CollectionUtils.isEmpty(ruleInfo.getCondition()) || CollectionUtils.isEmpty(ruleConditions)) {
            throw new MyException(ActivityDiscountErrorEnum.NO_ACT_RULE_DATA.getCode(), ActivityDiscountErrorEnum.NO_ACT_RULE_DATA.getMessage());
        }
        RuleCondition ruleCondition = ruleConditions.get(0);
        ConditionExplain conditionExplain = conditionExplains.get(0);
        //优惠计量方式【 1 - 按钱， 2 - 按件】
        Integer discountType = ruleCondition.getDiscountType();
        //条件值
        BigDecimal value = ruleCondition.getValue();
        //优惠方式【 1-打折，2-减价】
        Integer discountModel = conditionExplain.getDiscountModel();
        //销售 价 还是  原价
        String priceCondition = conditionExplain.getPriceCondition();
        //获取 优惠值
        BigDecimal discountVal = new BigDecimal(conditionExplain.getDiscountVal().toString());
        if (DiscountConstant.DISCOUNT_MODEL_RATIO == discountModel) {
            //  循环形式  不适用于打折
            throw new MyException(ResponseEnum.INSUFFICIENT_PERMISSIONS.getCode(), "活动配置错误，无法循环打折！");
        }
        //商品限制 ：是否相同商品
        if (ruleInfo.getIsIdentical()) {
            //根据商品spu 分类
            Map<String, List<ProduceInfo>> collect = satisfyProducts.stream().collect(Collectors.groupingBy(t -> t.getSpuId()));
            for (String productId : collect.keySet()) {
                List<ProduceInfo> produceInfos = collect.get(productId);
                if (CollectionUtils.isEmpty(produceInfos)) {
                    continue;
                }
                //判断 该商品集合 循环的次数
                BigDecimal circleTimes = getCircleTimes(value, discountType, priceCondition, produceInfos);

                ProcessInfo processInfo = boxProcessInfo(produceInfos);
                BigDecimal discountPrice = discountVal.multiply(circleTimes);
                if(discountPrice.compareTo(processInfo.getFinalPayPrice())>0){
                    //优惠金额最大值  为 此商品总价格
                    discountPrice = processInfo.getFinalPayPrice();
                }

                AimRulesInfo aimRulesInfo = new AimRulesInfo();
                if (circleTimes.compareTo(BigDecimal.ZERO) > 0) {
                    //循环次数大于0  则表示 满足该活动
                    aimRulesInfo.setDiscountValue(aimRulesInfo.getDiscountValue().add(discountPrice));
                    //将此次 命中规则的信息 加入集合中
                    aimRulesInfos.add(aimRulesInfo);
                    //记录 商品的  优惠价格
                    setProListAmountPrice(produceInfos, aimRulesInfo.getDiscountValue(), priceCondition);
                    aimRuleProList.add(produceInfos.get(0));
                    isSatisfy = true;
                }

            }

        } else {
            //没有相同商品限制
            BigDecimal circleTimes = getCircleTimes(value, discountType, priceCondition, satisfyProducts);

            //规范 相应优惠金额
            ProcessInfo processInfo = boxProcessInfo(satisfyProducts);
            BigDecimal discountPrice = discountVal.multiply(circleTimes);
            if(discountPrice.compareTo(processInfo.getFinalPayPrice())>0){
                //优惠金额最大值  为 此商品总价格
                discountPrice = processInfo.getFinalPayPrice();
            }

            if (circleTimes.compareTo(BigDecimal.ZERO) > 0) {
                AimRulesInfo aimRulesInfo = new AimRulesInfo();
                //循环次数大于0  则表示 满足该活动
                aimRulesInfo.setDiscountValue(aimRulesInfo.getDiscountValue().add(discountPrice));
                isSatisfy = true;
                //将此次 命中规则的信息 加入集合中
                aimRulesInfos.add(aimRulesInfo);
                setProListAmountPrice(satisfyProducts, aimRulesInfo.getDiscountValue(), priceCondition);
                //记录 命中优惠的  集合
                aimRuleProList.addAll(satisfyProducts);
            }

        }
        return isSatisfy;

    }

    /**
     * 根据 当前优惠的 金额 平摊商品
     *
     * @param satisfyProducts 命中的商品
     * @param currentDiscount 当前活动 优惠的金额
     * @param priceCondition  这钱折后
     */
    public void setProListAmountPrice(List<ProduceInfo> satisfyProducts, BigDecimal currentDiscount, String priceCondition) {
        //商品集合
        //总优惠金额
        boolean isBefore = DiscountConstant.PRICE_CONDITION_BEFORE.equals(priceCondition);
        //根据  优惠前、优惠后 平摊优惠
        dutchDiscount(satisfyProducts, currentDiscount, isBefore);

    }

    /**
     *
     * @param satisfyProducts 均摊优惠商品集合
     * @param currentDiscount 均摊 优惠值
     * @param isBefore 按 折前价/折后价 均摊   true :折前，false: 折后
     */
    public void dutchDiscount(List<ProduceInfo> satisfyProducts, BigDecimal currentDiscount, boolean isBefore) {
        ProcessInfo processInfo = boxProcessInfo(satisfyProducts);
        //获得总价
        BigDecimal totalPrice = isBefore ?
                //折前  销售价 ：折后  应付价
                processInfo.getSalePriceBefore() : processInfo.getFinalPayPrice();
        //此计算 累计记录的总优惠 方便 最后一商品行 填补误差金额
        BigDecimal additionalDiscount = BigDecimal.ZERO;

        //必须先排名，根据应付金额先排名，价格小的先遍历  升序
        satisfyProducts.sort(Comparator.comparing(ProduceInfo::getAmountPrice));
        //获得各商品比例
        for (int i = 0; i < satisfyProducts.size(); i++) {
            ProduceInfo produceInfo = satisfyProducts.get(i);
            //根据比例 平摊优惠金额 单个商品 优惠的金额
            BigDecimal singlePrice = isBefore ? produceInfo.getPosPrice() : produceInfo.getAmountPrice();
            //  根据此商品 总价格（单价X数量） 占比，得到平摊优惠
            BigDecimal singleDutchPrice = singlePrice.divide(totalPrice, 5, BigDecimal.ROUND_HALF_UP).multiply(currentDiscount);

            if(singleDutchPrice.compareTo(produceInfo.getAmountPrice())>0){
                // 如果 优惠金额比  应付金额大，则 最多平摊自己本身金额
                singleDutchPrice = produceInfo.getAmountPrice();
            }

            //商品行 均分的 优惠
            BigDecimal proRowDiscount = singleDutchPrice.multiply(produceInfo.getGoodsCount()).setScale(2, BigDecimal.ROUND_HALF_UP);
            //单个商品平摊的优惠（按照金额比例平摊）
            singleDutchPrice = singleDutchPrice.setScale(2, BigDecimal.ROUND_HALF_UP);

            if (i == satisfyProducts.size() - 1) {
                //最后一个 商品  优惠价=该活动总优惠价-之前商品行累计的优惠金额
                BigDecimal resultValue = currentDiscount.subtract(additionalDiscount);
                //设置 该商品 行 当前活动 优惠值
                produceInfo.setDiscountedPrice(produceInfo.getDiscountedPrice().add(validDiscountValue(resultValue)));
                if(produceInfo.getGoodsCount().compareTo(BigDecimal.ONE)>0){
                    //如果最后一行商品行数量大于一
                    //更新 该商品应付价
                    produceInfo.setAmountPrice(produceInfo.getAmountPrice().subtract(singleDutchPrice));
                }else if(produceInfo.getGoodsCount().compareTo(BigDecimal.ONE)==0){
                    //更新 该商品应付价
                    //如果最后一行商品行 商品数量等于一，则该商品优惠值==商品行优惠值
                    produceInfo.setAmountPrice(produceInfo.getAmountPrice().subtract(resultValue));
                }else {
                   log.error("请核对商品数量，数量必须大于零");
                }

            } else {
                //设置 该商品 当前活动 优惠价
                produceInfo.setDiscountedPrice(produceInfo.getDiscountedPrice().add(validDiscountValue(proRowDiscount)));
                //更新 该商品应付价
                produceInfo.setAmountPrice(produceInfo.getAmountPrice().subtract(validDiscountValue(singleDutchPrice)));
                //累加 优惠值 =原累加值+ 单个商品优惠值x商品数量
                additionalDiscount = additionalDiscount.add(validDiscountValue(proRowDiscount));
            }
        }
    }

    /**
     * 满减/满折  阶梯规则 优惠处理
     *
     * @param ruleInfo
     * @param satisfyProducts
     * @param aimRulesInfos
     */
    private void levelReduceRuleProcess(RuleInfo ruleInfo, List<ProduceInfo> satisfyProducts, List<AimRulesInfo> aimRulesInfos) {
        //封装当前 商品集合相关金额
        ProcessInfo processInfo = boxProcessInfo(satisfyProducts);
        ConditionExplain conditionExplain = ruleInfo.getConditionExplain().get(0);
        //优惠方式【 1-打折，2-减价】
        Integer discountModel = conditionExplain.getDiscountModel();
        String priceCondition = conditionExplain.getPriceCondition();
        //优惠值
        BigDecimal discountVal = new BigDecimal(conditionExplain.getDiscountVal().toString());

        AimRulesInfo aimRulesInfo = new AimRulesInfo();

        if (DiscountConstant.DISCOUNT_MODEL_RATIO == discountModel) {
            //打折 的 优惠值
            BigDecimal discountValue = BigDecimal.ONE.subtract(discountVal.divide(BigDecimal.TEN));
            //判断 折前/折后
            BigDecimal multiply = getValidDiscountValue(priceCondition, processInfo, discountValue);

            // 享受的  优惠值  如：100元，8折  则优惠值 20
            aimRulesInfo.setDiscountValue(aimRulesInfo.getDiscountValue().add(validDiscountValue(multiply)));

        } else if (DiscountConstant.DISCOUNT_MODEL_REDUCE == discountModel) {
            if (discountVal.compareTo(processInfo.getFinalPayPrice()) > 0) {
                //优惠金额最大值  为 此商品总价格
                discountVal = processInfo.getFinalPayPrice();
            }
            //减钱
            aimRulesInfo.setDiscountValue(aimRulesInfo.getDiscountValue().add(validDiscountValue(discountVal)));
        }
        aimRulesInfos.add(aimRulesInfo);

        //计算所有  命中 商品的价格
        setProListAmountPrice(satisfyProducts, aimRulesInfo.getDiscountValue(), conditionExplain.getPriceCondition());

    }

    /**
     * 如果优惠金额 小于0  规范化
     *
     * @param value
     * @return
     */
    public BigDecimal validDiscountValue(BigDecimal value) {
        return BigDecimal.ZERO.compareTo(value) > 0 ? BigDecimal.ZERO : value;

    }

    /**
     * 封装阶梯 满赠信息
     *
     * @param ruleInfo
     * @param aimRulesInfos
     */
    private void levelGivenProcess(RuleInfo ruleInfo, List<AimRulesInfo> aimRulesInfos) {
        AimRulesInfo aimRulesInfo = new AimRulesInfo();
        //获得可选赠品
        ConditionExplain conditionExplain = ruleInfo.getConditionExplain().get(0);
        List<GiftInfoVo> selectedGifts = conditionExplain.getGifts();
        //可选赠品 最多可选次数
        Integer giftsNum = conditionExplain.getGiftsNum();
        aimRulesInfo.setSelectedGifts(selectedGifts);
        aimRulesInfo.setSelectedGiftNum(giftsNum);

        //固定赠品
        ConditionExplain conditionExplain2 = ruleInfo.getConditionExplain2().get(0);
        List<GiftInfoVo> fixedGifts = conditionExplain2.getGifts();
        //封装 固定赠品
        aimRulesInfo.setFixedGifts(fixedGifts);
        aimRulesInfos.add(aimRulesInfo);

    }

    /**
     * 商品限制  判断  ：需满足相同商品
     *
     * @return
     */
    private Boolean checkIdentical(RuleInfo topRule, List<ProduceInfo> satisfyProducts) {
        //是否是相同商品
        Boolean isIdentical = topRule.getIsIdentical();
        if (isIdentical && satisfyProducts.size() > 1) {
            ProduceInfo produceInfo = satisfyProducts.get(0);
            //判断 是否全部相同spuId
            boolean allMatch = satisfyProducts.stream().allMatch(t -> t.getSpuId().equals(produceInfo.getSpuId()));
            return allMatch;
        }
        //若只有 一个商品  则返回为true
        return true;
    }

    /**
     * @param ruleInfos
     * @param satisfyProducts 当前活动下的 商品集合
     * @return
     * @throws MyException
     */
    private List<RuleInfo> getTopRule(List<RuleInfo> ruleInfos, List<ProduceInfo> satisfyProducts) throws MyException {
        //商品限制 ：是否相同商品
        Boolean isIdentical = ruleInfos.get(0).getIsIdentical();
        List<RuleInfo> resultList = new ArrayList<>();
        if (isIdentical) {
            //根据商品spu 分类
            Map<String, List<ProduceInfo>> collect = satisfyProducts.stream().collect(Collectors.groupingBy(t -> t.getSpuId()));
            for (String productId : collect.keySet()) {
                List<ProduceInfo> produceInfos = collect.get(productId);
                //获得当前商品 集合 满足的最高优惠层级
                RuleInfo topSatisfyRuleLevel = getTopSatisfyRuleLevel(ruleInfos, produceInfos);
                if (null == topSatisfyRuleLevel) {
                    //表示当前 商品 为满足 最低标准
                    continue;
                }
                topSatisfyRuleLevel.setUnitProduceList(produceInfos);
                resultList.add(topSatisfyRuleLevel);
            }

        } else {
            RuleInfo topSatisfyRuleLevel = getTopSatisfyRuleLevel(ruleInfos, satisfyProducts);
            if (null != topSatisfyRuleLevel) {

                topSatisfyRuleLevel.setUnitProduceList(satisfyProducts);
                resultList.add(topSatisfyRuleLevel);
            }
        }

        return resultList;

    }

    /**
     * 根据 所传 商品  获得  满足当前活动的 最高层级
     *
     * @param ruleInfos
     * @param satisfyProducts
     * @return
     * @throws MyException
     */
    private RuleInfo getTopSatisfyRuleLevel(List<RuleInfo> ruleInfos, List<ProduceInfo> satisfyProducts) throws MyException {
        //获取当前入参商品的 相关金额
        ProcessInfo processInfo = boxProcessInfo(satisfyProducts);
        int i = 0;
        for (; i < ruleInfos.size(); i++) {
            List<RuleCondition> ruleConditions = ruleInfos.get(i).getCondition();
            List<ConditionExplain> conditionExplains = ruleInfos.get(i).getConditionExplain();
            if (CollectionUtils.isEmpty(ruleConditions) || CollectionUtils.isEmpty(conditionExplains)) {
                throw new MyException(ActivityDiscountErrorEnum.NO_ACT_RULE_DATA.getCode(), ActivityDiscountErrorEnum.NO_ACT_RULE_DATA.getMessage());
            }
            RuleCondition ruleCondition = ruleConditions.get(0);
            ConditionExplain conditionExplain = conditionExplains.get(0);
            //优惠计量方式【 1 - 按钱， 2 - 按件】
            Integer discountType = ruleCondition.getDiscountType();

            if (null == discountType) {
                throw new MyException(ResponseEnum.INSUFFICIENT_PERMISSIONS.getCode(), "该活动优惠类型与规则类型不匹配！");
            }
            //根据活动类型 选择对应 priceCondition (折前折后价) 方式
            ActivityTypeEnum enumByValue = ActivityTypeEnum.getEnumByValue(ruleInfos.get(i).getRuleType());
            String priceCondition = enumByValue == ActivityTypeEnum.FULL_GIVEN ? ruleInfos.get(i).getPriceCondition() : conditionExplain.getPriceCondition();
            //条件值
            BigDecimal value = new BigDecimal(ruleCondition.getValue().toString());
            //定义一个比较数字 ： 可能是 金钱 也可能是价格
            BigDecimal compareNumByType = getCompareNumByType(discountType, priceCondition, processInfo);
            if (compareNumByType.compareTo(value) > 0) {
                continue;
            } else if (compareNumByType.compareTo(value) == 0) {
                return ruleInfos.get(i);
            } else {
                if (i == 0) {
                    return null;
                }
                return ruleInfos.get(i - 1);
            }

        }

        return ruleInfos.get(ruleInfos.size() - 1);
    }

    /**
     * 根据 规则类型确定   按钱  还是按 件 比较
     *
     * @param discountType
     * @param priceCondition
     * @return
     */
    private BigDecimal getCompareNumByType(Integer discountType, String priceCondition, ProcessInfo processInfo) {

        if (DiscountConstant.DISCOUNT_TYPE_QUANTITY == discountType) {
            //按件
            return processInfo.getTotalAmount();
        } else if (DiscountConstant.DISCOUNT_TYPE_MONEY == discountType) {
            //按钱
            return DiscountConstant.PRICE_CONDITION_ORIGIN_PRICE
                    .equals(priceCondition) ?
                    processInfo.getSalePriceBefore() : processInfo.getFinalPayPrice();
        }

        //都不满足  则返回默认值0
        return new BigDecimal("0");

    }

    /**
     * 根据所传参数  计算 满足循环 条件的次数
     *
     * @param value
     * @param discountType
     * @param priceCondition
     * @param satisfyProducts
     * @return
     */
    private BigDecimal getCircleTimes(BigDecimal value, Integer discountType, String priceCondition, List<ProduceInfo> satisfyProducts) {
        ProcessInfo processInfo = boxProcessInfo(satisfyProducts);
        int times = 0;
        if (DiscountConstant.DISCOUNT_TYPE_QUANTITY == discountType) {
            //按件
            times = processInfo.getTotalAmount().divide(value).intValue() ;
        } else if (DiscountConstant.DISCOUNT_TYPE_MONEY == discountType) {
            //按钱
            BigDecimal price = DiscountConstant.PRICE_CONDITION_ORIGIN_PRICE
                    .equals(priceCondition) ?
                    processInfo.getOriginPriceBefore() : processInfo.getSalePriceBefore();
            times = price.divide(value).intValue();

        }
        return BigDecimal.valueOf(times);
    }

    @Override
    public DiscountInfoVo getDiscountInfo(MarketingDiscountVO marketingDiscountVO) throws MyException {
        long starTime = System.currentTimeMillis();
        log.info("营销计算入参：==>>"+JSON.toJSONString(marketingDiscountVO));
        Long orgId = marketingDiscountVO.getOrgId();
        String shopId = marketingDiscountVO.getSid();

        //填充 商品信息、会员信息、门店id、机构id 属性值
        DiscountInfoVo discountInfoVo = calculateConvert.toDiscountInfoVo(marketingDiscountVO);
        // 获得优惠券 计算顺序
        Integer couponDiscountOrder = discountInfoVo.getCouponDiscountOrder();
        //获得商品信息列表
        List<ProduceInfo> produceInfos = discountInfoVo.getProduceInfos();
        //活动Ids
        List<String> activityIdList = marketingDiscountVO.getActivityList();

        //初始化相关 属性值 (优惠前后 总金额)
        initPartOfDiscountInfo(discountInfoVo);
        if(CollectionUtils.isEmpty(activityIdList)){
            if(!discountInfoVo.isFlag()){
                couponService.calculateCoupon(discountInfoVo);
            }

            return discountInfoVo;

        }

        if(CollUtil.isNotEmpty(activityIdList)&&activityIdList.size()>1){

            List<String> specialCouponCodes = orgParamsService.getSpecialCouponCode(orgId)
                    .stream().map(t->t.getActivityCode()).collect(Collectors.toList());
            //判断是否存在 特殊储值卡 优惠码  活动
            boolean existSpecialAct = activityIdList.stream().anyMatch(t -> activityListService.matchExistSpecial(t,specialCouponCodes));
            if(existSpecialAct){
                throw new MyException(ActivityDiscountErrorEnum.COUPON_CODE_ERROR_ENUM.getCode(), "储值卡优惠码特定活动只能单独使用！");
            }
        }

        //根据活动id  查询 对应活动集合（不含规则）
        List<ActivityListVo> activityDetailById = getActivityById(activityIdList, orgId, shopId);
        List<Integer> activityTypes = activityDetailById.stream().map(ActivityListVo::getActivityType).distinct().collect(Collectors.toList());

        //参数验证 活动类型  是否属于门店 并获得 活动类型 计算顺序
        List<ActivityTypeMutexVo> activityListInput = checkActivityMutexAndOrder(activityTypes, discountInfoVo);
        //
        if (!CollectionUtils.isEmpty(marketingDiscountVO.getCouponsList())) {
            //若 优惠券 列表不为空 则判断 该门店是否 可用
            if (null != discountInfoVo.getCouponDiscountOrder()) {
                discountInfoVo.setCouponEnable(true);
                couponDiscountOrder = discountInfoVo.getCouponDiscountOrder();
            }

        }

        //key: 活动 类型: value:互斥信息
        Map<Integer, ActivityTypeMutexVo> activityTypeMutexMap = new HashMap<>(16);
        if (!CollectionUtils.isEmpty(activityListInput)) {
            activityTypeMutexMap = activityListInput.stream().collect(Collectors.toMap(ActivityTypeMutexVo::getActivityType, Function.identity()));
        }
        //每个活动下 命中的商品  （基于入参 products） (get(key) ==null,表示该活动未命中任何商品)
        Map<String, List<ProduceInfo>> actProRelationMap = getActProRelation(produceInfos, activityIdList, orgId, shopId);
        //根据 活动id  查询活动list、活动规则
        Map<String, CampaignInfo> campaignMap = getCampaignMap(activityIdList, orgId, shopId);
        //活动计算顺序 排序后的 活动集合（不含规则）
        //此集合对象中 包含 计算顺序
        List<ActivityListVo> activityByMutexList = getActivityByMutexList(activityListInput, activityDetailById);


        //判断 该门店是否能使用优惠券

        for (ActivityListVo vo : activityByMutexList) {
            if (null != couponDiscountOrder && vo.getActivityOrder() > couponDiscountOrder) {
                if(discountInfoVo.isCouponEnable()&&!discountInfoVo.isFlag()){
                    //判断优惠券
                    //  先计算优惠券
                    couponService.calculateCoupon(discountInfoVo);
                    //表示优惠券已计算
                    discountInfoVo.setFlag(true);

                }

            }
            //填充活动属性 (重置)
            discountInfoVo.setCampaignInfo(campaignMap.get(vo.getActivityCode()));
            //校验活动基本属性：活动时间 、 下单渠道、会员等级、活动参与次数
            if (!doCheckers(discountInfoVo)) {
                //若此活动不满足则 跳过计算 步骤
                continue;
            }
            //计算
            calculate(discountInfoVo, activityTypeMutexMap, actProRelationMap);

        }

        if(discountInfoVo.isCouponEnable()&&!discountInfoVo.isFlag()){
            couponService.calculateCoupon(discountInfoVo);
        }

        discountInfoVo.setTotalDiscountPrice(discountInfoVo.getTotalDiscountPrice().setScale(2, BigDecimal.ROUND_HALF_UP));
        discountInfoVo.setPriceDiscountAfter(discountInfoVo.getPriceDiscountBefore().subtract(discountInfoVo.getTotalDiscountPrice()));
        //结束时间
        starTime = System.currentTimeMillis()-starTime;
        log.info("透出优惠计算接口花费时间：{}ms",starTime);
        return discountInfoVo;
    }

    /**
     * 封装  相关属性值
     *
     * @param discountInfoVo
     * @return
     */
    private void initPartOfDiscountInfo(DiscountInfoVo discountInfoVo) {
        //初始化 享受的优惠集合
        discountInfoVo.setDiscountValueList(new ArrayList<>());
        //初始化 未命中的优惠集合
        discountInfoVo.setUnReachCampaignInfos(new ArrayList<>());
        //获得
        ProcessInfo processInfo = boxProcessInfo(discountInfoVo.getProduceInfos());
        //优惠前总应付价
        BigDecimal amountPriceBefore = processInfo.getFinalPayPrice();
        discountInfoVo.setPriceDiscountBefore(amountPriceBefore);
        //未参加 任何活动时  优惠后应付价格 =优惠前应付价格
        discountInfoVo.setPriceDiscountAfter(amountPriceBefore);

    }

    @Override
    public List<ActivityListVo> getSatisfyActivity(MarketingDiscountVO marketingDiscountVO) throws MyException {
        //填充 商品信息、会员信息、门店id、机构id 属性值
        DiscountInfoVo discountInfoVo = calculateConvert.toDiscountInfoVo(marketingDiscountVO);
        Long orgId = discountInfoVo.getOrgId();
        String shopId = discountInfoVo.getShopId();
        //如果 没有活动id  则根据 所传商品 查询所有满足的 活动 （满足活动对应规则）
        List<CampaignInfo> activitiesByPro = getActivitiesByPro(discountInfoVo.getProduceInfos(), orgId, shopId);

        return calculateConvert.toCampaignInfoList(activitiesByPro);
    }

    /**
     * 通过商品列表 筛选出 其对应满足的 活动规则 （不包含互斥关系、计算顺序）
     *
     * @param produceInfos
     * @param orgId
     * @param shopId
     * @return
     * @throws MyException
     */
    private List<CampaignInfo> getActivitiesByPro(List<ProduceInfo> produceInfos, Long orgId, String shopId) throws MyException {
        List<String> productIds = produceInfos.stream().map(ProduceInfo::getSpuId).distinct().collect(Collectors.toList());
        //封装 查询关联商品的 商品id   集合
        //添加 全部商品的标识 -1
        productIds.add(DiscountConstant.ALL_PRODUCT_SIGNAL);
        //根据商品id 查询活动id
        ActivityGoodsSelectVo selectVo = new ActivityGoodsSelectVo()
                .setOrgId(orgId)
                .setShopId(shopId)
                .setProductId(productIds);
        List<ActivityGoodsVo> list = activityGoodService.getList(selectVo);

        //筛选出合格的 (门店下可用的 活动类型)
        List<ActivityTypeMutexVo> mutexVoList = mutexCheckerService.getShopActivityType(orgId, shopId);
        //获得筛选后的 集合（该门店下 可使用的营销活动的  关联商品）
        List<ActivityGoodsVo> collect = list.stream().filter(goodVo -> mutexVoList.stream().anyMatch(t -> t.getActivityType().equals(goodVo.getActivityType()))).collect(Collectors.toList());
        //活动id 集合
        List<String> activityIds = collect.stream().map(ActivityGoodsVo::getActivityCode).distinct().collect(Collectors.toList());
        //活动下 关联的商品 （该门店下有效的活动）
        Map<String, List<ProduceInfo>> actProRelation = getActProRelation(produceInfos, activityIds, orgId, shopId);
        //根据 活动id  查询活动list、活动规则
        Map<String, CampaignInfo> campaignMap = getCampaignMap(activityIds, orgId, shopId);
        List<CampaignInfo> result = Lists.newArrayList();
        for (String activityId : actProRelation.keySet()) {
            //该活动下的 商品
            List<ProduceInfo> activityPros = actProRelation.get(activityId);
            //该活动规则
            CampaignInfo campaignInfo = campaignMap.get(activityId);
            if(null==campaignInfo){
                //如果 为null，则跳过次循环
                continue;
            }
            if(ActivityTypeEnum.PROMOTION_CODE.getValue().equals(campaignInfo.getActivityType())){
                //如果是 优惠码 判断  满减满折优惠码条件值
                List<RuleInfo> ruleInfoList = campaignInfo.getRule();
                if(CollectionUtils.isEmpty(ruleInfoList)){
                    throw new MyException(ActivityDiscountErrorEnum.NO_ACT_RULE_DATA.getCode(),"该优惠码"+campaignInfo.getActivityCode()+"活动无规则数据");
                }
                RuleInfo ruleInfo = ruleInfoList.get(0);
                String actMethod = ruleInfo.getActMethod();
                if(DiscountConstant.CODE_ONE_PRICE.equals(actMethod)){
                    result.add(campaignInfo);
                    continue;
                }
                BigDecimal value = ruleInfo.getValue();
                ProcessInfo processInfo = boxProcessInfo(activityPros);
                String priceCondition = ruleInfo.getPriceCondition();
                BigDecimal  priceSum=DiscountConstant.PRICE_CONDITION_ORIGIN_PRICE
                        .equals(priceCondition) ?
                        processInfo.getSalePriceBefore() : processInfo.getFinalPayPrice();
                if(priceSum.compareTo(value)>=0){
                    //如果优惠码 满减满折 满足条件值，则加入 返回活动集合
                    result.add(campaignInfo);
                }

                continue;
            }

            List<ProduceInfo> toAimRuleProList = getProListFromALLActivityPros(produceInfos, activityPros);

            //优惠设置 ：1-阶梯设置  2-循环设置
            Integer discountForm = campaignInfo.getDiscountForm();
            //获得  活动规则 并根据等级排序 ：升序
            List<RuleInfo> ruleInfos = getSortedRules(campaignInfo.getRule());
            if (CollectionUtils.isEmpty(ruleInfos)) {
                throw new MyException(ActivityDiscountErrorEnum.NO_ACT_RULE_DATA.getCode(), ActivityDiscountErrorEnum.NO_ACT_RULE_DATA.getMessage());
            }

            //阶梯
            if (DiscountConstant.DISCOUNT_SET_GRADE == discountForm) {
                //获得当前 所有商品  满足的最高优惠等级 （集合）
                List<RuleInfo> topRuleList = getTopRule(ruleInfos, toAimRuleProList);
                if (CollectionUtils.isEmpty(topRuleList)) {
                    //表示 为满足 此活动最低等级  则不满足 此活动
                    continue;
                }
                result.add(campaignInfo);

            } else if (DiscountConstant.DISCOUNT_SET_CYCLIC == discountForm) {
                //循环
                //如果是 循环的话 则规则 至多一个
                RuleInfo ruleInfo = ruleInfos.get(0);
                //此方法  暂时用不到
                List<AimRulesInfo> aimRulesInfos = Lists.newArrayList();
                Boolean isSatisfy = circleReduceRuleProcess(ruleInfo, toAimRuleProList, aimRulesInfos, Lists.newArrayList());
                if (isSatisfy) {
                    result.add(campaignInfo);
                }
            }

        }

        return result;

    }

    /**
     * 活动计算顺序 排序后的 活动集合（不含规则）
     *
     * @param mutexVos
     * @param activityListVos
     * @return
     */
    private List<ActivityListVo> getActivityByMutexList(List<ActivityTypeMutexVo> mutexVos, List<ActivityListVo> activityListVos) {
        //根据时间 倒叙排序，后创建的，先计算
        List<ActivityListVo> collect = activityListVos.stream().sorted((t1, t2) -> (int) (t2.getCreatedTime().getTime() - t1.getCreatedTime().getTime())).collect(Collectors.toList());
        List<ActivityListVo> result = new ArrayList<>();
        //外层 根据活动类型  有计算顺序
        for (ActivityTypeMutexVo mutexVo : mutexVos) {
            //内层 相同类行 后创建的，顺序在前
            for (ActivityListVo activityListVo : collect) {
                if (mutexVo.getActivityType().equals(activityListVo.getActivityType())) {
                    activityListVo.setActivityOrder(mutexVo.getActivityOrder());
                    result.add(activityListVo);
                }

            }

        }
        return result;

    }

    /**
     * 根据商品信息设置优惠前价格
     * 应付单价*商品数量
     *
     * @param produceInfos
     * @return
     */
    public ProcessInfo boxProcessInfo(List<ProduceInfo> produceInfos) {
        BigDecimal originPriceBefore = new BigDecimal(0);
        BigDecimal salePriceBefore = new BigDecimal(0);
        BigDecimal finalPayPrice = new BigDecimal(0);
        BigDecimal totalAmount = new BigDecimal(0);

        for (ProduceInfo produceInfo : produceInfos) {
            //设置 优惠前 总原价
            originPriceBefore = originPriceBefore.add(produceInfo.getShowPrice().multiply(produceInfo.getGoodsCount()));
            //设置 优惠前 总销售价
            salePriceBefore = salePriceBefore.add(produceInfo.getPosPrice().multiply(produceInfo.getGoodsCount()));
            //设置 优惠前 总 pos应付金额
            finalPayPrice = finalPayPrice.add(produceInfo.getAmountPrice().multiply(produceInfo.getGoodsCount()));
            //累加总件数
            totalAmount = totalAmount.add(produceInfo.getGoodsCount());

        }
        return new ProcessInfo()
                //设置 优惠前 总原价
                .setOriginPriceBefore(originPriceBefore)
                //设置 优惠前 总销售价
                .setSalePriceBefore(salePriceBefore)
                //设置 优惠前 总pos应付金额
                .setFinalPayPrice(finalPayPrice)
                //总件数
                .setTotalAmount(totalAmount);
    }

    /**
     * 将活动规则信息封装成 map
     * key：活动id  value:活动信息 包含规则
     *
     * @param inputActivityIds
     * @param orgId
     * @param shopId
     * @return
     * @throws MyException
     */
    public Map<String, CampaignInfo> getCampaignMap(List<String> inputActivityIds, Long orgId, String shopId) throws MyException {
        //活动规则信息 包含rule部分
        Map<String, CampaignInfo> campaignInfoMap = new HashMap<>(16);
        //根据 活动id  查询活动list、活动规则
        for (String activityId : inputActivityIds) {
            //获得 该活动所有信息  包括规则
            ActivityListVo activityListVo = getActivityDetailById(activityId, orgId, shopId);
            //如果查询null，则表示 没此活动或 活动已过期
            if(null==activityListVo){
                continue;
            }
            CampaignInfo campaignInfo = calculateConvert.toCampaignInfo(activityListVo);

            //放入
            campaignInfoMap.put(activityId, campaignInfo);

        }
        return campaignInfoMap;

    }

    /**
     * 根据 活动id、机构id、门店id  查询活动基本信息及 规则信息
     *
     * @param activityId
     * @param orgId
     * @param shopId
     * @return
     * @throws MyException
     */
    public ActivityListVo getActivityDetailById(String activityId, Long orgId, String shopId) throws MyException {
        List<String> activityIds = new ArrayList<>();
        activityIds.add(activityId);
        ActivityListSelectVo activityListSelectVo = new ActivityListSelectVo()
                .setActivityCode(activityIds)
                .setOrgId(orgId)
                .setShopId(shopId);
        //查询 活动列表s
        List<ActivityListVo> activityListVos = activityListService.getActivityListVo(activityListSelectVo);
        activityListVos=activityListService.getFilteredListByTime(activityListVos);
        if (CollectionUtils.isEmpty(activityListVos)) {
            log.info("根据活动id查询，活动入参集合："+JSON.toJSONString(activityIds));
            //如果查询活动为null，则表示 参数异常
//            throw new MyException(ResponseEnum.INCORRECT_PARAMS.getCode(), "查询活动数据为null");
            return null;
        }

        //查询该活动下 规则
        List<ActivityRuleInfoVo> activityRuleVoList = ruleInfoService.getActivityRuleVoList(new ActivityRuleInfoSelectVo().setActivityId(activityId));
        //填充 规则属性
        ActivityListVo activityListVo = activityListVos.get(0).setRule(activityRuleVoList);

        return activityListVo;

    }

    /**
     * 查询  活动规则 集合
     *
     * @param activityIds
     * @param orgId
     * @param shopId
     * @return
     * @throws MyException
     */
    public List<ActivityListVo> getActivityById(List<String> activityIds, Long orgId, String shopId) throws MyException {
        ActivityListSelectVo activityListSelectVo = new ActivityListSelectVo()
                .setActivityCode(activityIds)
                .setOrgId(orgId)
                .setShopId(shopId);
        //查询 活动列表s
        List<ActivityListVo> activityListVos = activityListService.getActivityListVo(activityListSelectVo);
        //过滤 时间不符合的活动
        activityListVos=activityListService.getFilteredListByTime(activityListVos);
        if (CollectionUtils.isEmpty(activityListVos)) {
            //如果查询活动为null，则表示 参数异常
            throw new MyException(ResponseEnum.INCORRECT_PARAMS.getCode(), "暂无该活动，请核对入参！");
        }

        return activityListVos;

    }

    /**
     * 将 商品 与活动id  封装成  map  对象
     * key:activityId  value:productId
     *
     * @param produceInfos
     * @param activityList
     * @return
     */
    public Map<String, List<ProduceInfo>> getActProRelation(List<ProduceInfo> produceInfos, List<String> activityList, Long orgId, String shopId) {
        Map<String, List<ProduceInfo>> map = new HashMap<>(16);
        for (String t : activityList) {
            ActivityGoodsSelectVo activityGoodsSelectVo = new ActivityGoodsSelectVo()
                    .setActivityCode(t).setOrgId(orgId).setShopId(shopId);
            //根据活动id查询 所有 商品id
            List<ActivityGoodsVo> activityGoodsVos = activityGoodService.getList(activityGoodsSelectVo);
            //筛选出 满足该活动的 商品id
            List<ProduceInfo> collect = produceInfos.stream().filter(s -> checkProduces(activityGoodsVos,s)).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(collect)) {
                //不为null   则放入map
                map.put(t, collect);
            }
        }

        return map;

    }

    /**
     *  根据 入参商品信息，判断 是否符合 该活动下关联的 商品信息
     * @param activityGoodsVos
     * @param inputPro
     * @return
     */
    public Boolean checkProduces(List<ActivityGoodsVo> activityGoodsVos,ProduceInfo inputPro){
        if(CollectionUtils.isEmpty(activityGoodsVos)){
            //如果该活动 下 无任何 任何关联商品，则返回 false  对调用此方法者  标识全部过滤
            return false;
        }

        //判断是否  是全选 商品标记
        boolean allProSignal = activityGoodsVos.stream()
                .map(ActivityGoodsVo::getProductId)
                .allMatch(t -> DiscountConstant.ALL_PRODUCT_SIGNAL.equals(t));
        if(allProSignal){
            return true;
        }

        // 首先判断 商品 spu  是否相同
        boolean anyMatch = activityGoodsVos.stream()
                .map(ActivityGoodsVo::getProductId)
                .anyMatch(t -> t.equals(inputPro.getSpuId()));

        if(!anyMatch){
            //如果 spu不相同，则表示 此活动 不关联此商品
            return false;

        }
        //再来判断 是否是 优惠码活动
        if(ActivityTypeEnum.PROMOTION_CODE.getValue().equals(activityGoodsVos.get(0).getActivityType())){
            //判断是否 一口价 的优惠码，如果是，则需判断   入参sku 是否满足
            List<ActivityGoodsVo> collect = activityGoodsVos.stream().filter(t -> StrUtil.isNotEmpty(t.getSystemSku())).collect(Collectors.toList());
            if(!CollectionUtils.isEmpty(collect)){
                //存在优惠码  一口价 则判断 sku是否相等
                return collect.stream().anyMatch(t ->
                        t.getProductId().equals(inputPro.getSpuId()) &&
                                t.getSystemSku().equals(inputPro.getSkuId()));
            }

        }
        //不是优惠码，或者不是优惠码一口价活动时， 只判断 spu,上述以判断符合
        return true;
    }

    /**
     * 根据 券的 条件门槛值 升序排序
     *
     * @param ruleInfos
     * @return
     */
    public List<RuleInfo> getSortedRules(List<RuleInfo> ruleInfos) {
        if (CollectionUtils.isEmpty(ruleInfos)) {
            return new ArrayList<>();
        }

        List<RuleInfo> collect = ruleInfos.stream().sorted(Comparator.comparing(t -> t.getCondition().get(0).getValue())
        ).collect(Collectors.toList());
        return collect;

    }

    /**
     * 校验 活动互斥规则
     *
     * @param activityTypes  活动类型 集合
     * @param discountInfoVo
     */
    public List<ActivityTypeMutexVo> checkActivityMutexAndOrder(List<Integer> activityTypes, DiscountInfoVo discountInfoVo) throws MyException {
        Long orgId = discountInfoVo.getOrgId();
        String shopId = discountInfoVo.getShopId();
        //验证 入参活动id 是否属于门店下的活动id, 封装 优惠券 适用情况
        discountInfoVo = checkActivityAndBoxCouponEnable(activityTypes, discountInfoVo);
        //根据活动id  查询 活动互斥共享 关系
        //封装查询对象
        ActivityTypeMutexSelectVo vo = new ActivityTypeMutexSelectVo()
                .setOrgId(orgId)
                .setShopId(shopId)
                .setActivityType(activityTypes);
        List<ActivityTypeMutexVo> mutexVoList = activityTypeMutexService.getList(vo);

        //根据活动计算顺序排序 从小到大
        return mutexVoList.stream().sorted(Comparator.comparingInt(ActivityTypeMutexVo::getActivityOrder))
                .collect(Collectors.toList());

    }

    /**
     * 验证入参 活动类型  是否属于  门店 活动类型
     *
     * @param activityTypes
     * @param discountInfoVo
     * @return
     * @throws MyException
     */
    private DiscountInfoVo checkActivityAndBoxCouponEnable(List<Integer> activityTypes, DiscountInfoVo discountInfoVo) throws MyException {
        Long orgId = discountInfoVo.getOrgId();
        String shopId = discountInfoVo.getShopId();
        //封装查询对象
        ActivityTypeMutexSelectVo vo = new ActivityTypeMutexSelectVo()
                .setOrgId(orgId)
                .setShopId(shopId);
        //获得当前机构、当前门店下所有activityId
        List<ActivityTypeMutexVo> mutexVoList = activityTypeMutexService.getList(vo);
        if (CollectionUtils.isEmpty(mutexVoList)) {
            //如果当前门店下 没有活动互斥 数据，则检查入参
            throw new MyException(ResponseEnum.INCORRECT_PARAMS.getCode(), "该门店下无任何活动，请核对机构或活动！");
        }

        //判断当前所传 活动类型  均属于此门店下活动
        List<Integer> shopActivities = mutexVoList.stream().map(ActivityTypeMutexVo::getActivityType).collect(Collectors.toList());

        //入参的 每一个活动类型，都输入 该门店下  配置的 活动类型
        boolean checkIds = activityTypes.stream().allMatch(t -> shopActivities.stream().anyMatch(s -> s.equals(t)));
        //若有一个不属于，则 参数错误
        if (!checkIds) {
            throw new MyException(ResponseEnum.INCORRECT_PARAMS.getCode(), "存在非本门店下活动，请核对！");
        }

        //根据入参 获得对应的 互斥实体类
        Map<Integer, ActivityTypeMutexVo> mutexVoMap = mutexVoList.stream().collect(Collectors.toMap(ActivityTypeMutexVo::getActivityType, Function.identity()));

        List<ActivityTypeMutexVo> collectInput = activityTypes.stream()
                .map(t -> mutexVoMap.get(t))
                .sorted(Comparator.comparingInt(ActivityTypeMutexVo::getActivityOrder))
                .collect(Collectors.toList());
        //入参活动 是否存在与优惠券互斥
        boolean mutexCoupon = collectInput.stream()
                .flatMap(t -> t.getMutexIds().stream())
                .anyMatch(t -> t.trim().equals(ActivityTypeEnum.COUPON.getValue().toString()));


        //判断  该门店是否能使用 优惠券
        //默认 不可使用优惠券
        discountInfoVo.setCouponDiscountOrder(null);
        for (ActivityTypeMutexVo activityTypeMutexVo : mutexVoList) {
            if (activityTypeMutexVo.getActivityType()
                    .equals(ActivityTypeEnum.COUPON.getValue())) {
                // 计算顺序 eg: 5>4   则表最后
                boolean isLast = activityTypeMutexVo.getActivityOrder() > collectInput.get(collectInput.size() - 1).getActivityOrder();

                if(isLast){
                    //是否互斥
                    if(mutexCoupon){
                        //优惠券 是最后计算，且与其他活动 互斥，则优惠券不计算
                        break;
                    }

                }
                //设置  优惠券 计算 顺序 （如果为 null，则表示当前门店不支持 优惠券）
                discountInfoVo.setCouponDiscountOrder(activityTypeMutexVo.getActivityOrder());
                break;
            }
        }

        return discountInfoVo;
    }

    /**
     * 校验活动基本属性：活动时间 、 下单渠道、会员等级、活动参与次数
     *
     * @param discountInfoVo
     */
    private Boolean doCheckers(DiscountInfoVo discountInfoVo) throws MyException {
        for (QualificationChecker checker : checkerList) {
            Boolean checkResult = checker.check(discountInfoVo);
            if (!checkResult) {
                //不符合活动规则
                log.info(checker.getName() + "判定不通过！");
                throw new MyException(ResponseEnum.INCORRECT_PARAMS.getCode(), checker.getName() + "判定不通过！");
            }

        }

        return true;

    }

    /**
     * 满减满折 相关计算
     *
     * @param discountInfoVo
     * @param satisfyProducts
     * @param aimRulesInfos
     * @param aimRuleProList
     * @throws MyException
     */
    public void fullDiscount(DiscountInfoVo discountInfoVo, List<ProduceInfo> satisfyProducts, List<AimRulesInfo> aimRulesInfos, List<ProduceInfo> aimRuleProList) throws MyException {
        //优惠设置 ：1-阶梯设置  2-循环设置
        Integer discountForm = discountInfoVo.getCampaignInfo().getDiscountForm();
        //获得  活动规则 并根据等级排序 ：升序
        List<RuleInfo> ruleInfos = getSortedRules(discountInfoVo.getCampaignInfo().getRule());
        if (CollectionUtils.isEmpty(ruleInfos)) {
            throw new MyException(ActivityDiscountErrorEnum.NO_ACT_RULE_DATA.getCode(), ActivityDiscountErrorEnum.NO_ACT_RULE_DATA.getMessage());
        }

        //阶梯
        if (DiscountConstant.DISCOUNT_SET_GRADE == discountForm) {
            //获得当前 所有商品  满足的最高优惠等级 （集合）
            List<RuleInfo> topRuleList = getTopRule(ruleInfos, satisfyProducts);
            if (CollectionUtils.isEmpty(topRuleList)) {
                //表示 未满足 此活动最低等级  则不满足 此活动
                //记录未满足活动
                addUnReachCampaignInfo(discountInfoVo, DiscountConstant.REASON_UN_REACH);
                return;
            }

            for (RuleInfo ruleInfo : topRuleList) {
                List<ProduceInfo> unitProduceList = ruleInfo.getUnitProduceList();
                if (CollectionUtils.isEmpty(unitProduceList)) {
                    continue;
                }
                levelReduceRuleProcess(ruleInfo, unitProduceList, aimRulesInfos);
                //将 此次 享受 优惠的 商品 记录起来
                aimRuleProList.addAll(unitProduceList);
            }

        } else if (DiscountConstant.DISCOUNT_SET_CYCLIC == discountForm) {
            //循环
            //如果是 循环的话 则规则 至多一个
            RuleInfo ruleInfo = ruleInfos.get(0);
            Boolean isSatisfy = circleReduceRuleProcess(ruleInfo, satisfyProducts, aimRulesInfos, aimRuleProList);
            if (!isSatisfy) {
                //如果不满足，则记录当前活动
                addUnReachCampaignInfo(discountInfoVo, DiscountConstant.REASON_UN_REACH);
            }
        }
    }

    /**
     * 满赠相关  计算
     *
     * @param discountInfoVo
     * @param satisfyProducts
     * @param aimRulesInfos
     * @param aimRuleProList
     * @throws MyException
     */
    public void fullGiven(DiscountInfoVo discountInfoVo, List<ProduceInfo> satisfyProducts, List<AimRulesInfo> aimRulesInfos, List<ProduceInfo> aimRuleProList) throws MyException {
        //获得  活动规则 并根据等级排序 ：升序
        List<RuleInfo> ruleInfos = getSortedRules(discountInfoVo.getCampaignInfo().getRule());
        if (CollectionUtils.isEmpty(ruleInfos)) {
            throw new MyException(ActivityDiscountErrorEnum.NO_ACT_RULE_DATA.getCode(), ActivityDiscountErrorEnum.NO_ACT_RULE_DATA.getMessage());
        }
        //获得当前 所有商品  满足的最高优惠等级 （集合）
        List<RuleInfo> topRuleList = getTopRule(ruleInfos, satisfyProducts);
        if (CollectionUtils.isEmpty(topRuleList)) {
            //表示 未满足 此活动最低等级  则不满足 此活动
            //记录未满足活动
            addUnReachCampaignInfo(discountInfoVo, DiscountConstant.REASON_UN_REACH);
            return;
        }

        for (RuleInfo ruleInfo : topRuleList) {
            List<ProduceInfo> unitProduceList = ruleInfo.getUnitProduceList();
            if (CollectionUtils.isEmpty(unitProduceList)) {
                continue;
            }
            //记录 满赠 信息
            levelGivenProcess(ruleInfo, aimRulesInfos);
            //将 此次 享受 优惠的 商品 记录起来
            aimRuleProList.addAll(unitProduceList);
        }

    }

    /**
     * 优惠码相关计算
     *
     * @param discountInfoVo
     * @param satisfyProducts
     * @param aimRulesInfos
     * @param aimRuleProList
     * @throws MyException
     */
    public void promotionCode(DiscountInfoVo discountInfoVo, List<ProduceInfo> satisfyProducts, List<AimRulesInfo> aimRulesInfos, List<ProduceInfo> aimRuleProList) throws MyException {
        //获得  活动规则 不需排序
        List<RuleInfo> ruleInfoList = discountInfoVo.getCampaignInfo().getRule();

        if (CollectionUtils.isEmpty(ruleInfoList)) {
            throw new MyException(ActivityDiscountErrorEnum.NO_ACT_RULE_DATA.getCode(), ActivityDiscountErrorEnum.NO_ACT_RULE_DATA.getMessage());
        }
        RuleInfo ruleInfo = ruleInfoList.get(0);

        //优惠类型
        String actMethod = ruleInfo.getActMethod();

        if (DiscountConstant.CODE_ONE_PRICE.equals(actMethod)) {
            //一口价
            //根据sku 查询 对应的价格
            ActivityGoodsSelectVo selectVo = new ActivityGoodsSelectVo()
                    .setOrgId(discountInfoVo.getOrgId())
                    .setShopId(discountInfoVo.getShopId())
                    .setActivityCode(discountInfoVo.getCampaignInfo().getActivityCode());
            List<ActivityGoodsVo> list = activityGoodService.getList(selectVo);

            Map<String, List<ActivityGoodsVo>> goodsVoMap = list.stream().collect(Collectors.groupingBy(ActivityGoodsVo::getProductId));
            List<ProduceInfo> aimRulePros = Lists.newArrayList();
            for (ProduceInfo produceInfo : satisfyProducts) {
                if (goodsVoMap.containsKey(produceInfo.getSpuId())) {
                    List<ActivityGoodsVo> activityGoodsVos = goodsVoMap.get(produceInfo.getSpuId());
                    //获得 入参 sku
                    String skuId = produceInfo.getSkuId();
                    for (ActivityGoodsVo activityGoodsVo : activityGoodsVos) {
                        if (skuId.equals(activityGoodsVo.getSystemSku())) {
                            produceInfo.setSystemSku(activityGoodsVo.getSystemSku());
                            produceInfo.setInfo(activityGoodsVo.getInfo());
                            //记录 命中的 sku 商品
                            aimRulePros.add(produceInfo);
                        }
                    }

                }

            }
            //每一个商品行的 价格计算
            aimRulePros.forEach(t -> {
                //应付价格
                BigDecimal amountPrice = t.getAmountPrice();
                //实付价格
                BigDecimal price = new BigDecimal(JSON.parseObject(t.getInfo()).getString("price"));
                //优惠值
                BigDecimal subtract = validDiscountValue(amountPrice.subtract(price));
                BigDecimal proRowDiscount = subtract.multiply(t.getGoodsCount());
                //商品行 的 总 优惠
                t.setDiscountedPrice(proRowDiscount);
                //更新 该商品 应付价
                t.setAmountPrice(price);
                AimRulesInfo aimRulesInfo = new AimRulesInfo();
                aimRulesInfo.setDiscountValue(aimRulesInfo.getDiscountValue().add(proRowDiscount));
                aimRulesInfo.setSystemSku(t.getSystemSku());
                //记录 命中优惠值
                aimRulesInfos.add(aimRulesInfo);

            });
            //记录 享受优惠的 商品
            aimRuleProList.addAll(aimRulePros);

        } else {
            //封装 当前满足商品 相关价格值
            ProcessInfo processInfo = boxProcessInfo(satisfyProducts);
            if (null == ruleInfo.getValue() || null == ruleInfo.getActValue() || StringUtils.isEmpty(ruleInfo.getPriceCondition())) {
                throw new MyException(ActivityDiscountErrorEnum.COUPON_CODE_ERROR_ENUM.getCode(), "优惠码规则数据异常");
            }
            //条件值
            BigDecimal value = new BigDecimal(ruleInfo.getValue().toString());
            //优惠值
            BigDecimal actValue = ruleInfo.getActValue();
            // 折前、折后
            String priceCondition = ruleInfo.getPriceCondition();

            //满减
            BigDecimal compareNumByType = getCompareNumByType(DiscountConstant.DISCOUNT_TYPE_MONEY, priceCondition, processInfo);
            boolean reachRule = compareNumByType.compareTo(value) >= 0;
            if (!reachRule) {
                //记录未满足活动
                addUnReachCampaignInfo(discountInfoVo, DiscountConstant.REASON_UN_REACH);
                return;
            }
            //
            AimRulesInfo aimRulesInfo = new AimRulesInfo();

            if (DiscountConstant.CODE_REDUCTION.equals(actMethod)) {
                actValue = validDiscountValue(actValue);
                if (actValue.compareTo(processInfo.getFinalPayPrice()) > 0) {
                    //优惠金额最大值  为 此商品总价格
                    actValue = processInfo.getFinalPayPrice();
                }

                //减钱
                aimRulesInfo.setDiscountValue(aimRulesInfo.getDiscountValue().add(actValue));
                aimRulesInfos.add(aimRulesInfo);
                //处理  没每个商品 优惠值
                setProListAmountPrice(satisfyProducts, aimRulesInfo.getDiscountValue(), priceCondition);
                //将 此次 享受 优惠的 商品 记录起来
                aimRuleProList.addAll(satisfyProducts);
            } else if (DiscountConstant.CODE_DISCOUNT.equals(actMethod)) {
                //满折
                //打折 的 优惠值
                BigDecimal discountValue = BigDecimal.ONE.subtract(actValue.divide(BigDecimal.TEN));
                BigDecimal multiply = getValidDiscountValue(priceCondition, processInfo, discountValue);

                // 享受的  优惠值  如：100元，8折  则优惠值 20
                aimRulesInfo.setDiscountValue(aimRulesInfo.getDiscountValue().add(validDiscountValue(multiply)));

                //处理  没每个商品 优惠值
                setProListAmountPrice(satisfyProducts, aimRulesInfo.getDiscountValue(), priceCondition);

                aimRulesInfos.add(aimRulesInfo);
                //将 此次 享受 优惠的 商品 记录起来
                aimRuleProList.addAll(satisfyProducts);

            } else {
                throw new MyException(ActivityDiscountErrorEnum.COUPON_CODE_ERROR_ENUM.getCode(), "优惠码规则优惠类型异常");

            }
        }
    }

    /**
     * 折扣优惠时   优惠金额 务必 <= 当前商品  应付金额
     * @param priceCondition
     * @param processInfo
     * @param discountValue
     * @return
     */
    private BigDecimal getValidDiscountValue(String priceCondition, ProcessInfo processInfo, BigDecimal discountValue) {
        //判断 折前/折后
        boolean isBefore = DiscountConstant.PRICE_CONDITION_BEFORE.equals(priceCondition);
        //获得总价
        BigDecimal totalPrice = isBefore ?
                //折前  销售价 ：折后  应付价
                processInfo.getSalePriceBefore() : processInfo.getFinalPayPrice();
        //优惠值
        BigDecimal multiply = totalPrice.multiply(discountValue);
        if (multiply.compareTo(processInfo.getFinalPayPrice()) > 0) {
            //优惠金额最大值  为 此商品总价格
            multiply = processInfo.getFinalPayPrice();
        }
        return multiply;
    }

}


