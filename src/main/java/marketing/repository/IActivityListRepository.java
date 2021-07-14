package marketing.repository;

import org.etocrm.database.repository.BaseRepository;
import org.etocrm.marketing.entity.ActivityList;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 活动规则列表  Mapper 接口
 * </p>
 *
 * @author admin
 * @since 2021-03-19
 */
@Repository
public interface IActivityListRepository extends BaseRepository<ActivityList, Long>{

    List<ActivityList> findByOrgIdAndShopIdAndBrandIdAndActivityType(Long orgId, String sid, Long brandId, int i);

}