package org.crudboy.toolbar.test;

import ch.qos.logback.core.util.FileUtil;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.crudboy.toolbar.classhack.ClasspathUtil;
import org.crudboy.toolbar.office.excel.Row;
import org.crudboy.toolbar.office.excel.xlsx.XSSFExcelHandle;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;

public class ExcelTest {

    private XSSFExcelHandle xssfExcelHandle = new XSSFExcelHandle();

    @Test
    public void testReadALL() {
        long start = System.currentTimeMillis();
        String originFilePath = ClasspathUtil.getResourceAbsolutePath("test-data/office/BlackFriday.xlsx");
        List<Row> rowList = xssfExcelHandle.readSheetData(originFilePath, "rId1");
        System.out.printf("Read excel in %ds\n", (System.currentTimeMillis() - start) / 1000);
        printMemoryUsage();
    }

    @Test
    public void testWriteALL() throws IOException {
        long start = System.currentTimeMillis();
        String originFilePath = ClasspathUtil.getResourceAbsolutePath("test-data/office/BlackFriday.xlsx");
        List<Row> rowList = xssfExcelHandle.readSheetData(originFilePath, "rId1");
        System.out.printf("Read excel in %ds\n", (System.currentTimeMillis() - start) / 1000);
        printMemoryUsage();
        start = System.currentTimeMillis();
        File file = new File(ClasspathUtil.getClassURL(ExcelTest.class, null).getPath() + "test-data/office/BlackFriday_Output.xlsx");
        file.createNewFile();
        xssfExcelHandle.writeSheetData(file.getAbsolutePath(), "rId1", rowList);
        System.out.printf("Write excel in %ds\n", (System.currentTimeMillis() - start) / 1000);
        printMemoryUsage();

    }

    @Test
    public void printMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();

        NumberFormat format = NumberFormat.getInstance();

        StringBuilder sb = new StringBuilder();
        long maxMemory = runtime.maxMemory();
        long allocatedMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();

        sb.append("free memory: " + format.format(freeMemory / (1024 * 1024)) + "MB\n");
        sb.append("allocated memory: " + format.format(allocatedMemory / (1024 * 1024)) + "MB\n");
        sb.append("max memory: " + format.format(maxMemory / (1024 * 1024)) + "MB\n");
        sb.append("total free memory: " + format.format((freeMemory + (maxMemory - allocatedMemory)) / (1024 * 1024)) + "MB\n");
        System.out.println(sb.toString());
    }
}
