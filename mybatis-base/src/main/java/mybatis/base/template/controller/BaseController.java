package mybatis.base.template.controller;

import com.google.common.collect.Lists;
import mybatis.base.template.business.IBusiness;
import mybatis.base.template.controller.response.Result;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author guos
 * @date 2020/7/4 9:13
 **/
@CrossOrigin
public class BaseController<B extends IBusiness<T, Q, R>, T, Q, R> {

    @Autowired
    protected HttpServletRequest request;

    @Autowired
    protected B baseBusiness;


    /**
     * 根据id获取详情
     *
     * @param id
     * @return
     * @author guos
     * @date 2020/7/28 15:09
     **/
    @GetMapping("/{id}")
    public Result<R> get(@PathVariable("id") Integer id) {
        return Result.success(baseBusiness.get(id));
    }


    /**
     * 新增
     *
     * @param r
     * @return
     * @author guos
     * @date 2020/7/28 15:13
     **/
    @PostMapping
    public Result<Object> save(@RequestBody R r) {
        return baseBusiness.save(r) > 0 ? Result.success("添加成功") : Result.fail("添加失败");
    }


    /**
     * 更新
     *
     * @param r
     * @return
     * @author guos
     * @date 2020/7/28 15:15
     **/
    @PutMapping("/{id}")
    public Result<Object> update(@RequestBody R r) {
        return baseBusiness.update(r) > 0 ? Result.success("更新成功") : Result.fail("更新失败");
    }


    /**
     * 根据条件查询列表
     *
     * @param q q
     * @return
     * @author guos
     * @date 2020/7/28 15:17
     **/
    @GetMapping
    public Result<List<R>> list(Q q) {
        int count = baseBusiness.countByCondition(q);
        List<R> rList;
        if (count == 0) {
            rList = Lists.newArrayList();
        } else {
            rList = baseBusiness.listByCondition(q, this.getPageNum(), this.getPageSize());
        }
        return Result.success(rList, count);
    }


    protected int getPageNum() {
        return NumberUtils.toInt(this.request.getParameter("pageNum"), 1);
    }

    protected int getPageSize() {
        return NumberUtils.toInt(this.request.getParameter("pageSize"), 10);
    }


}
