<div style="text-align: center;">
    <h1>Ingenious (new dynamic sql)</h1>
    全新的动态SQL书写方式
</div>

---

## _#设计思想_

新的开篇目的不是为了创造重复的轮子, 创新来源于对以往经验的质疑, 以做出更完善实用便利为目的.

以往可以写动态sql的框架动态sql书写方式要么是接口 + 配置文件(XML)方式, 要么是Java链式语法/Lambda的方式以及进阶一些的组合技.
XML的动态sql方式确实比较臃肿, 当然可以写在其他文件格式中, 但是总是会有一些特殊符号限制等, 或者一些模板引擎并不理想.

于是我就在想除了这些, 能不能自定义文件格式来描述我们的动态sql, 让我们写起来更舒服一些, 最大化的按照功能强大/简化实用的思想基础上进行.

如果我们自定义语法了之后, 还是java接口 + 自定义语法的方式, 那感觉还是会很多余换汤不换药一样, 而且还不方便.
所以这个语法不需要让你在写接口了, 仅需要一个表对应的Java实体类 + 这个自定义的文件即可完成.

那么就有人要问了没有接口我们怎么去调用呢? 使用这个就可以像我们在使用lombok时候一样, 会为我们自动生成相关的接口等, 我们只需要关心我们的sql怎么去写就行了.

## _#语法展示(详细请看对应描述文件):_

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
            and phone_num = :phoneNum -> this != null && this::length == 11
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