package com.weibo.zxt.op;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import net.sf.json.JSONObject;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;



import com.weibo.zxt.utils.GlobalValues;
import com.weibo.zxt.utils.PreLoginData;


/**
 * @author ��Ц��
 *
 * @time 2015��6��16��
 * 
 */
public class WeiboLogin {
	
	private Logger logger =Logger.getLogger(WeiboLogin.class);
	private PreLoginData preLoginData =  new PreLoginData();
	private String preLoginURL = "http://login.sina.com.cn/sso/prelogin.php?entry=weibo&callback=sinaSSOController.preloginCallBack&su=&rsakt=mod&client=ssologin.js%28v1.4.18%29&_="+new Date().getTime();
	
private  void preLogin(){
		
		HttpGet httpGet = new HttpGet(preLoginURL);
		try {
			
			HttpResponse httpResponse = GlobalValues.httpClient.execute(httpGet);
			HttpEntity entity = httpResponse.getEntity();

			String rev = EntityUtils.toString(entity);
			
			//System.out.println(rev);
			//ת����JSON��ʽ
			rev = rev.substring(rev.indexOf('(')+1,rev.lastIndexOf(')'));
			JSONObject jsonObj = JSONObject.fromObject(rev);
			
			//����ȡ�������ݱ��������Ա�����
			preLoginData.setRetcode(jsonObj.getString("retcode"));
			preLoginData.setExectime(jsonObj.getString("exectime"));
			preLoginData.setNonce(jsonObj.getString("nonce"));
			preLoginData.setPcid(jsonObj.getString("pcid"));
			preLoginData.setPubkey(jsonObj.getString("pubkey"));
			preLoginData.setRsakv(jsonObj.getString("rsakv"));
			preLoginData.setServertime(jsonObj.getString("servertime"));
			
			//System.out.println(preLoginData);
		} catch (ClientProtocolException e) {
			logger.info("��ȡ��¼���ݳ���");
			e.printStackTrace();
		} catch (IOException e) {
			logger.info("��ȡ��¼���ݳ���");
			e.printStackTrace();
		}finally{
			httpGet.abort();
		}
	}

public void Login(String userName,String password) throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException{
	
	//���е�¼ǰ��׼������
	preLogin();
	
	HttpPost post = new HttpPost("http://login.sina.com.cn/sso/login.php?client=ssologin.js(v1.4.18)");
	
	
	//Ҫ��userName�����@��ǻ���ASII��,Ҳ����Ҫ��ȡ��su�Ĳ���
	userName = userName.replace("@", "%40");
	userName = new String(Base64.encodeBase64(userName.getBytes()));
	
	//��ȡpassword�ļ��ܽ����Ҳ����Ҫ��ȡsp����
	password = preLoginData.getServertime()+"\t"+preLoginData.getNonce()+"\n"+password;
	password = rsaCrypt(preLoginData.getPubkey(),"10001",password);
	
	List<NameValuePair> nvps = new ArrayList<NameValuePair>();
	
	nvps.add(new BasicNameValuePair("entry","weibo"));
	nvps.add(new BasicNameValuePair("gateway","1"));
	nvps.add(new BasicNameValuePair("from",""));
	nvps.add(new BasicNameValuePair("savestate","0"));
	nvps.add(new BasicNameValuePair("useticket","1"));
	nvps.add(new BasicNameValuePair("vsnf","1"));
	nvps.add(new BasicNameValuePair("su",userName));
	nvps.add(new BasicNameValuePair("service","miniblog"));
	nvps.add(new BasicNameValuePair("servertime",preLoginData.getServertime()));
	nvps.add(new BasicNameValuePair("nonce",preLoginData.getNonce()));
	nvps.add(new BasicNameValuePair("pwencode","rsa2"));
	nvps.add(new BasicNameValuePair("rsakv",preLoginData.getRsakv()));
	nvps.add(new BasicNameValuePair("sp",password));
	nvps.add(new BasicNameValuePair("sr","1366*768"));
	nvps.add(new BasicNameValuePair("encoding","UTF-8"));
	nvps.add(new BasicNameValuePair("prelt","82"));
	nvps.add(new BasicNameValuePair("url","http://weibo.com/ajaxlogin.php?framelogin=1&callback=parent.sinaSSOController.feedBackUrlCallBack"));
	nvps.add(new BasicNameValuePair("returntype","META"));
	
	post.setEntity(new UrlEncodedFormEntity(nvps,HTTP.UTF_8));
	
	try {
		HttpResponse httpResponse = GlobalValues.httpClient.execute(post);
	
		String entity = EntityUtils.toString(httpResponse.getEntity());
		
		//��ȡ����ת����
		//logger.info(entity);
		//����ò�ƱȽ϶���һ�㣬��ʱ����http%3A%2F%2Fweibo.com%2Fajaxlogin.php%3F����ʱ����http://weibo.com/ajaxlogin.php?
		int pos = 0;
		String url = "";
		if ((pos = entity.indexOf("http%3A%2F%2Fweibo.com%2Fajaxlogin.php%3F")) >0){
			url = entity.substring(pos,entity.indexOf("code=0")+6);
		}else{
			pos = entity.indexOf("http://weibo.com/ajaxlogin.php?");
			url = entity.substring(pos,entity.indexOf("code=0")+6);
		}
		
		
		post.abort();
		
		//��ȡʵ������,���ｲ��ȡ�������������UNICODE�ַ�ת����ASII�ַ���
		url =url.replace("%2F", "/");
		url = url.replace("%3A", ":");
		url = url.replace("%3F", "?");
		
		HttpGet get = new HttpGet(url);
		HttpResponse httpResponse1 = GlobalValues.httpClient.execute(get);
		GlobalValues.cookie = GlobalValues.httpClient.getCookieStore();
		String entity1= EntityUtils.toString(httpResponse1.getEntity());
		//System.out.println(entity);
		
		//��ȡ�û���id����Ϊ�Ժ���ʵ���Ҫ�ο�
		GlobalValues.uid = entity1.substring(entity1.indexOf("uniqueid")+11,entity1.indexOf("uniqueid")+21);
		//System.out.println(GlobalValues.uid);
	} catch (ClientProtocolException e) {
		logger.info("��¼ʧ��");
		e.printStackTrace();
		return ;
	} catch (IOException e) {
		logger.info("��½ʧ��");
		e.printStackTrace();
		return ;
	}
	logger.info("��¼�ɹ�");
}

private String rsaCrypt(String modeHex,String exponentHex,String message) throws NoSuchAlgorithmException, 
	InvalidKeySpecException, NoSuchPaddingException, 
	InvalidKeyException, IllegalBlockSizeException, 
	BadPaddingException, UnsupportedEncodingException{
	String result = "";
	KeyFactory factory = KeyFactory.getInstance("RSA");
	BigInteger m = new BigInteger(modeHex,16);
	BigInteger e = new BigInteger(exponentHex,16);
	RSAPublicKeySpec spec = new RSAPublicKeySpec(m,e);

	RSAPublicKey pub = (RSAPublicKey) factory.generatePublic(spec);
	Cipher enc = Cipher.getInstance("RSA");
	enc.init(Cipher.ENCRYPT_MODE, pub);
	byte[] encryptedContentKey = enc.doFinal(message.getBytes("GB2312"));
	result = new String(Hex.encodeHex(encryptedContentKey));
	
	return result;
}
}
