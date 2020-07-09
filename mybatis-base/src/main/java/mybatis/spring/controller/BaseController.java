package mybatis.spring.controller;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;

import javax.servlet.http.HttpServletRequest;

/**
 * @author guos
 * @date 2020/7/4 9:13
 **/
@CrossOrigin
public class BaseController {

    @Autowired
    protected HttpServletRequest request;


    protected int getPageNum() {
        return NumberUtils.toInt(this.request.getParameter("pageNum"), 1);
    }

    protected int getPageSize() {
        return NumberUtils.toInt(this.request.getParameter("pageSize"), 10);
    }


}
