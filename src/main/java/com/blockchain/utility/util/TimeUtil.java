package com.blockchain.utility.util;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.Instant;

/**
 * 时间工具类 - 统一管理UTC+8时区
 * @author zhangrucheng on 2025/1/15
 */
public class TimeUtil {
    
    /**
     * UTC+8时区偏移量
     */
    public static final ZoneOffset UTC_PLUS_8 = ZoneOffset.ofHours(8);
    
    /**
     * 获取当前UTC+8时区的LocalDateTime
     * @return UTC+8时区的当前时间
     */
    public static LocalDateTime now() {
        return LocalDateTime.now(UTC_PLUS_8);
    }
    
    /**
     * 获取当前UTC+8时区的时间戳（毫秒）
     * @return UTC+8时区的当前时间戳
     */
    public static long currentTimestamp() {
        return Instant.now().atOffset(UTC_PLUS_8).toInstant().toEpochMilli();
    }
    
    /**
     * 将LocalDateTime转换为UTC+8时区的时间戳
     * @param dateTime 时间
     * @return UTC+8时区的时间戳
     */
    public static long toTimestamp(LocalDateTime dateTime) {
        return dateTime.atOffset(UTC_PLUS_8).toInstant().toEpochMilli();
    }
    
    /**
     * 将时间戳转换为UTC+8时区的LocalDateTime
     * @param timestamp 时间戳（毫秒）
     * @return UTC+8时区的LocalDateTime
     */
    public static LocalDateTime fromTimestamp(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), UTC_PLUS_8);
    }
} 