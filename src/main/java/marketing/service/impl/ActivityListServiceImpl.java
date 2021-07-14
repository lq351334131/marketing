package marketing.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.database.enums.ResponseEnum;
import org.etocrm.database.exception.MyException;
import org.etocrm.database.util.BasePage;
import org.etocrm.database.util.QueryDslUtil;
import org.etocrm.marketing.constant.ActivityTypeEnum;
import org.etocrm.marketing.constant.DiscountConstant;
import org.etocrm.marketing.convert.ActivityListConvert;
import org.etocrm.marketing.convert.ActivityRuleInfoConvert;
import org.etocrm.marketing.convert.GiftInfoConvert;
import org.etocrm.marketing.entity.ActivityList;
import org.etocrm.marketing.entity.ActivityRuleInfo;
import org.etocrm.marketing.entity.QActivityList;
import org.etocrm.marketing.model.activitygoods.ActivityGoodsBatchSelectVo;
import org.etocrm.marketing.model.activitygoods.ActivityGoodsVo;
import org.etocrm.marketing.model.activitylist.*;
import org.etocrm.marketing.model.activityrule.ActivityRuleInfoSelectVo;
import org.etocrm.marketing.model.activityrule.ActivityRuleInfoVo;
import org.etocrm.marketing.model.discount.activityrule.ConditionExplain;
import org.etocrm.marketing.model.giftinfo.GiftInfoSelectVo;
import org.etocrm.marketing.model.giftinfo.GiftInfoVo;
import org.etocrm.marketing.repository.IActivityListRepository;
import org.etocrm.marketing.repository.IActivityRuleInfoRepository;
import org.etocrm.marketing.service.*;
import org.hibernate.StaleObjectStateException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @Author xingxing.xie
 * @Date 2021/3/23 15:28
 */
@Service
@Slf4j
public class ActivityListServiceImpl implements IActivityListService {
    @Autowired
    private IActivityListRepository activityListRepository;
    @Autowired
    private IActivityRuleInfoRepository ruleInfoRepository;
    @Autowired
    private ActivityListConvert activityListConvert;
    @Autowired
    private ActivityRuleInfoConvert ruleInfoConvert;
    @Autowired
    private GiftInfoConvert giftInfoConvert;
    @Autowired
    private IGiftInfoService giftInfoService;
    @Autowired
    private IActivityRuleInfoService ruleInfoService;
    @Autowired
    private IActivityGoodService goodService;
    @Autowired
    private IOrgParamsService orgParamsService;
    @Autowired
    JPAQueryFactory queryFactory;

    @Override
    public BasePage<ActivityListVo> getActivityListByPage(ActivityListPageVo activityListPageVo) {
        QActivityList qActivityList = QActivityList.activityList;
        BooleanBuilder booleanBuilder = new QueryDslUtil<>(ActivityList.class, "activityList").where(activityListPageVo);
        QueryResults<ActivityList> activityListQueryResults = queryFactory.select(new QActivityList(qActivityList))
                .from(qActivityList)
                .where(booleanBuilder).fetchResults();
        List<ActivityListVo> collect = activityListQueryResults.getResults().stream().map(s ->
                activityListConvert.doToVo(s)
        ).collect(Collectors.toList());

        return new BasePage<>(collect, activityListQueryResults.getOffset(), activityListQueryResults.getTotal(), activityListQueryResults.getTotal() / activityListQueryResults.getLimit());
    }

    @Override
    public List<ActivityListVo> getActivityListVo(ActivityListSelectVo activityListSelectVo) {
        //查询 有效的活动  is_enable  false  表示下架的活动
        QActivityList qActivityList = QActivityList.activityList;
        BooleanBuilder booleanBuilder = new QueryDslUtil<>(ActivityList.class, "activityList").where(activityListSelectVo);
        QueryResults<ActivityList> activityListQueryResults = queryFactory.
                select(new QActivityList(qActivityList))
                .from(qActivityList)
                .where(booleanBuilder)
                .fetchResults();
        List<ActivityListVo> collect = activityListQueryResults.getResults().stream().map(s ->
                activityListConvert.doToVo(s)
        ).collect(Collectors.toList());
        if(CollUtil.isEmpty(collect)){
            return Lists.newArrayList();
        }

        List<String> specialCouponCode = orgParamsService.getSpecialCouponCode(activityListSelectVo.getOrgId())
                .stream().map(t->t.getActivityCode()).collect(Collectors.toList());
        //封装 specialSign  字段 用于储值卡特定优惠 情况
        collect.forEach(t->setSpecialSignValue(t,specialCouponCode));

        return collect;
    }

