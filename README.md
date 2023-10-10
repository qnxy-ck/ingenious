# new-ds (new dynamic sql)
颠覆传统动态sql书写方式

## _设计草案_

简单示例:

```text

namespace com.demo.mapper

import entity com.demo.data.entity.UserInfo

search selectInfo(idList: mul Long, username: String, password: String, email: String, phoneNum: String): mul {
    select * from user_info
        where
            id #in :idList?   // 自动展开进行in查询, 如果idList中是对象, 则需要取出对象的某个值进行in查询. 示例: id #in(id) :conditions.objList
            and username #like :username?    // 如果username等于null, 则本行不参与查询
            and password = :password::encipher("MD5")?   // 调用 encipher 函数将密码信息MD5进行加密后进行查询, 并且密码为空此行不参与查询
            and email = :email?.regexMatch("@")  // 使用函数regexMatch进行正则匹配验证, '?.' 调用函数符号为regexMatch函数返回true则本行参与查询. 如: :username?.notBlank
            and phone_num = :phoneNum -> $ != null && $::length == 11
            // '->' 符号相比 '?.' 区别在于你可以在 "->" 后面写复杂逻辑表达式. 如果需要更复杂的则可以实现自定义函数, 然后使用 '?.' 符号进行函数调用来简化
            
// 以上仅是展示用法, 不必关心sql查询逻辑
}


// 添加数据
insert insertUserInfo(userInfo: UserInfo) {
    
    #insert_into user_info :userInfo {
        :username  
        :age
        :phoneNum::encipher("SM4")   // 这里的属性, 会自动转换为 phone_num, 并且进行加密后插入
        :birthday
        :email
        created_at = now()
    }
}


// 根据id查询一条数据
search selectById(id: Long) {

    #select_by :id
    
}

// 查询多条
search selectByUsernameAndEmail(username: String, email: String): mul {

    select * from user_info
        where
            username = :username?   // 不能为null
            and email = :email::hasContains("@") // 要求包含@符号
            
}

// 根据id查询多个 
search selectByIds(idList: mul Long): mul {
    
    select * from user_info
        where id #in :idList
        
}

// 模糊查询
search selectByNameKeyword(nameKeyword: String): mul {
    
    select * from user_info
        where name #like :nameKeyword
    
}

// -> 箭头语法的使用
search selectByEmail(email: String): mul {
    
    select * from user_info
        where email = :email -> $ != null && $::hasContains("@")    // 要求 :email 不等于null并且包含@符号

}


search selectByPhoneNum(phoneNum: String): mul {
    
    select * from user_info
        where phone_num = :phoneNum::encipher("SM4")    // 进行加密后查询
   
}




// 更新一条记录
update updateUserInfo(userInfo: UserInfo) {

    #update_set :userInfo {
        :username?.notBlank // 不为null并且不为空字符串才更新
        :age
        :email -> $ != null && $::hasContains("@")  // 不为null并且包含@符号才更新该字段
        :birthday?  // 为null则不更新该字段
    }
    where id = :userInfo.id
    
}


// 逻辑删除一条记录
update logicalDelete(id: Long): boolean {

    #update_logical_delete :id
    
}


delete deleteById(id: Long) {

    #delete_by :id
    
}


```