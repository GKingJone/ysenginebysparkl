/*     */ package com.facebook.presto.jdbc.internal.jackson.core.json;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator.Feature;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.ObjectCodec;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.SerializableString;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.Version;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.base.GeneratorBase;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.io.CharTypes;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.io.CharacterEscapes;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.io.IOContext;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.util.DefaultPrettyPrinter;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.util.VersionUtil;
/*     */ import java.io.IOException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class JsonGeneratorImpl
/*     */   extends GeneratorBase
/*     */ {
/*  31 */   protected static final int[] sOutputEscapes = ;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected final IOContext _ioContext;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  53 */   protected int[] _outputEscapes = sOutputEscapes;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected int _maximumNonEscapedChar;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected CharacterEscapes _characterEscapes;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  84 */   protected SerializableString _rootValueSeparator = DefaultPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected boolean _cfgUnqNames;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public JsonGeneratorImpl(IOContext ctxt, int features, ObjectCodec codec)
/*     */   {
/* 103 */     super(features, codec);
/* 104 */     this._ioContext = ctxt;
/* 105 */     if (JsonGenerator.Feature.ESCAPE_NON_ASCII.enabledIn(features))
/*     */     {
/* 107 */       this._maximumNonEscapedChar = 127;
/*     */     }
/* 109 */     this._cfgUnqNames = (!JsonGenerator.Feature.QUOTE_FIELD_NAMES.enabledIn(features));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public JsonGenerator enable(JsonGenerator.Feature f)
/*     */   {
/* 120 */     super.enable(f);
/* 121 */     if (f == JsonGenerator.Feature.QUOTE_FIELD_NAMES) {
/* 122 */       this._cfgUnqNames = false;
/*     */     }
/* 124 */     return this;
/*     */   }
/*     */   
/*     */   public JsonGenerator disable(JsonGenerator.Feature f)
/*     */   {
/* 129 */     super.disable(f);
/* 130 */     if (f == JsonGenerator.Feature.QUOTE_FIELD_NAMES) {
/* 131 */       this._cfgUnqNames = true;
/*     */     }
/* 133 */     return this;
/*     */   }
/*     */   
/*     */   protected void _checkStdFeatureChanges(int newFeatureFlags, int changedFeatures)
/*     */   {
/* 138 */     super._checkStdFeatureChanges(newFeatureFlags, changedFeatures);
/* 139 */     this._cfgUnqNames = (!JsonGenerator.Feature.QUOTE_FIELD_NAMES.enabledIn(newFeatureFlags));
/*     */   }
/*     */   
/*     */   public JsonGenerator setHighestNonEscapedChar(int charCode)
/*     */   {
/* 144 */     this._maximumNonEscapedChar = (charCode < 0 ? 0 : charCode);
/* 145 */     return this;
/*     */   }
/*     */   
/*     */   public int getHighestEscapedChar()
/*     */   {
/* 150 */     return this._maximumNonEscapedChar;
/*     */   }
/*     */   
/*     */ 
/*     */   public JsonGenerator setCharacterEscapes(CharacterEscapes esc)
/*     */   {
/* 156 */     this._characterEscapes = esc;
/* 157 */     if (esc == null) {
/* 158 */       this._outputEscapes = sOutputEscapes;
/*     */     } else {
/* 160 */       this._outputEscapes = esc.getEscapeCodesForAscii();
/*     */     }
/* 162 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public CharacterEscapes getCharacterEscapes()
/*     */   {
/* 171 */     return this._characterEscapes;
/*     */   }
/*     */   
/*     */   public JsonGenerator setRootValueSeparator(SerializableString sep)
/*     */   {
/* 176 */     this._rootValueSeparator = sep;
/* 177 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Version version()
/*     */   {
/* 188 */     return VersionUtil.versionFor(getClass());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final void writeStringField(String fieldName, String value)
/*     */     throws IOException
/*     */   {
/* 202 */     writeFieldName(fieldName);
/* 203 */     writeString(value);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\core\json\JsonGeneratorImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */