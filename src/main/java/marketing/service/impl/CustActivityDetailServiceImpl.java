package marketing.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.database.enums.ResponseEnum;
import org.etocrm.database.exception.MyException;
import org.etocrm.database.util.QueryDslUtil;
import org.etocrm.marketing.convert.CustActDetailConvert;
import org.etocrm.marketing.entity.CustActivityDetail;
import org.etocrm.marketing.entity.QCustActivityDetail;
import org.etocrm.marketing.model.activitydetail.CustActDetailSelectVo;
import org.etocrm.marketing.model.activitydetail.CustActivityDetailVo;
import org.etocrm.marketing.repository.ICustActivityDetailRepository;
import org.etocrm.marketing.service.ICustActivityDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author xingxing.xie
 * @Date 2021/3/29 10:43
 */
@Slf4j
@Service
public class CustActivityDetailServiceImpl implements ICustActivityDetailService {
    @Autowired
    private ICustActivityDetailRepository activityDetailRepository;
    @Autowired
    private CustActDetailConvert custActDetailConvert;
    @Autowired
    JPAQueryFactory queryFactory;

    @Override
    public CustActivityDetailVo save(CustActivityDetailVo custActivityDetailVo) throws MyException {
        CustActivityDetail custActivityDetail = custActDetailConvert.voToDo(custActivityDetailVo);
        CustActivityDetail save = activityDetailRepository.save(custActivityDetail);
        return custActDetailConvert.doToVo(save);
    }
    @Override
    public CustActivityDetailVo update(CustActivityDetailVo custActivityDetailVo,Boolean verify) throws MyException {
        log.warn("会员活动记录核销或解冻入参：{}", JSON.toJSONString(custActivityDetailVo));
        //查询冻结 的数据
        List<CustActivityDetailVo> listByCondition;
        if(verify){
            //如果是 核销  查询冻结的数据
             listByCondition = getListByCondition(custActivityDetailVo, STATUS_FREEZE);
        }else {
            //如果是解冻
             listByCondition = getListByCondition(custActivityDetailVo, null);
        }
        if(CollectionUtils.isEmpty(listByCondition)){
            throw new MyException(ResponseEnum.INCORRECT_PARAMS.getCode(), "会员活动记录查询为null，请检查入参");
        }
        CustActivityDetailVo vo = listByCondition.get(0);
        CustActivityDetail custActivityDetail = custActDetailConvert.voToDo(vo);
        //设置record_status 状态值  享受成功则 核销失败则解冻
        custActivityDetail.setRecordStatus(verify?STATUS_VERIFY:STATUS_UN_FREEZE);
        CustActivityDetail update = activityDetailRepository.update(custActivityDetail);
        return custActDetailConvert.doToVo(update);
    }

    @Override
    public Boolean delete(CustActivityDetailVo custActivityDetailVo) throws MyException {
        try {
            CustActivityDetail custActivityDetail = new CustActivityDetail();
            BeanUtil.copyProperties(custActivityDetailVo,custActivityDetail);
            activityDetailRepository.logicDelete(custActivityDetail);
        } catch (Exception e) {
            throw new MyException(ResponseEnum.FAILD, e);
        }
        return true;
    }

    @Override
    public List<CustActivityDetailVo> getList(CustActDetailSelectVo custActDetailSelectVo) {
        QCustActivityDetail qCustActivityDetail = QCustActivityDetail.custActivityDetail;
        BooleanBuilder booleanBuilder = new QueryDslUtil<>(CustActivityDetail.class, "custActivityDetail").where(custActDetailSelectVo);
        QueryResults<CustActivityDetail> queryResults = queryFactory.select(new QCustActivityDetail(qCustActivityDetail))
                .from(qCustActivityDetail)
                .where(booleanBuilder)
                .fetchResults();
        List<CustActivityDetailVo> collect = queryResults.getResults().stream().map(s ->
            custActDetailConvert.doToVo(s)
        ).collect(Collectors.toList());
        return collect;
    }

    /**
     *  查询当前用户 记录数
     * @param vo
     * @return
     */
    @Override
    public List<CustActivityDetailVo> getListByCondition(CustActivityDetailVo vo,Integer status) {
        CustActDetailSelectVo selectVo = new CustActDetailSelectVo()
                .setActivityId(vo.getActivityId())
                .setOrgId(vo.getOrgId())
                .setShopId(vo.getShopId())
                .setCustomerId(vo.getCustomerId());
        if(null!=status){
            selectVo.setRecordStatus(status);
        }
        List<CustActivityDetailVo> collect = getList(selectVo);
        if (CollectionUtils.isEmpty(collect)){
            return Lists.newArrayList();
        }
        return collect;
    }

    @Override
    public Boolean activityFreeze(CustActivityDetailVo custActivityDetailVo) {
        try {
            log.warn("会员活动记录冻结入参：{}", JSON.toJSONString(custActivityDetailVo));
            //查询此次用户 冻结状态数据
            List<CustActivityDetailVo> totalList = getListByCondition(custActivityDetailVo, ICustActivityDetailService.STATUS_FREEZE);
            //幂等
            if (CollectionUtils.isEmpty(totalList)) {
                //如果满足 优惠规则，则 预占库存
                //冻结状态
                custActivityDetailVo.setRecordStatus(ICustActivityDetailService.STATUS_FREEZE);
                save(custActivityDetailVo);
            }
            return true;
        } catch (MyException e) {
            return false;
        }

    }



}
