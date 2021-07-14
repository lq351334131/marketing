package marketing.service.impl.checker;

import lombok.extern.slf4j.Slf4j;
import org.etocrm.database.enums.ResponseEnum;
import org.etocrm.database.exception.MyException;
import org.etocrm.marketing.constant.ActivityDiscountErrorEnum;
import org.etocrm.marketing.constant.ActivityTypeEnum;
import org.etocrm.marketing.constant.DiscountConstant;
import org.etocrm.marketing.model.activitydetail.CustActDetailSelectVo;
import org.etocrm.marketing.model.activitydetail.CustActivityDetailVo;
import org.etocrm.marketing.model.discount.CampaignInfo;
import org.etocrm.marketing.model.discount.CampaignVO;
import org.etocrm.marketing.model.discount.DiscountInfoVo;
import org.etocrm.marketing.model.discount.activityrule.RuleInfo;
import org.etocrm.marketing.service.ICustActivityDetailService;
import org.etocrm.marketing.service.QualificationChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 活动参与次数 校验
 *
 * @Author xingxing.xie
 * @Date 2021/3/29 16:42
 */
@Component
@Slf4j
public class ParticipationLimitChecker implements QualificationChecker {
    @Autowired
    private ICustActivityDetailService detailService;

    @Override
    public Boolean check(DiscountInfoVo discountInfoVo) throws MyException {
        //首先获得 活动类型
        ActivityTypeEnum activityType = ActivityTypeEnum.getEnumByValue(discountInfoVo.getCampaignInfo().getActivityType());
        boolean result;

        switch (activityType) {
            case FULL_DISCOUNT:
                result = checkFullDiscountActivity(discountInfoVo);
                break;
            case FULL_GIVEN:
                //满赠 不做任何判断
                result = true;
                break;
            case PROMOTION_CODE:
                result = checkCouponCodeActivity(discountInfoVo);
                break;
            default:
                throw new MyException(ResponseEnum.INCORRECT_PARAMS.getCode(), "活动类型异常！");
        }

        return result;

    }

    @Override
    public String getName() {
        return "会员参与次数";
    }

    /**
     * 判断是否满足  活动总次数
     *
     * @return
     */
    public boolean satisfyTotalLimit(Long totalLimit, List<CustActivityDetailVo> list) {
        totalLimit = null == totalLimit ? 0 : totalLimit;
        return list.size() < totalLimit;
    }

    /**
     * 判断是都满足  单一用户可参加活动次数
     *
     * @return
     */
    public boolean satisfyPerLimit(Integer perLimit, List<CustActivityDetailVo> list, String customerId) {
        List<CustActivityDetailVo> collect = list.stream()
                .filter(t -> t.getCustomerId().equals(customerId))
                .collect(Collectors.toList());
        return collect.size() < perLimit;
    }

    /**
     * 根据 活动id 查询  活动 已被 有效参加 的总记录（即 除核销之外的）
     *
     * @param activityId
     * @return
     */
    public List<CustActivityDetailVo> getValidListByActivityId(String activityId, Long orgId) {
        //查询 该活动下 所有参与记录  （包含 冻结、核销、已完成）
        CustActDetailSelectVo selectVo = new CustActDetailSelectVo()
                .setActivityId(activityId)
                .setOrgId(orgId);
        List<CustActivityDetailVo> totalList = detailService.getList(selectVo);
        //活动 已被 有效参加 的总记录（即 除 解冻之外的）
        List<CustActivityDetailVo> validList = totalList.stream().
                filter(t -> t.getRecordStatus() != ICustActivityDetailService.STATUS_UN_FREEZE)
                .collect(Collectors.toList());

        return validList;
    }

    public Boolean checkFullDiscountActivity(DiscountInfoVo discountInfoVo) {
        CampaignInfo campaignInfo = discountInfoVo.getCampaignInfo();
        Boolean hasLimit = campaignInfo.getHasLimit();
        if (hasLimit == null || !hasLimit) {
            //如果为null 或者次数无限制，则通过
            return true;
        }

        // 获得 单一用户可参加活动次数
        Integer perLimit = campaignInfo.getPerLimit();
        //获得 活动总限制次数
        Long totalLimit = campaignInfo.getTotalLimit().longValue();
        return matchParticipants(campaignInfo.getActivityCode(),
                discountInfoVo.getOrgId(),
                totalLimit, perLimit,
                discountInfoVo.getCustomerInfo().getCustomerId());
    }

