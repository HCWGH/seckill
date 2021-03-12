package com.myseckill.seckill.utils;

import org.apache.commons.codec.digest.DigestUtils;

/**MD5密码加密
 * @author HCW
 * @date 2020/12/29-12:53
 */
public class MD5 {
    private static String salt="h96c06w27";

    public static String md5(String src){
        return DigestUtils.md5Hex(src);
    }

    /**
     * 第一次MD5加密，前台传数据到后台
     * @param inputPassword
     * @return
     */
    public static String passwordWebToBackstage(String inputPassword){
        String str = ""+salt.charAt(0)+salt.charAt(2) + inputPassword
                +salt.charAt(5) + salt.charAt(4);
        return  md5(str);
    }

    /**
     * 第二次加密从后台到数据库
     * @param inputPassword
     * @param salt //随机盐
     * @return
     */
    public static String backstageToDB(String inputPassword,String salt){
        String s = ""+salt.charAt(0)+salt.charAt(2) + inputPassword +salt.charAt(5)
                + salt.charAt(4);
        return  md5(s);
    }

    /**
     * 两次加密的整体调用
     * @param password
     * @param salt
     * @return
     */
    public static String inputWebToDB(String password,String salt){
        String passwordWebToBackstage = passwordWebToBackstage(password);
        String backstageToDB = backstageToDB(passwordWebToBackstage, salt);
        return  backstageToDB;
    }

    public static void main(String[] args) {
        String passwordWebToBackstage = passwordWebToBackstage("627627");
        System.out.println(passwordWebToBackstage);
        String backstageToDB = backstageToDB(passwordWebToBackstage, "hcw627");
        System.out.println(backstageToDB);
        System.out.println("==============");
        String inputWebToDB = inputWebToDB("627627", "hcw627");
        System.out.println(inputWebToDB);
    }
}
