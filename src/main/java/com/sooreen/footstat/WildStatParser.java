package com.sooreen.footstat;

import com.sooreen.footstat.po.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class WildStatParser extends AbstractStatParser{
    // http://wildstat.ru/p/2301/ch/ENG_1_2012_2013/stg/all/tour/last
    // http://wildstat.ru/p/2301/ch/ENG_1_2012_2013/stg/all/tour/all
    private static final String BASE_URL = "http://wildstat.ru/p/%s/ch/%s_%s_%s/stg/all/tour/pld";
    //private static final String UPDATE_URL = "http://wildstat.ru/p/%s/ch/%s_%s_%s/stg/all/tour/pld";
    private static final String INPUT_DATE_FORMAT = "dd.MM.yyyy";
    private static final String OUTPUT_DATE_FORMAT = "yyyy-MM-dd";
    private static SimpleDateFormat inSdf = new SimpleDateFormat(INPUT_DATE_FORMAT);
    private static SimpleDateFormat outSdf = new SimpleDateFormat(OUTPUT_DATE_FORMAT);

    static{
        inSdf.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
    }


    @Override
    public List<MatchResult> parseResults(List<Championate> champs) throws IOException {
        ArrayList<MatchResult> results = new ArrayList<MatchResult>();



        //loop over champs/leagues/seasons to get data
        for(Championate champ : champs){
            for(League league : champ.getLeagues()) {
                for(Season season : champ.getSeasons()) {
                    Document doc = Jsoup.connect(
                                        prepareUrl(champ.getId()/*+league.getId()*/,
                                                champ.getAbbr(),
                                                league.getId(),
                                                season.getId()
                                    ))
                                    .timeout(10000)
                                    .get();
                    List<ResultLine> lines = parseDocument(doc);
                    for(ResultLine line : lines) {
                        MatchResult matchResult = new MatchResult();
                        matchResult.setChampId(champ.getId());
                        matchResult.setChampName(champ.getName());
                        matchResult.setGameDate(outSdf.format(line.getDate()));
                        matchResult.setLeagueId(league.getId());
                        matchResult.setLeagueName(league.getName());
                        matchResult.setScore1(line.getScore1());
                        matchResult.setScore2(line.getScore2());
                        matchResult.setSeasonId(season.getId());
                        matchResult.setSeasonName(season.getName());
                        matchResult.setTeam1(line.getTeam1());
                        matchResult.setTeam2(line.getTeam2());
                        results.add(matchResult);
                    }
                }
            }
        }

        return results;
    }

    public List<MatchResult> parseResults(List<Championate> champs, Date dateFrom) throws IOException {
        ArrayList<MatchResult> results = new ArrayList<MatchResult>();
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateFrom);
        int yearFrom = cal.get(Calendar.YEAR);


        //loop over champs/leagues/seasons to get data
        for(Championate champ : champs){
            for(League league : champ.getLeagues()) {
                for(Season season : champ.getSeasons()) {
                    //select only last season
                    if(season.getId().contains(String.valueOf(yearFrom))){
                        Document doc = Jsoup.connect(
                                            prepareUrl(champ.getId()/*+league.getId()*/,
                                                    champ.getAbbr(),
                                                    league.getId(),
                                                    season.getId()
                                        ))
                                        .timeout(10000)
                                        .get();
                        List<ResultLine> lines = parseDocument(doc);
                        for(ResultLine line : lines) {
                            //check date is after dateFrom
                            if(line.getDate().after(dateFrom)){
                                MatchResult matchResult = new MatchResult();
                                matchResult.setChampId(champ.getId());
                                matchResult.setChampName(champ.getName());
                                matchResult.setGameDate(outSdf.format(line.getDate()));
                                matchResult.setLeagueId(league.getId());
                                matchResult.setLeagueName(league.getName());
                                matchResult.setScore1(line.getScore1());
                                matchResult.setScore2(line.getScore2());
                                matchResult.setSeasonId(season.getId());
                                matchResult.setSeasonName(season.getName());
                                matchResult.setTeam1(line.getTeam1());
                                matchResult.setTeam2(line.getTeam2());
                                results.add(matchResult);
                            }
                        }
                    }
                }
            }
        }

        return results;
    }


    @Override
    public String prepareUrl(String ... args) {
        return String.format(BASE_URL, (Object [])args);
    }

    @Override
    public List<ResultLine> parseDocument(Document doc) {
        ArrayList<ResultLine> results = new ArrayList<ResultLine>();


        Elements resultRows = doc.select("table.championship > tbody > tr");             /*:nth-child(2n+1)*/
        //http://wildstat.ru/p/2001/ch/RUS_1_2012_2013/stg/all/tour/all
        if (resultRows != null) {
            for (Element result : resultRows) {
                Elements cells = result.select("td[valign=middle]");
                if(cells.size()>0) {
                    String dateStr = cells.get(0).child(0).text();
                    Date date = null;
                    try {
                        date = inSdf.parse(dateStr);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    String t1 = cells.get(2).text();
                    String t2 = cells.get(4).text();
                    String score = cells.get(6).child(0).child(0).child(0).text();
                    String[]s = score.split(":");
                    results.add(new ResultLine(date, t1, t2, s[0], s[1]));
                    //print("%s / %s - %s / %s", date, t1, t2, score);
                    //print("%s / %s / %s / %s - %s / %s", s.getcName(), s.getlName(), s.getsName(), teamMap.get(t1), teamMap.get(t2), result.text());
                }
            }
        }

        return results;
    }
}
