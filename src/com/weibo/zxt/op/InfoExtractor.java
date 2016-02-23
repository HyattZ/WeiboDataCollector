package com.weibo.zxt.op;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.weibo.zxt.database.models.WeiboFollower;
import com.weibo.zxt.database.models.WeiboMessage;
import com.weibo.zxt.database.models.WeiboUser;

/**
 * @author ��Ц��
 *
 * @time 2016��1��27��
 * 
 */
public class InfoExtractor {
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
			
			//����ת��΢����ԭ����
			Elements eleOriginAuthor = docWeibo.select("div[class=WB_feed_expand] div[class=WB_expand S_bg1] div[class=WB_info]");
			if (eleOriginAuthor != null && eleOriginAuthor.size()!=0){
				System.out.println("Origin Author:"+eleOriginAuthor.get(0).text());
			}
			
			//����ת��΢��������
			Elements eleOriginContent = docWeibo.select("div[class=WB_feed_expand] div[class=WB_expand S_bg1] div[class=WB_text]");
			if (eleOriginContent != null && eleOriginContent.size() != 0){
				System.out.println("Origin Content:"+eleOriginContent.get(0).text());
			}
			
			//����ת��΢����ת�������ۣ�������
			Elements eleOriginStatusList = docWeibo.select("div[class=WB_feed_expand] div[class=WB_expand S_bg1] div[class=WB_func clearfix] div[class=WB_handle W_fr] ul[class = clearfix]");
			if (eleOriginStatusList != null && eleOriginStatusList.size() > 0){
				Elements originLis = eleOriginStatusList.get(0).getElementsByTag("li");
				
				//����ת����
				if (originLis.get(0).text().replace("ת��", "").trim() != ""){
					System.out.println("Origin ת����:"+ originLis.get(0).text().replace("ת��", "").trim());
				}else{
					System.out.println("Origin ת����:0");
				}
				
				//����������
				if (originLis.get(1).text().replace("����", "").trim() != ""){
					System.out.println("Origin ������:"+ originLis.get(1).text().replace("����", "").trim());
				}else{
					System.out.println("Origin ������:0");
				}
				
				//����������
				if (originLis.get(2).text().trim() != ""){
					System.out.println("Origin ������:"+ originLis.get(2).text().trim());
				}else{
					System.out.println("Origin ������:0");
				}
			}
			System.out.println("===============================");
		}

		return weiboMessages;
	}
	
	public List<WeiboUser> extractUserFans(String source){
		List<WeiboUser> fans = new ArrayList<WeiboUser>();
		Document doc = Jsoup.parse(source);
		//��ȡ������˿�б��ul��ǩ
		Elements eles = doc.select("div[class=follow_inner] ul[class=follow_list]");
		if (eles != null && eles.size() > 0){
			for (Element eleUl : eles){
				//��ȡ��˿�б�
				Elements fansLis = eleUl.select("li[class=follow_item S_line2]");
				
				for (Element fan : fansLis){
					WeiboFollower weiboFollower = new WeiboFollower();
					String fanHtml = fan.html();
					Document fanDoc = Jsoup.parse(fanHtml);
					//�����û���
					Elements elesUserName = fanDoc.select("dl[class=clearfix] dd[class=mod_info S_line1] div[class=info_name W_fb W_f14] a[class=S_txt1]");
					if (elesUserName != null && elesUserName.size() > 0){
//						System.out.println("UserName:"+elesUserName.get(0).text());
						weiboFollower.setUsername(elesUserName.get(0).text());
					}
					
					//�����û�uid
					if (elesUserName != null && elesUserName.size() > 0){
//						System.out.println("Uid:"+elesUserName.get(0).attr("usercard").substring(3,13));
						weiboFollower.setUid(elesUserName.get(0).attr("usercard").substring(3,13));
					}
					
					//�����û��Ա�
					Elements elesUserGenderFemale = fanDoc.select("dl[class=clearfix] dd[class=mod_info S_line1] div[class=info_name W_fb W_f14] a i[class=W_icon icon_female]");
					Elements elesUserGenderMale = fanDoc.select("dl[class=clearfix] dd[class=mod_info S_line1] div[class=info_name W_fb W_f14] a i[class=W_icon icon_male]");					
					if (elesUserGenderFemale != null && elesUserGenderFemale.size() > 0){
						weiboFollower.setGender("Ů");
					}else if (elesUserGenderMale != null && elesUserGenderMale.size() > 0){
						weiboFollower.setGender("��");
					}else{
						weiboFollower.setGender("δ֪");
					}
					
					//�����û�������Ϣ����ע������˿����΢����
					Elements elesConnect = fanDoc.select("dl[class=clearfix] dd[class=mod_info S_line1] div[class=info_connect] span em a");
					if (elesConnect != null && elesConnect.size() == 3){
						
						weiboFollower.setFocusNum(Integer.parseInt(elesConnect.get(0).text()));
						weiboFollower.setFollowerNum(Integer.parseInt(elesConnect.get(1).text()));
						weiboFollower.setWeiboNum(Integer.parseInt(elesConnect.get(2).text()));
					}
					
					//�����û���ַ
					Elements eleLocation = fanDoc.select("dl[class=clearfix] dd[class=mod_info S_line1] div[class=info_add] span");
					
					if (eleLocation != null && eleLocation.size() == 1){
						weiboFollower.setLocation(eleLocation.get(0).text());
					}
					
					//�����û����
					Elements eleIntro = fanDoc.select("dl[class=clearfix] dd[class=mod_info S_line1] div[class=info_intro] span");
					
					if (eleIntro != null && eleIntro.size() == 1){
						weiboFollower.setIntro(eleIntro.get(0).text());
					}
					
					System.out.println(weiboFollower);
					System.out.println("===============================");
				}
				
			}
		}
		return fans;
	}
	
	public void extractUserConnect(String html) {
		// TODO Auto-generated method stub
		
	}
}
