package marketing.repository;

import org.etocrm.database.repository.BaseRepository;
import org.etocrm.marketing.entity.ActivityPrioritySort;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 活动优先级顺序表  Mapper 接口
 * </p>
 *
 * @author admin
 * @since 2021-03-19
 */
@Repository
public interface IActivityPrioritySortRepository extends BaseRepository<ActivityPrioritySort, Long>{

}