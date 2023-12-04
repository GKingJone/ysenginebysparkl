/*     */ package com.facebook.presto.jdbc.internal.jackson.datatype.jsr310;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanDescription;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationConfig;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
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
/*     */ import com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class JavaTimeModule
/*     */   extends SimpleModule
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   
/*     */   public JavaTimeModule()
/*     */   {
/* 133 */     super(PackageVersion.VERSION);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 138 */     addDeserializer(Instant.class, InstantDeserializer.INSTANT);
/* 139 */     addDeserializer(OffsetDateTime.class, InstantDeserializer.OFFSET_DATE_TIME);
/* 140 */     addDeserializer(ZonedDateTime.class, InstantDeserializer.ZONED_DATE_TIME);
/*     */     
/*     */ 
/* 143 */     addDeserializer(Duration.class, DurationDeserializer.INSTANCE);
/* 144 */     addDeserializer(LocalDateTime.class, LocalDateTimeDeserializer.INSTANCE);
/* 145 */     addDeserializer(LocalDate.class, LocalDateDeserializer.INSTANCE);
/* 146 */     addDeserializer(LocalTime.class, LocalTimeDeserializer.INSTANCE);
/* 147 */     addDeserializer(MonthDay.class, MonthDayDeserializer.INSTANCE);
/* 148 */     addDeserializer(OffsetTime.class, OffsetTimeDeserializer.INSTANCE);
/* 149 */     addDeserializer(Period.class, JSR310StringParsableDeserializer.PERIOD);
/* 150 */     addDeserializer(Year.class, YearDeserializer.INSTANCE);
/* 151 */     addDeserializer(YearMonth.class, YearMonthDeserializer.INSTANCE);
/* 152 */     addDeserializer(ZoneId.class, JSR310StringParsableDeserializer.ZONE_ID);
/* 153 */     addDeserializer(ZoneOffset.class, JSR310StringParsableDeserializer.ZONE_OFFSET);
/*     */     
/*     */ 
/*     */ 
/* 157 */     addSerializer(Duration.class, DurationSerializer.INSTANCE);
/* 158 */     addSerializer(Instant.class, InstantSerializer.INSTANCE);
/* 159 */     addSerializer(LocalDateTime.class, LocalDateTimeSerializer.INSTANCE);
/* 160 */     addSerializer(LocalDate.class, LocalDateSerializer.INSTANCE);
/* 161 */     addSerializer(LocalTime.class, LocalTimeSerializer.INSTANCE);
/* 162 */     addSerializer(MonthDay.class, MonthDaySerializer.INSTANCE);
/* 163 */     addSerializer(OffsetDateTime.class, OffsetDateTimeSerializer.INSTANCE);
/* 164 */     addSerializer(OffsetTime.class, OffsetTimeSerializer.INSTANCE);
/* 165 */     addSerializer(Period.class, new ToStringSerializer(Period.class));
/* 166 */     addSerializer(Year.class, YearSerializer.INSTANCE);
/* 167 */     addSerializer(YearMonth.class, YearMonthSerializer.INSTANCE);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 174 */     addSerializer(ZonedDateTime.class, ZonedDateTimeSerializer.INSTANCE);
/*     */     
/*     */ 
/* 177 */     addSerializer(ZoneId.class, new ToStringSerializer(ZoneId.class));
/*     */     
/* 179 */     addSerializer(ZoneOffset.class, new ToStringSerializer(ZoneOffset.class));
/*     */     
/*     */ 
/* 182 */     addKeySerializer(ZonedDateTime.class, ZonedDateTimeKeySerializer.INSTANCE);
/*     */     
/*     */ 
/* 185 */     addKeyDeserializer(Duration.class, DurationKeyDeserializer.INSTANCE);
/* 186 */     addKeyDeserializer(Instant.class, InstantKeyDeserializer.INSTANCE);
/* 187 */     addKeyDeserializer(LocalDateTime.class, LocalDateTimeKeyDeserializer.INSTANCE);
/* 188 */     addKeyDeserializer(LocalDate.class, LocalDateKeyDeserializer.INSTANCE);
/* 189 */     addKeyDeserializer(LocalTime.class, LocalTimeKeyDeserializer.INSTANCE);
/* 190 */     addKeyDeserializer(MonthDay.class, MonthDayKeyDeserializer.INSTANCE);
/* 191 */     addKeyDeserializer(OffsetDateTime.class, OffsetDateTimeKeyDeserializer.INSTANCE);
/* 192 */     addKeyDeserializer(OffsetTime.class, OffsetTimeKeyDeserializer.INSTANCE);
/* 193 */     addKeyDeserializer(Period.class, PeriodKeyDeserializer.INSTANCE);
/* 194 */     addKeyDeserializer(Year.class, YearKeyDeserializer.INSTANCE);
/* 195 */     addKeyDeserializer(YearMonth.class, YearMothKeyDeserializer.INSTANCE);
/* 196 */     addKeyDeserializer(ZonedDateTime.class, ZonedDateTimeKeyDeserializer.INSTANCE);
/* 197 */     addKeyDeserializer(ZoneId.class, ZoneIdKeyDeserializer.INSTANCE);
/* 198 */     addKeyDeserializer(ZoneOffset.class, ZoneOffsetKeyDeserializer.INSTANCE);
/*     */   }
/*     */   
/*     */   public void setupModule(Module.SetupContext context)
/*     */   {
/* 203 */     super.setupModule(context);
/* 204 */     context.addValueInstantiators(new ValueInstantiators.Base()
/*     */     {
/*     */ 
/*     */       public ValueInstantiator findValueInstantiator(DeserializationConfig config, BeanDescription beanDesc, ValueInstantiator defaultInstantiator)
/*     */       {
/* 209 */         JavaType type = beanDesc.getType();
/* 210 */         Class<?> raw = type.getRawClass();
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 216 */         if (ZoneId.class.isAssignableFrom(raw))
/*     */         {
/* 218 */           if ((defaultInstantiator instanceof StdValueInstantiator)) {
/* 219 */             StdValueInstantiator inst = (StdValueInstantiator)defaultInstantiator;
/*     */             AnnotatedClass ac;
/*     */             AnnotatedClass ac;
/* 222 */             if (raw == ZoneId.class) {
/* 223 */               ac = beanDesc.getClassInfo();
/*     */             }
/*     */             else
/*     */             {
/* 227 */               ac = AnnotatedClass.construct(config.constructType(ZoneId.class), config);
/*     */             }
/* 229 */             if (!inst.canCreateFromString()) {
/* 230 */               AnnotatedMethod factory = JavaTimeModule.this._findFactory(ac, "of", new Class[] { String.class });
/* 231 */               if (factory != null) {
/* 232 */                 inst.configureFromStringCreator(factory);
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */         
/*     */ 
/* 239 */         return defaultInstantiator;
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   protected AnnotatedMethod _findFactory(AnnotatedClass cls, String name, Class<?>... argTypes)
/*     */   {
/* 247 */     int argCount = argTypes.length;
/* 248 */     for (AnnotatedMethod method : cls.getStaticMethods())
/* 249 */       if ((name.equals(method.getName())) && 
/* 250 */         (method.getParameterCount() == argCount))
/*     */       {
/*     */ 
/* 253 */         for (int i = 0; i < argCount; i++) {
/* 254 */           Class<?> argType = method.getParameter(i).getRawType();
/* 255 */           if (!argType.isAssignableFrom(argTypes[i])) {}
/*     */         }
/*     */         
/*     */ 
/* 259 */         return method;
/*     */       }
/* 261 */     return null;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\jsr310\JavaTimeModule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */