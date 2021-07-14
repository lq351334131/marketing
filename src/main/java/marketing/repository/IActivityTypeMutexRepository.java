package marketing.repository;

import org.etocrm.database.repository.BaseRepository;
import org.etocrm.marketing.entity.ActivityTypeMutex;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 活动类型互斥列表  Mapper 接口
 * </p>
 *
 * @author admin
 * @since 2021-03-19
 */
@Repository
public interface IActivityTypeMutexRepository extends BaseRepository<ActivityTypeMutex, Long>{

}