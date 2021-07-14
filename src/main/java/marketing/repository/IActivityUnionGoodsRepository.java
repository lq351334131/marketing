package marketing.repository;

import org.etocrm.database.repository.BaseRepository;
import org.etocrm.marketing.entity.ActivityUnionGoods;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 活动与商品关联表  Mapper 接口
 * </p>
 *
 * @author admin
 * @since 2021-03-19
 */
@Repository
public interface IActivityUnionGoodsRepository extends BaseRepository<ActivityUnionGoods, Long>{

}