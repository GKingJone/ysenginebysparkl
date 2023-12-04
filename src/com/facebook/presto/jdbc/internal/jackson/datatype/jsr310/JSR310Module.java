/*     */ package com.facebook.presto.jdbc.internal.jackson.datatype.jsr310;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanDescription;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationConfig;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.Module.SetupContext;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.ValueInstantiator;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.ValueInstantiators.Base;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.std.StdValueInstantiator;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotatedClass;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotatedMethod;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotatedParameter;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.module.SimpleModule;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.std.ToStringSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.deser.DurationDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.deser.InstantDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.deser.JSR310StringParsableDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.deser.LocalDateDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.deser.MonthDayDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.deser.OffsetTimeDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.deser.YearDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.deser.YearMonthDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.deser.key.DurationKeyDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.deser.key.InstantKeyDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.deser.key.LocalDateKeyDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.deser.key.LocalDateTimeKeyDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.deser.key.LocalTimeKeyDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.deser.key.MonthDayKeyDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.deser.key.OffsetDateTimeKeyDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.deser.key.OffsetTimeKeyDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.deser.key.PeriodKeyDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.deser.key.YearKeyDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.deser.key.YearMothKeyDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.deser.key.ZoneIdKeyDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.deser.key.ZoneOffsetKeyDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.deser.key.ZonedDateTimeKeyDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.ser.DurationSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.ser.InstantSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.ser.LocalDateSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.ser.LocalTimeSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.ser.MonthDaySerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.ser.OffsetDateTimeSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.ser.OffsetTimeSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.ser.YearMonthSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.ser.YearSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.ser.ZonedDateTimeWithZoneIdSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.ser.key.ZonedDateTimeKeySerializer;
/*     */ import java.time.Duration;
/*     */ import java.time.Instant;
/*     */ import java.time.LocalDate;
/*     */ import java.time.LocalDateTime;
/*     */ import java.time.LocalTime;
/*     */ import java.time.MonthDay;
/*     */ import java.time.OffsetDateTime;
/*     */ import java.time.OffsetTime;
/*     */ import java.time.Period;
/*     */ import java.time.Year;
/*     */ import java.time.YearMonth;
/*     */ import java.time.ZoneId;
/*     */ import java.time.ZoneOffset;
/*     */ import java.time.ZonedDateTime;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Deprecated
/*     */ public final class JSR310Module
/*     */   extends SimpleModule
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   
/*     */   public JSR310Module()
/*     */   {
/* 118 */     super(PackageVersion.VERSION);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 123 */     addDeserializer(Instant.class, InstantDeserializer.INSTANT);
/* 124 */     addDeserializer(OffsetDateTime.class, InstantDeserializer.OFFSET_DATE_TIME);
/* 125 */     addDeserializer(ZonedDateTime.class, InstantDeserializer.ZONED_DATE_TIME);
/*     */     
/*     */ 
/* 128 */     addDeserializer(Duration.class, DurationDeserializer.INSTANCE);
/* 129 */     addDeserializer(LocalDateTime.class, LocalDateTimeDeserializer.INSTANCE);
/* 130 */     addDeserializer(LocalDate.class, LocalDateDeserializer.INSTANCE);
/* 131 */     addDeserializer(LocalTime.class, LocalTimeDeserializer.INSTANCE);
/* 132 */     addDeserializer(MonthDay.class, MonthDayDeserializer.INSTANCE);
/* 133 */     addDeserializer(OffsetTime.class, OffsetTimeDeserializer.INSTANCE);
/* 134 */     addDeserializer(Period.class, JSR310StringParsableDeserializer.PERIOD);
/* 135 */     addDeserializer(Year.class, YearDeserializer.INSTANCE);
/* 136 */     addDeserializer(YearMonth.class, YearMonthDeserializer.INSTANCE);
/* 137 */     addDeserializer(ZoneId.class, JSR310StringParsableDeserializer.ZONE_ID);
/* 138 */     addDeserializer(ZoneOffset.class, JSR310StringParsableDeserializer.ZONE_OFFSET);
/*     */     
/*     */ 
/* 141 */     addSerializer(Duration.class, DurationSerializer.INSTANCE);
/* 142 */     addSerializer(Instant.class, InstantSerializer.INSTANCE);
/* 143 */     addSerializer(LocalDateTime.class, LocalDateTimeSerializer.INSTANCE);
/* 144 */     addSerializer(LocalDate.class, LocalDateSerializer.INSTANCE);
/* 145 */     addSerializer(LocalTime.class, LocalTimeSerializer.INSTANCE);
/* 146 */     addSerializer(MonthDay.class, MonthDaySerializer.INSTANCE);
/* 147 */     addSerializer(OffsetDateTime.class, OffsetDateTimeSerializer.INSTANCE);
/* 148 */     addSerializer(OffsetTime.class, OffsetTimeSerializer.INSTANCE);
/* 149 */     addSerializer(Period.class, new ToStringSerializer(Period.class));
/* 150 */     addSerializer(Year.class, YearSerializer.INSTANCE);
/* 151 */     addSerializer(YearMonth.class, YearMonthSerializer.INSTANCE);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 156 */     addSerializer(ZonedDateTime.class, _zonedWithZoneId());
/*     */     
/* 158 */     addSerializer(ZoneId.class, new ToStringSerializer(ZoneId.class));
/*     */     
/* 160 */     addSerializer(ZoneOffset.class, new ToStringSerializer(ZoneOffset.class));
/*     */     
/*     */ 
/* 163 */     addKeySerializer(ZonedDateTime.class, ZonedDateTimeKeySerializer.INSTANCE);
/*     */     
/*     */ 
/* 166 */     addKeyDeserializer(Duration.class, DurationKeyDeserializer.INSTANCE);
/* 167 */     addKeyDeserializer(Instant.class, InstantKeyDeserializer.INSTANCE);
/* 168 */     addKeyDeserializer(LocalDateTime.class, LocalDateTimeKeyDeserializer.INSTANCE);
/* 169 */     addKeyDeserializer(LocalDate.class, LocalDateKeyDeserializer.INSTANCE);
/* 170 */     addKeyDeserializer(LocalTime.class, LocalTimeKeyDeserializer.INSTANCE);
/* 171 */     addKeyDeserializer(MonthDay.class, MonthDayKeyDeserializer.INSTANCE);
/* 172 */     addKeyDeserializer(OffsetDateTime.class, OffsetDateTimeKeyDeserializer.INSTANCE);
/* 173 */     addKeyDeserializer(OffsetTime.class, OffsetTimeKeyDeserializer.INSTANCE);
/* 174 */     addKeyDeserializer(Period.class, PeriodKeyDeserializer.INSTANCE);
/* 175 */     addKeyDeserializer(Year.class, YearKeyDeserializer.INSTANCE);
/* 176 */     addKeyDeserializer(YearMonth.class, YearMothKeyDeserializer.INSTANCE);
/* 177 */     addKeyDeserializer(ZonedDateTime.class, ZonedDateTimeKeyDeserializer.INSTANCE);
/* 178 */     addKeyDeserializer(ZoneId.class, ZoneIdKeyDeserializer.INSTANCE);
/* 179 */     addKeyDeserializer(ZoneOffset.class, ZoneOffsetKeyDeserializer.INSTANCE);
/*     */   }
/*     */   
/*     */   private static JsonSerializer<ZonedDateTime> _zonedWithZoneId() {
/* 183 */     return ZonedDateTimeWithZoneIdSerializer.INSTANCE;
/*     */   }
/*     */   
/*     */   public void setupModule(Module.SetupContext context)
/*     */   {
/* 188 */     super.setupModule(context);
/* 189 */     context.addValueInstantiators(new ValueInstantiators.Base()
/*     */     {
/*     */ 
/*     */       public ValueInstantiator findValueInstantiator(DeserializationConfig config, BeanDescription beanDesc, ValueInstantiator defaultInstantiator)
/*     */       {
/* 194 */         JavaType type = beanDesc.getType();
/* 195 */         Class<?> raw = type.getRawClass();
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 200 */         if (ZoneId.class.isAssignableFrom(raw))
/*     */         {
/* 202 */           if ((defaultInstantiator instanceof StdValueInstantiator)) {
/* 203 */             StdValueInstantiator inst = (StdValueInstantiator)defaultInstantiator;
/*     */             AnnotatedClass ac;
/*     */             AnnotatedClass ac;
/* 206 */             if (raw == ZoneId.class) {
/* 207 */               ac = beanDesc.getClassInfo();
/*     */             }
/*     */             else
/*     */             {
/* 211 */               ac = AnnotatedClass.construct(config.constructType(ZoneId.class), config);
/*     */             }
/* 213 */             if (!inst.canCreateFromString()) {
/* 214 */               AnnotatedMethod factory = JSR310Module.this._findFactory(ac, "of", new Class[] { String.class });
/* 215 */               if (factory != null) {
/* 216 */                 inst.configureFromStringCreator(factory);
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */         
/*     */ 
/* 223 */         return defaultInstantiator;
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   protected AnnotatedMethod _findFactory(AnnotatedClass cls, String name, Class<?>... argTypes)
/*     */   {
/* 231 */     int argCount = argTypes.length;
/* 232 */     for (AnnotatedMethod method : cls.getStaticMethods())
/* 233 */       if ((name.equals(method.getName())) && 
/* 234 */         (method.getParameterCount() == argCount))
/*     */       {
/*     */ 
/* 237 */         for (int i = 0; i < argCount; i++) {
/* 238 */           Class<?> argType = method.getParameter(i).getRawType();
/* 239 */           if (!argType.isAssignableFrom(argTypes[i])) {}
/*     */         }
/*     */         
/*     */ 
/* 243 */         return method;
/*     */       }
/* 245 */     return null;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\jsr310\JSR310Module.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */