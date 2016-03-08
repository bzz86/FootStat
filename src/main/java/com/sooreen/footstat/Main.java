package com.sooreen.footstat;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sooreen.footstat.po.Championate;
import com.sooreen.footstat.po.FinalResult;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Main {
    private static List<Championate> champs = null;
    private static FinalResult fullResult;
    //private static List<MatchResult> matchResults;
    private static List<MatchResult> recentMatchResults;
    //private static Date lastUpdate = null;

    private static final String CHAMPS_FILE = "./config/championates.json";
    private static final String BIG_OUTPUT_FILE = "./data/output_big.json";
    private static final String RECENT_OUTPUT_FILE = "./data/output_recent.json";
    private static final String XLS_FILE = "./excel/footstat.xlsx";

    static{
       init();
    }

    public static void main( String[] args ) throws IOException {
        boolean exit = false;
        do {
            System.out.println("\n\n          FootStat");
            System.out.println("--------------------------------------");
            System.out.println("1 - Load initial base");
            System.out.println("2 - Get updates");
            System.out.println("3 - Export to Excel");
            //System.out.println("4 - Read config file");
            //System.out.println("5 - Load results from big file");
            System.out.println("0 - Exit");
            System.out.print("\nSelect a Menu Option: ");
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                int code = Integer.parseInt(br.readLine());

                try {
                    switch (code) {
                        case 1:
                            loadInitialBase();
                            break;
                        case 2:
                            loadUpdate();
                            break;
                        case 3:
                            exportToExcel();
                            break;
                       /* case 4:
                            init();*/
                            /*try{
                                champs = loadChampionatesConfig(CHAMPS_FILE);
                            }catch(IOException e){
                                URL configUrl = Main.class.getResource(CHAMPS_FILE);
                                champs = loadChampionatesConfig(configUrl.getPath());
                            }
                            print(champs.size() + " championates loaded");*/
                            /*for(Championate champ : champs){
                                print(champ.getName());
                            }*/
                           /* break;
                        case 5:
                            fullResult = loadResultsFromFile(BIG_OUTPUT_FILE);
                            recentMatchResults = loadResultsFromFile(RECENT_OUTPUT_FILE).getMatchResults();
                            break;*/
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


    private static void loadInitialBase() throws IOException {
        Date lastUpdate = new Date();
        long startTime = System.currentTimeMillis();
        if(champs != null){
            WildStatParser wsp = new WildStatParser();
            List<MatchResult> results = wsp.parseResults(champs);
            fullResult = new FinalResult();
            fullResult.setLastUpdate(lastUpdate);
            fullResult.setMatchResults(results);
            saveResultsToFile(BIG_OUTPUT_FILE, fullResult);
            saveResultsToFile(RECENT_OUTPUT_FILE, new ArrayList<MatchResult>());

            print(results.size() + " matches loaded");
            print("Loading time: %s seconds", (System.currentTimeMillis() - startTime) / 1000 );
            /*for(MatchResult r : results){
                print("%s   %s - %s  %s:%s", r.getGameDate(), r.getTeam1(), r.getTeam2(), r.getScore1(), r.getScore2());
            }*/
        }else{
            print("Config not loaded!");
        }
    }

    private static void init(){
        try{
            print("Initialization...");

            //load config
            try{
                champs = loadChampionatesConfig(CHAMPS_FILE);
            }catch(IOException e){
                URL configUrl = Main.class.getResource(CHAMPS_FILE);
                champs = loadChampionatesConfig(configUrl.getPath());
            }
            print("Config loading: %s championates loaded", champs.size());

            //load stored data from files
            try{
                fullResult = loadResultsFromFile(BIG_OUTPUT_FILE);
                recentMatchResults = loadResultsFromFile(RECENT_OUTPUT_FILE).getMatchResults();
                print("Stored data loaded: %s matches in a full base, %s matches in a recent base", fullResult.getMatchResults().size(), recentMatchResults.size());
            }catch(IOException e){
                print("Problem with loading stored data, please run 'Load initial base' if it's first run or you've deleted the files");
            }

            print("Initialization complete");
        }catch(IOException e){
            print("Problems with config loading");
            e.printStackTrace();
        }
    }

    private static void loadUpdate() throws IOException {
        if(fullResult != null){

            Calendar cal = Calendar.getInstance();
            cal.setTime(fullResult.getLastUpdate());
            cal.add(Calendar.DATE, -1);
            Date dateFrom = cal.getTime();

            Date currentDate = new Date();
            long startTime = System.currentTimeMillis();
            if(champs != null){
                WildStatParser wsp = new WildStatParser();
                List<MatchResult> results = wsp.parseResults(champs, dateFrom);
                /*fullResult = new FinalResult();
                fullResult.setLastUpdate(lastUpdate);
                fullResult.setMatchResults(results);*/

                List<MatchResult> recentResults = new ArrayList<MatchResult>();
                List<MatchResult> fullResults = fullResult.getMatchResults();
                //check if update not presented in full result
                for(MatchResult result : results){
                    if(!fullResults.contains(result)){
                        recentResults.add(result);
                        fullResults.add(result);
                    }
                }

                //print("fullResults size: %s", fullResults.size());
                //print("fullResult matches size: %s", fullResult.getMatchResults().size());
                fullResult.setLastUpdate(currentDate);
                saveResultsToFile(BIG_OUTPUT_FILE, fullResult);
                saveResultsToFile(RECENT_OUTPUT_FILE, recentResults);
                print(recentResults.size() + " matches loaded");
                print("Loading time: %s seconds", (System.currentTimeMillis() - startTime) / 1000 );

                /*for(MatchResult r : results){
                    print("%s   %s - %s  %s:%s", r.getGameDate(), r.getTeam1(), r.getTeam2(), r.getScore1(), r.getScore2());
                }*/
            }else{
                print("Config not loaded!");
            }
        }else{
            print("Please run 'Load initial base' first");
        }
    }


    private static void exportToExcel() throws IOException, SAXException {
        if(fullResult != null){
            createExcel(XLS_FILE, fullResult.getMatchResults(), recentMatchResults);
            print("export complete");
        }else{
            print("Please run 'Load initial base' first");
        }
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

    public static void saveResultsToFile(String fileName, Object matchResults){
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

    private static FinalResult loadResultsFromFile(String fileName) throws IOException {
        FinalResult finalResult = new FinalResult();
        List<MatchResult> results = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        JsonFactory f = new MappingJsonFactory();


        JsonParser jp = f.createJsonParser(new File(fileName));
        JsonToken current;
        current = jp.nextToken();
        if (current != JsonToken.START_ARRAY) {
            if(current != JsonToken.START_OBJECT){
                System.out.println("Error: root should be array or object: quiting.");
                return finalResult;
            }
            while (current != JsonToken.START_ARRAY) {
                current = jp.nextToken();
                if(JsonToken.FIELD_NAME.equals(current)){
                    String fieldName = jp.getCurrentName();
                    current = jp.nextToken();

                    if("lastUpdate".equals(fieldName)){
                        try{
                            finalResult.setLastUpdate(new Date(jp.getValueAsLong()));
                        }catch(Exception e){
                            print("wrong date in a file");
                        }
                    }
                }
            }
            //System.out.println("Error: root should be array: quiting.");
            //return finalResult;
        }

        while (jp.nextToken() != JsonToken.END_ARRAY) {
            JsonNode node = jp.readValueAsTree();
            results.add(mapper.treeToValue(node, MatchResult.class));
        }

        finalResult.setMatchResults(results);

        return finalResult;
    }


    public static void createExcel(String xlsFile, List<MatchResult> fullResults) throws IOException, SAXException {
        createExcel(xlsFile, fullResults, null);
    }

    public static void createExcel(String xlsFile, String bigJsonFile, String updateJsonFile) throws IOException, SAXException {
        createExcel(xlsFile, loadResultsFromFile(bigJsonFile).getMatchResults(), loadResultsFromFile(updateJsonFile).getMatchResults());
    }


    public static void createExcel(String xlsFile, List<MatchResult> fullResults, List<MatchResult> recentResults) throws IOException, SAXException {
        if(fullResults != null){
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
        }else{
            print("Please run 'Load initial base' first");
        }
    }

}
