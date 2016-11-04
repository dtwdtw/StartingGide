package com.beabow.utils;

import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;

public class CryptoTools {

	private static byte[] DESIV = { 0x32, (byte) 0x84, 0x66, 0x73, (byte) 0x92, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF };
	private AlgorithmParameterSpec iv = null;// �����㷨�Ĳ���ӿڣ�IvParameterSpec�����һ��ʵ��
	private Key key = null;

	public CryptoTools(String keyStr) throws Exception {
		byte[] DESkey = keyStr.getBytes("utf-8");
		DESKeySpec keySpec = new DESKeySpec(DESkey);// ������Կ����
		iv = new IvParameterSpec(DESIV);// ��������
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");// �����Կ����
		key = keyFactory.generateSecret(keySpec);// �õ���Կ����
	}

	public String encode(String data) throws Exception {
		Cipher enCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");// �õ����ܶ���Cipher
		enCipher.init(Cipher.ENCRYPT_MODE, key, iv);// ���ù���ģʽΪ����ģʽ�������Կ������
		byte[] pasByte = enCipher.doFinal(data.getBytes("utf-8"));
		BASE64Encoder base64Encoder = new BASE64Encoder();
		return base64Encoder.encode(pasByte);
	}

	public String decode(String data) throws Exception {
		Cipher deCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		deCipher.init(Cipher.DECRYPT_MODE, key, iv);
		BASE64Decoder base64Decoder = new BASE64Decoder();

		byte[] pasByte = deCipher.doFinal(base64Decoder.decodeBuffer(data));

		return new String(pasByte, "UTF-8");
	}

	public static void main(String[] args) {
		try {
			String test = "aaaaaa";
			CryptoTools des = new CryptoTools("95362111");// �Զ�����Կ
			System.out.println("����ǰ���ַ�" + test);
			System.out.println("���ܺ���ַ�" + des.encode(test));
			System.out.println("���ܺ���ַ�" + des.decode(des.encode(test)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
