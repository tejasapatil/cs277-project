package com.tp.business;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class TrainBusinessData {

  final static Charset ENCODING = StandardCharsets.UTF_8;

  public static void main(String[] args) {
    JSONParser parser = new JSONParser();
    JSONObject jsonObject;

    try {
      String indexFileName = "/media/tejas/Trunk/SkyDrive/277/Kaggle/data/train/indexes/business.index";
      Path indexPath = Paths.get(indexFileName);
      HashMap<String, String> mapping = new HashMap<String, String>();
      Scanner scanner =  new Scanner(indexPath, ENCODING.name());

      while (scanner.hasNextLine()) { 
        String currLine = scanner.nextLine();
        int index = currLine.indexOf(':');
        mapping.put(currLine.substring(0,index-1), currLine.substring(index+1));
      }
      scanner.close();
      BufferedWriter indexWriter = Files.newBufferedWriter(indexPath, ENCODING);
      
      String filename = "/media/tejas/Trunk/SkyDrive/277/Kaggle/data/train/original/business.json";
      String outfilename = "/media/tejas/Trunk/SkyDrive/277/Kaggle/data/train/reduced/business.json";
      Path path = Paths.get(filename);
      Path path2 = Paths.get(outfilename);

      BufferedWriter writer = Files.newBufferedWriter(path2, ENCODING);
      scanner =  new Scanner(path, ENCODING.name());

      while (scanner.hasNextLine()) { 
        jsonObject = (JSONObject) parser.parse(scanner.nextLine().trim());

        if(mapping.containsKey(jsonObject.get("business_id").toString()) != true) {
          mapping.put(jsonObject.get("business_id").toString(), String.valueOf(mapping.size()));
          indexWriter.write(jsonObject.get("business_id").toString() + ":" + String.valueOf(mapping.size()-1));
          indexWriter.newLine();
          indexWriter.flush();
        }

        System.out.println(jsonObject.get("business_id").toString() +" -> " + mapping.get(jsonObject.get("business_id").toString()));
        String business_id = "{\"business_id\": " + mapping.get(jsonObject.get("business_id").toString()) + ", ";

        JSONArray categories = (JSONArray) jsonObject.get("categories");
        String category = "\"category\": 3, ";

        if(categories.size() > 0) {
          int i = 0;
          for(; i < categories.size()-1; i++) {
            if(categories.get(i).toString().compareTo("Restaurants") == 0) {
              category = "\"category\": 1, ";
              break;
            } else if(categories.get(i).toString().compareTo("Shopping") == 0) {
              category = "\"category\": 2, ";
              break;
            } else
              category = "\"category\": 3, ";
          }
        }

        String open = "\"open\"" + ": " + (jsonObject.get("open").toString().compareTo("true") == 0 ? "1" : "0") + ", ";
        String review_count = "\"review_count\"" + ": " + jsonObject.get("review_count").toString() + ", ";
        String stars = "\"stars\"" + ": " + (String) jsonObject.get("stars").toString();

        String line = business_id + open + category + review_count + stars + "}";
        //System.out.println(line);
        writer.write(line);
        writer.newLine();
        writer.flush();
      }
      scanner.close();
      writer.close();
      indexWriter.close();
    } catch (FileNotFoundException e1) {
      e1.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ParseException e) {
      e.printStackTrace();
    }
  }
}