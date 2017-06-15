package com.service.boot.security;

import org.springframework.security.authentication.encoding.Md5PasswordEncoder;

import java.util.Random;

/**
 * Created by sanders on 2017/4/20.
 */
public class PasswordTest {

    public static void main(String[] args) {
        Md5PasswordEncoder encoder = new Md5PasswordEncoder();
        encoder.setEncodeHashAsBase64(true);
        String[] names = {"李俊琪", "李双双", "李璘", "杨欣瑜", "王丹丹", "马元睿", "陈丽燕"};
        String[] username = {"lijunqi", "lishuangshuang", "liling", "yangxinyu", "wangdandan", "mayuanrui", "chenliyan"};
        String[] emails = {"lijunqi@nilai.com", "lishuangshuang@nilai.com", "liling@nilai.com", "yangxinyu@nilai.com", "wangdandan@nilai.com", "mayuanrui@nilai.com", "chenliyan@nilai.com"};
        for(int i=0; i<names.length; i++){
            String randomPassword = genRandomNum(8);
            System.out.println(randomPassword + " - " + encoder.encodePassword(randomPassword, "vbao"));
        }
        System.out.println(encoder.encodePassword("yunyou2014", "vbao"));
    }

    public static String genRandomNum(int pwd_len) {
// 26*2个字母+10个数字
        final int maxNum = 62;
        int i; // 生成的随机数
        int count = 0; // 生成的密码的长度
        char[] str = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
                'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
                'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        StringBuffer pwd = new StringBuffer("");
        Random r = new Random();
        while (count < pwd_len) {
// 生成随机数，取绝对值，防止生成负数，
            i = Math.abs(r.nextInt(maxNum)); // 生成的数最大为62-1
            if (i >= 0 && i < str.length) {
                pwd.append(str[i]);
                count++;
            }
        }
        return pwd.toString();
    }

}
