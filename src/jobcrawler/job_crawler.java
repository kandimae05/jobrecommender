/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jobcrawler;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author candymae
 */
public class job_crawler {
    private static ArrayList<String> jobViewKeys = new ArrayList<String>();
   
    public static void main(String[] args) throws MalformedURLException, IOException, NoSuchFieldException  {
        job_crawler jcrawl = new job_crawler();
        
        jcrawl.crawlAllJobs();
        for(int i=0; i < jobViewKeys.size(); i++){
          jcrawl.crawlJobTitle(jobViewKeys.get(i));
        }
    }
    
    //just like the crawlAllPassers
    public void crawlAllJobs() throws IOException, NoSuchFieldException{
        int page;
        String pre = "http://www.jobstreet.com.ph/en/job-search/job-vacancy.php?"
                + "key=Computer+Science&area=1&option=1&job-source=1%2C64&classified=1&job-"
                + "posted=0&sort=2&order=0&pg=";
        String post = "&src=16&srcr=16";
        String url = "";
        
        /*For Education Jobs*/
//        String pre = "http://www.jobstreet.com.ph/en/job-search/job-vacancy.php?"
//                + "key=education&area=1&option=1&job-source=1%2C64&classified=1&job-"
//                + "posted=0&sort=2&order=0&pg=";
//        String post = "&src=16&srcr=12";
        
        for(int i = 101; i <= 205; i++){
            page = i;
            url = pre+page+post;
            System.out.println("Crawling page " + page +"...");
            crawlSpecificJob(url);
        }
    }
    
    /* function for a general crawler and it accepts a string to 
       be converted to a URL. This should return the string.
    */
    public void general_crawler(String url) throws IOException{
        URL u = new URL(url);
        HttpURLConnection httpcon = (HttpURLConnection) u.openConnection(); 
        httpcon.addRequestProperty("User-Agent", "Mozilla/4.76");  
        InputStream is= httpcon.getInputStream();
        InputStreamReader reader = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(reader);
        String s = "";
        
        while(null != (s = br.readLine())){
            System.out.println(s);
        } 
    }
    
