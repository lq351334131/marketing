package marketing.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Lists;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.database.enums.ResponseEnum;
import org.etocrm.database.exception.MyException;
import org.etocrm.database.util.BasePage;
import org.etocrm.database.util.QueryDslUtil;
import org.etocrm.marketing.convert.DictListConvert;
import org.etocrm.marketing.entity.DictList;
import org.etocrm.marketing.entity.QDictList;
import org.etocrm.marketing.model.dict.DictListPageVo;
import org.etocrm.marketing.model.dict.DictListSelectVo;
import org.etocrm.marketing.model.dict.DictListVo;
import org.etocrm.marketing.repository.IDictListRepository;
import org.etocrm.marketing.service.IDictListService;
import org.hibernate.StaleObjectStateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author chengrong.yang
 * @Date 2021-03-04 10:57:03
 */
@Service
@Slf4j
public class DictListServiceImpl implements IDictListService {

    @Autowired
    IDictListRepository dictListRepository;
    @Autowired
    private DictListConvert dictListConvert;

    @Autowired
    JPAQueryFactory queryFactory;

    @Override
    public BasePage<DictListVo> getDictListByPage(DictListPageVo dictListPageVo) {
        QDictList qDictList = QDictList.dictList;
        BooleanBuilder booleanBuilder = new QueryDslUtil<>(DictList.class, "dictList").where(dictListPageVo);
        QueryResults<DictList> dictListQueryResults = queryFactory.select(new QDictList(qDictList))
                .from(qDictList)
                .where(booleanBuilder)
                .orderBy(qDictList.id.desc())
                .offset(dictListPageVo.getOffset())
                .limit(dictListPageVo.getLimit())
                .fetchResults();
        List<DictListVo> collect = dictListQueryResults.getResults().stream().map(s -> {
            DictListVo sVo = new DictListVo();
            BeanUtil.copyProperties(s, sVo);
            return sVo;
        }).collect(Collectors.toList());
        return new BasePage<>(collect, dictListQueryResults.getOffset(), dictListQueryResults.getTotal(), dictListQueryResults.getTotal() / dictListQueryResults.getLimit());
    }

    @Override
    public List<DictListVo> getDictList(DictListSelectVo dictListSelectVo) {
        QDictList qDictList = QDictList.dictList;
        BooleanBuilder booleanBuilder = new QueryDslUtil<>(DictList.class, "dictList").where(dictListSelectVo);
        return queryFactory.select(new QDictList(qDictList))
                .from(qDictList)
                .where(booleanBuilder)
                .orderBy(qDictList.id.desc())
                .fetch()
                .stream()
                .map(s ->
                    dictListConvert.doToVo(s)).collect(Collectors.toList());
    }

    @Override
    public DictListVo save(DictListVo dictListVo) throws MyException {
        try {
            DictList save = dictListRepository.save(dictListConvert.voToDo(dictListVo));
            dictListVo = dictListConvert.doToVo(save);
        } catch (ObjectOptimisticLockingFailureException e) {
            if (e.getCause() instanceof StaleObjectStateException) {
                throw new MyException(ResponseEnum.RECORD_ALREADY_UPDATE, e.getCause());
            }
        }
        return dictListVo;
    }

    @Override
    public DictListVo update(DictListVo dictListVo) throws MyException {
        DictList dictList = new DictList();
        BeanUtil.copyProperties(dictListVo, dictList);
        DictList update = dictListRepository.update(dictList);
        BeanUtil.copyProperties(update, dictListVo);
        return dictListVo;
    }

    @Override
    public Boolean delete(DictListVo dictListVo) throws MyException {
        try {
            DictList dictList = new DictList();
            BeanUtil.copyProperties(dictListVo, dictList);
            dictListRepository.logicDelete(dictList);
        } catch (Exception e) {
            throw new MyException(ResponseEnum.FAILD, e);
        }
        return true;
    }

    @Override
    public List<DictListVo> getSpecialCouponCode(Long orgId,String shopId) {
        DictListSelectVo dictListSelectVo = new DictListSelectVo()
                .setOrgId(orgId)
                .setStoreId(shopId);
        List<DictListVo> dictList = getDictList(dictListSelectVo);
        if(CollectionUtil.isEmpty(dictList)){
            return Lists.newArrayList();
        }
        return dictList;
    }
}