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
        //?????? ???????????????  is_enable  false  ?????????????????????
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
        //?????? specialSign  ?????? ??????????????????????????? ??????
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
            //?????????????????? ??????
            List<ActivityRuleInfoVo> activityRuleVoList = ruleInfoService.getActivityRuleVoList(new ActivityRuleInfoSelectVo().setActivityId(activity.getActivityCode()));
            //?????? ????????????
            activity.setRule(activityRuleVoList);
        }
        if (null != currentTimeMillis) {
            //???????????????????????? ??????
            List<ActivityListVo> result = activityListVo.stream().filter(t ->
                    //?????? ??????
                    t.getIsEnable()&& t.getActivityStart().getTime() <= currentTimeMillis
                    && currentTimeMillis <= t.getActivityEnd().getTime()
                   ).collect(Collectors.toList());
            return result;
        }
        //??????????????? null   ?????????????????????
        return activityListVo;
    }

    @Override
    public ActivityListVo save(ActivityListVo activityListVo) throws MyException {
        log.warn("???????????????????????????====>>"+JSON.toJSONString(activityListVo));
        try {
            coverExistActivityData(activityListVo);
            //??????????????????activity_list
            ActivityList activityList = activityListConvert.voToDo(activityListVo);
            List<ActivityList> activitySaveList = new ArrayList<>();
            activityListVo.getShopIds().stream().forEach(t -> {
                activityList.setShopId(t);
                activitySaveList.add(activityListConvert.doToSelf(activityList));
            });
            activityListRepository.saveAll(activitySaveList);
            // ?????????????????? ??????????????????
            // ???activity_rule_info ?????????????????????
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
            throw new MyException(ResponseEnum.FAILD.getCode(), "???????????????????????????");
        }

        return true;
    }

    @Override
    public ActivityListVo getCodeActivity(ActivityCodeListVo activityCodeListVo) {
        //?????????rule ???????????????????????????
        ActivityListVo activityListVo = activityListConvert.codeVoToVo(activityCodeListVo);
        // ?????? rule ??????
        ActivityRuleInfoVo codeRule = activityCodeListVo.getRule();
        List<ActivityRuleInfoVo> activityRuleInfoVos = Lists.newArrayList();
        activityRuleInfoVos.add(codeRule);
        activityListVo.setRule(activityRuleInfoVos);

        return activityListVo;
    }

    /**
     * ?????????????????????   ?????????????????????  ?????? orgId shopId activityId
     *
     * @param activityListVo
     * @throws MyException
     */
    public void coverExistActivityData(ActivityListVo activityListVo) throws MyException {
        List<String> selectActivityIds = new ArrayList<>();
        selectActivityIds.add(activityListVo.getActivityCode());
        //?????????
        ActivityListBatchSelectVo selectVo = new ActivityListBatchSelectVo()
                .setOrgId(activityListVo.getOrgId())
                .setActivityCode(selectActivityIds);
        if (!CollectionUtils.isEmpty(activityListVo.getShopIds())) {
            selectVo.setShopId(activityListVo.getShopIds());
        }
        //?????? ??????????????? ??????
        List<ActivityListVo> deleteActivityList = getActivityListByBatchVo(selectVo);
        if (CollectionUtils.isEmpty(deleteActivityList)) {
            return;
        }
        Boolean delete = delete(deleteActivityList);
        //?????? ?????????????????????????????? ??????
        if (delete) {
            //????????????id ?????? activity_rule???
            ActivityRuleInfoSelectVo ruleInfoSelectVo = new ActivityRuleInfoSelectVo()
                    .setOrgId(activityListVo.getOrgId())
                    .setActivityId(activityListVo.getActivityCode());
            List<ActivityRuleInfoVo> activityRuleVoList = ruleInfoService.getActivityRuleVoList(ruleInfoSelectVo);
            //?????????????????? ???????????????
            ruleInfoService.delete(activityRuleVoList);

            //????????????id  ?????? activity_goods???
            ActivityGoodsBatchSelectVo goodsBatchSelectVo = new ActivityGoodsBatchSelectVo()
                    .setOrgId(activityListVo.getOrgId())
                    .setActivityCode(activityListVo.getActivityCode())
                    .setShopId(activityListVo.getShopIds());
            List<ActivityGoodsVo> listByShopIds = goodService.getListByShopIds(goodsBatchSelectVo);
            //??????????????? ????????????
            goodService.delete(listByShopIds);

            //?????? ?????? ????????????????????????????????????
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
                //???????????? ???????????????
                ruleInfoList=boxFullDiscountRule(ruleInfoVoList);
                break;
            case FULL_GIVEN:
                //????????????
                boxFullGivenRule(ruleInfoVoList);
                break;
            case PROMOTION_CODE:
                //????????? ?????? rule ??????
                ruleInfoList = boxCouponCodeRule(ruleInfoVoList);
                break;
            default:
                throw new MyException(ResponseEnum.INCORRECT_PARAMS.getCode(), "?????????????????????");
        }
        //??? ?????? ???
        ruleInfoRepository.saveAll(ruleInfoList);

    }

    /**
     *  ????????????  ?????????????????? ??????
     * @param ruleInfoVoList
     * @return
     */
    public List<ActivityRuleInfo> boxFullDiscountRule(List<ActivityRuleInfoVo> ruleInfoVoList) {
        //????????? do??? ????????????
        List<ActivityRuleInfo> ruleInfoList = ruleInfoVoList.stream().map(t -> {
            ActivityRuleInfo ruleInfo = ruleInfoConvert.voToDo(t);
            ruleInfoConvert.boxRuleDo(ruleInfo, t.getCondition().get(0), t.getConditionExplain().get(0));
            return ruleInfo;
        }).collect(Collectors.toList());
        return ruleInfoList;
    }

    /**
     *  ?????? ???????????? ?????? ??????
     * @param ruleInfoVoList
     * @return
     */
    public void boxFullGivenRule(List<ActivityRuleInfoVo> ruleInfoVoList) throws MyException{
        boolean matchGiftList = ruleInfoVoList.stream().anyMatch(t ->
                CollectionUtils.isEmpty(t.getCondition())
        );
        if(matchGiftList){
            throw new MyException(ResponseEnum.INCORRECT_PARAMS.getCode(), "??????????????????????????????");
        }

        for(ActivityRuleInfoVo ruleInfoVo:ruleInfoVoList){
            //??? ???????????????
            Boolean saveSuccess = saveFullGivenRule(ruleInfoVo);
            if(saveSuccess){
                saveFullGivenGift(ruleInfoVo);
            }else {
                throw new MyException(ResponseEnum.FAILD.getCode(), "????????????????????????");
            }

        }
    }

    /**
     *  ?????????  ?????????????????? ??????
     * @param ruleInfoVoList
     * @return
     */
    public List<ActivityRuleInfo> boxCouponCodeRule(List<ActivityRuleInfoVo> ruleInfoVoList) {
        //????????? do??? ????????????
        List<ActivityRuleInfo> ruleInfoList = ruleInfoVoList.stream()
                .map(t -> ruleInfoConvert.voToDo(t))
                .collect(Collectors.toList());
        return ruleInfoList;
    }

    /**
     *  ?????? ??????????????? ???????????? ????????????????????????id
     * @param activityListVo
     * @return
     */
    public List<ActivityRuleInfoVo> boxBasicRuleInfo(ActivityListVo activityListVo) {
        List<ActivityRuleInfoVo> ruleInfoVoList = activityListVo.getRule();
        ruleInfoVoList.forEach(t -> {
            //??????orgId
            t.setOrgId(activityListVo.getOrgId());
            //????????????id
            t.setBrandId(activityListVo.getBrandId());
            //????????????id
            t.setActivityId(activityListVo.getActivityCode());
            //?????? ruleType
            t.setRuleType(activityListVo.getActivityType());
            //?????? ruleId
            t.setRuleId(UUID.randomUUID().toString());
        });
        return ruleInfoVoList;

    }

    /**
     * ???????????? ?????????
     * @param ruleInfoVo
     */
    public Boolean saveFullGivenRule(ActivityRuleInfoVo ruleInfoVo) {
        //????????? do??? ????????????
        ActivityRuleInfo ruleInfo = ruleInfoConvert.voToDo(ruleInfoVo);
        ruleInfoConvert.boxByRuleCondition(ruleInfo, ruleInfoVo.getCondition().get(0));
        //?????? ???????????????????????? ?????? ?????????????????????????????? ???????????????
        ruleInfo.setGiftsNum(ruleInfoVo.getConditionExplain().get(0).getGiftsNum());
        //??? ?????????
        ActivityRuleInfo save = ruleInfoRepository.save(ruleInfo);
        if(null!=save){
            return true;
        }
        return false;
    }

    /**
     * ???????????? ?????????
     * @param ruleInfoVo
     */
    public void saveFullGivenGift(ActivityRuleInfoVo ruleInfoVo) throws MyException {
        boolean selectedSave =!CollectionUtils.isEmpty(ruleInfoVo.getConditionExplain());

        boolean fixSave =!CollectionUtils.isEmpty(ruleInfoVo.getConditionExplain2());
        //????????????
        if(selectedSave){
            //????????????
            ConditionExplain conditionExplain = ruleInfoVo.getConditionExplain().get(0);
            List<GiftInfoVo> selectableGifts = conditionExplain.getGifts();
            selectableGifts.forEach(t->{
                giftInfoConvert.partValueSet(t,ruleInfoVo);
                t.setGiftType(DiscountConstant.GIFT_SELECTABLE);
            });
            giftInfoService.save(selectableGifts);

        }
        if(fixSave){
            //????????????
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
     * ?????? specialSign  ?????? ??????????????????????????? ??????
     * @param activityListVo
     */
    public void setSpecialSignValue(ActivityListVo activityListVo, List<String> specialCouponCodes){
       //????????????????????? ??????????????????????????????
        if(CollectionUtil.isNotEmpty(specialCouponCodes)){
            // ?????????   ????????? ????????? ?????????????????????????????? ??????  ?????????????????????
            if(matchExistSpecial(activityListVo.getActivityCode(),specialCouponCodes)){
                activityListVo.setSpecialSign(DiscountConstant.USED_ALONE);
            }
        }

    }


}