    /*Crawls all the jobs in a page and store them in a list.*/
    public void crawlSpecificJob(String url){
        try {
            URL u = new URL(url);
            
            HttpURLConnection httpcon = (HttpURLConnection) u.openConnection(); 
            httpcon.addRequestProperty("User-Agent", "Mozilla/4.76"); 
            
            InputStream is= httpcon.getInputStream();
            InputStreamReader reader = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(reader);
            String s = br.readLine();
            
            int ctr = 0;
            boolean end = false;
            String validInfo = "position-title-link";
            String endS = "pagination_panel";
            
            while(s!=null){
                if(s.contains(endS)){
                    end = true;
                }
                if(s.contains(validInfo)){
                    ctr++;
                    s = getViewLink(s);
                    jobViewKeys.add(s);
                }
                s = br.readLine();
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(job_crawler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(job_crawler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void crawlJobTitle(String viewKey) throws NoSuchFieldException{
        String pre = "http://www.jobstreet.com.ph/en/job/";
        String post = "?fr=21&src=16&srcr=16";
        String url = "";
        
        url = pre+viewKey+post;
        try{
            URL u = new URL(url);
            
            HttpURLConnection httpcon = (HttpURLConnection) u.openConnection(); 
            httpcon.addRequestProperty("User-Agent", "Mozilla/4.76"); 
            
            InputStream is= httpcon.getInputStream();
            InputStreamReader reader = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(reader);
            String s = br.readLine();
            
            int ctr = 0;
            boolean end = false;
            String validInfo = "itemprop=\"title";
            String endS = "</h1>";
            
            while(s != null && end == false){
                if(s.contains(validInfo)){ // split the string to get the title
                    String pre_title = "<h1 id=\"position_title\" class=\"job-position\" itemprop=\"title\">";
                    String[] s1 = s.split(pre_title);
                     
                    String[] title = s1[1].split(endS);
                    System.out.println("Job title: " + title[0]);
                    crawlSkills(title[0], url);
                }
                if(s.contains(endS)){
                    end = true;
                }
                s = br.readLine();
            }
        }catch (MalformedURLException ex) {
            Logger.getLogger(job_crawler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(job_crawler.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
     
    public static String getViewLink(String s){
        String[] result;
        
        if(s.contains("ph/jobs")){
          String pre = "default/80/";
          String post = ".htm?fr=21&src=16&srcr=12";
          String[] s1 = s.split(pre);
          String[] s2 = s1[1].split(post);
          String s3 = s2[0];
          result = s3.split(".htm");
        }
        else{
          String pre = "href=\"http://www.jobstreet.com.ph/en/job/";
          String post = "\"?fr=21&src=16&srcr=12";
          String[] s1 = s.split(pre);
          String[] s2 = s1[1].split(post);
          String s3 = s2[0];
          result = s3.split("[?]");
        }
        return result[0];
    }
    
    public static String preClean(String jobTitle, String s) throws IOException, NoSuchFieldException{
      String pre = "<div itemprop=\"description\" class=\"unselectable wrap-text\" id=\"job_description\">";
      String[] s1 = s.split(pre);
      boolean end = false;
      System.out.println("s1[1]: " + s1[1]);
      return s1[1];
    }
    
    public static String removeTags(String currentLine){
      currentLine = currentLine.replaceAll("\\<.*?\\>", "");
      currentLine = currentLine.replaceAll("&nbsp;"," ");
      return currentLine;
    }
    
    public void postClean(String jobTitle, String currentLine) throws IOException, NoSuchFieldException{
      String delim = ">";
      String[] string = currentLine.split(delim);
      
      for(int i = 0; i < string.length;){
        if((string[i].contains("<")) || (string[i].contains(">"))){
          System.out.println("increment");
          i++;
        }
        else{
          System.out.println("write this: " + string[i]);
          fileWriting(jobTitle, string[i]);
          i++;
        }
      }
    }
    
    public void crawlSkills(String jobTitle, String url) throws IOException, NoSuchFieldException{
        URL u = new URL(url);
            
        HttpURLConnection httpcon = (HttpURLConnection) u.openConnection(); 
        httpcon.addRequestProperty("User-Agent", "Mozilla/4.76"); 
        
        InputStream is= httpcon.getInputStream();
        InputStreamReader reader = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(reader);
        String s = br.readLine();
        
        int ctr = 0;
        boolean end = false;
        String validInfo = "job_description";
        String endS = "<div class=\"panel panel-clean\">";
        String result = "";
        File file = null;
        
        while(s != null){
            if(s.contains(validInfo)){
                result = preClean(jobTitle, s);
                fileWriting(jobTitle, result);
            }
            if(s.contains(endS))
                end = true;
            
            s = br.readLine();
        }
    }
 
    public static void fileWriting(String jobTitle, String content) throws IOException, NoSuchFieldException{
      File dir = new File("C:\\Users\\DCS-SERVER.DCS-SERVER-PC\\Desktop\\Job_Crawler\\src\\data-cs"); //Static
      dir.mkdirs(); 
      
      File outputFile = new File(dir, jobTitle + ".txt");
      String[] temp = null;
      String result = null;
      boolean append = true;

      try{
          if(!outputFile.exists()){outputFile.createNewFile();}

          FileWriter fileWriter = new FileWriter(outputFile, append);
          BufferedWriter outStream= new BufferedWriter(fileWriter);

          String title = "Job Title: " + jobTitle;
          outStream.write(title);
          outStream.newLine();
          outStream.newLine();
          
          result = removeTags(content);
          outStream.write(result);
          outStream.newLine();
          outStream.close();
          append = false;
      }
      catch(IOException e){
          e.printStackTrace();
      }
    }
}
