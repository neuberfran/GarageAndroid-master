package com.blogspot.ryanfx.security;

import javax.net.ssl.SSLSocketFactory;

public class GarageSSLSocketFactory {
        
        private static SSLSocketFactory instance;
        private static final String PASSWORD = "Password!";
        
   /*
        public static SSLSocketFactory getSSLSocketFactory(Context context){
                if (instance != null)
                        return instance;
                try {
                        KeyStore trusted = KeyStore.getInstance("BKS");
                        InputStream in = context.getResources().openRawResource(R.raw.mystore);
                        SSLContext sslContext  = null;
                        try {
                                trusted.load(in, PASSWORD.toCharArray());
                                TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
                                tmf.init(trusted);
                                sslContext = SSLContext.getInstance("TLS");
                                sslContext.init(null, tmf.getTrustManagers(), null);
                        } finally {
                                in.close();
                        }
                        instance = sslContext.getSocketFactory();
                        return instance;
                } catch (Exception e) {
                        throw new AssertionError(e);
                }
        }

        */
}