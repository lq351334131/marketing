package marketing.repository;

import org.etocrm.database.repository.BaseRepository;
import org.etocrm.marketing.entity.OrgParams;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author xingxing.xie
 * @Date 2021/5/31 14:22
 */
@Repository
public interface IOrgParamsRepository extends BaseRepository<OrgParams,Long> {
    /**
     *  根据机构id  查询 数据集合
     * @param orgId
     * @return
     */
    List<OrgParams> findByOrgId(Long orgId);
}
