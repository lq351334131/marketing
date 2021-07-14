package marketing.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.database.enums.ResponseEnum;
import org.etocrm.database.exception.MyException;
import org.etocrm.database.util.*;
import org.etocrm.marketing.entity.DictInfo;
import org.etocrm.marketing.entity.DictList;
import org.etocrm.marketing.entity.QDictInfo;
import org.etocrm.marketing.model.dict.DictInfoPageVo;
import org.etocrm.marketing.model.dict.DictInfoSelectVo;
import org.etocrm.marketing.model.dict.DictInfoVo;
import org.etocrm.marketing.repository.IDictInfoRepository;
import org.etocrm.marketing.service.IDictInfoService;
import org.hibernate.StaleObjectStateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author chengrong.yang
 * @Date 2021-03-04 10:55:45
 */
@Service
@Slf4j
public class DictInfoServiceImpl implements IDictInfoService {

    @Autowired
    IDictInfoRepository dictInfoRepository;

    @Autowired
    JPAQueryFactory queryFactory;

    @Override
    public BasePage<DictInfoVo> getDictInfoByPage(DictInfoPageVo dictInfoPageVo) {
        QDictInfo qDictInfo = QDictInfo.dictInfo;
        BooleanBuilder booleanBuilder = new QueryDslUtil<>(DictList.class,"dictInfo").where(dictInfoPageVo);
        QueryResults<DictInfo> dictInfoQueryResults = queryFactory.select(new QDictInfo(qDictInfo))
                .from(qDictInfo)
                .where(booleanBuilder)
                .orderBy(qDictInfo.id.desc())
                .offset(dictInfoPageVo.getOffset())
                .limit(dictInfoPageVo.getLimit())
                .fetchResults();
        List<DictInfoVo> collect = dictInfoQueryResults.getResults().stream().map(s -> {
            DictInfoVo sVo = new DictInfoVo();
            BeanUtil.copyProperties(s, sVo);
            return sVo;
        }).collect(Collectors.toList());
        return new BasePage<>(collect, dictInfoQueryResults.getOffset(), dictInfoQueryResults.getTotal(), dictInfoQueryResults.getTotal() / dictInfoQueryResults.getLimit());
    }

    @Override
    public List<DictInfoVo> getDictInfo(DictInfoSelectVo dictInfoSelectVo) {
        QDictInfo qDictInfo = QDictInfo.dictInfo;
        BooleanBuilder booleanBuilder = new QueryDslUtil<>(DictList.class,"dictInfo").where(dictInfoSelectVo);
        return queryFactory.select(new QDictInfo(qDictInfo))
                .from(qDictInfo)
                .where(booleanBuilder)
                .orderBy(qDictInfo.id.desc())
                .fetch()
                .stream()
                .map(s->{
                    DictInfoVo sVo = new DictInfoVo();
                    BeanUtil.copyProperties(s, sVo);
                    return sVo;
                }).collect(Collectors.toList());
    }

    @Override
    public DictInfoVo save(DictInfoVo dictInfoVo) throws MyException {
        try {
            DictInfo dictInfo = new DictInfo();
            BeanUtil.copyProperties(dictInfoVo, dictInfo);
            DictInfo save = dictInfoRepository.save(dictInfo);
            BeanUtil.copyProperties(save, dictInfoVo);
        } catch (ObjectOptimisticLockingFailureException e) {
            if (e.getCause() instanceof StaleObjectStateException) {
                throw new MyException(ResponseEnum.RECORD_ALREADY_UPDATE, e.getCause());
            }
        }
        return dictInfoVo;
    }

    @Override
    public DictInfoVo update(DictInfoVo dictInfoVo) throws MyException {
        DictInfo dictInfo = new DictInfo();
        BeanUtil.copyProperties(dictInfoVo, dictInfo);
        DictInfo update = dictInfoRepository.update(dictInfo);
        BeanUtil.copyProperties(update, dictInfoVo);
        return dictInfoVo;
    }

    @Override
    public Boolean delete(DictInfoVo dictInfoVo) throws MyException {
        try {
            DictInfo dictInfo = new DictInfo();
            BeanUtil.copyProperties(dictInfoVo, dictInfo);
            dictInfoRepository.logicDelete(dictInfo);
        } catch (Exception e) {
            throw new MyException(ResponseEnum.FAILD, e);
        }
        return true;
    }

}