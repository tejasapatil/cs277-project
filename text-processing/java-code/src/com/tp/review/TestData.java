package com.tp.review;

import java.util.Date;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public class TestData extends Base {
  
  static Logger logger = Logger.getLogger(TestData.class);
  
  public String processRecord(String record) {
    
    JSONObject jsonObject = null;
    try {
      jsonObject = (JSONObject) parser.parse(record);
    } catch (ParseException e) {
      logger.info("Error while parsing record : " + record);
      e.printStackTrace();
      System.exit(-1);
    }

    String line = "";
    line += "{\"review_id\": " + rmapping.get(jsonObject.get("review_id").toString()) + ", ";
    
    line += "\"business_id\": " + ((bmapping.containsKey(jsonObject.get("business_id").toString()) == true) ?
                                   (bmapping.get(jsonObject.get("business_id").toString()) + ", ") : "-1, ");
    
    line += "\"user_id\": " + ((umapping.containsKey(jsonObject.get("user_id").toString()) == true) ?
                               (umapping.get(jsonObject.get("user_id").toString()) + ", ") : "-1, ");

    try {
      Date end = format.parse(jsonObject.get("date").toString());
      line += "\"age\"" + ": " + String.valueOf((getStartDate("2013-03-12").getTime() - end.getTime()) / (24 * 60 * 60 * 1000)) + ", ";
    } catch (java.text.ParseException e) {
      System.err.println("Error while parsing date for record : " + record);
      System.exit(-1);
      e.printStackTrace();
    }
    
    line += "\"useful_votes\": -1, ";
    line += "\"stars\"" + ": " + (String) jsonObject.get("stars").toString() + ", ";
    
    String[] splits = jsonObject.get("text").toString().toLowerCase().replaceAll("[^a-z]"," ").split("\\s+");
    line += "\"text_wc\"" + ": " + String.valueOf(splits.length);
    
    StringBuilder stemmedText = new StringBuilder();
    for(String term : splits) {
      stemmer.setCurrent(term);
      stemmer.stem();
      stemmedText.append(stemmer.getCurrent() + " ");
    }
    
    String stemmedLine = stemmedText.toString();
    Iterator<String> iterator = popularWords.iterator();
    
    while(iterator.hasNext()) {
      String next = iterator.next();
      if(stemmedLine.contains(next)) { 
        line +=  ", \"" + next + "\": 1";
      } else {
        line +=  ", \"" + next + "\": 0";
      }
    }
    line +=  "}";    
    return line;
  }
  
  public static void main(String[] args) {
    TestData temp = new TestData();
    temp.run(args, logger);
  }
}