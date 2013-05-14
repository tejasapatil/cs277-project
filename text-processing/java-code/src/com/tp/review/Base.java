package com.tp.review;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.json.simple.parser.JSONParser;
import org.tartarus.snowball.ext.PorterStemmer;

public abstract class Base {
  protected final static Charset ENCODING = StandardCharsets.UTF_8;
  protected final static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");  
  
  protected static JSONParser parser = new JSONParser();
  protected static PorterStemmer stemmer = new PorterStemmer();
  
  protected HashSet<String> popularWords = new HashSet<String>();
  protected HashMap<String, String> bmapping = new HashMap<String, String>();
  protected HashMap<String, String> umapping = new HashMap<String, String>();
  protected HashMap<String, String> rmapping = new HashMap<String, String>();
  
  protected String bindexFileName = null; 
  protected String uindexFileName = null;
  protected String rindexFileName = null;
  protected String outfilename = null;
  protected String inputfilename = null;
  protected String freqWordsFilename = null;
 
  static Logger logger = Logger.getLogger(Base.class);
  
  protected static Date getStartDate(String date) {
    Date start = null;
    
    try {
      start = format.parse(date);
    } catch (java.text.ParseException e) {
      e.printStackTrace();
    } 
    return start;
  }
  
  public void parseArguments(String[] args) {
    
    logger.info("Processing input arguments");
    for(int i = 0 ; i < args.length;) {
      if(args[i].compareTo("-bindex") == 0) {
        bindexFileName = args[++i];
        i++;
      } if(args[i].compareTo("-uindex") == 0) {
        uindexFileName = args[++i];
        i++;
      } if(args[i].compareTo("-rindex") == 0) {
        rindexFileName = args[++i];
        i++;
      } if(args[i].compareTo("-freqwords") == 0) {
        freqWordsFilename = args[++i];
        i++;
      } if(args[i].compareTo("-input") == 0) {
        inputfilename = args[++i];
        i++;
      } if(args[i].compareTo("-output") == 0) {
        outfilename = args[++i];
        i++;
      }
    }
  }
  
  public void populateStopWords() {

    try {
      logger.info("Populating stop words");
      Scanner scanner = new Scanner(Paths.get(freqWordsFilename), ENCODING.name());
      
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine().trim();
        popularWords.add(line);
      }
      
      scanner.close();
    } catch (IOException e) {
      logger.info("Error reading file " + freqWordsFilename);
      e.printStackTrace();
      System.exit(-1);
    }
  }
  
  public void populateMapping(String indexFileName, HashMap<String, String> mapping) {
    try {
      logger.info("Populating index mappings on " + indexFileName);
      Scanner scanner;
      scanner = new Scanner(Paths.get(indexFileName), ENCODING.name());
      
      while (scanner.hasNextLine()) { 
        String currLine = scanner.nextLine().trim();
        int index = currLine.indexOf(':');
        mapping.put(currLine.substring(0,index), currLine.substring(index+1));
      }
      scanner.close();
    } catch (IOException e) {
      logger.info("Error reading file " + indexFileName);
      e.printStackTrace();
      System.exit(-1);
    }   
  }
  
  public String processRecord(String record) {
    logger.info("Error processRecord of base called !!");
    return null;
  }
  
  public void run(String[] args, Logger logger) {
    
   Base.logger = logger;
    
    if(args.length != 12) {
      System.err.println("Invalid number of arguments !!");
      System.exit(-1);
    }
    
    parseArguments(args);
    populateStopWords();
    populateMapping(bindexFileName, bmapping);
    populateMapping(uindexFileName, umapping);
    populateMapping(rindexFileName, rmapping);
    
    try {
      Scanner scanner =  new Scanner(Paths.get(inputfilename), ENCODING.name());
      BufferedWriter writer = Files.newBufferedWriter(Paths.get(outfilename), ENCODING);
      
      logger.info("Processing input file " + inputfilename);
      while (scanner.hasNextLine()) { 
        writer.write(processRecord(scanner.nextLine().trim()));
        writer.newLine();
        writer.flush();
      }
      scanner.close();
      writer.close();

      logger.info("Output written to file " + outfilename);
    } catch (FileNotFoundException e1) {
      e1.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } 
  }
}
