package marketing.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.database.enums.ResponseEnum;
import org.etocrm.database.exception.MyException;
import org.etocrm.database.util.QueryDslUtil;
import org.etocrm.marketing.constant.DiscountConstant;
import org.etocrm.marketing.convert.ActivityMutexConvert;
import org.etocrm.marketing.entity.ActivityTypeMutex;
import org.etocrm.marketing.entity.QActivityTypeMutex;
import org.etocrm.marketing.model.activitymutex.ActivityMutexData;
import org.etocrm.marketing.model.activitymutex.ActivityTypeInfo;
import org.etocrm.marketing.model.activitymutex.ActivityTypeMutexSelectVo;
import org.etocrm.marketing.model.activitymutex.ActivityTypeMutexVo;
import org.etocrm.marketing.repository.IActivityTypeMutexRepository;
import org.etocrm.marketing.service.IActivityTypeMutexService;
import org.hibernate.StaleObjectStateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author xingxing.xie
 * @Date 2021/3/29 11:19
 */
@Slf4j
@Service
public class ActivityTypeMutexServiceImpl implements IActivityTypeMutexService {
    @Autowired
    private IActivityTypeMutexRepository activityTypeMutexRepository;
    @Autowired
    private ActivityMutexConvert activityMutexConvert;
    @Autowired
    JPAQueryFactory queryFactory;

    @Override
    public ActivityTypeMutexVo save(ActivityMutexData activityMutexData) throws MyException {
        try {
            log.warn("活动互斥关系同步入参：{}", JSON.toJSONString(activityMutexData));
            Long brandId = activityMutexData.getBrandId();
            Long orgId = activityMutexData.getOrganizationId();
            String configId = activityMutexData.getConfigId();
            List<String> shopIds = activityMutexData.getShopIds();
            if(CollectionUtils.isEmpty(shopIds)){
                logicDeleteByConfigId(brandId, orgId, configId);
                return new ActivityTypeMutexVo();
            }
            //参数校验
            checkDataValidity(activityMutexData);
            //清空之前所有
            logicDeleteByConfigId(brandId, orgId, configId);

            List<ActivityTypeInfo> paymentRule = activityMutexData.getPaymentRule();
            //设置品牌及 机构id
            ActivityTypeMutexVo activityTypeMutexVo = new ActivityTypeMutexVo()
                    .setOrgId(orgId)
                    .setConfigId(configId)
                    .setBrandId(brandId);
            Map<String, Map<String, Integer>> marketingRelation = activityMutexData.getMarketingRelation();
            for(String shopId:shopIds){
                activityTypeMutexVo.setShopId(shopId);
                for(int i=0;i<paymentRule.size();i++){
                    ActivityTypeInfo activityTypeInfo = paymentRule.get(i);

                    activityMutexConvert.boxByActivityTypeInfo(activityTypeMutexVo, activityTypeInfo);
                    //设置活动计算顺序
                    activityTypeMutexVo.setActivityOrder(i);
                    Map<String, Integer> mutexRelationMap = marketingRelation.get(activityTypeInfo.getNid());
                    if (CollectionUtils.isEmpty(mutexRelationMap)) {
                        throw new MyException(ResponseEnum.INCORRECT_PARAMS.getCode(), "非法入参，请核对活动类型互斥信息！");
                    }
                    //封装 对应的共享、互斥 ids
                    boxIdsByRelationMap(mutexRelationMap,activityTypeMutexVo);
                    activityTypeMutexRepository.save(activityMutexConvert.voToDo(activityTypeMutexVo));

                }

            }

            return activityTypeMutexVo;
        } catch (ObjectOptimisticLockingFailureException e) {
            if (e.getCause() instanceof StaleObjectStateException) {
                throw new MyException(ResponseEnum.RECORD_ALREADY_UPDATE, e.getCause());
            }
            return null;
        }
    }

