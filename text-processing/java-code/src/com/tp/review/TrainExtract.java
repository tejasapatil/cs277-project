package com.tp.review;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class TrainExtract {

  final static Charset ENCODING = StandardCharsets.UTF_8;
  
  public static void main(String[] args) {
    JSONParser parser = new JSONParser();
    JSONObject jsonObject;

    try {
      String filename = "/media/tejas/Trunk/SkyDrive/277/Kaggle/data/train/original/review.json";
      Scanner scanner =  new Scanner(Paths.get(filename), ENCODING.name());

      String outfilename = "/media/tejas/Trunk/SkyDrive/277/Kaggle/data/train/reduced/review.votes.txt";
      BufferedWriter writer = Files.newBufferedWriter(Paths.get(outfilename), ENCODING);

      while (scanner.hasNextLine()) { 
        jsonObject = (JSONObject) parser.parse(scanner.nextLine().trim());

        JSONObject votes = (JSONObject) jsonObject.get("votes");
        String usefulVotes = votes.get("useful").toString();
        String text = (String) jsonObject.get("text").toString().toLowerCase().replaceAll("[^a-z]"," ");
        writer.write(usefulVotes + " " + text);
        writer.newLine();
        writer.flush();
      }
      scanner.close();
      writer.close();
    } catch (FileNotFoundException e1) {
      e1.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ParseException e) {
      e.printStackTrace();
    }
  }
}