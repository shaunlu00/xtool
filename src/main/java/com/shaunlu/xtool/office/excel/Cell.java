package com.shaunlu.xtool.office.excel;

public class Cell {

    private int colNum;

    private String content;

    public Cell(int colNum, String content) {
        this.colNum = colNum;
        this.content = content;
    }

    public String getContent() {
        return this.content;
    }
}
