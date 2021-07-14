package marketing.controller;

import com.alibaba.fastjson.JSON;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.database.enums.ResponseEnum;
import org.etocrm.database.exception.MyException;
import org.etocrm.database.util.ResponseVO;
import org.etocrm.marketing.constant.ActivityTypeEnum;
import org.etocrm.marketing.constant.DiscountConstant;
import org.etocrm.marketing.convert.ActivityTypeDetailConvert;
import org.etocrm.marketing.model.activitydetail.CustActivityDetailVo;
import org.etocrm.marketing.model.activitylist.ActivityListSelectVo;
import org.etocrm.marketing.model.activitylist.ActivityListVo;
import org.etocrm.marketing.model.activitymutex.ActivityTypeMutexVo;
import org.etocrm.marketing.model.discount.*;
import org.etocrm.marketing.service.CouponService;
import org.etocrm.marketing.service.DiscountCalculateService;
import org.etocrm.marketing.service.IActivityListService;
import org.etocrm.marketing.service.IActivityMutexCheckerService;
import org.etocrm.marketing.service.impl.checker.ParticipationLimitChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author xingxing.xie
 * @Date 2021/3/26 10:39
 */
@Api(value = "营销第二中台计算", tags = "营销中台计算")
@RestController
@RequestMapping("/discountCalculate")
@Slf4j
public class DiscountCalculateController {
    @Autowired
    private DiscountCalculateService discountCalculateService;
    @Autowired
    private IActivityListService activityListService;
    @Autowired
    private IActivityMutexCheckerService checkerService;
    @Autowired
    private ParticipationLimitChecker participationLimitChecker;
    @Autowired
    private ActivityTypeDetailConvert detailConvert;

    @Autowired
    private CouponService couponService;

    @ApiOperation("透出优惠信息")
    @ApiOperationSupport(order = 10)
    @PostMapping("/getDiscountInfo")
    public ResponseVO<DiscountInfoVo> getDiscountInfo(@RequestBody @Valid MarketingDiscountVO marketingDiscountVO) throws MyException {

        return ResponseVO.success(discountCalculateService.getDiscountInfo(marketingDiscountVO));
    }

    @ApiOperation("获取所有活动列表")
    @ApiOperationSupport(order = 20)
    @PostMapping("/getValidActivity")
    public ResponseVO<List<ActivityListVo>> getValidActivity(@RequestBody MarketingDiscountVO marketingDiscountVO) throws MyException {
        log.warn("获取所有活动列表入参：{}", JSON.toJSONString(marketingDiscountVO));
        List<MarketingGoodsSkuVO> goods = marketingDiscountVO.getGoods();
        //获得会员等级
        MemberDetail memberDetail = marketingDiscountVO.getMemberDetail();
        if (null == memberDetail || null == memberDetail.getVipLevelId()) {
            throw new MyException(ResponseEnum.INCORRECT_PARAMS.getCode(), "请补齐会员信息");
        }
        Integer vipLevelId = memberDetail.getVipLevelId();
        List<ActivityListVo> collect;
        if (CollectionUtils.isEmpty(goods)) {
            ActivityListSelectVo activityListSelectVo = new ActivityListSelectVo()
                    .setOrgId(marketingDiscountVO.getOrgId())
                    .setShopId(marketingDiscountVO.getSid());
            long currentTimeMillis = System.currentTimeMillis();
            //过滤掉时间无效的 活动
            List<ActivityListVo> result = activityListService.getValidActivityByShopId(activityListSelectVo, currentTimeMillis);
            List<ActivityTypeMutexVo> shopActivityType = checkerService.getShopActivityType(marketingDiscountVO.getOrgId(), marketingDiscountVO.getSid());
            //筛选出 该门店可用的 活动类型
            collect = checkerService.pickActivityFromShopType(result, shopActivityType);

        } else {
            //计算该商品能满足的 所有活动
            //不能有活动id
            collect = discountCalculateService.getSatisfyActivity(marketingDiscountVO);
        }

        //过滤活动  根据会员等级过滤
        collect = collect.stream().filter(
                t -> t.getVipRang().stream().anyMatch(
                        vipId -> vipId.trim().equals(vipLevelId.toString())))
                //验证 活动次数
                .filter(t -> {
                    Boolean hasLimit = t.getHasLimit();
                    //首先获得 活动类型
                    ActivityTypeEnum activityType = ActivityTypeEnum.getEnumByValue(t.getActivityType());
                    if (hasLimit == null || !hasLimit) {
                        //如果为null 或者次数无限制，则通过
                        if (activityType == ActivityTypeEnum.PROMOTION_CODE) {
                            //活动 已被 有效参加 的总记录（即 除  解冻之外之外的）
                            List<CustActivityDetailVo> validList =
                                    participationLimitChecker.getValidListByActivityId(t.getActivityCode(), marketingDiscountVO.getOrgId());
                            return participationLimitChecker.satisfyTotalLimit(t.getRule().get(0).getTotalAmount(), validList);
                        }
                        return true;
                    }

                    //获取 码使用次数
                    Long totalLimit;
                    if (activityType == ActivityTypeEnum.PROMOTION_CODE) {
                        //如果是 优惠码 活动  优惠码总限制次数
                        totalLimit = t.getRule().get(0).getTotalAmount();
                    } else {
                        //活动总限制次数
                        totalLimit = t.getTotalLimit().longValue();
                    }
                    return participationLimitChecker.matchParticipants(
                            t.getActivityCode(),
                            marketingDiscountVO.getOrgId(),
                            totalLimit,
                            t.getPerLimit(),
                            memberDetail.getId());
                })
                .collect(Collectors.toList());
        return ResponseVO.success(collect);

    }

    @ApiOperation("根据已选活动id返回可叠加的活动")
    @ApiOperationSupport(order = 30)
    @PostMapping("/getStackableActivityIds")
    public ResponseVO<List<String>> getStackableActivityIds(@RequestBody StackableActivityInputVo inputVo) throws MyException {

        return ResponseVO.success(checkerService.getStackableActivityIds(inputVo));
    }

    @ApiOperation("获得门店下营销活动类型信息")
    @ApiOperationSupport(order = 40)
    @PostMapping("/getActivityTypeByShop")
    public ResponseVO<List<ShopActivityTypeDetail>> getActivityTypeByShop(@RequestBody StackableActivityInputVo inputVo) throws MyException {
        List<ActivityTypeMutexVo> mutexVoList = checkerService.getShopActivityType(inputVo.getOrgId(), inputVo.getShopId());
        List<ShopActivityTypeDetail> collect = mutexVoList.stream().map(t ->
                detailConvert.boxShopActivityType(t, DiscountConstant.BRAND_PARTAKE == t.getPartake())
        ).collect(Collectors.toList());
        return ResponseVO.success(collect);
    }

    @ApiOperation("计算优惠卷价格")
    @ApiOperationSupport(order = 10)
    @PostMapping("/calculateCoupon")
    public ResponseVO<DiscountInfoVo> calculateCoupon(@Valid @RequestBody DiscountInfoVo discountInfoVo) throws MyException {

        return ResponseVO.success(couponService.calculateCoupon(discountInfoVo));
    }

}
