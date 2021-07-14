package marketing.repository;

import org.etocrm.database.repository.BaseRepository;
import org.etocrm.marketing.entity.ActivityPromotionCode;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 活动优惠码关联表  Mapper 接口
 * </p>
 *
 * @author admin
 * @since 2021-03-19
 */
@Repository
public interface IActivityPromotionRepository extends BaseRepository<ActivityPromotionCode, Long>{

}