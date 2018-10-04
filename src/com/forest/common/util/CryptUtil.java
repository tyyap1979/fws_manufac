package com.forest.common.util;

import java.security.SecureRandom;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.apache.log4j.Logger;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public final class CryptUtil
{
	private Logger logger = Logger.getLogger (this.getClass ());
	private final static String KEY_PHASE = "com.pshop.shared.util.PasswordUtil";
	private static CryptUtil _instance;
	private CryptUtil(){
		
	}
	
//	private String getHexString(byte[] raw) throws Exception{
//	    byte[] hex = new byte[2 * raw.length];
//	    int index = 0;
//
//	    for (byte b : raw) {
//	      int v = b & 0xFF;
//	      hex[index++] = HEX_CHAR_TABLE[v >>> 4];
//	      hex[index++] = HEX_CHAR_TABLE[v & 0xF];
//	    } 
//	    return new String(hex, "ASCII");
//	}
	
	private String getHexString(byte[] b) {
		String result = "";
		for (int i=0; i < b.length; i++) {
			result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
		}
		return result;
	}

	public String generateSecureCookies() {
		Random ranGen = new SecureRandom();
		byte[] aesKey = new byte[16]; // 16 bytes = 128 bits
		ranGen.nextBytes(aesKey);		
        
        return getHexString(aesKey);
	}
	
	public synchronized String encrypt(String plaintext){
		String encryptedPwd = null;
		try{
			DESKeySpec keySpec = new DESKeySpec(KEY_PHASE.getBytes("UTF8"));
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey key = keyFactory.generateSecret(keySpec);
			BASE64Encoder base64encoder = new BASE64Encoder();
			
			// ENCODE plainTextPassword String			
			byte[] cleartext = plaintext.getBytes("UTF8");      
	
			Cipher cipher = Cipher.getInstance("DES"); // cipher is not thread safe
			cipher.init(Cipher.ENCRYPT_MODE, key);
			encryptedPwd = base64encoder.encode(cipher.doFinal(cleartext));
			
			// now you can store it 
	

		}catch(Exception e){
			logger.error (e, e);
		}
		return encryptedPwd; //step 6
	}
	
	public synchronized String decrypt(String encryptedPwd){
		BASE64Decoder base64decoder = new BASE64Decoder();
		StringBuffer decryptedPwd = new StringBuffer();
		try{
			if(CommonUtil.isEmpty(encryptedPwd)) return "";
			
			DESKeySpec keySpec = new DESKeySpec(KEY_PHASE.getBytes("UTF8"));
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey key = keyFactory.generateSecret(keySpec);
			
			Cipher cipher = Cipher.getInstance("DES"); // cipher is not thread safe
			
			// DECODE encryptedPwd String
			byte[] encrypedPwdBytes = base64decoder.decodeBuffer(encryptedPwd);
	
			cipher = Cipher.getInstance("DES");// cipher is not thread safe
			cipher.init(Cipher.DECRYPT_MODE, key);
			byte[] plainTextPwdBytes = (cipher.doFinal(encrypedPwdBytes));
			
			for(int i=0; i<plainTextPwdBytes.length; i++){
				decryptedPwd.append ((char)plainTextPwdBytes[i]);			
			}
		}catch(Exception e){
			logger.error (e, e);
		}
		return decryptedPwd.toString ();
	}
	
	public static synchronized CryptUtil getInstance() {
		if (_instance==null) {
			_instance = new CryptUtil();
		} 
		return _instance;
	}
}