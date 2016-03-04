package com.sooreen.footstat.util;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;

public class POIHelper {

    public static void createCell(Row row, int pos, String value)
    {
        Cell cell = row.createCell(pos);
        if(value != null){
            cell.setCellValue(value);
        }
    }

    public static void createCell(Row row, int pos, String value, CellStyle style)
    {
        Cell cell = row.createCell(pos);
        if(value != null){
            cell.setCellValue(value);
        }
        if(style != null){
            cell.setCellStyle(style);
        }
    }

    public static void createCell(Row row, int pos, Double value, CellStyle style)
    {
        Cell cell = row.createCell(pos);
        if(value != null){
            cell.setCellValue(value);
        }
        if(style != null){
            cell.setCellStyle(style);
        }
    }

    public static void createCell(Row row, int pos, Integer value)
    {
        Cell cell = row.createCell(pos);
        if(value != null){
            cell.setCellValue(value);
        }
    }

    public static void createCell(Row row, int pos, Double value)
    {
        Cell cell = row.createCell(pos);
        if(value != null){
            cell.setCellValue(value);
        }
    }

}
