package marketing.service.impl;

import com.google.common.collect.Lists;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.database.enums.ResponseEnum;
import org.etocrm.database.exception.MyException;
import org.etocrm.database.util.QueryDslUtil;
import org.etocrm.marketing.convert.GiftInfoConvert;
import org.etocrm.marketing.entity.GiftInfo;
import org.etocrm.marketing.entity.QGiftInfo;
import org.etocrm.marketing.model.giftinfo.GiftInfoSelectVo;
import org.etocrm.marketing.model.giftinfo.GiftInfoVo;
import org.etocrm.marketing.repository.IGiftInfoRepository;
import org.etocrm.marketing.service.IGiftInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author xingxing.xie
 * @Date 2021/4/16 14:34
 */
@Service
@Slf4j
public class GiftInfoServiceImpl implements IGiftInfoService {
    @Autowired
    JPAQueryFactory queryFactory;
    @Autowired
    private GiftInfoConvert giftInfoConvert;
    @Autowired
    private IGiftInfoRepository repository;

    @Override
    public List<GiftInfoVo> getGiftInfoVo(GiftInfoSelectVo giftInfoSelectVo) {
        QGiftInfo qGiftInfo = QGiftInfo.giftInfo;
        BooleanBuilder booleanBuilder = new QueryDslUtil<>(GiftInfo.class, "giftInfo").where(giftInfoSelectVo);
        QueryResults<GiftInfo> activityListQueryResults = queryFactory.
                select(new QGiftInfo(qGiftInfo))
                .from(qGiftInfo)
                .where(booleanBuilder)
                .fetchResults();
        return activityListQueryResults.getResults().stream().map(s ->
                giftInfoConvert.doToVo(s)
        ).collect(Collectors.toList());
    }

    @Override
    public  List<GiftInfo> save(List<GiftInfoVo> giftInfoVo) throws MyException {
        try {
            List<GiftInfo> giftInfos = giftInfoConvert.voListToDoList(giftInfoVo);
            return repository.saveAll(giftInfos);
        } catch (Exception e) {
            throw new MyException(ResponseEnum.FAILD.getCode(), "赠品信息插入失败！");
        }
    }

    @Override
    public Boolean delete(List<GiftInfoVo> giftInfoVos) throws MyException {
        try {
            //批量逻辑 删除
            repository.logicDelete(giftInfoConvert.voListToDoList(giftInfoVos));
        } catch (Exception e) {
            throw new MyException(ResponseEnum.FAILD.getCode(), "满赠活动赠品删除失败!");
        }

        return true;
    }

    @Override
    public List<GiftInfoVo> getGiftInfoByRuleId(Long orgId,String activityId,String ruleId){
        List<String> ruleIds = new ArrayList<>();
        ruleIds.add(ruleId);
        GiftInfoSelectVo giftInfoSelectVo = new GiftInfoSelectVo()
                .setOrgId(orgId)
                .setActivityId(activityId)
                .setRuleId(ruleIds);
        List<GiftInfoVo> giftInfoVo = getGiftInfoVo(giftInfoSelectVo);
        if(CollectionUtils.isEmpty(giftInfoVo)){
            return Lists.newArrayList();
        }
        return giftInfoVo;
    }
}
