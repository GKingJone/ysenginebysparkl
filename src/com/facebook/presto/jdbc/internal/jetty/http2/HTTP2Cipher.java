/*     */ package com.facebook.presto.jdbc.internal.jetty.http2;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.ArrayTrie;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.Trie;
/*     */ import java.util.Comparator;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HTTP2Cipher
/*     */ {
/*  28 */   public static final Comparator<String> COMPARATOR = new CipherComparator();
/*     */   
/*  30 */   private static final Trie<Boolean> __blackProtocols = new ArrayTrie(30);
/*  31 */   private static final Trie<Boolean> __blackCiphers = new ArrayTrie(11000);
/*     */   
/*     */   static
/*     */   {
/*  35 */     for (String p : new String[] { "TLSv1.2", "TLSv1.1", "TLSv1", "SSL", "SSLv2", "SSLv3" })
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*  40 */       __blackProtocols.put(p, Boolean.TRUE);
/*     */     }
/*     */     
/*  43 */     for (String c : new String[] { "TLS_NULL_WITH_NULL_NULL", "TLS_RSA_WITH_NULL_MD5", "TLS_RSA_WITH_NULL_SHA", "TLS_RSA_EXPORT_WITH_RC4_40_MD5", "TLS_RSA_WITH_RC4_128_MD5", "TLS_RSA_WITH_RC4_128_SHA", "TLS_RSA_EXPORT_WITH_RC2_CBC_40_MD5", "TLS_RSA_WITH_IDEA_CBC_SHA", "TLS_RSA_EXPORT_WITH_DES40_CBC_SHA", "TLS_RSA_WITH_DES_CBC_SHA", "TLS_RSA_WITH_3DES_EDE_CBC_SHA", "TLS_DH_DSS_EXPORT_WITH_DES40_CBC_SHA", "TLS_DH_DSS_WITH_DES_CBC_SHA", "TLS_DH_DSS_WITH_3DES_EDE_CBC_SHA", "TLS_DH_RSA_EXPORT_WITH_DES40_CBC_SHA", "TLS_DH_RSA_WITH_DES_CBC_SHA", "TLS_DH_RSA_WITH_3DES_EDE_CBC_SHA", "TLS_DHE_DSS_EXPORT_WITH_DES40_CBC_SHA", "TLS_DHE_DSS_WITH_DES_CBC_SHA", "TLS_DHE_DSS_WITH_3DES_EDE_CBC_SHA", "TLS_DHE_RSA_EXPORT_WITH_DES40_CBC_SHA", "TLS_DHE_RSA_WITH_DES_CBC_SHA", "TLS_DHE_RSA_WITH_3DES_EDE_CBC_SHA", "TLS_DH_anon_EXPORT_WITH_RC4_40_MD5", "TLS_DH_anon_WITH_RC4_128_MD5", "TLS_DH_anon_EXPORT_WITH_DES40_CBC_SHA", "TLS_DH_anon_WITH_DES_CBC_SHA", "TLS_DH_anon_WITH_3DES_EDE_CBC_SHA", "TLS_KRB5_WITH_DES_CBC_SHA", "TLS_KRB5_WITH_3DES_EDE_CBC_SHA", "TLS_KRB5_WITH_RC4_128_SHA", "TLS_KRB5_WITH_IDEA_CBC_SHA", "TLS_KRB5_WITH_DES_CBC_MD5", "TLS_KRB5_WITH_3DES_EDE_CBC_MD5", "TLS_KRB5_WITH_RC4_128_MD5", "TLS_KRB5_WITH_IDEA_CBC_MD5", "TLS_KRB5_EXPORT_WITH_DES_CBC_40_SHA", "TLS_KRB5_EXPORT_WITH_RC2_CBC_40_SHA", "TLS_KRB5_EXPORT_WITH_RC4_40_SHA", "TLS_KRB5_EXPORT_WITH_DES_CBC_40_MD5", "TLS_KRB5_EXPORT_WITH_RC2_CBC_40_MD5", "TLS_KRB5_EXPORT_WITH_RC4_40_MD5", "TLS_PSK_WITH_NULL_SHA", "TLS_DHE_PSK_WITH_NULL_SHA", "TLS_RSA_PSK_WITH_NULL_SHA", "TLS_RSA_WITH_AES_128_CBC_SHA", "TLS_DH_DSS_WITH_AES_128_CBC_SHA", "TLS_DH_RSA_WITH_AES_128_CBC_SHA", "TLS_DHE_DSS_WITH_AES_128_CBC_SHA", "TLS_DHE_RSA_WITH_AES_128_CBC_SHA", "TLS_DH_anon_WITH_AES_128_CBC_SHA", "TLS_RSA_WITH_AES_256_CBC_SHA", "TLS_DH_DSS_WITH_AES_256_CBC_SHA", "TLS_DH_RSA_WITH_AES_256_CBC_SHA", "TLS_DHE_DSS_WITH_AES_256_CBC_SHA", "TLS_DHE_RSA_WITH_AES_256_CBC_SHA", "TLS_DH_anon_WITH_AES_256_CBC_SHA", "TLS_RSA_WITH_NULL_SHA256", "TLS_RSA_WITH_AES_128_CBC_SHA256", "TLS_RSA_WITH_AES_256_CBC_SHA256", "TLS_DH_DSS_WITH_AES_128_CBC_SHA256", "TLS_DH_RSA_WITH_AES_128_CBC_SHA256", "TLS_DHE_DSS_WITH_AES_128_CBC_SHA256", "TLS_RSA_WITH_CAMELLIA_128_CBC_SHA", "TLS_DH_DSS_WITH_CAMELLIA_128_CBC_SHA", "TLS_DH_RSA_WITH_CAMELLIA_128_CBC_SHA", "TLS_DHE_DSS_WITH_CAMELLIA_128_CBC_SHA", "TLS_DHE_RSA_WITH_CAMELLIA_128_CBC_SHA", "TLS_DH_anon_WITH_CAMELLIA_128_CBC_SHA", "TLS_DHE_RSA_WITH_AES_128_CBC_SHA256", "TLS_DH_DSS_WITH_AES_256_CBC_SHA256", "TLS_DH_RSA_WITH_AES_256_CBC_SHA256", "TLS_DHE_DSS_WITH_AES_256_CBC_SHA256", "TLS_DHE_RSA_WITH_AES_256_CBC_SHA256", "TLS_DH_anon_WITH_AES_128_CBC_SHA256", "TLS_DH_anon_WITH_AES_256_CBC_SHA256", "TLS_RSA_WITH_CAMELLIA_256_CBC_SHA", "TLS_DH_DSS_WITH_CAMELLIA_256_CBC_SHA", "TLS_DH_RSA_WITH_CAMELLIA_256_CBC_SHA", "TLS_DHE_DSS_WITH_CAMELLIA_256_CBC_SHA", "TLS_DHE_RSA_WITH_CAMELLIA_256_CBC_SHA", "TLS_DH_anon_WITH_CAMELLIA_256_CBC_SHA", "TLS_PSK_WITH_RC4_128_SHA", "TLS_PSK_WITH_3DES_EDE_CBC_SHA", "TLS_PSK_WITH_AES_128_CBC_SHA", "TLS_PSK_WITH_AES_256_CBC_SHA", "TLS_DHE_PSK_WITH_RC4_128_SHA", "TLS_DHE_PSK_WITH_3DES_EDE_CBC_SHA", "TLS_DHE_PSK_WITH_AES_128_CBC_SHA", "TLS_DHE_PSK_WITH_AES_256_CBC_SHA", "TLS_RSA_PSK_WITH_RC4_128_SHA", "TLS_RSA_PSK_WITH_3DES_EDE_CBC_SHA", "TLS_RSA_PSK_WITH_AES_128_CBC_SHA", "TLS_RSA_PSK_WITH_AES_256_CBC_SHA", "TLS_RSA_WITH_SEED_CBC_SHA", "TLS_DH_DSS_WITH_SEED_CBC_SHA", "TLS_DH_RSA_WITH_SEED_CBC_SHA", "TLS_DHE_DSS_WITH_SEED_CBC_SHA", "TLS_DHE_RSA_WITH_SEED_CBC_SHA", "TLS_DH_anon_WITH_SEED_CBC_SHA", "TLS_RSA_WITH_AES_128_GCM_SHA256", "TLS_RSA_WITH_AES_256_GCM_SHA384", "TLS_DH_RSA_WITH_AES_128_GCM_SHA256", "TLS_DH_RSA_WITH_AES_256_GCM_SHA384", "TLS_DH_DSS_WITH_AES_128_GCM_SHA256", "TLS_DH_DSS_WITH_AES_256_GCM_SHA384", "TLS_DH_anon_WITH_AES_128_GCM_SHA256", "TLS_DH_anon_WITH_AES_256_GCM_SHA384", "TLS_PSK_WITH_AES_128_GCM_SHA256", "TLS_PSK_WITH_AES_256_GCM_SHA384", "TLS_RSA_PSK_WITH_AES_128_GCM_SHA256", "TLS_RSA_PSK_WITH_AES_256_GCM_SHA384", "TLS_PSK_WITH_AES_128_CBC_SHA256", "TLS_PSK_WITH_AES_256_CBC_SHA384", "TLS_PSK_WITH_NULL_SHA256", "TLS_PSK_WITH_NULL_SHA384", "TLS_DHE_PSK_WITH_AES_128_CBC_SHA256", "TLS_DHE_PSK_WITH_AES_256_CBC_SHA384", "TLS_DHE_PSK_WITH_NULL_SHA256", "TLS_DHE_PSK_WITH_NULL_SHA384", "TLS_RSA_PSK_WITH_AES_128_CBC_SHA256", "TLS_RSA_PSK_WITH_AES_256_CBC_SHA384", "TLS_RSA_PSK_WITH_NULL_SHA256", "TLS_RSA_PSK_WITH_NULL_SHA384", "TLS_RSA_WITH_CAMELLIA_128_CBC_SHA256", "TLS_DH_DSS_WITH_CAMELLIA_128_CBC_SHA256", "TLS_DH_RSA_WITH_CAMELLIA_128_CBC_SHA256", "TLS_DHE_DSS_WITH_CAMELLIA_128_CBC_SHA256", "TLS_DHE_RSA_WITH_CAMELLIA_128_CBC_SHA256", "TLS_DH_anon_WITH_CAMELLIA_128_CBC_SHA256", "TLS_RSA_WITH_CAMELLIA_256_CBC_SHA256", "TLS_DH_DSS_WITH_CAMELLIA_256_CBC_SHA256", "TLS_DH_RSA_WITH_CAMELLIA_256_CBC_SHA256", "TLS_DHE_DSS_WITH_CAMELLIA_256_CBC_SHA256", "TLS_DHE_RSA_WITH_CAMELLIA_256_CBC_SHA256", "TLS_DH_anon_WITH_CAMELLIA_256_CBC_SHA256", "TLS_EMPTY_RENEGOTIATION_INFO_SCSV", "TLS_ECDH_ECDSA_WITH_NULL_SHA", "TLS_ECDH_ECDSA_WITH_RC4_128_SHA", "TLS_ECDH_ECDSA_WITH_3DES_EDE_CBC_SHA", "TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA", "TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA", "TLS_ECDHE_ECDSA_WITH_NULL_SHA", "TLS_ECDHE_ECDSA_WITH_RC4_128_SHA", "TLS_ECDHE_ECDSA_WITH_3DES_EDE_CBC_SHA", "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA", "TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA", "TLS_ECDH_RSA_WITH_NULL_SHA", "TLS_ECDH_RSA_WITH_RC4_128_SHA", "TLS_ECDH_RSA_WITH_3DES_EDE_CBC_SHA", "TLS_ECDH_RSA_WITH_AES_128_CBC_SHA", "TLS_ECDH_RSA_WITH_AES_256_CBC_SHA", "TLS_ECDHE_RSA_WITH_NULL_SHA", "TLS_ECDHE_RSA_WITH_RC4_128_SHA", "TLS_ECDHE_RSA_WITH_3DES_EDE_CBC_SHA", "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA", "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA", "TLS_ECDH_anon_WITH_NULL_SHA", "TLS_ECDH_anon_WITH_RC4_128_SHA", "TLS_ECDH_anon_WITH_3DES_EDE_CBC_SHA", "TLS_ECDH_anon_WITH_AES_128_CBC_SHA", "TLS_ECDH_anon_WITH_AES_256_CBC_SHA", "TLS_SRP_SHA_WITH_3DES_EDE_CBC_SHA", "TLS_SRP_SHA_RSA_WITH_3DES_EDE_CBC_SHA", "TLS_SRP_SHA_DSS_WITH_3DES_EDE_CBC_SHA", "TLS_SRP_SHA_WITH_AES_128_CBC_SHA", "TLS_SRP_SHA_RSA_WITH_AES_128_CBC_SHA", "TLS_SRP_SHA_DSS_WITH_AES_128_CBC_SHA", "TLS_SRP_SHA_WITH_AES_256_CBC_SHA", "TLS_SRP_SHA_RSA_WITH_AES_256_CBC_SHA", "TLS_SRP_SHA_DSS_WITH_AES_256_CBC_SHA", "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256", "TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384", "TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA256", "TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA384", "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256", "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384", "TLS_ECDH_RSA_WITH_AES_128_CBC_SHA256", "TLS_ECDH_RSA_WITH_AES_256_CBC_SHA384", "TLS_ECDH_ECDSA_WITH_AES_128_GCM_SHA256", "TLS_ECDH_ECDSA_WITH_AES_256_GCM_SHA384", "TLS_ECDH_RSA_WITH_AES_128_GCM_SHA256", "TLS_ECDH_RSA_WITH_AES_256_GCM_SHA384", "TLS_ECDHE_PSK_WITH_RC4_128_SHA", "TLS_ECDHE_PSK_WITH_3DES_EDE_CBC_SHA", "TLS_ECDHE_PSK_WITH_AES_128_CBC_SHA", "TLS_ECDHE_PSK_WITH_AES_256_CBC_SHA", "TLS_ECDHE_PSK_WITH_AES_128_CBC_SHA256", "TLS_ECDHE_PSK_WITH_AES_256_CBC_SHA384", "TLS_ECDHE_PSK_WITH_NULL_SHA", "TLS_ECDHE_PSK_WITH_NULL_SHA256", "TLS_ECDHE_PSK_WITH_NULL_SHA384", "TLS_RSA_WITH_ARIA_128_CBC_SHA256", "TLS_RSA_WITH_ARIA_256_CBC_SHA384", "TLS_DH_DSS_WITH_ARIA_128_CBC_SHA256", "TLS_DH_DSS_WITH_ARIA_256_CBC_SHA384", "TLS_DH_RSA_WITH_ARIA_128_CBC_SHA256", "TLS_DH_RSA_WITH_ARIA_256_CBC_SHA384", "TLS_DHE_DSS_WITH_ARIA_128_CBC_SHA256", "TLS_DHE_DSS_WITH_ARIA_256_CBC_SHA384", "TLS_DHE_RSA_WITH_ARIA_128_CBC_SHA256", "TLS_DHE_RSA_WITH_ARIA_256_CBC_SHA384", "TLS_DH_anon_WITH_ARIA_128_CBC_SHA256", "TLS_DH_anon_WITH_ARIA_256_CBC_SHA384", "TLS_ECDHE_ECDSA_WITH_ARIA_128_CBC_SHA256", "TLS_ECDHE_ECDSA_WITH_ARIA_256_CBC_SHA384", "TLS_ECDH_ECDSA_WITH_ARIA_128_CBC_SHA256", "TLS_ECDH_ECDSA_WITH_ARIA_256_CBC_SHA384", "TLS_ECDHE_RSA_WITH_ARIA_128_CBC_SHA256", "TLS_ECDHE_RSA_WITH_ARIA_256_CBC_SHA384", "TLS_ECDH_RSA_WITH_ARIA_128_CBC_SHA256", "TLS_ECDH_RSA_WITH_ARIA_256_CBC_SHA384", "TLS_RSA_WITH_ARIA_128_GCM_SHA256", "TLS_RSA_WITH_ARIA_256_GCM_SHA384", "TLS_DH_RSA_WITH_ARIA_128_GCM_SHA256", "TLS_DH_RSA_WITH_ARIA_256_GCM_SHA384", "TLS_DH_DSS_WITH_ARIA_128_GCM_SHA256", "TLS_DH_DSS_WITH_ARIA_256_GCM_SHA384", "TLS_DH_anon_WITH_ARIA_128_GCM_SHA256", "TLS_DH_anon_WITH_ARIA_256_GCM_SHA384", "TLS_ECDH_ECDSA_WITH_ARIA_128_GCM_SHA256", "TLS_ECDH_ECDSA_WITH_ARIA_256_GCM_SHA384", "TLS_ECDH_RSA_WITH_ARIA_128_GCM_SHA256", "TLS_ECDH_RSA_WITH_ARIA_256_GCM_SHA384", "TLS_PSK_WITH_ARIA_128_CBC_SHA256", "TLS_PSK_WITH_ARIA_256_CBC_SHA384", "TLS_DHE_PSK_WITH_ARIA_128_CBC_SHA256", "TLS_DHE_PSK_WITH_ARIA_256_CBC_SHA384", "TLS_RSA_PSK_WITH_ARIA_128_CBC_SHA256", "TLS_RSA_PSK_WITH_ARIA_256_CBC_SHA384", "TLS_PSK_WITH_ARIA_128_GCM_SHA256", "TLS_PSK_WITH_ARIA_256_GCM_SHA384", "TLS_RSA_PSK_WITH_ARIA_128_GCM_SHA256", "TLS_RSA_PSK_WITH_ARIA_256_GCM_SHA384", "TLS_ECDHE_PSK_WITH_ARIA_128_CBC_SHA256", "TLS_ECDHE_PSK_WITH_ARIA_256_CBC_SHA384", "TLS_ECDHE_ECDSA_WITH_CAMELLIA_128_CBC_SHA256", "TLS_ECDHE_ECDSA_WITH_CAMELLIA_256_CBC_SHA384", "TLS_ECDH_ECDSA_WITH_CAMELLIA_128_CBC_SHA256", "TLS_ECDH_ECDSA_WITH_CAMELLIA_256_CBC_SHA384", "TLS_ECDHE_RSA_WITH_CAMELLIA_128_CBC_SHA256", "TLS_ECDHE_RSA_WITH_CAMELLIA_256_CBC_SHA384", "TLS_ECDH_RSA_WITH_CAMELLIA_128_CBC_SHA256", "TLS_ECDH_RSA_WITH_CAMELLIA_256_CBC_SHA384", "TLS_RSA_WITH_CAMELLIA_128_GCM_SHA256", "TLS_RSA_WITH_CAMELLIA_256_GCM_SHA384", "TLS_DH_RSA_WITH_CAMELLIA_128_GCM_SHA256", "TLS_DH_RSA_WITH_CAMELLIA_256_GCM_SHA384", "TLS_DH_DSS_WITH_CAMELLIA_128_GCM_SHA256", "TLS_DH_DSS_WITH_CAMELLIA_256_GCM_SHA384", "TLS_DH_anon_WITH_CAMELLIA_128_GCM_SHA256", "TLS_DH_anon_WITH_CAMELLIA_256_GCM_SHA384", "TLS_ECDH_ECDSA_WITH_CAMELLIA_128_GCM_SHA256", "TLS_ECDH_ECDSA_WITH_CAMELLIA_256_GCM_SHA384", "TLS_ECDH_RSA_WITH_CAMELLIA_128_GCM_SHA256", "TLS_ECDH_RSA_WITH_CAMELLIA_256_GCM_SHA384", "TLS_PSK_WITH_CAMELLIA_128_GCM_SHA256", "TLS_PSK_WITH_CAMELLIA_256_GCM_SHA384", "TLS_RSA_PSK_WITH_CAMELLIA_128_GCM_SHA256", "TLS_RSA_PSK_WITH_CAMELLIA_256_GCM_SHA384", "TLS_PSK_WITH_CAMELLIA_128_CBC_SHA256", "TLS_PSK_WITH_CAMELLIA_256_CBC_SHA384", "TLS_DHE_PSK_WITH_CAMELLIA_128_CBC_SHA256", "TLS_DHE_PSK_WITH_CAMELLIA_256_CBC_SHA384", "TLS_RSA_PSK_WITH_CAMELLIA_128_CBC_SHA256", "TLS_RSA_PSK_WITH_CAMELLIA_256_CBC_SHA384", "TLS_ECDHE_PSK_WITH_CAMELLIA_128_CBC_SHA256", "TLS_ECDHE_PSK_WITH_CAMELLIA_256_CBC_SHA384", "TLS_RSA_WITH_AES_128_CCM", "TLS_RSA_WITH_AES_256_CCM", "TLS_RSA_WITH_AES_128_CCM_8", "TLS_RSA_WITH_AES_256_CCM_8", "TLS_PSK_WITH_AES_128_CCM", "TLS_PSK_WITH_AES_256_CCM", "TLS_PSK_WITH_AES_128_CCM_8", "TLS_PSK_WITH_AES_256_CCM_8" })
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 323 */       __blackCiphers.put(c, Boolean.TRUE);
/*     */     }
/*     */   }
/*     */   
/*     */   public static boolean isBlackListProtocol(String tlsProtocol)
/*     */   {
/* 329 */     Boolean b = (Boolean)__blackProtocols.get(tlsProtocol);
/* 330 */     return (b != null) && (b.booleanValue());
/*     */   }
/*     */   
/*     */   public static boolean isBlackListCipher(String tlsCipher)
/*     */   {
/* 335 */     Boolean b = (Boolean)__blackCiphers.get(tlsCipher);
/* 336 */     return (b != null) && (b.booleanValue());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static class CipherComparator
/*     */     implements Comparator<String>
/*     */   {
/*     */     public int compare(String c1, String c2)
/*     */     {
/* 347 */       boolean b1 = HTTP2Cipher.isBlackListCipher(c1);
/* 348 */       boolean b2 = HTTP2Cipher.isBlackListCipher(c2);
/* 349 */       if (b1 == b2)
/* 350 */         return 0;
/* 351 */       if (b1)
/* 352 */         return 1;
/* 353 */       return -1;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\HTTP2Cipher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */