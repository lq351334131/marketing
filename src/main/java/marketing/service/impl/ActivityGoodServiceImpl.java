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
            log.warn("???????????????????????????????????????{}", JSON.toJSONString(activityUnionGoodVo));
            //???????????????????????????
            List<String> activityIds = new ArrayList<>();
            List<String> shopIds = activityUnionGoodVo.getShopIds();
            activityIds.add(activityUnionGoodVo.getActivityId());
            ActivityListSelectVo activityListSelectVo = new ActivityListSelectVo().setOrgId(activityUnionGoodVo.getOrgId()).setActivityCode(activityIds);
            List<ActivityListVo> activityListVos = activityListService.getActivityListVo(activityListSelectVo);
            if(CollectionUtils.isEmpty(activityListVos)){
                throw new MyException(ResponseEnum.INCORRECT_PARAMS.getCode(),"?????????????????????");
            }
            List<ActivityUnionGoods> list = toList(activityUnionGoodVo,activityListVos.get(0));
            //??????id?????????????????? ??????????????????
            for(String shopId:shopIds){
                list.forEach(t->{
                    t.setShopId(shopId);
                });
                List<ActivityUnionGoods> addList = list.stream().map(t -> {
                    t.setShopId(shopId);
                    return activityGoodsConvert.doToSelf(t);
                }).collect(Collectors.toList());
                //????????????
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
            //???????????? ??????
            repository.logicDelete(activityGoodsConvert.voListToDoList(activityGoodsVos));
        } catch (Exception e) {
            throw new MyException(ResponseEnum.FAILD.getCode(), "?????????????????????????????????");
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
     * ??????????????? ??????????????????????????????shopId
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
     * ???????????? ???????????? ????????? ?????????ID?????????ID???  ?????? ??????/??????
     * @param activityUnionGoodVo
     * @return
     */
    public List<ActivityUnionGoods> toList(ActivityUnionGoodVo activityUnionGoodVo,ActivityListVo activityListVo) {

        List<ActivityUnionGoods> list = new ArrayList<>();
            //?????????????????? ????????? ?????????ID?????????ID???
        activityUnionGoodVo.getGoods().forEach(t -> {
            ActivityUnionGoods activityUnionGoods = activityGoodsConvert.voToDo(activityListVo);
            //?????? productId
            activityUnionGoods.setProductId(t.getProductId());
            //?????? systemSku
            activityUnionGoods.setSystemSku(t.getSystemSku());
            //?????? info  json?????????
            activityUnionGoods.setInfo(t.getInfo());
            list.add(activityUnionGoods);
        });
        return list;
    }
}
