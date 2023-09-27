# new-ds (new dynamic sql)
颠覆传统动态sql书写方式

## _设计草案_

简单示例:

```text

// 当前类所在的包路径
namespace com.demo.mapper

// 导入类 并标志为当前类为实体类
// 导入的实体类只能存在一个
import entity com.demo.data.entity.UserInfo

// 导入其他相关类
import java.util.List



// ----- 添加数据 --------


// 添加一条数据
// insert 添加方法关键字
// insertUserInfo 对应方法名称
// userInfo: UserInfo 方法参数
// 该方法默认返回影响行数(返回类型为Long类型), 如果需要返回结果为bool类型, 则需要手动声明
// 如: insert bool xxxMethodName() {}
insert insertUserInfo(userInfo: UserInfo) {
    
    // 方式一
    insert into user_info(username, age, birthday, email, created_at)
    values(:userInfo.username, :userInfo.age, :userInfo.birthday, :userInfo.email, now())
    
    // 方式二
    // #insert_into 插入数据关键字
    // user_info 插入数据对应表名, 如果是对当前实体类映射表插入数据则可省略
    // :userInfo 方法参数对象
    #insert_into user_info :userInfo {
        :username   // 如果表字段和 userInfo 对象中的属性字段名称一样直接这样写即可
        :age
        birthday = :userBirthday   // 如果不一样则需要手动指定, 自动对属性名称进行驼峰转换 
        :email
        created_at = now()  //
    }

}

// 批量添加
insert batchInsertUserInfo(userInfoList: List<UserInfo>) {

    // #batch_insert 批量插入关键字
    // user_info 插入数据对应表名, 如果是对当前实体类映射表插入数据则可省略
    #batch_insert user_info :userInfoList {
        :username
        :age
        :birthday
        :email
    }

}


// ----- 查询数据 --------


// 根据id查询一条数据
// search 为查询方法定义关键字
// UserInfo 为查询返回结果类型, 如果查询结果类型为当前实体类类型一致, 可以省略否则则需要显示书写并且需要导入对应的类
// selectById 为生成对于方法名称
// id: Long 为查询参数
search UserInfo selectById(id: Long) {
    
    select * from user_info where id = :id
    
}

// mul (multiple) 查询结果为多个
// UserInfo 为查询返回结果类型, 如果查询结果类型为当前实体类类型一致, 可以省略否则则需要显示书写并且需要导入对应的类
search mul UserInfo selectByNameAndEmail(username: String, email: String) {

    select * from user_info
        where
            // 如果条件成立 则下面一行将会拼接, 如果不成立则不会拼接
            // 在调用函数时 如果该函数没有任何参数, 则括号可以省略
            username = :username::notNull   // 控制当前行, 条件满足之后才会拼接改行sql
            and email = :email::hasContains("@")
            
            // 以上代码会生成java代码, 可以理解为jdbc动态拼接sql
            // 每个参数调用的函数为java实现
            // 可以理解为 Tools.hasContains(email, "@")
            // 用户也可以实现某些接口的情况下, 自定义自己的函数
        
}

// 根据id查询多个 
search mul selectByIds(idList: List<Long>) {
    
    select * from user_info
        // 等价为 id in (?, ?, ?)
        // 实现方式使用 #in 关键字处理, 而不是使用函数来处理
        // 关键字为编译期, 函数为执行期, 按理说编译处理的效率会高一些
        // 如果需要特殊处理则需要自定义函数进行调用
        where id #in :idList

}

// 模糊查询
search mul selectByNameKeyword(nameKeyword: String) {
    
    select * from user_info
        // 模糊查询
        // #like_l  等于 name like '%?' 
        // #like_r  等于 name like '?%'
        where name #like :nameKeyword
    
}

// -> 箭头语法
search mul selectByEmail(email: String) {
    
    select * from user_info
        // 对一个变量调用多个函数
        // 语法格式: :xxx -> boolean expression
        // 如: :email -> 1 == 1 && "s" == "l" || :status = 1
        where email = :email -> notNull && hasContains("@")
        
        // :email -> notNull && hasContains("@") 
        // 是对 email 参数进行调用两个函数, 也可以在箭头后面对其他参数调用函数
        // 如: :email -> :status::notNull
    
}


// 控制多行条件, 复杂表达式
// #if 关键字
search mul selectByStatus(status: Int) {
    
    select * from user_info
        #if status == 1 {
            where
                username = 'test'
                and age = 22
        }
   
}


// #when 关键字
search mul selectAllSortBy(sortType: Int) {

    select * from user_info
        order by
        #when :sortType {
            // 根据年龄倒序
            0 -> age desc
            
            // 根据年龄升序
            1 -> age
            
            // 根据创建记录时间倒叙
            2 -> created_at desc
            
            // 默认
            else -> created_at
            
        }
    
}


// ----- 更新数据 --------



// 更新一条记录
// update 更新方法定义关键字
// updateUserInfo 更新方法名称
// userInfo: UserInfo 参数信息
// 默认返回更新影响行数 (Long)
// 如果需要返回布尔值, 则需手动指定
// 如: update boolean updateUserInfo(userInfo: UserInfo) { }
update updateUserInfo(userInfo: UserInfo) {

    // 原始方式
    update user_info
        set
            username = :userInfo.username,
            age = :userInfo.age,
            email = :userInfo.email,
            birthday = :userInfo.birthday
        where id = :userInfo.id


    // 进阶方式
    // 如果更新数据的表为当前实体对应表 表名可以省略
    #update_set user_info :userInfo {
        // 对指定字段进行更新, 如果更新字段名称和当前对象属性名称一样可以省略等号前的内容
        username = :username
        :age
        :email
        :birthday::notNull  // 如果当前属性为空则不进行sql的拼接, 该字段就不会更新
    }
    where id = :userInfo.id
    
}


// 逻辑删除一条记录
update boolean logicalDelete(id: Long) {

    // 逻辑删除指定记录
    // 等价sql
    // update user_info set deleted = true where id = :id
    // 如果更新数据的表为当前实体对应表 表名可以省略
    // 如: #update_logical_delete :id
    // tips: 那么这样实现的话, 怎么知道那个是逻辑字段呢? 把逻辑字段修改成什么值呢?
    // 这个目前觉得在表对应实体类种进行配置比较合适
    #update_logical_delete user_info :id
    
}

// 逻辑恢复一条数据
update updateLogicalRecoveryById(id: Long) {
    #update_logical_recovery :id
}


// 逻辑删除多条记录
update boolean batchLogicalDelete(idList: List<Long>) {

    // 相对于单个逻辑删除, 仅需要加一个 mul 标记即可
    // 等价sql
    // update user_info set deleted = true where id in (?, ?, ?)
    #update_logical_delete user_info mul :idList

}


// ----- 删除数据 --------



// delete 删除方法定义关键字
// 默认返回更新影响行数 (Long)
// 如果需要返回布尔值, 则需手动指定
// 如: delete boolean deleteById(id: Long) { }
delete deleteById(id: Long) {

    // 原始写法
    delete from user_info where id = :id
    
    // 进阶写法
    // 如果删除数据的表为当前实体对应表 表名可以省略
    // 如: #delete_where id = :id
    #delete_where user_info id = :id
    
}


```