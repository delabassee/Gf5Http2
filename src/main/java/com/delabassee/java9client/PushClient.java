/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2017 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package com.delabassee.java9client;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import jdk.incubator.http.HttpClient;
import jdk.incubator.http.HttpRequest;
import jdk.incubator.http.HttpResponse;
import jdk.incubator.http.HttpResponse.MultiProcessor;

/**
 *
 * @author davidd
 */
public class PushClient {

    static String newLine = System.getProperty("line.separator");

    public static void main(String[] args) throws IOException, InterruptedException, NoSuchAlgorithmException, URISyntaxException {
        System.out.println("WARNING: Certs aren't validated!");

        URI gfURI = new URI("https://localhost:8181/SamplePush/Push");

        HttpClient client = HttpClient.newBuilder()
                .sslContext(dummySslCtx())
                .version(HttpClient.Version.HTTP_2)
                .build();
        
        HttpRequest request = HttpRequest
                .newBuilder(gfURI)
                .GET()
                .build();
        
        Map<HttpRequest, CompletableFuture<HttpResponse<String>>> results = client
                .sendAsync(request, MultiProcessor.asMap(
                        (req) -> Optional.of(HttpResponse.BodyHandler.asString())))
                .join();
        
        results.forEach((HttpRequest req, CompletableFuture<HttpResponse<String>> rep) -> {
            try {
                System.out.println("** Response Body **" + newLine + "[" + rep.get().body() + "]");
            } catch (InterruptedException | ExecutionException ex) {
                System.out.println("EX:" + ex.getMessage());
            }
        });

    }

    private static TrustManager dummyTrustManager() {
        TrustManager dummyTrustManager = new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                // dont't trust!
            }
            
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                // dont't trust!
            }
            
            public X509Certificate[] getAcceptedIssuers() {
                // dont't trust!
                return null;
            }
        };
        return dummyTrustManager;
    }

    private static SSLContext dummySslCtx() {
        SSLContext sslContext = null;        
        
        try {
            sslContext = SSLContext.getInstance("SSL");
            // brainless trust manager, don't trust!!!
            TrustManager[] trustAllCerts = new TrustManager[1];
            trustAllCerts[0] = dummyTrustManager();
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        } catch (KeyManagementException | NoSuchAlgorithmException ex) {
            System.out.println("EX:" + ex.getMessage());
        }
        return sslContext;
    }

}
