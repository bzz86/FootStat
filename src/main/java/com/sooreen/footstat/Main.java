package com.sooreen.footstat;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sooreen.footstat.po.Championate;
import com.sooreen.footstat.po.MatchResult;
import com.sooreen.footstat.util.POIHelper;
import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.xml.sax.SAXException;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static List<Championate> champs = null;
    private static List<MatchResult> matchResults;

    public static void main( String[] args ) throws IOException {
        boolean exit = false;
        do {
            System.out.println("\n\n          FootStat");
            System.out.println("--------------------------------------");
            System.out.println("1 - Load initial base");
            System.out.println("2 - Get updates");
            System.out.println("3 - Export to Excel");
            System.out.println("4 - Read config file");
            System.out.println("5 - Load results from big file");
            System.out.println("0 - Exit");
            System.out.print("\nSelect a Menu Option: ");
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                int code = Integer.parseInt(br.readLine());

                try {
                    switch (code) {
                        case 1:
                            if(champs != null){
                                WildStatParser wsp = new WildStatParser();
                                List<MatchResult> results = wsp.parseResults(champs);

                                saveResultsToFile("D:\\Temp\\workspace\\FootStat\\src\\main\\resources\\output_big.json", results);

                                print(results.size() + " matches loaded");
                                /*for(MatchResult r : results){
                                    print("%s   %s - %s  %s:%s", r.getGameDate(), r.getTeam1(), r.getTeam2(), r.getScore1(), r.getScore2());
                                }*/
                            }
                            break;
                        case 2:
                            URL url = Main.class.getProtectionDomain().getCodeSource().getLocation();
                            print(url.getPath());
                            break;
                        case 3:
                            createExcel("./test.xlsx"/*"D:\\Temp\\workspace\\FootStat\\src\\main\\resources\\result.xlsx"*/, matchResults);
                            print("export complete");
                            break;
                        case 4:
                            champs = loadChampionatesConfig("D:\\Temp\\workspace\\FootStat\\src\\main\\resources\\championates.json");
                            for(Championate champ : champs){
                                print(champ.getName());
                            }
                            break;
                        case 5:
                            matchResults = loadResultsFromFile("D:\\Temp\\workspace\\FootStat\\src\\main\\resources\\output_big.json");
                            break;
                        case 0:
                            exit = true;
                            break;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

        } while (!exit);
    }




    private static void print(String msg, Object... args) {
        System.out.println(String.format(msg, args));
    }

    private static List<Championate> loadChampionatesConfig(String fileName) throws IOException {

        List<Championate> result = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        JsonFactory f = new MappingJsonFactory();


        JsonParser jp = f.createJsonParser(new File(fileName));
        JsonToken current;
        current = jp.nextToken();
        if (current != JsonToken.START_ARRAY) {
            System.out.println("Error: root should be array: quiting.");
            return result;
        }

        while (jp.nextToken() != JsonToken.END_ARRAY) {
            JsonNode node = jp.readValueAsTree();
            result.add(mapper.treeToValue(node, Championate.class));
        }

        return result;
    }

    public static void saveResultsToFile(String fileName, List<MatchResult> matchResults){
        boolean hasErrors = false;

        try (OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(fileName + "_tmp"), "UTF-8")){
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(out, matchResults);
        } catch (IOException e) {
            e.printStackTrace();
            hasErrors = true;
        }

        //copy tmp file
        if(!hasErrors) {
            try {
                File tmp = new File(fileName + "_tmp");
                FileUtils.copyFile(tmp, new File(fileName));
                tmp.delete();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private static List<MatchResult> loadResultsFromFile(String fileName) throws IOException {
        List<MatchResult> results = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        JsonFactory f = new MappingJsonFactory();


        JsonParser jp = f.createJsonParser(new File(fileName));
        JsonToken current;
        current = jp.nextToken();
        if (current != JsonToken.START_ARRAY) {
            System.out.println("Error: root should be array: quiting.");
            return results;
        }

        while (jp.nextToken() != JsonToken.END_ARRAY) {
            JsonNode node = jp.readValueAsTree();
            results.add(mapper.treeToValue(node, MatchResult.class));
        }

        return results;
    }


    public static void createExcel(String xlsFile, List<MatchResult> fullResults) throws IOException, SAXException {
        createExcel(xlsFile, fullResults, null);
    }

    public static void createExcel(String xlsFile, String bigJsonFile, String updateJsonFile) throws IOException, SAXException {
        createExcel(xlsFile, loadResultsFromFile(bigJsonFile), loadResultsFromFile(updateJsonFile));
    }


    public static void createExcel(String xlsFile, List<MatchResult> fullResults, List<MatchResult> recentResults) throws IOException, SAXException {

        SXSSFWorkbook workbook = new SXSSFWorkbook(1000);

        //two sheets - full and recent results

        //full results
        Sheet sheet = workbook.createSheet("База целиком");

        int rowCount = 0;

        //header
        CellStyle headerStyle = sheet.getWorkbook().createCellStyle();
        headerStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
        headerStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        Row row = sheet.createRow(rowCount);
        int columnCount = 0;

        POIHelper.createCell(row, columnCount++, "Чемпионат", headerStyle);
        POIHelper.createCell(row, columnCount++, "Лига", headerStyle);
        POIHelper.createCell(row, columnCount++, "Сезон", headerStyle);
        POIHelper.createCell(row, columnCount++, "Дата игры", headerStyle);
        POIHelper.createCell(row, columnCount++, "Команда 1", headerStyle);
        POIHelper.createCell(row, columnCount++, "Команда 2", headerStyle);
        POIHelper.createCell(row, columnCount++, "Счет 1", headerStyle);
        POIHelper.createCell(row, columnCount++, "Счет 2", headerStyle);


        for (MatchResult mr : fullResults) {
            row = sheet.createRow(++rowCount);

            columnCount = 0;

            POIHelper.createCell(row, columnCount++, mr.getChampName());
            POIHelper.createCell(row, columnCount++, mr.getLeagueName());
            POIHelper.createCell(row, columnCount++, mr.getSeasonName());
            POIHelper.createCell(row, columnCount++, mr.getGameDate());
            POIHelper.createCell(row, columnCount++, mr.getTeam1());
            POIHelper.createCell(row, columnCount++, mr.getTeam2());
            try {
                POIHelper.createCell(row, columnCount++, Integer.valueOf(mr.getScore1()));
                POIHelper.createCell(row, columnCount++, Integer.valueOf(mr.getScore2()));
            }catch (NumberFormatException e){
                print("Problem on a row %s of the full database", rowCount);
            }

            //if(rowCount > 10000) break;
        }


        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
        sheet.autoSizeColumn(4);
        sheet.autoSizeColumn(5);
        sheet.autoSizeColumn(6);
        sheet.autoSizeColumn(7);


        //recent results
        if(recentResults != null) {
            sheet = workbook.createSheet("Последние обновления");

            rowCount = 0;
            row = sheet.createRow(rowCount);
            columnCount = 0;

            POIHelper.createCell(row, columnCount++, "Чемпионат", headerStyle);
            POIHelper.createCell(row, columnCount++, "Лига", headerStyle);
            POIHelper.createCell(row, columnCount++, "Сезон", headerStyle);
            POIHelper.createCell(row, columnCount++, "Дата игры", headerStyle);
            POIHelper.createCell(row, columnCount++, "Команда 1", headerStyle);
            POIHelper.createCell(row, columnCount++, "Команда 2", headerStyle);
            POIHelper.createCell(row, columnCount++, "Счет 1", headerStyle);
            POIHelper.createCell(row, columnCount++, "Счет 2", headerStyle);


            for (MatchResult mr : recentResults) {
                row = sheet.createRow(++rowCount);

                columnCount = 0;

                POIHelper.createCell(row, columnCount++, mr.getChampName());
                POIHelper.createCell(row, columnCount++, mr.getLeagueName());
                POIHelper.createCell(row, columnCount++, mr.getSeasonName());
                POIHelper.createCell(row, columnCount++, mr.getGameDate());
                POIHelper.createCell(row, columnCount++, mr.getTeam1());
                POIHelper.createCell(row, columnCount++, mr.getTeam2());
                try {
                    POIHelper.createCell(row, columnCount++, Integer.valueOf(mr.getScore1()));
                    POIHelper.createCell(row, columnCount++, Integer.valueOf(mr.getScore2()));
                }catch (NumberFormatException e){
                    print("Problem on a row %s of the recent matches", rowCount);
                }
            }


            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);
            sheet.autoSizeColumn(3);
            sheet.autoSizeColumn(4);
            sheet.autoSizeColumn(5);
            sheet.autoSizeColumn(6);
            sheet.autoSizeColumn(7);
        }

        FileOutputStream outputStream = new FileOutputStream(xlsFile);
        workbook.write(outputStream);
        outputStream.flush();
        outputStream.close();

        workbook.dispose();

    }

}