    private void logicDeleteByConfigId(Long brandId, Long orgId, String configId)throws MyException {
        if(null==orgId||null==brandId|| StringUtils.isEmpty(configId)){
            throw new MyException(ResponseEnum.INCORRECT_PARAMS.getCode(), "请补机构或品牌规则信息！");
        }
        //清空之前所有
        ActivityTypeMutexSelectVo selectDeleteVo = new ActivityTypeMutexSelectVo()
                .setOrgId(orgId)
                .setBrandId(brandId)
                .setConfigId(configId);
        List<ActivityTypeMutexVo> list = getList(selectDeleteVo);
        if(!CollectionUtils.isEmpty(list)){
            //如果查询为 不为null，则删除 对应数据
            activityTypeMutexRepository.logicDelete(activityMutexConvert.voListToDoList(list));
        }
    }

    /**
     * 封装 对应的共享、互斥 ids
     * @param mutexRelationMap
     * @param activityTypeMutexVo
     */
    private void boxIdsByRelationMap(Map<String, Integer> mutexRelationMap,ActivityTypeMutexVo activityTypeMutexVo) throws MyException {
        List<String> mutexIds = Lists.newArrayList();
        List<String> stackableIds = Lists.newArrayList();
        for(String activityType:mutexRelationMap.keySet()){
            Integer value = mutexRelationMap.get(activityType);
            if(value==DiscountConstant.ACTIVITY_MUTEX){
                //互斥
                mutexIds.add(activityType);
            }else if(value==DiscountConstant.ACTIVITY_STACKABLE){
                //可叠加
                stackableIds.add(activityType);
            }

        }
        activityTypeMutexVo.setMutexIds(mutexIds);
        activityTypeMutexVo.setStackableIds(stackableIds);
    }

    @Override
    public ActivityTypeMutexVo update(ActivityTypeMutexVo activityTypeMutexVo) throws MyException {
        ActivityTypeMutex activityTypeMutex = activityMutexConvert.voToDo(activityTypeMutexVo);
        ActivityTypeMutex update = activityTypeMutexRepository.update(activityTypeMutex);
        return activityMutexConvert.doToVo(update);
    }

    @Override
    public Boolean delete(ActivityTypeMutexVo activityTypeMutexVo) throws MyException {
        try {
            ActivityTypeMutex activityTypeMutex = new ActivityTypeMutex();
            BeanUtil.copyProperties(activityTypeMutexVo,activityTypeMutex);
            activityTypeMutexRepository.logicDelete(activityTypeMutex);
        } catch (Exception e) {
            throw new MyException(ResponseEnum.FAILD, e);
        }

        return true;
    }

    @Override
    public List<ActivityTypeMutexVo>  getList(ActivityTypeMutexSelectVo activityTypeMutexSelectVo){
        QActivityTypeMutex qActivityTypeMutex = QActivityTypeMutex.activityTypeMutex;
        BooleanBuilder booleanBuilder = new QueryDslUtil<>(ActivityTypeMutex.class, "activityTypeMutex").where(activityTypeMutexSelectVo);
        QueryResults<ActivityTypeMutex> results = queryFactory.
                select(new QActivityTypeMutex(qActivityTypeMutex))
                .from(qActivityTypeMutex)
                .where(booleanBuilder)
                .fetchResults();
        return results.getResults().stream().map(s ->
                activityMutexConvert.doToVo(s)
        ).collect(Collectors.toList());
    }

    /**
     * 数据校验
     * @param activityMutexData
     * @throws MyException
     */
    private void checkDataValidity(ActivityMutexData activityMutexData) throws MyException {

        List<ActivityTypeInfo> paymentRule = activityMutexData.getPaymentRule();
        Map<String, Map<String, Integer>> marketingRelation = activityMutexData.getMarketingRelation();
        if(CollectionUtils.isEmpty(paymentRule)){
            throw new MyException(ResponseEnum.INCORRECT_PARAMS.getCode(), "请补活动计算顺序信息！");
        }
        if(marketingRelation.keySet().size()<=0){
            throw new MyException(ResponseEnum.INCORRECT_PARAMS.getCode(), "请补活动互斥信息！");
        }
    }

}
