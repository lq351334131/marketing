package marketing.service;

import org.etocrm.database.exception.MyException;
import org.etocrm.marketing.model.activitydetail.CustActDetailSelectVo;
import org.etocrm.marketing.model.activitydetail.CustActivityDetailVo;

import java.util.List;

/**
 * @author xingxing.xie
 */
public interface ICustActivityDetailService {
    /**
     * 冻结
     */
    int STATUS_FREEZE = 1;
    /**
     * 解冻
     */
    int STATUS_UN_FREEZE = 2;
    /**
     * 核销
     */
    int STATUS_VERIFY = 3;

    /**
     *  更改 用户活动记录数据d
     *  一般 只更改record_status 字段
     * @param custActivityDetailVo
     * @return
     */
    CustActivityDetailVo update(CustActivityDetailVo custActivityDetailVo, Boolean verify) throws MyException;

    /**
     *  数据入库
     * @param custActivityDetailVo
     * @return
     * @throws MyException
     */
    CustActivityDetailVo save(CustActivityDetailVo custActivityDetailVo) throws MyException;

    /**
     *  逻辑删除数据
     * @param custActivityDetailVo
     * @return
     * @throws MyException
     */
    Boolean delete(CustActivityDetailVo custActivityDetailVo) throws MyException;

    /**
     * 更据条件 查询集合
     * @param custActDetailSelectVo
     * @return
     */
    List<CustActivityDetailVo> getList(CustActDetailSelectVo custActDetailSelectVo);

    /**
     * 查询当前用户 对应状态 记录数
     * @param vo
     * @param status
     * @return
     */
    List<CustActivityDetailVo> getListByCondition(CustActivityDetailVo vo, Integer status);

    /**
     *  冻结
     * @param custActivityDetailVo
     * @return
     */
    Boolean activityFreeze(CustActivityDetailVo custActivityDetailVo);
}
