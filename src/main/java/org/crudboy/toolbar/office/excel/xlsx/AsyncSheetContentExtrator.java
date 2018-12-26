package org.crudboy.toolbar.office.excel.xlsx;

import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.crudboy.toolbar.office.excel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class AsyncSheetContentExtrator implements XSSFSheetXMLHandler.SheetContentsHandler {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private int currentRowNum = -1;
    private int currentColNum = -1;
    private Row currentRow;

    private int rowStart;
    private int rowEnd;
    private List<Row> retBuffer;

    public AsyncSheetContentExtrator(List<Row> retBuffer, int rowStart, int rowEnd) {
        this.retBuffer = retBuffer;
        this.rowStart = rowStart;
        this.rowEnd = rowEnd;
    }

    private void outputMissingRows(int rowNum) {
        logger.info("Missing row :{}", rowNum);
    }

    private boolean isOutOfRange(int rowNum) {
        return rowNum<rowStart || rowNum>rowEnd;
    }

    @Override
    public void startRow(int rowNum) {
        if (isOutOfRange(rowNum)){
            currentRowNum = rowNum;
            return;
        }
        // If there were gaps, output the missing rows
        for (int i = currentRowNum + 1; i < rowNum; i++) {
            outputMissingRows(i);
        }
        // Prepare for this row
        currentRowNum = rowNum;
        currentColNum = -1;
        currentRow = new Row(rowNum);
        retBuffer.add(currentRow);
    }

    @Override
    public void endRow(int rowNum) {
        // Ensure the minimum number of columns
    }

    @Override
    public void cell(String cellReference, String formattedValue, XSSFComment comment) {
        if (isOutOfRange(currentRowNum)){
            return;
        }

        // gracefully handle missing CellRef here in a similar way as XSSFCell does
        if (cellReference == null) {
            cellReference = new CellAddress(currentRowNum, currentColNum+1).formatAsString();
        }

        // Did we miss any cells?
        int thisColNum = (new CellReference(cellReference)).getCol();
        for (int i = currentColNum + 1; i < thisColNum; i++) {
            currentRow.addCell(i, formattedValue);
        }
        currentColNum = thisColNum;

        // Number or string?
        try {
            //noinspection ResultOfMethodCallIgnored
            Double.parseDouble(formattedValue);
            currentRow.addCell(currentColNum, formattedValue);
        } catch (NumberFormatException e) {
            currentRow.addCell(currentColNum, '"' + formattedValue + '"');
        }
    }
}