    public Boolean checkCouponCodeActivity(DiscountInfoVo discountInfoVo) throws MyException {
        CampaignInfo campaignInfo = discountInfoVo.getCampaignInfo();
        List<RuleInfo> ruleInfoList = campaignInfo.getRule();
        if (CollectionUtils.isEmpty(ruleInfoList)) {
            throw new MyException(ResponseEnum.INCORRECT_PARAMS.getCode(), "活动id:" + discountInfoVo.getCampaignInfo().getActivityCode() + "无规则数据！");
        }
        RuleInfo ruleInfo = ruleInfoList.get(0);
        String couponCode = ruleInfo.getCouponCode();
        if (CollectionUtils.isEmpty(discountInfoVo.getCouponCodeList())) {
            log.info("优惠码活动与码值对应信息为空");
            throw new MyException(ActivityDiscountErrorEnum.COUPON_CODE_ERROR_ENUM.getCode(),"请补全活动及优惠码值信息！");
        }
        Map<String, CampaignVO> couponCodeMap = discountInfoVo.getCouponCodeList().stream().collect(Collectors.toMap(CampaignVO::getActivityCode, Function.identity()));
        //获得 该 活动  对应的 优惠码值
        CampaignVO couponCodeInput = couponCodeMap.get(campaignInfo.getActivityCode());
        if(!DiscountConstant.USED_ALONE.equalsIgnoreCase(couponCodeInput.getSpecialSign())) {
            //当不是特殊储值卡 优惠码活动时
            if (StringUtils.isEmpty(couponCodeInput.getCouponCode()) || !couponCodeInput.getCouponCode().equals(couponCode)) {
                log.info("活动优惠码：==>>" + couponCode + "\t输入优惠码：==" + couponCodeInput);
                throw new MyException(ActivityDiscountErrorEnum.COUPON_CODE_ERROR_ENUM.getCode(), "优惠码不正确！");
            }
        }
        //优惠码 活动中，这个仅标识 用户使用次数  限制
        Boolean hasLimit = campaignInfo.getHasLimit();
        //获取 码使用次数
        Long totalAmount = ruleInfo.getTotalAmount();
        if (hasLimit == null || !hasLimit) {
            //活动 已被 有效参加 的总记录（即 除  解冻之外之外的）
            List<CustActivityDetailVo> validList = getValidListByActivityId(campaignInfo.getActivityCode(), discountInfoVo.getOrgId());
            return satisfyTotalLimit(totalAmount, validList);
        }

        // 获得 单一用户可参加活动次数
        Integer perLimit = discountInfoVo.getCampaignInfo().getPerLimit();

        return matchParticipants(campaignInfo.getActivityCode(),
                discountInfoVo.getOrgId(),
                totalAmount, perLimit,
                discountInfoVo.getCustomerInfo().getCustomerId());
    }

    /**
     * 核对 活动、会员参与次数
     *
     * @param activityCode 活动信息
     * @param orgId        机构
     * @param perLimit     用户限制次数
     * @param totalLimit   活动限制次数
     * @param customerId   用户id
     * @return
     */
    public Boolean matchParticipants(String activityCode, Long orgId, Long totalLimit, Integer perLimit, String customerId) {
        //活动 已被 有效参加 的总记录（即 除  解冻之外之外的）
        List<CustActivityDetailVo> validList = getValidListByActivityId(activityCode, orgId);
        //判断 该活动总参与次数 是否满足
        boolean satisfyTotalLimit = satisfyTotalLimit(totalLimit, validList);
        if (!satisfyTotalLimit) {
            //若 活动总次数 不满足，则不满足
            return false;
        }
        //判断  单一用户可参加活动次数 是否满足
        return satisfyPerLimit(perLimit, validList, customerId);
    }

}
