/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jobcrawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
    Jobs job;
    
    public static void main(String[] args) throws MalformedURLException, IOException  {
        job_crawler jcrawl = new job_crawler();
        
        jcrawl.crawlAllJobs();
        for(int i=0; i < jobViewKeys.size(); i++){
//             jcrawl.crawlQualifications(jobViewKeys.get(i));
               jcrawl.crawlJobTitle(jobViewKeys.get(i));
              
        }
        
//        jcrawl.general_crawler("http://www.jobstreet.com.ph/en/job/6192918?fr=21&src=16&srcr=16");
    }
    
    //just like the crawlAllPassers
    public void crawlAllJobs() throws IOException{
        int page;
        String pre = "http://www.jobstreet.com.ph/en/job-search/job-vacancy.php?"
                + "key=Computer+Science&area=1&option=1&job-source=1%2C64&classified=1&job-"
                + "posted=0&sort=2&order=0&pg=";
        String post = "&src=16&srcr=16";
        String url = "";
        
        for(int i = 1; i <= 1; i++ ){
            page = i;
            url = pre+page+post;
            System.out.println("Crawling page " + page +"...");
            crawlSpecificJob(url);
            System.out.println("done");
        }
    }
    
    /* function for a general crawler and it accepts a string to 
       be converted to a URL.
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
    
    public void crawlJobTitle(String viewKey){
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
//                    return title[0];
                }
                if(s.contains(endS)){
                    end = true;
                }
                s = br.readLine();
            }
        }catch (MalformedURLException ex) {
            Logger.getLogger(JobCrawler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(JobCrawler.class.getName()).log(Level.SEVERE, null, ex);
        } 
//        return null;
    }
    
    public void crawlSkills(String jobTitle, String url) throws IOException{
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
        
        while(s != null){
            if(s.contains(validInfo)){
                System.out.println(s);
            }
            if(s.contains(endS))
                end = true;
            
            s = br.readLine();
        }
    }
    
    public void crawlQualifications(String viewKey){
        String pre = "http://www.jobstreet.com.ph/en/job/";
        String post = "?fr=21&src=16&srcr=16";
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
//                    s = getContentsOnly(s);
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
        
    public static String getViewLink(String s){
        String pre = "href=\"http://www.jobstreet.com.ph/en/job/";
        String post = "\"?fr=21&src=16&srcr=16";
        
        String[] s1 = s.split(pre);
        String[] s2 = s1[1].split(post);
        String s3 = s2[0];
        String[] result = s3.split("[?]");
        return result[0];
    }
}
