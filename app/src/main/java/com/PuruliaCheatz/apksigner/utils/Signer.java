/*
 *  ApkSignerTool an Signer for Android Applications
 * Copyright 2024, PuruliaKing007
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following disclaimer
 * in the documentation and/or other materials provided with the
 * distribution.
 *     * Neither the name of developer-krushna nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
 */


package com.PuruliaCheatz.apksigner.utils;

import android.sun.security.provider.JavaKeyStoreProvider;
import com.android.apksig.ApkSigner;
import com.android.apksig.apk.ApkFormatException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.EncryptedPrivateKeyInfo;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/*
Author @PuruliaKing007
*/

public class Signer {

    public static final String TAG = "Signer";
	public static String signKeyName = "testkey";
	public static boolean setV1SigningEnabled = true;
	public static boolean setV2SigningEnabled = true;
	public static boolean setV3SigningEnabled = true;
	
    public Signer() {
	}

	public void calculateSignature(String inputApkFile, String outputApkFile)
	throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException, ApkFormatException, InvalidKeyException, SignatureException, GeneralSecurityException {	
		Security.addProvider(new JavaKeyStoreProvider());
		ApkSigner apkSigner = new ApkSigner.Builder(Collections.singletonList(loadTestKey()))
			.setV1SigningEnabled(setV1SigningEnabled)
			.setV2SigningEnabled(setV2SigningEnabled)
			.setCreatedBy("PuruliaCheatz")
			//.setVerityEnabled(true)
			//.setForceSourceStampOverwrite(true)
			//.setAlignFileSize(true)
			.setV3SigningEnabled(setV3SigningEnabled)
			//.setV4SigningEnabled(true)
			//.setV4ErrorReportingEnabled(true)
			//.setV4SignatureOutputFile(new File("/storage/emulated/0/AppPro.sig"))
			.setInputApk(new File(inputApkFile))
			.setOutputApk(new File(outputApkFile))
		//	.setOtherSignersSignaturesPreserved(false)
			.build();

		apkSigner.sign();
	}
	
	private ApkSigner.SignerConfig loadTestKey() throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException, GeneralSecurityException {
		List<X509Certificate> results = new LinkedList<>();
		results.add(readPublicKey(getClass().getResource("/assets/keys/" + signKeyName + ".x509.pem").openStream()));
		PrivateKey privateKey   = (PrivateKey) readPrivateKey(getClass().getResource("/assets/keys/" + signKeyName + ".pk8").openStream());
		return new ApkSigner.SignerConfig.Builder("Apk Signer Tool", privateKey, results).build();
	}
	
	public static X509Certificate readPublicKey(InputStream input)throws IOException, GeneralSecurityException {
        try {
            CertificateFactory cf=CertificateFactory.getInstance("X.509");
            return (X509Certificate)cf.generateCertificate(input);
        } finally {
            input.close();
        }
    }

	public static KeySpec decryptPrivateKey(byte[] encryptedPrivateKey, String keyPassword)throws GeneralSecurityException {
		EncryptedPrivateKeyInfo epkInfo=null;
		try {
			epkInfo = new EncryptedPrivateKeyInfo(encryptedPrivateKey);
		} catch (IOException ex) {
			return null;
		}
		char[] password=keyPassword.toCharArray();
		SecretKeyFactory skFactory=SecretKeyFactory.getInstance(epkInfo.getAlgName());
		Key key=skFactory.generateSecret(new PBEKeySpec(password));
		Cipher cipher=Cipher.getInstance(epkInfo.getAlgName());
		cipher.init(Cipher.DECRYPT_MODE, key, epkInfo.getAlgParameters());
		try {
			return epkInfo.getKeySpec(cipher);
		} catch (InvalidKeySpecException ex) {
			System.err.println("PrivateKey may be bad.");
			throw ex;
		}
	}

	/** Read a PKCS 8 format private key. */
	public static PrivateKey readPrivateKey(InputStream input)throws IOException, GeneralSecurityException {
		try {
			byte[] bytes=readBytes(input);
			KeySpec spec=decryptPrivateKey(bytes, "");
			if (spec == null) {
				spec = new PKCS8EncodedKeySpec(bytes);
			}
			try {
				return KeyFactory.getInstance("RSA").generatePrivate(spec);
			} catch (InvalidKeySpecException ex) {
				return KeyFactory.getInstance("DSA").generatePrivate(spec);
			}
		} finally {
			input.close();
		}
	}

	public static byte[] readBytes(InputStream in)throws IOException {
		byte[] buf=new byte[1024];
		ByteArrayOutputStream out=new ByteArrayOutputStream();
		int num;
		while ((num = in.read(buf, 0, buf.length)) != -1)
			out.write(buf, 0, num);
		return out.toByteArray();
	}
	
}
