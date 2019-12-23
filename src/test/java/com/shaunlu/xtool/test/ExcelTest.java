package com.shaunlu.xtool.test;

import com.shaunlu.xtool.office.excel.Row;
import com.shaunlu.xtool.office.excel.xlsx.XSSFExcelHandle;
import com.shaunlu.xtool.classhack.ClasspathUtil;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class ExcelTest {

    private XSSFExcelHandle xssfExcelHandle = new XSSFExcelHandle();

    @Test
    public void testReadALL() throws IOException {
        long start = System.currentTimeMillis();
        String originFilePath = ClasspathUtil.getResourceAbsolutePath("test-data/office/BlackFriday.xlsx");
        List<Row> buffer = new ArrayList<>();
        xssfExcelHandle.readSheetData(originFilePath, "rId1", buffer);
        System.out.printf("Read excel in %ds\n", (System.currentTimeMillis() - start) / 1000);
        printMemoryUsage();
    }

    @Test
    public void testWriteALL() throws IOException {
        long start = System.currentTimeMillis();
        String originFilePath = ClasspathUtil.getResourceAbsolutePath("test-data/office/BlackFriday.xlsx");
        List<Row> buffer = new ArrayList<>();
        xssfExcelHandle.readSheetData(originFilePath, "rId1", buffer);
        System.out.printf("Read excel in %ds\n", (System.currentTimeMillis() - start) / 1000);
        printMemoryUsage();
        start = System.currentTimeMillis();
        File file = new File(ClasspathUtil.getClassURL(ExcelTest.class, null).getPath() + "test-data/office/BlackFriday_Output.xlsx");
        file.createNewFile();
        xssfExcelHandle.writeDataToSheet(file.getAbsolutePath(), "rId1", buffer);
        System.out.printf("Write excel in %ds\n", (System.currentTimeMillis() - start) / 1000);
        printMemoryUsage();

    }

    @Test
    public void testAsyncTask() throws IOException, InterruptedException {
        long start = System.currentTimeMillis();
        String originFilePath = ClasspathUtil.getResourceAbsolutePath("test-data/office/BlackFriday.xlsx");
        BlockingQueue<Row> buffer = new ArrayBlockingQueue<Row>(1000);
        AsyncExcelReader reader = new AsyncExcelReader(originFilePath, "rId1", xssfExcelHandle, buffer);
        AsyncExcelConsumer consumer = new AsyncExcelConsumer(buffer);
        reader.start();
        consumer.start();
        reader.join();
        consumer.join();
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

    public static class AsyncExcelReader extends Thread {

        private String filePath;
        private String sheetName;
        private BlockingQueue<Row> queue;
        private XSSFExcelHandle excelHandle;

        public AsyncExcelReader(String filePath, String sheetName, XSSFExcelHandle excelHandle, BlockingQueue<Row> queue) {
            this.filePath = filePath;
            this.sheetName = sheetName;
            this.queue = queue;
            this.excelHandle = excelHandle;
        }

        public void run() {
            excelHandle.readSheetDataToBlockingQueue(filePath, sheetName, queue, 10);
        }
    }

    public static class AsyncExcelConsumer extends Thread {

        private BlockingQueue<Row> queue;

        public AsyncExcelConsumer(BlockingQueue<Row> queue) {
            this.queue = queue;
        }

        public void run() {
            while (true) {
                try {
                    Row row = queue.poll(3, TimeUnit.SECONDS);
                    if (null == row) {
                        return;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
