package com.lucasjosino.hawapi.cache.generator;

import com.lucasjosino.hawapi.services.base.BaseService;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * Generate key for 'findAll' cache methods using only params delimited with <strong>comma</strong>.
 *
 * @see BaseService#findAll(Map, List)
 */
@SuppressWarnings("NullableProblems")
public class FindAllKeyGenerator implements KeyGenerator {

    @Override
    public Object generate(Object target, Method method, Object... params) {
        return StringUtils.arrayToDelimitedString(params, ",");
    }
}
