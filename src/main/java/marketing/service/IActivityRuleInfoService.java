package marketing.service;

import org.etocrm.database.exception.MyException;
import org.etocrm.marketing.model.activityrule.ActivityRuleInfoSelectVo;
import org.etocrm.marketing.model.activityrule.ActivityRuleInfoVo;

import java.util.List;

/**
 * @Author xingxing.xie
 * @Date 2021/3/31 10:48
 */
public interface IActivityRuleInfoService {

    /**
     * 根据 活动id 查询 活动规则记录
     * @param ruleInfoSelectVo
     * @return
     */
    List<ActivityRuleInfoVo> getActivityRuleVoList(ActivityRuleInfoSelectVo ruleInfoSelectVo);


    /**
     * 删除 活动规则 数据
     * @param activityRuleInfoVos
     * @return
     * @throws MyException
     */
    Boolean delete(List<ActivityRuleInfoVo> activityRuleInfoVos) throws MyException;
}
