package marketing.service;

import org.etocrm.database.exception.MyException;
import org.etocrm.marketing.model.activitytype.ActivityTypeDetailSelectVo;
import org.etocrm.marketing.model.activitytype.ActivityTypeDetailVo;

import java.util.List;

/**
 * @author xingxing.xie
 * @version 1.0
 * @date 2021/3/23 14:58
 */
public interface IActivityTypeDetailService {


    /**
     * 查询 所有活动类型信息
     * @param activityTypeDetailSelectVo
     * @return
     * @throws MyException
     */
    List<ActivityTypeDetailVo> getList(ActivityTypeDetailSelectVo activityTypeDetailSelectVo);


}