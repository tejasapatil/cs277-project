package com.tp.user;

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

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class TrainUserData {

  final static Charset ENCODING = StandardCharsets.UTF_8;

  public static void main(String[] args) {
    JSONParser parser = new JSONParser();
    JSONObject jsonObject;

    try {
      String indexFileName = "/media/tejas/Trunk/SkyDrive/277/Kaggle/data/train/indexes/user.index";
      Path indexPath = Paths.get(indexFileName);
      HashMap<String, String> mapping = new HashMap<String, String>();
      Scanner scanner =  new Scanner(indexPath, ENCODING.name());

      while (scanner.hasNextLine()) { 
        String currLine = scanner.nextLine();
        int index = currLine.indexOf(':');
        mapping.put(currLine.substring(0,index), currLine.substring(index+1));
      }
      scanner.close();
      BufferedWriter indexWriter = Files.newBufferedWriter(indexPath, ENCODING);
      
      String filename = "/media/tejas/Trunk/SkyDrive/277/Kaggle/data/train/original/user.json";
      scanner =  new Scanner(Paths.get(filename), ENCODING.name());
      
      String outfilename = "/media/tejas/Trunk/SkyDrive/277/Kaggle/data/train/reduced/user.json";
      BufferedWriter writer = Files.newBufferedWriter(Paths.get(outfilename), ENCODING);

      while (scanner.hasNextLine()) { 
        jsonObject = (JSONObject) parser.parse(scanner.nextLine().trim());

        if(mapping.containsKey(jsonObject.get("user_id").toString()) != true) {
          mapping.put(jsonObject.get("user_id").toString(), String.valueOf(mapping.size()));
          indexWriter.write(jsonObject.get("user_id").toString() + ":" + String.valueOf(mapping.size()-1));
          indexWriter.newLine();
          indexWriter.flush();
        }

        System.out.println(jsonObject.get("user_id").toString() +" -> " + mapping.get(jsonObject.get("user_id").toString()));
        String user_id = "{\"user_id\": " + mapping.get(jsonObject.get("user_id").toString()) + ", ";

        JSONObject votes = (JSONObject) jsonObject.get("votes");
        String usefulVotes = "\"useful_votes\": " + votes.get("useful").toString() + ", ";
        String review_count = "\"review_count\"" + ": " + jsonObject.get("review_count").toString() + ", ";
        String stars = "\"avg_stars\"" + ": " + jsonObject.get("average_stars").toString();

        String line = user_id + usefulVotes + review_count + stars + "}";
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