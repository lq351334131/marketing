package marketing.repository;

import org.etocrm.database.repository.BaseRepository;
import org.etocrm.marketing.entity.CustActivityDetail;
import org.springframework.stereotype.Repository;

/**
 * 用户参与活动明细表  Mapper 接口
 * @author xingxing.xie
 */
@Repository
public interface ICustActivityDetailRepository extends BaseRepository<CustActivityDetail,Long> {
}
