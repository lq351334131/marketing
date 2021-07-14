package marketing.service;

import org.etocrm.database.exception.MyException;
import org.etocrm.database.util.BasePage;
import org.etocrm.marketing.model.dict.DictInfoPageVo;
import org.etocrm.marketing.model.dict.DictInfoSelectVo;
import org.etocrm.marketing.model.dict.DictInfoVo;

import java.util.List;

/**
 * @Author chengrong.yang
 * @Date 2021-03-04 10:55:43
 */
public interface IDictInfoService {

    BasePage<DictInfoVo> getDictInfoByPage(DictInfoPageVo dictInfoPageVo);

    List<DictInfoVo> getDictInfo(DictInfoSelectVo dictInfoSelectVo);

    DictInfoVo save(DictInfoVo dictInfoVo) throws MyException;

    DictInfoVo update(DictInfoVo dictInfoVo) throws MyException;

    Boolean delete(DictInfoVo dictInfoVo) throws MyException;
}