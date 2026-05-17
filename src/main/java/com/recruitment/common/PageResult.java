package com.recruitment.common;

import java.util.List;

/**
 * 分页响应
 * 包含当前页数据和分页信息，前端用来渲染分页条
 *
 * @param <T> 数据类型
 */
public class PageResult<T> {

    private long total;         // 总记录数
    private int pageNum;        // 当前页码（从1开始）
    private int pageSize;       // 每页条数
    private int totalPages;     // 总页数
    private List<T> list;       // 当前页数据

    public PageResult() {}

    /**
     * 根据 total、pageNum、pageSize 自动计算 totalPages
     */
    public PageResult(long total, int pageNum, int pageSize, List<T> list) {
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.totalPages = (int) Math.ceil((double) total / pageSize);
        this.list = list;
    }

    // ---------- getter / setter ----------
    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }

    public int getPageNum() { return pageNum; }
    public void setPageNum(int pageNum) { this.pageNum = pageNum; }

    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }

    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }

    public List<T> getList() { return list; }
    public void setList(List<T> list) { this.list = list; }
}
