package cn.finull.framework.db.orm;

import java.util.ArrayList;
import java.util.List;

public class Page<T> extends ArrayList<T> {

    // 总页数
    private int totalPages;

    // 总数据条数
    private int totalCols;

    // 数据库开始索引
    private int startIndex;

    // 每页数据条数
    private int pageSize;

    // 是否有上一页
    private boolean hasPreviousPage;

    // 是否有下一页
    private boolean hasNextPage;

    // 是否是第一页
    private boolean isFirstPage;

    // 是否是最后一页
    private boolean isLastPage;

    // 当前页数
    private int curPage;

    // 前一页
    private int previousPage;

    // 下一页
    private int nextPage;

    // 第一页
    private int firstPage;

    // 最后一页
    private int lastPage;

    // 数据列表
    private List<T> list;

    public Page(int curPage, int pageSize, int totalCols) {
        this.totalCols = totalCols;
        this.pageSize = pageSize;
        if (totalCols == 0) {
            this.totalPages = 1;
            this.curPage = 1;
            this.hasPreviousPage = false;
            this.hasNextPage = false;
            this.isFirstPage = true;
            this.isLastPage = true;
            this.previousPage = 1;
            this.nextPage = 1;
            this.firstPage = 1;
            this.lastPage = 1;
        }
        else {
            this.totalPages = totalCols / pageSize + (totalCols % pageSize == 0 ? 0 : 1);
            if (curPage > totalPages) {
                this.curPage = totalPages;
            }
            else if (curPage < 1) {
                this.curPage = 1;
            }
            else {
                this.curPage = curPage;
            }
            this.hasPreviousPage = this.curPage != 1;
            this.hasNextPage = this.curPage != this.totalPages;
            this.isFirstPage = this.curPage == 1;
            this.isLastPage = this.curPage == this.totalPages;
            this.previousPage = this.hasPreviousPage ? this.curPage - 1 : 1;
            this.nextPage = this.hasNextPage ? this.curPage + 1 : this.totalPages;
            this.firstPage = 1;
            this.lastPage = this.totalPages;
        }
        this.startIndex = (this.curPage - 1) * this.pageSize;
    }

    public Page(List<T> list) {
        setList(list);
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getTotalCols() {
        return totalCols;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public boolean getHasPreviousPage() {
        return hasPreviousPage;
    }

    public boolean getHasNextPage() {
        return hasNextPage;
    }

    public boolean getIsFirstPage() {
        return isFirstPage;
    }

    public boolean getIsLastPage() {
        return isLastPage;
    }

    public int getCurPage() {
        return curPage;
    }

    public int getPreviousPage() {
        return previousPage;
    }

    public int getNextPage() {
        return nextPage;
    }

    public int getFirstPage() {
        return firstPage;
    }

    public int getLastPage() {
        return lastPage;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        if (list instanceof Page) {
            Page<T> page = (Page<T>) list;
            this.totalPages = page.totalPages;
            this.totalCols = page.totalCols;
            this.startIndex = page.startIndex;
            this.pageSize = page.pageSize;
            this.lastPage = page.lastPage;
            this.firstPage = page.firstPage;
            this.nextPage = page.nextPage;
            this.previousPage = page.previousPage;
            this.isLastPage = page.isLastPage;
            this.isFirstPage = page.isFirstPage;
            this.hasNextPage = page.hasNextPage;
            this.hasPreviousPage = page.hasPreviousPage;
            this.curPage = page.curPage;
            this.list = page.list;
        }
        else {
            this.list = list;
        }
    }
}
