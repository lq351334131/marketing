package marketing.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Lists;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.database.enums.ResponseEnum;
import org.etocrm.database.exception.MyException;
import org.etocrm.database.util.QueryDslUtil;
import org.etocrm.marketing.convert.OrgParamsConvert;
import org.etocrm.marketing.entity.OrgParams;
import org.etocrm.marketing.entity.QOrgParams;
import org.etocrm.marketing.model.orgparams.OrgParamsSelectVo;
import org.etocrm.marketing.model.orgparams.OrgParamsVo;
import org.etocrm.marketing.repository.IOrgParamsRepository;
import org.etocrm.marketing.service.IOrgParamsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author xingxing.xie
 * @Date 2021/5/31 14:21
 */
@Service
@Slf4j
public class OrgParamsServiceImpl implements IOrgParamsService {

    @Autowired
    private IOrgParamsRepository repository;
    @Autowired
    private OrgParamsConvert convert;
    @Autowired
    JPAQueryFactory queryFactory;

    @Override
    public List<OrgParamsVo> getOrgParamList(OrgParamsSelectVo orgParamsSelectVo) {
        QOrgParams qOrgParams = QOrgParams.orgParams;
        BooleanBuilder booleanBuilder = new QueryDslUtil<>(OrgParams.class, "orgParams").where(orgParamsSelectVo);
        List<OrgParams> collect = queryFactory.select(new QOrgParams(qOrgParams))
                .from(qOrgParams)
                .where(booleanBuilder)
                .fetchResults()
                .getResults();
        if(CollectionUtil.isEmpty(collect)){
            return Lists.newArrayList();
        }
        return collect.stream().map(t->convert.doToVo(t)).collect(Collectors.toList());
    }

    @Override
    public OrgParamsVo save(OrgParamsVo orgParamsVo) throws MyException {

        List<OrgParams> byOrgId = repository.findByOrgId(orgParamsVo.getOrgId());
        if(CollectionUtil.isNotEmpty(byOrgId)){
            //如果有，则先删除  之前的数据
            repository.logicDelete(byOrgId);
        }
        try {
            OrgParams save = repository.save(convert.voToDo(orgParamsVo));
            return convert.doToVo(save);
        } catch (Exception e) {
            throw new MyException(ResponseEnum.FAILD.getCode(),"机构参数配置数据同步失败！");
        }

    }

    @Override
    public OrgParamsVo update(OrgParamsVo orgParamsVo) throws MyException {

        try {
            OrgParams save = repository.update(convert.voToDo(orgParamsVo));
            return convert.doToVo(save);
        } catch (Exception e) {
            throw new MyException(ResponseEnum.FAILD.getCode(),"参数配置数据，更新失败！");
        }
    }

    @Override
    public Boolean delete(List<OrgParamsVo> orgParamsVos) throws MyException {
        try {
            List<OrgParams> collect = orgParamsVos.stream().map(t->convert.voToDo(t)).collect(Collectors.toList());
            repository.logicDelete(collect);
            return true;
        } catch (Exception e) {
            throw new MyException(ResponseEnum.FAILD.getCode(),"参数配置数据，删除失败！");
        }
    }


    @Override
    public List<OrgParamsVo> getSpecialCouponCode(Long orgId) {
        OrgParamsSelectVo orgParamsSelectVo = new OrgParamsSelectVo()
                .setOrgId(orgId);
        List<OrgParamsVo> orgParamList = getOrgParamList(orgParamsSelectVo);
        if(CollectionUtil.isEmpty(orgParamList)){
            return Lists.newArrayList();
        }
        return orgParamList;
    }

}
