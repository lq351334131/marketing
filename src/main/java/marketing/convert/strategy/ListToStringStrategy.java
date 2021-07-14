package marketing.convert.strategy;

import com.google.common.collect.Lists;
import org.etocrm.marketing.constant.DiscountConstant;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * @Author xingxing.xie
 * @Date 2021/3/31 13:33
 */
@Component
public class ListToStringStrategy {

    /**
     *  将 list 转换成  不带 [] 的字符串
     *  eg："[1,2,3]"==>"1,2,3" 存库     方便后续取出时 转 list
     * @param list
     * @return
     */
    public String listToString(List<String> list) {
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        String s = list.toString();
        int endIndex = s.lastIndexOf("]");
        return s.substring(1, endIndex);

    }

    /**
     * eg："1,2,3"==>"[1,2,3]" 取出时 转 list
     * @param s
     * @return
     */
    public List<String> stringToList(String s) {
        if(StringUtils.isEmpty(s)){
            return Lists.newArrayList();
        }
        //转换为 java.util.ArrayList() 不要适用Arrays 下的内部类ArrayList
        String[] split = s.split(DiscountConstant.SPLIT_REGEX);
        List<String> result=Lists.newArrayList();
        result.addAll(Arrays.asList(split));
        return result;

    }


}
