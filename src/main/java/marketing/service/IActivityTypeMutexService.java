package marketing.service;

import org.etocrm.database.exception.MyException;
import org.etocrm.marketing.model.activitymutex.ActivityMutexData;
import org.etocrm.marketing.model.activitymutex.ActivityTypeMutexSelectVo;
import org.etocrm.marketing.model.activitymutex.ActivityTypeMutexVo;

import java.util.List;

/**
 * @author xingxing.xie
 * @version 1.0
 * @date 2021/3/23 14:58
 */
public interface IActivityTypeMutexService {

    /**
     * 活动规则数据同步
     * @param activityMutexData
     * @return
     * @throws MyException
     */
    ActivityTypeMutexVo save(ActivityMutexData activityMutexData) throws MyException;

    /**
     * 更改 活动列表数据
     * @param activityTypeMutexVo
     * @return
     * @throws MyException
     */
    ActivityTypeMutexVo update(ActivityTypeMutexVo activityTypeMutexVo) throws MyException;

    /**
     * 删除 活动列表数据
     * @param activityTypeMutexVo
     * @return
     * @throws MyException
     */
    Boolean delete(ActivityTypeMutexVo activityTypeMutexVo) throws MyException;

    /**
     * 查询活动 叠加列表
     * @param activityTypeMutexSelectVo
     * @return
     * @throws MyException
     */
    List<ActivityTypeMutexVo> getList(ActivityTypeMutexSelectVo activityTypeMutexSelectVo);


}