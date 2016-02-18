/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jobcrawler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author candymae
 */
public class JobCrawler {
    
    private static ArrayList<String> jobViewKeys = new ArrayList<String>();
    public static void main(String[] args)  {
        JobCrawler jcrawl = new JobCrawler();
        
        jcrawl.crawlAllJobs();
        for(int i = 0; i < jobViewKeys.size(); i++){
            jcrawl.crawlQualifications(jobViewKeys.get(i));
        }
        
//       try {
//            URL my_url = new URL("http://cebu.mynimo.com/jobs/browse/all?city=2-Cebu-City");
//            BufferedReader br = new BufferedReader(new InputStreamReader(my_url.openStream()));
//            String strTemp = "";
            
//            while(null != (strTemp = br.readLine())){
//                System.out.println("VIEWKEY IS: "+ jcrawl.getViewKey(strTemp));
//                System.out.println(strTemp);
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
    }
    
    //just like the crawlAllPassers
    public void crawlAllJobs(){
        int page;
        String pre = "http://cebu.mynimo.com/?q=&order=Job.starts&sort=DESC&page=";
        String post = "?city=2-Cebu-City";
        String url = "";
        
        for(int i = 1; i <= 1; i++ ){
            page = i;
            url = pre+page+post;
            System.out.println("Crawling page " + page +"...");
            crawlSpecificJob(url);
            System.out.println("done");
        }
    } 
    
    //Crawls all the jobs in a page and store them in a list.
    public void crawlSpecificJob(String url){
        try {
            URL u = new URL(url);
            
            HttpURLConnection httpcon = (HttpURLConnection) u.openConnection(); 
            httpcon.addRequestProperty("User-Agent", "Mozilla/4.76"); 
            
            InputStream is= httpcon.getInputStream();
            InputStreamReader reader = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(reader);
            String s = br.readLine();
            String endS = "Showing";
            int ctr = 0;
            boolean end = false;
            String validInfo = "/jobs/view/";
            
            while(s!=null){
                if(s.contains(endS)){
                    end = true;
                }
                if(s.contains(validInfo)){
                    ctr++;
                    s = getViewKey(s);
                    System.out.println("viewkey "+ctr+": "+s);
                    jobViewKeys.add(s);
                }
                s = br.readLine();
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(JobCrawler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(JobCrawler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
   
    public void crawlQualifications(String viewKey){
        String pre = "http://cebu.mynimo.com/jobs/view/";
        String post = "?city=2-Cebu-City";
        String url = "";
    
        url = pre+viewKey+post;
        System.out.println();
        System.out.println("========== CRAWLING JOB " + viewKey + " ==========");
        try {
            URL new_url = new URL(url);
            
            HttpURLConnection httpcon = (HttpURLConnection) new_url.openConnection(); 
            httpcon.addRequestProperty("User-Agent", "Mozilla/4.76"); 
 
            InputStream is= httpcon.getInputStream();
            InputStreamReader reader = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(reader);
            String s = br.readLine();
            String endS = "How to Apply";
            int ctr = 0;
            boolean end = false;
            String label = "<div><strong>";
            String validInfo = "<li>";
            String extra = "<a href";
            

            while(s!=null){
                if(s.contains(endS)){
                    end = true;
                }
                if((s.contains(validInfo) && !s.contains(extra)) || 
                    (s.contains(label) && !s.contains(extra))){
                    s = getContentsOnly(s);
                    System.out.println(s);
                }
                s = br.readLine();
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(JobCrawler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(JobCrawler.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
    public static String getViewKey(String s){
        String pre = "<a href=\"/jobs/view/";
        String post = "\" class=\"jobTitleLink\">";
        String[] s1 = s.split(pre);
        
        String[] s2 = s1[1].split(post);        
        return s2[0];
    }  
    
    public static String getContentsOnly(String s){
        String[] s1 = null;
        String[] s2 = null;
        String pre, post = null;
        
        if(s.contains("<div><strong>")){
            pre = "<div><strong>";
            post = "</strong></div>";
            
            s1 = s.split(pre);
            s2 = s1[1].split(post);
        }
        
        else if(s.contains("<li>")){
            pre = "<li>";
            post = "</li>";
            s1 = s.split(pre);
            s2 = s1[1].split(post);
        }
        return s2[0];
    }
    
    public void fileWriting(String content) throws IOException{
        File outputFile = new File("Jobs_Per_Page.txt");
        
        FileWriter fileWrite = null;
        BufferedWriter bufferedWriter = null;
            
        try{
            if(!outputFile.exists()){outputFile.createNewFile();}
     
            fileWrite = new FileWriter(outputFile);
            bufferedWriter = new BufferedWriter(fileWrite);
            bufferedWriter.write(content);
        }
        catch(IOException e){
            e.printStackTrace();
        }
        finally{
            if(bufferedWriter != null && fileWrite != null){
                try{
                    bufferedWriter.close();
                    fileWrite.close();
                }
                catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
    }
}
