package marketing.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.database.enums.ResponseEnum;
import org.etocrm.database.exception.MyException;
import org.etocrm.marketing.model.activitylist.ActivityListSelectVo;
import org.etocrm.marketing.model.activitylist.ActivityListVo;
import org.etocrm.marketing.model.activitymutex.ActivityTypeMutexSelectVo;
import org.etocrm.marketing.model.activitymutex.ActivityTypeMutexVo;
import org.etocrm.marketing.model.discount.MemberDetail;
import org.etocrm.marketing.model.discount.StackableActivityInputVo;
import org.etocrm.marketing.repository.IActivityListRepository;
import org.etocrm.marketing.service.IActivityListService;
import org.etocrm.marketing.service.IActivityMutexCheckerService;
import org.etocrm.marketing.service.IActivityTypeMutexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author xingxing.xie
 * @Date 2021/4/7 10:50
 */
@Slf4j
@Service
public class ActivityMutexCheckerServiceImpl implements IActivityMutexCheckerService {
    @Autowired
    private IActivityTypeMutexService activityTypeMutexService;

    @Autowired
    private IActivityListService activityListService;

    @Autowired
    private IActivityListRepository activityListRepository;

    @Autowired
    private DiscountCalculateServiceImpl discountCalculateService;

    @Override
    public List<String> getStackableActivityIds(StackableActivityInputVo inputVo) throws MyException {
        log.info("获取所有活动列表入参：{}", JSON.toJSONString(inputVo));
        if (CollectionUtils.isEmpty(inputVo.getActivityIds())) {
            throw new MyException(ResponseEnum.INCORRECT_PARAMS.getCode(), "请输入活动ID");
        }
        //获得会员等级
        MemberDetail memberDetail = inputVo.getMemberDetail();
        if(null==memberDetail||null==memberDetail.getVipLevelId()){
            throw new MyException(ResponseEnum.INCORRECT_PARAMS.getCode(),"请补齐会员信息");
        }
        Integer vipLevelId = memberDetail.getVipLevelId();
        ActivityListSelectVo activityListSelectVo = new ActivityListSelectVo()
                .setOrgId(inputVo.getOrgId())
                .setShopId(inputVo.getShopId());
        //获得门店下所有活动列表
        List<ActivityListVo> activityAllListVo = activityListService.getActivityListVo(activityListSelectVo);
        //过滤时间 过期的 活动
        activityAllListVo = activityListService.getFilteredListByTime(activityAllListVo);
        if (CollectionUtils.isEmpty(activityAllListVo)) {
            throw new MyException(ResponseEnum.INCORRECT_PARAMS.getCode(), "请输入正确的机构、门店ID");
        }
        List<ActivityTypeMutexVo> mutexVoList = getShopActivityType(inputVo.getOrgId(), inputVo.getShopId());
        //筛选出  门店可用的活动列表
        List<ActivityListVo> afterPickActivity = pickActivityFromShopType(activityAllListVo, mutexVoList);
        Map<String, Integer> getTypeByIdMap = afterPickActivity.stream().collect(Collectors.toMap(ActivityListVo::getActivityCode, ActivityListVo::getActivityType));
        Map<Integer, List<String>> mutexMap = Maps.newHashMap();
        if (!CollectionUtils.isEmpty(mutexVoList)) {
            //不为 null  则转化为 map key:活动类型  value:对应 互斥类型 集合
            mutexMap = mutexVoList.stream().collect(Collectors.toMap(t -> t.getActivityType(), t -> t.getMutexIds(), (key1, key2) -> key2));
        }

        List<String> toMatchMutexIds = Lists.newArrayList();
        // 获得 入参id 中  所有互斥id
        for (String activityId : inputVo.getActivityIds()) {
            //获得该活动类型
            Integer activityType = getTypeByIdMap.get(activityId);
            //获得互斥 活动id
            List<String> mutexIds = mutexMap.get(activityType);
            if(!CollectionUtils.isEmpty(mutexIds)){
                toMatchMutexIds.addAll(mutexIds);
            }
        }


        return afterPickActivity.stream()
                //过滤掉 与已选活动互斥的类型
                .filter(t -> !toMatchMutexIds.stream()
                        .anyMatch(mutexId -> mutexId.equals(t.getActivityType().toString())))
                //过滤掉特殊 储值卡优惠码活动
                .filter(t-> StringUtils.isEmpty(t.getSpecialSign()))
                //根据会员等级过滤
                .filter(t -> t.getVipRang().stream().anyMatch(
                        vipId ->vipId.trim().equals(vipLevelId.toString())))
                .map(ActivityListVo::getActivityCode).collect(Collectors.toList());
    }

    @Override
    public List<ActivityTypeMutexVo> getShopActivityType(Long orgId, String shopId) {
        //查询所有互斥信息
        ActivityTypeMutexSelectVo mutexSelectVo = new ActivityTypeMutexSelectVo()
                .setOrgId(orgId)
                .setShopId(shopId);
        //根据 活动类型 查询对应的 互斥关系
        List<ActivityTypeMutexVo> mutexVoList = activityTypeMutexService.getList(mutexSelectVo);
        if (CollectionUtils.isEmpty(mutexVoList)) {
            return Lists.newArrayList();
        }
        return mutexVoList;
    }

    @Override
    public List<ActivityListVo> pickActivityFromShopType(List<ActivityListVo> originActivityList, List<ActivityTypeMutexVo> shopMutexTypeList) {
        //筛选出 该门店可用的 活动类型
        List<Integer> activityTypeByShop = shopMutexTypeList.stream().map(ActivityTypeMutexVo::getActivityType).collect(Collectors.toList());
        List<ActivityListVo> result = originActivityList.stream().filter(t ->
                activityTypeByShop.stream().anyMatch(type -> type.equals(t.getActivityType()))
        ).collect(Collectors.toList());

        return result;
    }

}
