# new-ds (new dynamic sql)
颠覆传统动态sql书写方式

## _设计草案_

简单示例:

```text

namespace com.demo.mapper

import entity com.demo.data.entity.UserInfo

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