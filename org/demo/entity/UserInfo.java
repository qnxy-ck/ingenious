package org.demo.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户表实体类
 * 
 * @author 陈坤
 * 2023/9/27
 */
public record UserInfo(
        Long id,
        String name,
        Integer age,
        String email,
        LocalDate birthday,
        LocalDateTime created_at,
        LocalDateTime updated_at
) {
}
