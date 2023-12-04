/*     */ package com.facebook.presto.jdbc.internal.jetty.http.pathmap;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.TypeUtil;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
/*     */ import java.util.Set;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class UriTemplatePathSpec
/*     */   extends RegexPathSpec
/*     */ {
/*  43 */   private static final Logger LOG = Log.getLogger(UriTemplatePathSpec.class);
/*     */   
/*  45 */   private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{(.*)\\}");
/*     */   
/*     */ 
/*     */ 
/*     */   private static final String VARIABLE_RESERVED = ":/?#[]@!$&'()*+,;=";
/*     */   
/*     */ 
/*     */   private static final String VARIABLE_SYMBOLS = "-._";
/*     */   
/*     */ 
/*  55 */   private static final Set<String> FORBIDDEN_SEGMENTS = new HashSet();
/*  56 */   static { FORBIDDEN_SEGMENTS.add("/./");
/*  57 */     FORBIDDEN_SEGMENTS.add("/../");
/*  58 */     FORBIDDEN_SEGMENTS.add("//");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public UriTemplatePathSpec(String rawSpec)
/*     */   {
/*  66 */     Objects.requireNonNull(rawSpec, "Path Param Spec cannot be null");
/*     */     
/*  68 */     if (("".equals(rawSpec)) || ("/".equals(rawSpec)))
/*     */     {
/*  70 */       this.pathSpec = "/";
/*  71 */       this.pattern = Pattern.compile("^/$");
/*  72 */       this.pathDepth = 1;
/*  73 */       this.specLength = 1;
/*  74 */       this.variables = new String[0];
/*  75 */       this.group = PathSpecGroup.EXACT; return;
/*     */     }
/*     */     
/*     */     StringBuilder err;
/*  79 */     if (rawSpec.charAt(0) != '/')
/*     */     {
/*     */ 
/*  82 */       err = new StringBuilder();
/*  83 */       err.append("Syntax Error: path spec \"");
/*  84 */       err.append(rawSpec);
/*  85 */       err.append("\" must start with '/'");
/*  86 */       throw new IllegalArgumentException(err.toString());
/*     */     }
/*     */     
/*  89 */     for (String forbidden : FORBIDDEN_SEGMENTS)
/*     */     {
/*  91 */       if (rawSpec.contains(forbidden))
/*     */       {
/*  93 */         StringBuilder err = new StringBuilder();
/*  94 */         err.append("Syntax Error: segment ");
/*  95 */         err.append(forbidden);
/*  96 */         err.append(" is forbidden in path spec: ");
/*  97 */         err.append(rawSpec);
/*  98 */         throw new IllegalArgumentException(err.toString());
/*     */       }
/*     */     }
/*     */     
/* 102 */     this.pathSpec = rawSpec;
/*     */     
/* 104 */     StringBuilder regex = new StringBuilder();
/* 105 */     regex.append('^');
/*     */     
/* 107 */     List<String> varNames = new ArrayList();
/*     */     
/* 109 */     String[] segments = rawSpec.substring(1).split("/");
/* 110 */     char[] segmentSignature = new char[segments.length];
/* 111 */     this.pathDepth = segments.length;
/* 112 */     for (int i = 0; i < segments.length; i++)
/*     */     {
/* 114 */       String segment = segments[i];
/* 115 */       Matcher mat = VARIABLE_PATTERN.matcher(segment);
/*     */       StringBuilder err;
/* 117 */       if (mat.matches())
/*     */       {
/*     */ 
/* 120 */         String variable = mat.group(1);
/* 121 */         if (varNames.contains(variable))
/*     */         {
/*     */ 
/* 124 */           err = new StringBuilder();
/* 125 */           err.append("Syntax Error: variable ");
/* 126 */           err.append(variable);
/* 127 */           err.append(" is duplicated in path spec: ");
/* 128 */           err.append(rawSpec);
/* 129 */           throw new IllegalArgumentException(err.toString());
/*     */         }
/*     */         
/* 132 */         assertIsValidVariableLiteral(variable);
/*     */         
/* 134 */         segmentSignature[i] = 'v';
/*     */         
/* 136 */         varNames.add(variable);
/*     */         
/* 138 */         regex.append("/([^/]+)");
/*     */       } else {
/* 140 */         if (mat.find(0))
/*     */         {
/*     */ 
/* 143 */           StringBuilder err = new StringBuilder();
/* 144 */           err.append("Syntax Error: variable ");
/* 145 */           err.append(mat.group());
/* 146 */           err.append(" must exist as entire path segment: ");
/* 147 */           err.append(rawSpec);
/* 148 */           throw new IllegalArgumentException(err.toString());
/*     */         }
/* 150 */         if ((segment.indexOf('{') >= 0) || (segment.indexOf('}') >= 0))
/*     */         {
/*     */ 
/* 153 */           StringBuilder err = new StringBuilder();
/* 154 */           err.append("Syntax Error: invalid path segment /");
/* 155 */           err.append(segment);
/* 156 */           err.append("/ variable declaration incomplete: ");
/* 157 */           err.append(rawSpec);
/* 158 */           throw new IllegalArgumentException(err.toString());
/*     */         }
/* 160 */         if (segment.indexOf('*') >= 0)
/*     */         {
/*     */ 
/* 163 */           err = new StringBuilder();
/* 164 */           err.append("Syntax Error: path segment /");
/* 165 */           err.append(segment);
/* 166 */           err.append("/ contains a wildcard symbol (not supported by this uri-template implementation): ");
/* 167 */           err.append(rawSpec);
/* 168 */           throw new IllegalArgumentException(err.toString());
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 173 */         segmentSignature[i] = 'e';
/*     */         
/* 175 */         regex.append('/');
/*     */         
/* 177 */         StringBuilder err = segment.toCharArray();err = err.length; for (StringBuilder localStringBuilder1 = 0; localStringBuilder1 < err; localStringBuilder1++) { char c = err[localStringBuilder1];
/*     */           
/* 179 */           if ((c == '.') || (c == '[') || (c == ']') || (c == '\\'))
/*     */           {
/* 181 */             regex.append('\\');
/*     */           }
/* 183 */           regex.append(c);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 189 */     if (rawSpec.charAt(rawSpec.length() - 1) == '/')
/*     */     {
/* 191 */       regex.append('/');
/*     */     }
/*     */     
/* 194 */     regex.append('$');
/*     */     
/* 196 */     this.pattern = Pattern.compile(regex.toString());
/*     */     
/* 198 */     int varcount = varNames.size();
/* 199 */     this.variables = ((String[])varNames.toArray(new String[varcount]));
/*     */     
/*     */ 
/* 202 */     String sig = String.valueOf(segmentSignature);
/*     */     
/* 204 */     if (Pattern.matches("^e*$", sig))
/*     */     {
/* 206 */       this.group = PathSpecGroup.EXACT;
/*     */     }
/* 208 */     else if (Pattern.matches("^e*v+", sig))
/*     */     {
/* 210 */       this.group = PathSpecGroup.PREFIX_GLOB;
/*     */     }
/* 212 */     else if (Pattern.matches("^v+e+", sig))
/*     */     {
/* 214 */       this.group = PathSpecGroup.SUFFIX_GLOB;
/*     */     }
/*     */     else
/*     */     {
/* 218 */       this.group = PathSpecGroup.MIDDLE_GLOB;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private String[] variables;
/*     */   
/*     */ 
/*     */   private void assertIsValidVariableLiteral(String variable)
/*     */   {
/* 229 */     int len = variable.length();
/*     */     
/* 231 */     int i = 0;
/*     */     
/* 233 */     boolean valid = len > 0;
/*     */     
/* 235 */     while ((valid) && (i < len))
/*     */     {
/* 237 */       int codepoint = variable.codePointAt(i);
/* 238 */       i += Character.charCount(codepoint);
/*     */       
/*     */ 
/* 241 */       if ((!isValidBasicLiteralCodepoint(codepoint)) && 
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 247 */         (!Character.isSupplementaryCodePoint(codepoint)))
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 253 */         if (codepoint == 37)
/*     */         {
/* 255 */           if (i + 2 > len)
/*     */           {
/*     */ 
/* 258 */             valid = false;
/*     */           }
/*     */           else {
/* 261 */             codepoint = TypeUtil.convertHexDigit(variable.codePointAt(i++)) << 4;
/* 262 */             codepoint |= TypeUtil.convertHexDigit(variable.codePointAt(i++));
/*     */             
/*     */ 
/* 265 */             if (isValidBasicLiteralCodepoint(codepoint)) {}
/*     */           }
/*     */           
/*     */ 
/*     */         }
/*     */         else
/* 271 */           valid = false;
/*     */       }
/*     */     }
/* 274 */     if (!valid)
/*     */     {
/*     */ 
/* 277 */       StringBuilder err = new StringBuilder();
/* 278 */       err.append("Syntax Error: variable {");
/* 279 */       err.append(variable);
/* 280 */       err.append("} an invalid variable name: ");
/* 281 */       err.append(this.pathSpec);
/* 282 */       throw new IllegalArgumentException(err.toString());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private boolean isValidBasicLiteralCodepoint(int codepoint)
/*     */   {
/* 289 */     if (((codepoint >= 97) && (codepoint <= 122)) || ((codepoint >= 65) && (codepoint <= 90)) || ((codepoint >= 48) && (codepoint <= 57)))
/*     */     {
/*     */ 
/*     */ 
/* 293 */       return true;
/*     */     }
/*     */     
/*     */ 
/* 297 */     if ("-._".indexOf(codepoint) >= 0)
/*     */     {
/* 299 */       return true;
/*     */     }
/*     */     
/*     */ 
/* 303 */     if (":/?#[]@!$&'()*+,;=".indexOf(codepoint) >= 0)
/*     */     {
/* 305 */       LOG.warn("Detected URI Template reserved symbol [{}] in path spec \"{}\"", new Object[] { Character.valueOf((char)codepoint), this.pathSpec });
/* 306 */       return false;
/*     */     }
/*     */     
/* 309 */     return false;
/*     */   }
/*     */   
/*     */   public Map<String, String> getPathParams(String path)
/*     */   {
/* 314 */     Matcher matcher = getMatcher(path);
/* 315 */     if (matcher.matches())
/*     */     {
/* 317 */       if (this.group == PathSpecGroup.EXACT)
/*     */       {
/* 319 */         return Collections.emptyMap();
/*     */       }
/* 321 */       Map<String, String> ret = new HashMap();
/* 322 */       int groupCount = matcher.groupCount();
/* 323 */       for (int i = 1; i <= groupCount; i++)
/*     */       {
/* 325 */         ret.put(this.variables[(i - 1)], matcher.group(i));
/*     */       }
/* 327 */       return ret;
/*     */     }
/* 329 */     return null;
/*     */   }
/*     */   
/*     */   public int getVariableCount()
/*     */   {
/* 334 */     return this.variables.length;
/*     */   }
/*     */   
/*     */   public String[] getVariables()
/*     */   {
/* 339 */     return this.variables;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http\pathmap\UriTemplatePathSpec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */