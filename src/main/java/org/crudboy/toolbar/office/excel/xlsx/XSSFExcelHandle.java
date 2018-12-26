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
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.crudboy.toolbar.office.excel.Cell;
import org.crudboy.toolbar.office.excel.Row;
import org.crudboy.toolbar.office.excel.error.ErrorConstants;
import org.crudboy.toolbar.office.excel.error.ExcelHandleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class XSSFExcelHandle {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public void writeSheetData(String filePath, String sheetName, List<Row> data) {
        // keep 1000 rows in memory, exceeding rows will be flushed to disk
        SXSSFWorkbook wb = new SXSSFWorkbook(1000);
        // temp files will be gzipped
        wb.setCompressTempFiles(true);
        Sheet sheet = wb.createSheet(sheetName);
        for (int rowNum=0; rowNum<data.size(); rowNum ++) {
            org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowNum);
            List<Cell> cells = data.get(rowNum).getCells();
            for(int cellnum = 0; cellnum < cells.size(); cellnum++){
                org.apache.poi.ss.usermodel.Cell cell = row.createCell(cellnum);
                cell.setCellValue(cells.get(cellnum).getContent());
            }
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filePath);
            wb.write(out);
        } catch (Exception e) {
            logger.error("write excel error", e);
            throw new ExcelHandleException(ErrorConstants.EXCEL_WRITE_ERROR, e);
        } finally {
            if (null != out) {
                try {
                    out.close();
                } catch (IOException e) {
                    logger.error("close excel error");
                    throw new ExcelHandleException(ErrorConstants.EXCEL_CLOSE_ERROR, e);
                }
            }
            // dispose of temporary files backing this workbook on disk
            wb.dispose();
        }


    }

    public List<Row> readSheetData(String filePath, String sheetName) {
        List<Row> ret = new ArrayList<>();
        InputStream sheet = null;
        OPCPackage pkg = null;
        try {
            pkg = OPCPackage.open(filePath, PackageAccess.READ);
            XSSFReader xssfReader = new XSSFReader(pkg);
            StylesTable styles = xssfReader.getStylesTable();
            ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(pkg);
            sheet = xssfReader.getSheet(sheetName);
            InputSource sheetSource = new InputSource(sheet);
            DataFormatter formatter = new DataFormatter();
            XMLReader sheetParser = SAXHelper.newXMLReader();
            ContentHandler handler = new XSSFSheetXMLHandler(
                    styles, null, strings, new SheetContentExtrator(ret), formatter, false);
            sheetParser.setContentHandler(handler);
            sheetParser.parse(sheetSource);
        } catch (Exception e) {
            logger.error("read sheet data error", e);
            throw new ExcelHandleException(ErrorConstants.EXCEL_READ_ERROR, e);
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

        return ret;
    }
}
