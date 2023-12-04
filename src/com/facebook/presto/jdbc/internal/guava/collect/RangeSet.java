package com.facebook.presto.jdbc.internal.guava.collect;

import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
import java.util.Set;
import javax.annotation.Nullable;

@Beta
public abstract interface RangeSet<C extends Comparable>
{
  public abstract boolean contains(C paramC);
  
  public abstract Range<C> rangeContaining(C paramC);
  
  public abstract boolean encloses(Range<C> paramRange);
  
  public abstract boolean enclosesAll(RangeSet<C> paramRangeSet);
  
  public abstract boolean isEmpty();
  
  public abstract Range<C> span();
  
  public abstract Set<Range<C>> asRanges();
  
  public abstract RangeSet<C> complement();
  
  public abstract RangeSet<C> subRangeSet(Range<C> paramRange);
  
  public abstract void add(Range<C> paramRange);
  
  public abstract void remove(Range<C> paramRange);
  
  public abstract void clear();
  
  public abstract void addAll(RangeSet<C> paramRangeSet);
  
  public abstract void removeAll(RangeSet<C> paramRangeSet);
  
  public abstract boolean equals(@Nullable Object paramObject);
  
  public abstract int hashCode();
  
  public abstract String toString();
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\collect\RangeSet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */