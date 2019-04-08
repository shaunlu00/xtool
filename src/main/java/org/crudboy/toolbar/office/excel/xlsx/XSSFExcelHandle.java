package org.crudboy.toolbar.office.excel.xlsx;

import org.apache.poi.ooxml.util.SAXHelper;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.crudboy.toolbar.office.excel.Cell;
import org.crudboy.toolbar.office.excel.Row;
import org.crudboy.toolbar.toolbarerror.CRUDToolbarException;
import org.crudboy.toolbar.toolbarerror.ErrorConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class XSSFExcelHandle {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Write data into excel sheet
     *
     * @param filePath  Excel file path
     * @param sheetName Sheet name
     * @param data      Row list
     */
    public void writeDataToSheet(String filePath, String sheetName, List<Row> data) {
        // keep 1000 rows in memory, exceeding rows will be flushed to disk
        try (SXSSFWorkbook wb = new SXSSFWorkbook(1000);
             FileOutputStream out = new FileOutputStream(filePath)
        ) {
            // temp files will be gzipped
            wb.setCompressTempFiles(true);
            Sheet sheet = wb.createSheet(sheetName);
            for (int rowNum = 0; rowNum < data.size(); rowNum++) {
                org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowNum);
                List<Cell> cells = data.get(rowNum).getCells();
                for (int cellnum = 0; cellnum < cells.size(); cellnum++) {
                    org.apache.poi.ss.usermodel.Cell cell = row.createCell(cellnum);
                    cell.setCellValue(cells.get(cellnum).getContent());
                }
            }
            wb.write(out);
            wb.dispose();
        } catch (IOException e) {
            logger.error("write excel error", e);
            throw new CRUDToolbarException(ErrorConstants.EXCEL_WRITE_ERROR, e);
        }
    }

    /**
     * Write data to multiple sheets
     *
     * @param filePath Excel file path
     * @param sheetMap A sheet - data map
     */
    public void writeDataToMultipleSheet(String filePath, Map<String, List<Row>> sheetMap) {
        // keep 1000 rows in memory, exceeding rows will be flushed to disk
        try (SXSSFWorkbook wb = new SXSSFWorkbook(1000);
             FileOutputStream out = new FileOutputStream(filePath)
        ) {
            // temp files will be gzipped
            wb.setCompressTempFiles(true);
            for (Map.Entry<String, List<Row>> item : sheetMap.entrySet()) {
                Sheet sheet = wb.createSheet(item.getKey());
                List<Row> sheetData = item.getValue();
                for (int rowNum = 0; rowNum < sheetData.size(); rowNum++) {
                    org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowNum);
                    List<Cell> cells = sheetData.get(rowNum).getCells();
                    for (int cellnum = 0; cellnum < cells.size(); cellnum++) {
                        org.apache.poi.ss.usermodel.Cell cell = row.createCell(cellnum);
                        cell.setCellValue(cells.get(cellnum).getContent());
                    }
                }
            }
            wb.write(out);
            wb.dispose();
        } catch (IOException e) {
            logger.error("write excel error", e);
            throw new CRUDToolbarException(ErrorConstants.EXCEL_WRITE_ERROR, e);
        }
    }

    /**
     * Extract sheet data from excel and write it into a blocking queue
     *
     * @param filePath         Excel file path
     * @param sheetName        Sheet name
     * @param buffer           The blocking queue used to store sheet data
     * @param timeoutInSeconds waiting time when the queue is full, throw error when the time is reached
     */
    public void readSheetDataToBlockingQueue(String filePath, String sheetName, BlockingQueue<Row> buffer, int timeoutInSeconds) {
        SheetContentExtrator sheetContentExtrator = new SheetContentExtrator(buffer, timeoutInSeconds);
        extractData(openExcelReadOnly(filePath), sheetName, sheetContentExtrator);
    }

    public void readSheetDataToBlockingQueue(InputStream inputStream, String sheetName, BlockingQueue<Row> buffer, int timeoutInSeconds) {
        SheetContentExtrator sheetContentExtrator = new SheetContentExtrator(buffer, timeoutInSeconds);
        extractData(openExcelReadOnly(inputStream), sheetName, sheetContentExtrator);
    }

    /**
     * Extract sheet data from excel and write it into a list buffer
     *
     * @param filePath  Excel file path
     * @param sheetName Sheet name
     * @param buffer    List buffer
     */
    public void readSheetData(String filePath, String sheetName, final List<Row> buffer) {
        SheetContentExtrator sheetContentExtrator = new SheetContentExtrator(buffer);
        extractData(openExcelReadOnly(filePath), sheetName, sheetContentExtrator);
    }

    public void readSheetData(InputStream inputStream, String sheetName, final List<Row> buffer) {
        SheetContentExtrator sheetContentExtrator = new SheetContentExtrator(buffer);
        extractData(openExcelReadOnly(inputStream), sheetName, sheetContentExtrator);
    }

    private OPCPackage openExcelReadOnly(String filePath) {
        OPCPackage pkg = null;
        try {
            pkg = OPCPackage.open(filePath, PackageAccess.READ);
        } catch (Exception e) {
            logger.error("open excel error", e);
            throw new CRUDToolbarException(ErrorConstants.EXCEL_OPEN_ERROR, e);
        }
        return pkg;
    }

    private OPCPackage openExcelReadOnly(InputStream inputStream) {
        OPCPackage pkg = null;
        try {
            pkg = OPCPackage.open(inputStream);
        } catch (Exception e) {
            logger.error("open excel error", e);
            throw new CRUDToolbarException(ErrorConstants.EXCEL_OPEN_ERROR, e);
        }
        return pkg;
    }

    /**
     * Extract sheet data
     *
     * @param pkg
     * @param sheetName            Sheet name
     * @param sheetContentExtrator Data extractor
     */
    public void extractData(OPCPackage pkg, String sheetName, SheetContentExtrator sheetContentExtrator) {
        InputStream sheet = null;
//        OPCPackage pkg = null;
        try {
//            pkg = OPCPackage.open(filePath, PackageAccess.READ);
            XSSFReader xssfReader = new XSSFReader(pkg);
            StylesTable styles = xssfReader.getStylesTable();
            ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(pkg);
            sheet = xssfReader.getSheet(sheetName);
            InputSource sheetSource = new InputSource(sheet);
            DataFormatter formatter = new DataFormatter();
            XMLReader sheetParser = SAXHelper.newXMLReader();
            ContentHandler handler = new XSSFSheetXMLHandler(
                    styles, null, strings, sheetContentExtrator, formatter, false);
            sheetParser.setContentHandler(handler);
            sheetParser.parse(sheetSource);
        } catch (Exception e) {
            logger.error("read sheet data error", e);
            throw new CRUDToolbarException(ErrorConstants.EXCEL_READ_ERROR, e);
        } finally {
            if (null != sheet) {
                try {
                    sheet.close();
                } catch (IOException e) {
                    logger.error("close sheet error");
                }
            }
            if (null != pkg) {
                try {
                    pkg.close();
                } catch (IOException e) {
                    logger.error("close excel error");
                }
            }
        }
    }

}
