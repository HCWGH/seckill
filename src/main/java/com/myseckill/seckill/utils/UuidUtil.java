package com.myseckill.seckill.utils;

import org.junit.Test;

import java.util.UUID;

/**
 * 生成uuid做为session的一部分
 * @author HCW
 * @date 2020/12/30-20:55
 */
public class UuidUtil {
    public static String getUuid(){
        return UUID.randomUUID().toString().replace("-","");
    }
   @Test
    public void testUuid(){
       System.out.println(getUuid());
   }


}
