package marketing.service.impl;

import com.alibaba.fastjson.JSON;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.database.enums.ResponseEnum;
import org.etocrm.database.exception.MyException;
import org.etocrm.database.util.QueryDslUtil;
import org.etocrm.marketing.convert.ActivityGoodsConvert;
import org.etocrm.marketing.entity.ActivityUnionGoods;
import org.etocrm.marketing.entity.QActivityUnionGoods;
import org.etocrm.marketing.model.activitygoods.ActivityGoodsBatchSelectVo;
import org.etocrm.marketing.model.activitygoods.ActivityGoodsSelectVo;
import org.etocrm.marketing.model.activitygoods.ActivityGoodsVo;
import org.etocrm.marketing.model.activitygoods.ActivityUnionGoodVo;
import org.etocrm.marketing.model.activitylist.ActivityListSelectVo;
import org.etocrm.marketing.model.activitylist.ActivityListVo;
import org.etocrm.marketing.repository.IActivityUnionGoodsRepository;
import org.etocrm.marketing.service.IActivityGoodService;
import org.etocrm.marketing.service.IActivityListService;
import org.hibernate.StaleObjectStateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author xingxing.xie
 * @Date 2021/3/29 15:54
 */
@Service
@Slf4j
public class ActivityGoodServiceImpl implements IActivityGoodService {
    @Autowired
    private IActivityUnionGoodsRepository repository;
    @Autowired
    private IActivityListService activityListService;
    @Autowired
    private ActivityGoodsConvert activityGoodsConvert;
    @Autowired
    JPAQueryFactory queryFactory;

    @Override
    public ActivityUnionGoodVo save(ActivityUnionGoodVo activityUnionGoodVo) throws MyException {
        try {
            log.warn("活动关联商品数据同步入参：{}", JSON.toJSONString(activityUnionGoodVo));
            //查询对应的活动列表
            List<String> activityIds = new ArrayList<>();
            List<String> shopIds = activityUnionGoodVo.getShopIds();
            activityIds.add(activityUnionGoodVo.getActivityId());
            ActivityListSelectVo activityListSelectVo = new ActivityListSelectVo().setOrgId(activityUnionGoodVo.getOrgId()).setActivityCode(activityIds);
            List<ActivityListVo> activityListVos = activityListService.getActivityListVo(activityListSelectVo);
            if(CollectionUtils.isEmpty(activityListVos)){
                throw new MyException(ResponseEnum.INCORRECT_PARAMS.getCode(),"暂无该活动信息");
            }
            List<ActivityUnionGoods> list = toList(activityUnionGoodVo,activityListVos.get(0));
            //门店id，每个门店下 插入对应商品
            for(String shopId:shopIds){
                list.forEach(t->{
                    t.setShopId(shopId);
                });
                List<ActivityUnionGoods> addList = list.stream().map(t -> {
                    t.setShopId(shopId);
                    return activityGoodsConvert.doToSelf(t);
                }).collect(Collectors.toList());
                //批量插入
                repository.saveAll(addList);
            }
        } catch (ObjectOptimisticLockingFailureException e) {
            if (e.getCause() instanceof StaleObjectStateException) {
                throw new MyException(ResponseEnum.RECORD_ALREADY_UPDATE, e.getCause());
            }
        }

        return activityUnionGoodVo;
    }

    @Override
    public Boolean delete(List<ActivityGoodsVo> activityGoodsVos) throws MyException {
        try {
            //批量逻辑 删除
            repository.logicDelete(activityGoodsConvert.voListToDoList(activityGoodsVos));
        } catch (Exception e) {
            throw new MyException(ResponseEnum.FAILD.getCode(), "活动关联商品删除失败！");
        }

        return true;
    }

    @Override
    public List<ActivityGoodsVo> getList(ActivityGoodsSelectVo activityGoodsSelectVo) {
        QActivityUnionGoods qActivityUnionGoods = QActivityUnionGoods.activityUnionGoods;
        BooleanBuilder booleanBuilder = new QueryDslUtil<>(ActivityUnionGoods.class, "activityUnionGoods").where(activityGoodsSelectVo);
        QueryResults<ActivityUnionGoods> queryResults = queryFactory.select(new QActivityUnionGoods(qActivityUnionGoods))
                .from(qActivityUnionGoods)
                .where(booleanBuilder)
                .fetchResults();
        List<ActivityGoodsVo> collect = queryResults.getResults().stream().map(s ->
            activityGoodsConvert.doToVo(s)
        ).collect(Collectors.toList());
        return collect;
    }

    /**
     * 此查询方法 不同点：条件包含对各shopId
     * @param activityGoodsBatchSelectVo
     * @return
     */
    @Override
    public List<ActivityGoodsVo> getListByShopIds(ActivityGoodsBatchSelectVo activityGoodsBatchSelectVo) {
        QActivityUnionGoods qActivityUnionGoods = QActivityUnionGoods.activityUnionGoods;
        BooleanBuilder booleanBuilder = new QueryDslUtil<>(ActivityUnionGoods.class, "activityUnionGoods").where(activityGoodsBatchSelectVo);
        QueryResults<ActivityUnionGoods> queryResults = queryFactory.select(new QActivityUnionGoods(qActivityUnionGoods))
                .from(qActivityUnionGoods)
                .where(booleanBuilder)
                .fetchResults();
        List<ActivityGoodsVo> collect = queryResults.getResults().stream().map(s ->
            activityGoodsConvert.doToVo(s)
        ).collect(Collectors.toList());
        return collect;
    }

    /**
     * 根据入参 赋值对象 仅包括 （活动ID、商品ID）  批量 新增/删除
     * @param activityUnionGoodVo
     * @return
     */
    public List<ActivityUnionGoods> toList(ActivityUnionGoodVo activityUnionGoodVo,ActivityListVo activityListVo) {

        List<ActivityUnionGoods> list = new ArrayList<>();
            //赋值入库对象 仅包括 （活动ID、商品ID）
        activityUnionGoodVo.getGoods().forEach(t -> {
            ActivityUnionGoods activityUnionGoods = activityGoodsConvert.voToDo(activityListVo);
            //设置 productId
            activityUnionGoods.setProductId(t.getProductId());
            //设置 systemSku
            activityUnionGoods.setSystemSku(t.getSystemSku());
            //设置 info  json字符串
            activityUnionGoods.setInfo(t.getInfo());
            list.add(activityUnionGoods);
        });
        return list;
    }
}
