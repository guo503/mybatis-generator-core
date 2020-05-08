package mybatis.core.page;

import java.util.List;

public class PageInfo<T> {

    private static final int DEFAULT_PAGE_SIZE = 10;

    private static final int MAX_PAGE_SIZE = 2000;

    /**
     * 实体类
     */
    private List<T> list;
    /**
     * 每页数量
     */
    private int pageSize = DEFAULT_PAGE_SIZE;

    /**
     * 当前页码 ,从 1 开始
     */
    private int pageNum;
    /**
     * 总数量
     */
    private int count;
    /**
     * 总页数
     */
    private int totalPage;

    public PageInfo() {
    }

    public PageInfo(int pageSize, int pageNum) {
        this.pageSize = pageSize;
        this.pageNum = pageNum;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize > MAX_PAGE_SIZE ? MAX_PAGE_SIZE : pageSize;
    }

    /**
     * 当前页数 小于 1 的默认为第一页
     *
     * @return 页数
     */
    public int getPageNum() {
        return pageNum < 1 ? 1 : pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
        if (count % this.pageSize == 0) {
            this.totalPage = count / this.pageSize;
        } else {
            this.totalPage = count / this.pageSize + 1;
        }
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    @Override
    public String toString() {
        return "PageInfo{" +
                "list=" + list +
                ", pageSize=" + pageSize +
                ", pageNum=" + pageNum +
                ", count=" + count +
                ", totalPage=" + totalPage +
                '}';
    }
}