    @Override
    public List<ActivityListVo> getActivityListByBatchVo(ActivityListBatchSelectVo activityListBatchSelectVo) {
        QActivityList qActivityList = QActivityList.activityList;
        BooleanBuilder booleanBuilder = new QueryDslUtil<>(ActivityList.class, "activityList").where(activityListBatchSelectVo);
        QueryResults<ActivityList> activityListQueryResults = queryFactory.
                select(new QActivityList(qActivityList))
                .from(qActivityList)
                .where(booleanBuilder)
                .fetchResults();
        return activityListQueryResults.getResults().stream().map(s ->
                activityListConvert.doToVo(s)
        ).collect(Collectors.toList());
    }

    @Override
    public List<ActivityListVo> getValidActivityByShopId(ActivityListSelectVo activityListSelectVo, Long currentTimeMillis) {
        List<ActivityListVo> activityListVo = getActivityListVo(activityListSelectVo);
        for (ActivityListVo activity : activityListVo) {
            //查询该活动下 规则
            List<ActivityRuleInfoVo> activityRuleVoList = ruleInfoService.getActivityRuleVoList(new ActivityRuleInfoSelectVo().setActivityId(activity.getActivityCode()));
            //填充 规则属性
            activity.setRule(activityRuleVoList);
        }
        if (null != currentTimeMillis) {
            //过滤掉时间无效的 活动
            List<ActivityListVo> result = activityListVo.stream().filter(t ->
                    //是否 上架
                    t.getIsEnable()&& t.getActivityStart().getTime() <= currentTimeMillis
                    && currentTimeMillis <= t.getActivityEnd().getTime()
                   ).collect(Collectors.toList());
            return result;
        }
        //如果时间为 null   则默认查询所有
        return activityListVo;
    }

    @Override
    public ActivityListVo save(ActivityListVo activityListVo) throws MyException {
        log.warn("活动规则同步入参：====>>"+JSON.toJSONString(activityListVo));
        try {
            coverExistActivityData(activityListVo);
            //存活动规则表activity_list
            ActivityList activityList = activityListConvert.voToDo(activityListVo);
            List<ActivityList> activitySaveList = new ArrayList<>();
            activityListVo.getShopIds().stream().forEach(t -> {
                activityList.setShopId(t);
                activitySaveList.add(activityListConvert.doToSelf(activityList));
            });
            activityListRepository.saveAll(activitySaveList);
            // 不同活动类型 规则结构不同
            // 存activity_rule_info 活动规则信息表
            saveRuleDataByType(activityListVo,ActivityTypeEnum.getEnumByValue(activityListVo.getActivityType()));

        } catch (ObjectOptimisticLockingFailureException e) {
            if (e.getCause() instanceof StaleObjectStateException) {
                throw new MyException(ResponseEnum.RECORD_ALREADY_UPDATE, e.getCause());
            }
        }

        return activityListVo;
    }

    @Override
    public ActivityListVo update(ActivityListVo activityListVo) throws MyException {
        ActivityList activityList = activityListConvert.voToDo(activityListVo);
        ActivityList save = activityListRepository.update(activityList);
        BeanUtils.copyProperties(save, activityListVo);
        return activityListVo;
    }

    @Override
    public Boolean delete(List<ActivityListVo> activityListVos) throws MyException {
        try {
            List<ActivityList> activityLists = activityListConvert.voListToDoList(activityListVos);
            activityListRepository.logicDelete(activityLists);
        } catch (Exception e) {
            throw new MyException(ResponseEnum.FAILD.getCode(), "活动数据删除失败！");
        }

        return true;
    }

    @Override
    public ActivityListVo getCodeActivity(ActivityCodeListVo activityCodeListVo) {
        //先将除rule 属性外其他字段赋值
        ActivityListVo activityListVo = activityListConvert.codeVoToVo(activityCodeListVo);
        // 处理 rule 字段
        ActivityRuleInfoVo codeRule = activityCodeListVo.getRule();
        List<ActivityRuleInfoVo> activityRuleInfoVos = Lists.newArrayList();
        activityRuleInfoVos.add(codeRule);
        activityListVo.setRule(activityRuleInfoVos);

        return activityListVo;
    }

