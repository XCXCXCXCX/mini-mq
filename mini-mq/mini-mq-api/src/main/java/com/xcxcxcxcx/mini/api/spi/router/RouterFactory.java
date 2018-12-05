package com.xcxcxcxcx.mini.api.spi.router;

import com.xcxcxcxcx.mini.api.spi.SpiLoader;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class RouterFactory {

    private static class RouterHolder{
        private static Router service = SpiLoader.load(Router.class);
    }

    public static Router create(){
        return RouterHolder.service;
    }

}
