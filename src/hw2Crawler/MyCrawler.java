package hw2Crawler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.http.HttpStatus;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class MyCrawler extends WebCrawler {
 private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg" + "|png|mp3|mp3|zip|gz))$");
 private Set<String> urls = new HashSet<String>();
 private int notfound = 0;
 private int penalty = 0;
 private int totoal_forb = 0;
 private int total_unauth = 0;
 private int total_ok = 0;
 private static int indomain = 0;
 private static int notindomain = 0;
 private String domain = getDomainName("https://www.latimes.com");
 
 
 
 
 @Override
 public boolean shouldVisit(Page referringPage, WebURL url) {
  String href = url.getURL().toLowerCase(); 
  return !FILTERS.matcher(href).matches() && href.startsWith("https://www.latimes.com");
 }
 
 
 
 @Override
 public void visit(Page page) {
  String url = page.getWebURL().getURL(); 
//  System.out.println("URL: " + url);
  if (page.getParseData() instanceof HtmlParseData) {
   HtmlParseData htmlParseData = (HtmlParseData) page.getParseData(); String text = htmlParseData.getText();
   String html = htmlParseData.getHtml();
   
   Set<WebURL> links = htmlParseData.getOutgoingUrls();

   int statusCode = page.getStatusCode();
//   urls.add(url + "," + statusCode);
//   System.out.println("urls len: " + urls.size()); 
//   System.out.println("Text length: " + text.length()); 
//   System.out.println("Html length: " + html.length()); 
//   System.out.println("Number of outgoing links: " + links.size());
//   write_fetch();
   for (WebURL link: links) {
	   writeURLsToFile(link.getURL(), domain);
	   
   }

   if (statusCode == 200) {
   write_visit(page, url);
   }
   
  } 
  
 }
 public Set<String> getResultList() {
	 return this.urls;
 }
 
 @Override
 protected void handlePageStatusCode(WebURL webUrl, int statusCode, String statusDescription) {
  System.out.println("here is the weburl" + webUrl);
     if (statusCode != HttpStatus.SC_OK) {
      
         if (statusCode == HttpStatus.SC_NOT_FOUND) {
        	 notfound ++;
         } else if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY){
        	 penalty++;
         } else if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
        	 total_unauth++;
         } else {
          totoal_forb++;
         }
     } else {
      total_ok++;
     }
     write_fetch(webUrl, statusCode);
//     writeURLsToFile(webUrl.getURL(), domain);
     
 }
 
 @Override
 public void onBeforeExit() {
//	 System.out.println(urls);
//	 System.out.println(domain);
//	 write_fetch();
	 write_statusCount();
 }
 
// public void write_fetch() {
//	 File file = new File("fetch_latimes.csv");
//	 boolean isFileExist = file.exists() && !file.isDirectory();
//     try(PrintWriter writer = new PrintWriter(new FileWriter(file, true))){
//    	 if (!isFileExist) {
//     writer.println("URL,Status");}
//     for (String url : urls) {
//       writer.println(url);
//         }
//    writer.flush();
//    writer.close();
//     } catch (IOException e) {
//		e.printStackTrace();
//	}
//     	 
// }
 
 public void write_fetch(WebURL url, int StatusCode) {
     File file = new File("fetch_latimes.csv");
     boolean isFileExist = file.exists() && !file.isDirectory();
     try (PrintWriter writer = new PrintWriter(new FileWriter(file, true))) {
         if (!isFileExist) {
             writer.println("URL,Status");
         }
         writer.println(url + "," + StatusCode);
        
         writer.flush();
     } catch (IOException e) {
         e.printStackTrace();
     }
 }
 
 public void write_visit(Page page,String url) {
	 
	 int size = page.getContentData().length;
	 File file = new File("visit_latimes.csv");
     Set<WebURL> outLinks = page.getParseData().getOutgoingUrls();
     String contentType = page.getContentType().split(";")[0];
     try(PrintWriter writer = new PrintWriter(new FileWriter(file, true))){
     writer.printf("%s,%d,%d,%s%n", url, size, outLinks.size(), contentType);
     writer.flush();
     writer.close();
     } catch (IOException e) {
		e.printStackTrace();
	}

 }
 
 public void write_statusCount() {
	 File file = new File("stats_latimes.csv");
	 boolean isFileExist = file.exists() && !file.isDirectory();
     try(PrintWriter writer = new PrintWriter(new FileWriter(file, true))){
    	 if (!isFileExist) {
    	     writer.println("OK, NotFound,Penalty,Unauth, Forb, INdomain, Notindomain");}
     writer.printf("%d,%d,%d,%d,%d, %d, %d%n", total_ok, notfound, penalty, total_unauth, totoal_forb, indomain, notindomain);
     writer.flush();
     writer.close();
     } catch (IOException e) {
		e.printStackTrace();
	}

 }
 
 private static String getDomainName(String url) {
	    try {
	        URI uri = new URI(url);
	        String domain = uri.getHost();
	        if (domain != null) {
	            return domain.startsWith("www.") ? domain.substring(4) : domain;
	        }
	    } catch (URISyntaxException e) {
	        e.printStackTrace();
	    }
	    return "";
	}
 private static void writeURLsToFile(String u, String domain) {
	    String mydomain = null;
	    try {
	        URI uri = new URI(u);
	        mydomain = uri.getHost();
	        if (mydomain != null) {
	            mydomain = mydomain.startsWith("www.") ? mydomain.substring(4) : mydomain;
	        }
	    } catch (URISyntaxException e) {
	        e.printStackTrace();
	    }
//	    System.out.println("show mydomin "+ mydomain +"/n");
	    try (PrintWriter writer = new PrintWriter(new FileWriter("OkvsNot.csv", true))) {
	        System.out.println("seen: " + u);
	        if (mydomain != null && mydomain.startsWith(domain)) {
	            writer.println(u + ",OK");
	            indomain ++;
	        } else {
	            writer.println(u + ",N_OK");
	            notindomain++;
	        }

	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

// 
// private static void writeURLsToFile(String u, String domain) {try {
//     URI uri = new URI(u);
//     String mydomain = uri.getHost();
//     if (mydomain != null) {
//    	 mydomain= mydomain.startsWith("www.") ? domain.substring(4) : domain;
//     }
// } catch (URISyntaxException e) {
//     e.printStackTrace();
// }
// 
//	    try (PrintWriter writer = new PrintWriter(new FileWriter("OkvsNot.csv", true))) {
//	    	System.out.println("seen: " + u); 
//	            if (mydomain.startsWith(domain)) {
//	                writer.println(u + ",OK");
//	            } else {
//	                writer.println(u + ",N_OK");
//	            }
//	        
//	    } catch (IOException e) {
//	        e.printStackTrace();
//	    }
//	}
// 
 
}
