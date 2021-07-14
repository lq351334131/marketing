package marketing.convert;

import org.etocrm.marketing.entity.ActivityRuleInfo;
import org.etocrm.marketing.model.activityrule.ActivityRuleInfoVo;
import org.etocrm.marketing.model.discount.activityrule.ConditionExplain;
import org.etocrm.marketing.model.discount.activityrule.RuleCondition;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * @Author xingxing.xie
 * @Date 2021/3/31 11:36
 */
@Mapper(componentModel = "spring")
public interface ActivityRuleInfoConvert {

    /**
     * 转换对应的vo类
     * @param activityRuleInfo
     * @return
     */
    ActivityRuleInfoVo doToVo(ActivityRuleInfo activityRuleInfo);

    /**
     * 转换对应的DO类
     * @param activityListVo
     * @return
     */
    ActivityRuleInfo voToDo(ActivityRuleInfoVo activityListVo);
    /**
     * 转换对应的DO类
     * @param activityRuleInfo
     * @return
     */
    RuleCondition doToCondition(ActivityRuleInfo activityRuleInfo);
    /**
     * 转换对应的DO类
     * @param activityRuleInfo
     * @return
     */
    ConditionExplain doToConditionExplain(ActivityRuleInfo activityRuleInfo);

    /**
     * 将 规则 和规则说明 封装进do 类
     * @param activityRuleInfo
     * @param ruleCondition
     * @param conditionExplain
     */
    void boxRuleDo(@MappingTarget ActivityRuleInfo activityRuleInfo, RuleCondition ruleCondition, ConditionExplain conditionExplain);


    /**
     * 转换对应的 vo list类
     * @param activityRuleInfoVos vo list类
     * @return
     */
    List<ActivityRuleInfo> voListToDoList(List<ActivityRuleInfoVo> activityRuleInfoVos);



    /**
     *  满赠 规则封装 （RuleCondition 部分）
     * @param activityRuleInfo
     * @param ruleCondition
     */
    void boxByRuleCondition(@MappingTarget ActivityRuleInfo activityRuleInfo, RuleCondition ruleCondition);

    /**
     *  满赠 规则封装 （固定赠品部分）
     * @param activityRuleInfo
     * @param fixed
     */
    void boxByFixGift(@MappingTarget ActivityRuleInfo activityRuleInfo, ConditionExplain fixed);


    /**
     * 满赠 规则封装（可选赠品部分）
     * @param activityRuleInfo
     * @param selectable
     */
    void boxBySelectable(@MappingTarget ActivityRuleInfo activityRuleInfo, ConditionExplain selectable);


}
