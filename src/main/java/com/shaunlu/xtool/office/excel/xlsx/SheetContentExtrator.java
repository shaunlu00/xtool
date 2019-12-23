package com.shaunlu.xtool.office.excel.xlsx;

import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.usermodel.XSSFComment;
import com.shaunlu.xtool.error.ErrorCode;
import com.shaunlu.xtool.error.XToolException;
import com.shaunlu.xtool.office.excel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class SheetContentExtrator implements XSSFSheetXMLHandler.SheetContentsHandler {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private int currentRowNum = -1;
    private int currentColNum = -1;
    private Row currentRow;

    private List<Row> retBuffer;

    private boolean isBlockingWay;
    private BlockingQueue<Row> blockingRetBuffer;
    private int timeoutInSeconds;

    public SheetContentExtrator(List<Row> retBuffer) {
        this.retBuffer = retBuffer;
    }

    public SheetContentExtrator(BlockingQueue<Row> blockingRetBuffer, int timeoutInSeconds) {
        this.isBlockingWay = true;
        this.blockingRetBuffer = blockingRetBuffer;
        this.timeoutInSeconds = timeoutInSeconds;
    }

    private void outputMissingRows(int rowNum) {
        logger.info("Missing row :{}", rowNum);
    }

    @Override
    public void startRow(int rowNum) {
        // If there were gaps, output the missing rows
        for (int i = currentRowNum + 1; i < rowNum; i++) {
            outputMissingRows(i);
        }
        // Prepare for this row
        currentRowNum = rowNum;
        currentColNum = -1;
        currentRow = new Row(rowNum);
        if (isBlockingWay) {
            try {
                blockingRetBuffer.offer(currentRow, timeoutInSeconds, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                logger.error("put result to blockingqueue error", e);
                Thread.currentThread().interrupt();
                throw new XToolException(ErrorCode.EXCEL_READ_ERROR, e);
            }
        } else {
            retBuffer.add(currentRow);
        }
    }

    @Override
    public void endRow(int rowNum) {
        // Ensure the minimum number of columns
    }

    @Override

    public void cell(String cellReference, String formattedValue, XSSFComment comment) {
        // gracefully handle missing CellRef here in a similar way as XSSFCell does
        if (cellReference == null) {
            cellReference = new CellAddress(currentRowNum, currentColNum + 1).formatAsString();
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

