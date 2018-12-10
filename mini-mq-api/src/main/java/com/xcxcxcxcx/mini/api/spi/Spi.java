package com.xcxcxcxcx.mini.api.spi;

import java.lang.annotation.*;

/**
 *
 * spi
 * @author XCXCXCXCX
 * @Since 1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Spi {

    /**
     * SPI name
     *
     * @return name
     */
    String value() default "";

    /**
     * 排序顺序
     *
     * @return sortNo
     */
    int order() default 0;

}