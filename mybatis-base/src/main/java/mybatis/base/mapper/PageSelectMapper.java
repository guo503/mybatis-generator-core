package mybatis.base.mapper;


import mybatis.base.exception.ParamErrorException;
import mybatis.core.entity.Condition;
import mybatis.core.entity.LimitCondition;
import mybatis.core.page.PageInfo;

import java.util.Collections;

/**
 * 分页查询
 *
 * @author lgt
 * @date 2019/5/23 : 2:40 PM
 */
public interface PageSelectMapper<T> extends SelectMapper<T> {

    default PageInfo<T> pageByEntity(PageInfo<T> pageInfo, T t) {
        if (pageInfo.getPageSize() <= 0) {
            throw new ParamErrorException("每页数量不能少于0");
        }
        int count = countx(t);
        pageInfo.setCount(count);
        if (pageInfo.getPageNum() > pageInfo.getTotalPage()) {
            //当前页数，大于总页数
            pageInfo.setList(Collections.emptyList());
            return pageInfo;
        }
        int offset = (pageInfo.getPageNum() - 1) * pageInfo.getPageSize();
        pageInfo.setList(listLimitx(t, new LimitCondition(offset, pageInfo.getPageSize())));
        return pageInfo;
    }

    default PageInfo<T> pageByCondition(PageInfo<T> pageInfo, Condition<T> condition) {
        if (pageInfo.getPageSize() <= 0) {
            throw new ParamErrorException("每页数量不能少于0");
        }
        int count = countByConditionx(condition);
        pageInfo.setCount(count);
        if (pageInfo.getPageNum() > pageInfo.getTotalPage()) {
            //当前页数，大于总页数
            pageInfo.setList(Collections.emptyList());
            return pageInfo;
        }
        condition.limit(pageInfo.getPageNum(), pageInfo.getPageSize());
        pageInfo.setList(listByConditionx(condition));
        return pageInfo;
    }
}
