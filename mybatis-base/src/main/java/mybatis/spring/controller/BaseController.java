package mybatis.spring.controller;

import mybatis.core.page.PageInfo;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;

/**
 * @author guos
 * @date 2020/7/4 9:13
 **/
public class BaseController {

    @Autowired
    protected HttpServletRequest request;


    public PageInfo<?> initPage() {
        int pageNum = NumberUtils.toInt(this.request.getParameter("pageNum"), 1);
        int pageSize = NumberUtils.toInt(this.request.getParameter("pageSize"), 10);
        PageInfo<?> page = new PageInfo<>();
        page.setPageNum(pageNum);
        page.setPageSize(pageSize);
        return page;
    }


}
