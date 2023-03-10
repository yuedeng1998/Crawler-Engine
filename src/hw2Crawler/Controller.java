package hw2Crawler;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Set;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class Controller {

 public static void main(String[] args) throws Exception {
  // TODO Auto-generated method stub
  String crawlStorageFolder = "/Users/dengyue/eclipse-workspace/hw2Crawler/output"; 
  int numberOfCrawlers = 7;
  int maxPages = 2000;
  int maxDepth = 16;
  int politeDelay = 10;
  CrawlConfig config = new CrawlConfig(); 
  config.setCrawlStorageFolder(crawlStorageFolder);
  config.setMaxPagesToFetch(maxPages);
  config.setMaxDepthOfCrawling(maxDepth);
  config.setPolitenessDelay(politeDelay);
  
  /*
  * Instantiate the controller for this crawl. */
  PageFetcher pageFetcher = new PageFetcher(config);
  RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
  RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
  
  CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
  /*
  * For each crawl, you need to add some seed url. These are the first * URLs that are fetched and then the crawler starts following links
  * which are found in these pages
  */
  controller.addSeed("https://www.latimes.com"); /*
  * Start the crawl. This is a blocking operation, meaning that your code * will reach the line after this only when crawling is finished.
  */
  controller.start(MyCrawler.class, numberOfCrawlers);
  
 }

}
