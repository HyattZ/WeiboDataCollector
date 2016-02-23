package com.weibo.zxt.op;

import java.io.IOException;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.weibo.zxt.tools.WeiboPageParser;
import com.weibo.zxt.utils.GlobalValues;

/**
 * @author ��Ц��
 *
 * @time 2015��6��16��
 * 
 */
public class WeiboUserOps {
	
	/*��ȡ΢������*/
	public void getUserWeibo(String uid) throws Exception{
		//��ȡҳ��Դ��
		HttpGet getMethod = new HttpGet("http://weibo.com/u/"+uid+"?is_search=0&visible=0&is_all=1&is_tag=0&profile_ftype=1&page=1#feedtop");
		HttpResponse httpResponse = GlobalValues.httpClient.execute(getMethod);
		String entity = EntityUtils.toString(httpResponse.getEntity());
		//��ҳ��Դ��ת��Ϊhtml��ʽ
		String html = WeiboPageParser.extractHtml(entity);
		//������Ϣ��ȡ����ȡ��Ϣ�������ȡ����΢������
		InfoExtractor ie = new InfoExtractor();
		ie.extractWeiboMessage(html);
	}
	
	/*��ȡ�û���˿�б�*/
	public void getUserFans(String uid) throws Exception{
		//��ȡҳ��Դ��
		HttpGet getMethod = new HttpGet("http://weibo.com/p/100505"+uid+"/follow?relate=fans&page=2#Pl_Official_HisRelation__65");
		HttpResponse httpResponse = GlobalValues.httpClient.execute(getMethod);
		String entity = EntityUtils.toString(httpResponse.getEntity());
		//��ҳ��Դ��ת��Ϊhtml��ʽ
		String html = WeiboPageParser.extractHtml(entity);
		//������Ϣ��ȡ����ȡ��Ϣ�������ȡ�����û���˿�б�
		InfoExtractor ie = new InfoExtractor();
		ie.extractUserFans(html);
	}
	
	/*��ȡ�û���ע�б�*/
	public void getUserFocus(String uid) throws Exception{
		//��ȡҳ��Դ��
		HttpGet getMethod = new HttpGet("http://weibo.com/p/100505"+uid+"/follow?page=2#Pl_Official_HisRelation__65");
		HttpResponse httpResponse = GlobalValues.httpClient.execute(getMethod);
		String entity = EntityUtils.toString(httpResponse.getEntity());
		//��ҳ��Դ��ת��Ϊhtml��ʽ
		String html = WeiboPageParser.extractHtml(entity);
		//������Ϣ��ȡ����ȡ��Ϣ�������ȡ�����û���ע�б�
		InfoExtractor ie = new InfoExtractor();
		ie.extractUserFans(html);
	}
	
	/*��ȡ�û��ķ�˿���͹�ע��*/
	public void getUserConnnect(String uid) throws Exception{
		JSONObject connect = new JSONObject();
		
		HttpGet getMethod = new HttpGet("http://weibo.com/p/100505"+uid+"/follow?relate=fans&from=100505&wvr=6&mod=headfans#place");
		HttpResponse httpResponse = GlobalValues.httpClient.execute(getMethod);
		String entity = EntityUtils.toString(httpResponse.getEntity());
		
		String html = WeiboPageParser.extractHtml(entity);
		
		InfoExtractor ie = new InfoExtractor();
		ie.extractUserConnect(html);
		
	}
}
