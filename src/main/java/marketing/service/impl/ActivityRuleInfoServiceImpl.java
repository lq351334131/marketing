package marketing.service.impl;

import com.google.common.collect.Lists;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.database.enums.ResponseEnum;
import org.etocrm.database.exception.MyException;
import org.etocrm.database.util.QueryDslUtil;
import org.etocrm.marketing.constant.ActivityTypeEnum;
import org.etocrm.marketing.convert.ActivityRuleInfoConvert;
import org.etocrm.marketing.entity.ActivityRuleInfo;
import org.etocrm.marketing.entity.QActivityRuleInfo;
import org.etocrm.marketing.model.activityrule.ActivityRuleInfoSelectVo;
import org.etocrm.marketing.model.activityrule.ActivityRuleInfoVo;
import org.etocrm.marketing.model.discount.activityrule.ConditionExplain;
import org.etocrm.marketing.model.discount.activityrule.RuleCondition;
import org.etocrm.marketing.model.giftinfo.GiftInfoVo;
import org.etocrm.marketing.repository.IActivityRuleInfoRepository;
import org.etocrm.marketing.service.IActivityRuleInfoService;
import org.etocrm.marketing.service.IGiftInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author xingxing.xie
 * @Date 2021/3/31 10:49
 */
@Service
@Slf4j
public class ActivityRuleInfoServiceImpl implements IActivityRuleInfoService {
    @Autowired
    private IActivityRuleInfoRepository ruleInfoRepository;
    @Autowired
    private ActivityRuleInfoConvert ruleInfoConvert;
    @Autowired
    private IGiftInfoService giftInfoService;
    @Autowired
    JPAQueryFactory queryFactory;

    @Override
    public List<ActivityRuleInfoVo> getActivityRuleVoList(ActivityRuleInfoSelectVo ruleInfoSelectVo) {
        QActivityRuleInfo qActivityRuleInfo = QActivityRuleInfo.activityRuleInfo;
        BooleanBuilder booleanBuilder = new QueryDslUtil<>(ActivityRuleInfo.class, "activityRuleInfo").where(ruleInfoSelectVo);
        QueryResults<ActivityRuleInfo> activityListQueryResults = queryFactory.
                select(new QActivityRuleInfo(qActivityRuleInfo))
                .from(qActivityRuleInfo)
                .where(booleanBuilder)
                .fetchResults();
        return activityListQueryResults.getResults().stream().map(s -> {
            ArrayList<RuleCondition> conditions = new ArrayList<>();
            ArrayList<ConditionExplain> explains = new ArrayList<>();
            ActivityRuleInfoVo activityRuleInfoVo = ruleInfoConvert.doToVo(s);
            if(ActivityTypeEnum.FULL_GIVEN.getValue().equals(s.getRuleType())){
                //满赠
                conditions.add(ruleInfoConvert.doToCondition(s));
                //可选赠品、固定赠品
                convertToVo(activityRuleInfoVo);

            }else {
                conditions.add(ruleInfoConvert.doToCondition(s));
                explains.add(ruleInfoConvert.doToConditionExplain(s));
                activityRuleInfoVo.setConditionExplain(explains);
            }
            activityRuleInfoVo.setCondition(conditions);
            return activityRuleInfoVo;
        }).collect(Collectors.toList());

    }


    @Override
    public Boolean delete(List<ActivityRuleInfoVo>  activityRuleInfoVos) throws MyException {
        try {
            List<ActivityRuleInfo> ruleInfoList = ruleInfoConvert.voListToDoList(activityRuleInfoVos);
            ruleInfoRepository.logicDelete(ruleInfoList);
        } catch (Exception e) {
            throw new MyException(ResponseEnum.FAILD.getCode(), "活动规则删除失败！");
        }

        return true;
    }

    /**
     * 满赠活动 赠品 规则查询时   填充赠品信息值
     * @param ruleInfoVo
     */
    public void convertToVo(ActivityRuleInfoVo ruleInfoVo) {

        List<GiftInfoVo> giftInfoByRuleId = giftInfoService.getGiftInfoByRuleId(ruleInfoVo.getOrgId(), ruleInfoVo.getActivityId(), ruleInfoVo.getRuleId());
        Map<Integer, List<GiftInfoVo>> map = giftInfoByRuleId.stream().collect(Collectors.groupingBy(GiftInfoVo::getGiftType));
        ConditionExplain conditionExplain = new ConditionExplain();
        ConditionExplain conditionExplain2 = new ConditionExplain();
        List<GiftInfoVo> fixGiftInfoVos = map.get(IGiftInfoService.GIFT_TYPE_FIXED);
        //空值处理
        conditionExplain2.setGifts(CollectionUtils.isEmpty(fixGiftInfoVos)? Lists.newArrayList():fixGiftInfoVos);
        List<GiftInfoVo> selectedGiftInfoVos = map.get(IGiftInfoService.GIFT_TYPE_SELECTED);
        //空值处理
        conditionExplain.setGifts(CollectionUtils.isEmpty(selectedGiftInfoVos)? Lists.newArrayList():selectedGiftInfoVos);
        //固定赠品
        ArrayList<ConditionExplain> explains = new ArrayList<>();
        explains.add(conditionExplain);
        ArrayList<ConditionExplain> explains2 = new ArrayList<>();
        explains2.add(conditionExplain2);

        ruleInfoVo.setConditionExplain(explains);
        ruleInfoVo.setConditionExplain2(explains2);
    }

}
