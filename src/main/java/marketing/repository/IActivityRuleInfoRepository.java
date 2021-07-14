package marketing.repository;

import org.etocrm.database.repository.BaseRepository;
import org.etocrm.marketing.entity.ActivityRuleInfo;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 活动规则信息表  Mapper 接口
 * </p>
 *
 * @author admin
 * @since 2021-03-19
 */
@Repository
public interface IActivityRuleInfoRepository extends BaseRepository<ActivityRuleInfo, Long>{

}