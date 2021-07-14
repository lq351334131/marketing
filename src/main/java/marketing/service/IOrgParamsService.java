package marketing.service;

import org.etocrm.database.exception.MyException;
import org.etocrm.marketing.model.orgparams.OrgParamsSelectVo;
import org.etocrm.marketing.model.orgparams.OrgParamsVo;

import java.util.List;

/**
 * @description: 机构参数配置业务类
 * @author xingxing.xie
 * @date 2021/5/31 14:10
 * @version 1.0
 */
public interface IOrgParamsService {

    /**
     * 机构参数配置 查询
     * @param orgParamsSelectVo
     * @return
     */
    List<OrgParamsVo> getOrgParamList(OrgParamsSelectVo orgParamsSelectVo);

    /**
     * 机构配置参数 存储
     * @param orgParamsVo
     * @return
     * @throws MyException
     */
    OrgParamsVo save(OrgParamsVo orgParamsVo) throws MyException;

    /**
     * 更改 机构配置参数信息
     * @param  orgParamsVo
     * @return 更新后的  实体
     * @throws MyException
     */
    OrgParamsVo update(OrgParamsVo orgParamsVo) throws MyException;

    /**
     * 删除 机构配置参数信息
     * @param orgParamsVos
     * @return
     * @throws MyException
     */
    Boolean delete(List<OrgParamsVo> orgParamsVos) throws MyException;

    /**
     * 查询 机构下 特殊的优惠码储值卡 活动
     * @param orgId 机构id
     * @return
     */
    List<OrgParamsVo> getSpecialCouponCode(Long orgId);



}