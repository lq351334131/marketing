package marketing.service.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.database.exception.MyException;
import org.etocrm.database.util.QueryDslUtil;
import org.etocrm.marketing.convert.ActivityPromotionConvert;
import org.etocrm.marketing.entity.ActivityPromotionCode;
import org.etocrm.marketing.entity.QActivityPromotionCode;
import org.etocrm.marketing.model.promotioncode.ActivityPromotionCodeSelectVo;
import org.etocrm.marketing.model.promotioncode.ActivityPromotionCodeVo;
import org.etocrm.marketing.service.IActivityPromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author xingxing.xie
 * @Date 2021/4/15 21:01
 */
@Service
@Slf4j
public class ActivityPromotionServiceImpl implements IActivityPromotionService {
    @Autowired
    JPAQueryFactory queryFactory;
    @Autowired
    private ActivityPromotionConvert promotionConvert;

    @Override
    public List<ActivityPromotionCodeVo> getList(ActivityPromotionCodeSelectVo promotionCodeSelectVo) {
        QActivityPromotionCode promotionCode = QActivityPromotionCode.activityPromotionCode;
        BooleanBuilder booleanBuilder = new QueryDslUtil<>(ActivityPromotionCode.class, "activityPromotionCode")
                .where(promotionCodeSelectVo);
        QueryResults<ActivityPromotionCode> results = queryFactory.select(new QActivityPromotionCode(promotionCode))
                .from(promotionCode)
                .where(booleanBuilder)
                .fetchResults();
        return results.getResults().stream().map(t->
            promotionConvert.doToVo(t)).collect(Collectors.toList());
    }

    @Override
    public ActivityPromotionCodeVo save(ActivityPromotionCodeVo promotionCodeVo) throws MyException {
        return null;
    }

    @Override
    public Boolean delete(List<ActivityPromotionCodeVo> promotionCodeVos) throws MyException {
        return null;
    }
}
