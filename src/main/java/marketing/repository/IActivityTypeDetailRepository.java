package marketing.repository;

import org.etocrm.database.repository.BaseRepository;
import org.etocrm.marketing.entity.ActivityTypeDetail;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 活动类型 信息  Mapper 接口
 * </p>
 *
 * @author xingxing.xie
 * @since 2021-04-15
 */
@Repository
public interface IActivityTypeDetailRepository extends BaseRepository<ActivityTypeDetail, Long>{

}