    /**
     * 同步活动信息时   覆盖已存在数据  根据 orgId shopId activityId
     *
     * @param activityListVo
     * @throws MyException
     */
    public void coverExistActivityData(ActivityListVo activityListVo) throws MyException {
        List<String> selectActivityIds = new ArrayList<>();
        selectActivityIds.add(activityListVo.getActivityCode());
        //先查询
        ActivityListBatchSelectVo selectVo = new ActivityListBatchSelectVo()
                .setOrgId(activityListVo.getOrgId())
                .setActivityCode(selectActivityIds);
        if (!CollectionUtils.isEmpty(activityListVo.getShopIds())) {
            selectVo.setShopId(activityListVo.getShopIds());
        }
        //查询 将要删除的 活动
        List<ActivityListVo> deleteActivityList = getActivityListByBatchVo(selectVo);
        if (CollectionUtils.isEmpty(deleteActivityList)) {
            return;
        }
        Boolean delete = delete(deleteActivityList);
        //如果 活动删除成功，则删除 规则
        if (delete) {
            //根据活动id 查询 activity_rule表
            ActivityRuleInfoSelectVo ruleInfoSelectVo = new ActivityRuleInfoSelectVo()
                    .setOrgId(activityListVo.getOrgId())
                    .setActivityId(activityListVo.getActivityCode());
            List<ActivityRuleInfoVo> activityRuleVoList = ruleInfoService.getActivityRuleVoList(ruleInfoSelectVo);
            //删除该活动下 的规则数据
            ruleInfoService.delete(activityRuleVoList);

            //根据活动id  删除 activity_goods表
            ActivityGoodsBatchSelectVo goodsBatchSelectVo = new ActivityGoodsBatchSelectVo()
                    .setOrgId(activityListVo.getOrgId())
                    .setActivityCode(activityListVo.getActivityCode())
                    .setShopId(activityListVo.getShopIds());
            List<ActivityGoodsVo> listByShopIds = goodService.getListByShopIds(goodsBatchSelectVo);
            //删除对应的 关联商品
            goodService.delete(listByShopIds);

            //删除 赠品 （如果是满赠活动的情况）
            boolean isFullGiven = ActivityTypeEnum.getEnumByValue(activityListVo.getActivityType()).equals(ActivityTypeEnum.FULL_GIVEN);
            if(isFullGiven){
                List<String> ruleIdList = activityRuleVoList.stream().map(t -> t.getRuleId()).collect(Collectors.toList());
                GiftInfoSelectVo giftInfoSelectVo = new GiftInfoSelectVo()
                        .setOrgId(activityListVo.getOrgId())
                        .setActivityId(activityListVo.getActivityCode())
                        .setRuleId(ruleIdList);
                List<GiftInfoVo> giftInfoVo = giftInfoService.getGiftInfoVo(giftInfoSelectVo);
                if (!CollectionUtils.isEmpty(giftInfoVo)) {
                    giftInfoService.delete(giftInfoVo);
                }
            }
        }
    }

    public void saveRuleDataByType(ActivityListVo activityListVo,ActivityTypeEnum activityType) throws MyException {
        List<ActivityRuleInfoVo> ruleInfoVoList = boxBasicRuleInfo(activityListVo);
        List<ActivityRuleInfo> ruleInfoList = new ArrayList<>();
        switch (activityType){
            case FULL_DISCOUNT:
                //封装将要 插入的数据
                ruleInfoList=boxFullDiscountRule(ruleInfoVoList);
                break;
            case FULL_GIVEN:
                //数据入库
                boxFullGivenRule(ruleInfoVoList);
                break;
            case PROMOTION_CODE:
                //优惠码 相关 rule 部分
                ruleInfoList = boxCouponCodeRule(ruleInfoVoList);
                break;
            default:
                throw new MyException(ResponseEnum.INCORRECT_PARAMS.getCode(), "活动类型异常！");
        }
        //入 规则 库
        ruleInfoRepository.saveAll(ruleInfoList);

    }

    /**
     *  满减满折  活动规则部分 存储
     * @param ruleInfoVoList
     * @return
     */
    public List<ActivityRuleInfo> boxFullDiscountRule(List<ActivityRuleInfoVo> ruleInfoVoList) {
        //转换为 do类 准备入库
        List<ActivityRuleInfo> ruleInfoList = ruleInfoVoList.stream().map(t -> {
            ActivityRuleInfo ruleInfo = ruleInfoConvert.voToDo(t);
            ruleInfoConvert.boxRuleDo(ruleInfo, t.getCondition().get(0), t.getConditionExplain().get(0));
            return ruleInfo;
        }).collect(Collectors.toList());
        return ruleInfoList;
    }

