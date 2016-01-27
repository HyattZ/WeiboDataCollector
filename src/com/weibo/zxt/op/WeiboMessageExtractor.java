package com.weibo.zxt.op;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.weibo.zxt.database.models.WeiboMessage;

/**
 * @author ��Ц��
 *
 * @time 2016��1��27��
 * 
 */
public class WeiboMessageExtractor {
	public List<WeiboMessage> extractWeiboMessage(String source){
		//System.out.println(source);
		List<WeiboMessage> weiboMessages = new ArrayList<WeiboMessage>();
		Document doc = Jsoup.parse(source);
			
		Elements eles = doc.select("div[class=WB_cardwrap WB_feed_type S_bg2]");
		for (Element ele:eles){
			String weiboHtml = ele.html();
			Document docWeibo = Jsoup.parse(weiboHtml);
			//����΢������
			Elements elesContent = docWeibo.select("div[class=WB_text W_f14]");
			if (elesContent != null && elesContent.size() == 1){
				System.out.println("Content:"+elesContent.get(0).text());
			}
			
			//����ʱ����Դ
			Elements elesTimeAndSource = docWeibo.select("div[class=WB_from S_txt2]");
			if (elesTimeAndSource != null && elesTimeAndSource.size() == 1){
				System.out.println("Time And Source:"+elesTimeAndSource.get(0).text());
			}else{
				System.out.println("Origin Time And Source:"+elesTimeAndSource.get(0).text());
				System.out.println("Time And Source:"+elesTimeAndSource.get(1).text());
			}
			
			//�����йص���ת������
			Elements elesStatusList = docWeibo.select("ul[class=WB_row_line WB_row_r4 clearfix S_line2]");
			Elements lis = elesStatusList.get(0).getElementsByTag("li");
			
			//�����ղ���
			if (lis.get(0).text().replace("�ղ�", "").trim() != ""){
				System.out.println("�ղ���:"+ lis.get(0).text().replace("�ղ�", "").trim());
			}else{
				System.out.println("�ղ���:0");
			}
			
			//����ת����
			if (lis.get(1).text().replace("ת��", "").trim() != ""){
				System.out.println("ת����:"+ lis.get(1).text().replace("ת��", "").trim());
			}else{
				System.out.println("ת����:0");
			}
			
			//����������
			if (lis.get(2).text().replace("����", "").trim() != ""){
				System.out.println("������:"+ lis.get(2).text().replace("����", "").trim());
			}else{
				System.out.println("������:0");
			}
			
			//����������
			if (lis.get(3).text().trim() != ""){
				System.out.println("������:"+ lis.get(3).text().trim());
			}else{
				System.out.println("������:0");
			}

			
			System.out.println("===============================");
		}

		return weiboMessages;
	}
	
}
