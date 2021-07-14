package marketing.service.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.database.util.QueryDslUtil;
import org.etocrm.marketing.convert.ActivityTypeDetailConvert;
import org.etocrm.marketing.entity.ActivityTypeDetail;
import org.etocrm.marketing.entity.QActivityTypeDetail;
import org.etocrm.marketing.model.activitytype.ActivityTypeDetailSelectVo;
import org.etocrm.marketing.model.activitytype.ActivityTypeDetailVo;
import org.etocrm.marketing.service.IActivityTypeDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author xingxing.xie
 * @Date 2021/4/15 15:24
 */
@Service
@Slf4j
public class ActivityTypeDetailServiceImpl implements IActivityTypeDetailService {

    @Autowired
    private ActivityTypeDetailConvert detailConvert;
    @Autowired
    JPAQueryFactory queryFactory;
    @Override
    public List<ActivityTypeDetailVo> getList(ActivityTypeDetailSelectVo activityTypeDetailSelectVo) {
        QActivityTypeDetail qActivityTypeDetail = QActivityTypeDetail.activityTypeDetail;
        BooleanBuilder booleanBuilder = new QueryDslUtil<>(ActivityTypeDetail.class, "activityTypeDetail").where(activityTypeDetailSelectVo);
        QueryResults<ActivityTypeDetail> activityTypeDetailQueryResults = queryFactory.select(new QActivityTypeDetail(qActivityTypeDetail))
                .from(qActivityTypeDetail)
                .where(booleanBuilder).fetchResults();
        List<ActivityTypeDetailVo> collect = activityTypeDetailQueryResults.getResults().stream().map(s ->
                detailConvert.doToVo(s)
        ).collect(Collectors.toList());
        return collect;
    }
}