    /**
     *  满赠 活动规则 部分 存储
     * @param ruleInfoVoList
     * @return
     */
    public void boxFullGivenRule(List<ActivityRuleInfoVo> ruleInfoVoList) throws MyException{
        boolean matchGiftList = ruleInfoVoList.stream().anyMatch(t ->
                CollectionUtils.isEmpty(t.getCondition())
        );
        if(matchGiftList){
            throw new MyException(ResponseEnum.INCORRECT_PARAMS.getCode(), "请补全活动规则信息！");
        }

        for(ActivityRuleInfoVo ruleInfoVo:ruleInfoVoList){
            //存 满赠规则表
            Boolean saveSuccess = saveFullGivenRule(ruleInfoVo);
            if(saveSuccess){
                saveFullGivenGift(ruleInfoVo);
            }else {
                throw new MyException(ResponseEnum.FAILD.getCode(), "规则数据同步失败");
            }

        }
    }

    /**
     *  优惠码  活动规则部分 存储
     * @param ruleInfoVoList
     * @return
     */
    public List<ActivityRuleInfo> boxCouponCodeRule(List<ActivityRuleInfoVo> ruleInfoVoList) {
        //转换为 do类 准备入库
        List<ActivityRuleInfo> ruleInfoList = ruleInfoVoList.stream()
                .map(t -> ruleInfoConvert.voToDo(t))
                .collect(Collectors.toList());
        return ruleInfoList;
    }

    /**
     *  填充 活动规则类 基本属性 如：机构、活动等id
     * @param activityListVo
     * @return
     */
    public List<ActivityRuleInfoVo> boxBasicRuleInfo(ActivityListVo activityListVo) {
        List<ActivityRuleInfoVo> ruleInfoVoList = activityListVo.getRule();
        ruleInfoVoList.forEach(t -> {
            //设置orgId
            t.setOrgId(activityListVo.getOrgId());
            //设置品牌id
            t.setBrandId(activityListVo.getBrandId());
            //设置活动id
            t.setActivityId(activityListVo.getActivityCode());
            //设置 ruleType
            t.setRuleType(activityListVo.getActivityType());
            //设置 ruleId
            t.setRuleId(UUID.randomUUID().toString());
        });
        return ruleInfoVoList;

    }

    /**
     * 存储满赠 规则表
     * @param ruleInfoVo
     */
    public Boolean saveFullGivenRule(ActivityRuleInfoVo ruleInfoVo) {
        //转换为 do类 准备入库
        ActivityRuleInfo ruleInfo = ruleInfoConvert.voToDo(ruleInfoVo);
        ruleInfoConvert.boxByRuleCondition(ruleInfo, ruleInfoVo.getCondition().get(0));
        //设置 可选赠品最多赠送 几件 （与可选赠品可选件数 区分开来）
        ruleInfo.setGiftsNum(ruleInfoVo.getConditionExplain().get(0).getGiftsNum());
        //存 规则表
        ActivityRuleInfo save = ruleInfoRepository.save(ruleInfo);
        if(null!=save){
            return true;
        }
        return false;
    }

    /**
     * 存储满赠 赠品表
     * @param ruleInfoVo
     */
    public void saveFullGivenGift(ActivityRuleInfoVo ruleInfoVo) throws MyException {
        boolean selectedSave =!CollectionUtils.isEmpty(ruleInfoVo.getConditionExplain());

        boolean fixSave =!CollectionUtils.isEmpty(ruleInfoVo.getConditionExplain2());
        //存赠品表
        if(selectedSave){
            //可选赠品
            ConditionExplain conditionExplain = ruleInfoVo.getConditionExplain().get(0);
            List<GiftInfoVo> selectableGifts = conditionExplain.getGifts();
            selectableGifts.forEach(t->{
                giftInfoConvert.partValueSet(t,ruleInfoVo);
                t.setGiftType(DiscountConstant.GIFT_SELECTABLE);
            });
            giftInfoService.save(selectableGifts);

        }
        if(fixSave){
            //固定赠品
            ConditionExplain conditionExplain2 = ruleInfoVo.getConditionExplain2().get(0);
            List<GiftInfoVo> fixedGifts = conditionExplain2.getGifts();
            fixedGifts.forEach(t->{
                giftInfoConvert.partValueSet(t,ruleInfoVo);
                t.setGiftType(DiscountConstant.GIFT_FIXED);
            });
            giftInfoService.save(fixedGifts);

        }

    }

    /**
     * 封装 specialSign  字段 用于储值卡特定优惠 情况
     * @param activityListVo
     */
    public void setSpecialSignValue(ActivityListVo activityListVo, List<String> specialCouponCodes){
       //如果该门店存在 特殊储值卡优惠码活动
        if(CollectionUtil.isNotEmpty(specialCouponCodes)){
            // 该活动   与门店 配置的 特殊储值卡优惠码活动 匹配  则设置特殊字符
            if(matchExistSpecial(activityListVo.getActivityCode(),specialCouponCodes)){
                activityListVo.setSpecialSign(DiscountConstant.USED_ALONE);
            }
        }

    }


}
