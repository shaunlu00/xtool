package org.crudboy.toolbar.office.excel;

import java.util.ArrayList;
import java.util.List;

public class Row {

    private int rowNum;

    private List<Cell> cells;

    public Row(int rowNum) {
        this.rowNum = rowNum;
        cells = new ArrayList<>();
    }

    public void addCell(int colNum, String content) {
        Cell cell = new Cell(colNum, content);
        cells.add(cell);
    }

    public List<Cell> getCells() {
        return cells;
    }

    public int getRowNum() {
        return rowNum;
    }
}
