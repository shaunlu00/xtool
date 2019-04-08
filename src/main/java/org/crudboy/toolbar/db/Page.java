package org.crudboy.toolbar.db;

public class Page {

    private int currentPageNum;

    private int size;

    private int totalRecords;

    public Page(){}

    public Page(int currentPageNum, int size, int totalRecords) {
        this.currentPageNum = currentPageNum;
        this.size = size;
        this.totalRecords = totalRecords;
    }

    public int getCurrentPageNum() {
        return currentPageNum;
    }

    public void setCurrentPageNum(int currentPageNum) {
        this.currentPageNum = currentPageNum;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }
}
