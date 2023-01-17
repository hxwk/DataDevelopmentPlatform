package com.dfssi.dataplatform.external.common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class HMacMD5 {

	/**
	 * 计算参数的md5信息
	 * 
	 * @param str
	 *            待处理的字节数组
	 * @return md5摘要信息
	 * @throws NoSuchAlgorithmException
	 */
	private static byte[] md5(byte[] str) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(str);
		return md.digest();
	}

	/**
	 * 将待加密数据data，通过密钥key，使用hmac-md5算法进行加密，然后返回加密结果。 参照rfc2104 HMAC算法介绍实现。
	 * @param key
	 *            密钥
	 * @param data
	 *            待加密数据
	 * @return 加密结果
	 * @throws NoSuchAlgorithmException
	 */
	public static byte[] getHmacMd5Bytes(byte[] key, byte[] data)
			throws NoSuchAlgorithmException {
		/*
		 * HmacMd5 calculation formula: H(K XOR opad, H(K XOR ipad, text))
		 * HmacMd5 计算公式：H(K XOR opad, H(K XOR ipad, text))
		 * H代表hash算法，本类中使用MD5算法，K代表密钥，text代表要加密的数据 ipad为0x36，opad为0x5C。
		 */
		int length = 64;
		byte[] ipad = new byte[length];
		byte[] opad = new byte[length];
		for (int i = 0; i < 64; i++) {
			ipad[i] = 0x36;
			opad[i] = 0x5C;
		}
		byte[] actualKey = key; // Actual key.
		byte[] keyArr = new byte[length]; // Key bytes of 64 bytes length
		/*
		 * If key's length is longer than 64,then use hash to digest it and use
		 * the result as actual key. 如果密钥长度，大于64字节，就使用哈希算法，计算其摘要，作为真正的密钥。
		 */
		if (key.length > length) {
			actualKey = md5(key);
		}
		for (int i = 0; i < actualKey.length; i++) {
			keyArr[i] = actualKey[i];
		}
		/*
		 * append zeros to K 如果密钥长度不足64字节，就使用0x00补齐到64字节。
		 */
		if (actualKey.length < length) {
			for (int i = actualKey.length; i < keyArr.length; i++)
				keyArr[i] = 0x00;
		}

		/*
		 * calc K XOR ipad 使用密钥和ipad进行异或运算。
		 */
		byte[] kIpadXorResult = new byte[length];
		for (int i = 0; i < length; i++) {
			kIpadXorResult[i] = (byte) (keyArr[i] ^ ipad[i]);
		}

		/*
		 * append "text" to the end of "K XOR ipad" 将待加密数据追加到K XOR ipad计算结果后面。
		 */
		byte[] firstAppendResult = new byte[kIpadXorResult.length + data.length];
		for (int i = 0; i < kIpadXorResult.length; i++) {
			firstAppendResult[i] = kIpadXorResult[i];
		}
		for (int i = 0; i < data.length; i++) {
			firstAppendResult[i + keyArr.length] = data[i];
		}

		/*
		 * calc H(K XOR ipad, text) 使用哈希算法计算上面结果的摘要。
		 */
		byte[] firstHashResult = md5(firstAppendResult);

		/*
		 * calc K XOR opad 使用密钥和opad进行异或运算。
		 */
		byte[] kOpadXorResult = new byte[length];
		for (int i = 0; i < length; i++) {
			kOpadXorResult[i] = (byte) (keyArr[i] ^ opad[i]);
		}

		/*
		 * append "H(K XOR ipad, text)" to the end of "K XOR opad" 将H(K XOR
		 * ipad, text)结果追加到K XOR opad结果后面
		 */
		byte[] secondAppendResult = new byte[kOpadXorResult.length
				+ firstHashResult.length];
		for (int i = 0; i < kOpadXorResult.length; i++) {
			secondAppendResult[i] = kOpadXorResult[i];
		}
		for (int i = 0; i < firstHashResult.length; i++) {
			secondAppendResult[i + keyArr.length] = firstHashResult[i];
		}

		/*
		 * H(K XOR opad, H(K XOR ipad, text)) 对上面的数据进行哈希运算。
		 */
		byte[] hmacMd5Bytes = md5(secondAppendResult);
		return hmacMd5Bytes;
	}

	public static String getHmacMd5Str(String key, String data) {
		String result = "";
		try {
			byte[] keyByte = key.getBytes("UTF-8");
			byte[] dataByte = data.getBytes("UTF-8");
			byte[] hmacMd5Byte = getHmacMd5Bytes(keyByte, dataByte);
			StringBuffer md5StrBuff = new StringBuffer();
			for (int i = 0; i < hmacMd5Byte.length; i++) {
				if (Integer.toHexString(0xFF & hmacMd5Byte[i]).length() == 1)
					md5StrBuff.append("0").append(
							Integer.toHexString(0xFF & hmacMd5Byte[i]));
				else
					md5StrBuff.append(Integer
							.toHexString(0xFF & hmacMd5Byte[i]));
			}
			result = md5StrBuff.toString().toUpperCase();

		} catch (Exception e) {
			// logger.error("error getHmacMd5Str()",e);
			e.printStackTrace();
		}
		return result;

	}
	
	public static void main(String[] args) throws NoSuchAlgorithmException {
//		System.out.println(HMacMD5.getHmacMd5Str("1234567890qwerty", "MSnv1QhZBWw3xe1rkGiK9c1VNrsYkIunOG2rkruZn+0pDirXwmC6XVNirXHyGlZhpGeL6Qobdo6olclABvHCTRd9j5gRJj5NN76u+NshqJK0ckb18gyI4BoBZlF4A7CEjgUcSdq/8Aowak/WMww8H4NIuoOAkAmRyHfElZ0mjOyqsy+NjKsf8MMG/Uy2mbrQ9JjLdiByYeJwBoeHfBSM5rOh5iIQ+MdsqLp5QpozL3shGvNFqRRXr3cZeS9jiznfkoo1ZwLPw4VAyUY1ac7f8C+/iJluZHLcD69SeObJOdd4RNdjIo+66edmgtj4844cNcAl5Yt6B4bQNVXqTZtp8PGTaff1uCgqpxeSBTTfMxzFrPUiarR/hOTB/vNLLwM7rFZk0BTR6r270Lwp/R7ysg=="));
		System.out.println(HMacMD5.getHmacMd5Str("1234567890abcdef","731043872MmpqZekn6ObV6d2GmuZhZaYZK89TSUOEIv6QbuUKPu+ztB/VlRiH2Jufd6D7wmzTTtsKW1fdOjhtPb2SUidl8SDl1rHgk7IdKUNxDl8v4p2Uuw4EFMGTLzSLLdjK1ouLTiS0RFZ8BAOoCHAKsFMhX/TKI5Aa2u+frkdm3y/fYdgBr+NSQJuRghuQKPepl9YwiYxy63QHIT3CAAb4WNoh/nUvc3D7k+YgrGpff+lzFHuL5OKHE7nrCkBKqA8q199EQbJ4nIqJpvPLfMhZYax2fDe7JVgFOj5sQkX5mc00xUMaaVfw38oxT1yVKs1EHUOxzzNFfwK+8ZzPgeLkWN9LhKEl3SjBUqCIuLpveD6nvPWv8QGa4JvFPkQHZJ28ckEQA7JXxgpjttKdzfzAuUQ7WIeAXLlADgBQmk427VkeXzx8xya9hdSFhTz4vPrW7BVthX1EJDVYa63DWw7UeuyWuZdKvz42xxjNzDnkVtg9tjvKZAxm0yEr18JXMfXfInFOoijwZDvTipMHDC8J6v5x+NmK8zFTGso/GSFnbqtUew/uwr43OVgGYbHydRZ9ePvEnp2P8wyAJjWTXxaCp6oZBRettMwCOJNViAzvwKry7gko+RoXavpLGchLiYA1uLWJ6nlypBNe9h0+HkBAsWzcRRNwR7tyIMXO3U+q48WDxs+Cw+pIA21v/IVh7RqpJj8oLS1B3f2rcLqtQlXHDeDDWD/3jvObrzfLvaJJpDf+FpczGMqr8WJiXOuqBzRDt3eLkwQ4RwA5T2RjBR6Rc55Dh6KUyLE06ReZm1GpHJsbxYsR68wDE+rbRqgZsLsYtaTcMUne1uDjm7qClLx6KNisbcynJuJXBHYxcHYjN77jA/3NpKnWSymI55L1DSdiLGOh+R/NLvo0AfJqIF3HZ+S5ABNhCxKTje2stVJ3qEHHL0XOhHvYldr1Mw4xHr2SMIX3mdGfhxAlC2qqQ9V840VJH31+RFU9yQ/bzjbcTrw3QKt/cSo5wRKisnbhvnk4dI5JVUUwV13nBax2qf0+3dVIqUd/X2FyY2JcGnmwLPux147dDEbroxsSZUa8lwAiyvcrABpMUHBpucxQlh7V6DUjbjO4FBgK9APtN7bsGSGurdcgavQ/yXZxgWrDnygCqO1D/rgJ3OlymSnmo1uE1hZ7lFUAyo3WAM4YoQY/aNnFLurtfE7DKcmojClfTU/v7rUzVTXhIELyNZ7CE9j+Q+LurrwPr0sF/7hsoAQXtgLbqbJ5dIYUZb9i4Nh/b0RDvYuj83ZhGLbqp71q+D5rEoU95s53TW14K+XlxHpyI3/7cH9f6GhxAhvM+Hql7KALQusHsccoosL1MuUtkQUDwQw1z+uWiU9V0ArL/bP1ss9m0ZZWStYrdJv8lY6I5dFVv2ZQ3uUM0aOP1YlhK5wiAjyX106Adfzdnkr0uYGHK9yURNs=201806021641110001"));
	}
}