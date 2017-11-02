package cn.zr;

import java.util.List;


import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;


public class BXPageProcessor implements PageProcessor{

	// 抓取网站的相关配置，包括编码、抓取间隔、重试次数等
	private Site site = Site.me().setRetryTimes(3).setSleepTime(1000).setTimeOut(10000);
	private static int count =0;

	public void process(Page page) {
		
		if(page.getUrl().regex("/xuetang$").match()){
			List<String> urls = page.getHtml().xpath("//div[@class='r-side']/div[@class='hot-product hot-label']/div[@class='con']").links().all();
			page.addTargetRequests(urls);
		}

		if(page.getUrl().regex("/[\\w]+/[\\w]+/$").match()){
			List<String> list = page.getHtml().xpath("//div[@class='crumbs clearfix']/a/text()").all();
			page.putField("name",list.get(list.size()-1));
			//获取当前页的url
			String url = page.getUrl().toString();
			//获取有多少页
			String pageNum = page.getHtml().xpath("//div[@class='pagenum']/text()").toString();
			pageNum = pageNum.trim();
			pageNum = pageNum.substring(1, pageNum.length() - 1);
			for (int i = 1; i <= Integer.parseInt(pageNum) ; i++) {
				page.addTargetRequest(url + i + ".html");
			}
		}

		if(page.getUrl().regex("/[\\w\\-]+/[\\w\\-]+/[1-9]+.html$").match()) {
			List<String> links = page.getHtml().xpath("//div[@class='l-side']/div[@class='newall']/div[@class='conone']/div[@class='zhincon']/ul[1]/li/h3").links().all();
			page.addTargetRequests(links);
		}

		if(page.getUrl().regex("/[\\w\\-]+/[0-9]+/[\\w\\-]+.html").match()){
			page.putField("标题", page.getHtml().xpath("//div[@class='article']/h1/text()").toString());
			page.putField("内容", page.getHtml().xpath("//div[@class='wenfont']/p/text()").all().toString());
		}
		
    }
    
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {

		long startTime, endTime;
		System.out.println("开始爬取...");
		startTime = System.currentTimeMillis();
        Spider.create(new BXPageProcessor())
        				.addUrl("https://www.bxd365.com/xuetang")
        				.thread(5)
        				.run();
		endTime = System.currentTimeMillis();
		System.out.println("爬取结束，耗时约" + ((endTime - startTime) / 1000) + "秒，抓取了"+count+"条记录");
    }
}
