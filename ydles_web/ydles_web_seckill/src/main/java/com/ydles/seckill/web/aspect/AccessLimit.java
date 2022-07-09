package com.ydles.seckill.web.aspect;

import java.lang.annotation.*;

/**
 * @author Scz
 * @date 2022/4/25 21:36
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AccessLimit {
}
