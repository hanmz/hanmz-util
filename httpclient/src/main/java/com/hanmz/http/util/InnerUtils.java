package com.hanmz.http.util;

import lombok.experimental.UtilityClass;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

/**
 * *
 * Created by hanmz on 2017/10/4.
 */
@UtilityClass
public class InnerUtils {
  public boolean isNullOrEmpty(Object obj) {
    if (obj == null) {
      return true;
    }
    Class<?> clz = obj.getClass();
    if (clz.isArray()) {
      return Array.getLength(obj) == 0;
    } else if (Collection.class.isAssignableFrom(clz)) {
      return ((Collection<?>) obj).isEmpty();
    } else if (Map.class.isAssignableFrom(clz)) {
      return ((Map<?, ?>) obj).isEmpty();
    }
    return false;
  }
}
