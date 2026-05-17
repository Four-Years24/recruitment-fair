package com.recruitment.common;

// ============================================================
// [编写顺序 3.1] 第一个公共类
// [前置] 5 个 Entity 全部完成
// [思维] 为什么在写 Service 之前先写 Result？
//        因为 Service 的返回值需要统一格式
//        如果 Service 有的返回 String，有的返回对象，有的抛异常
//        前端解析时就得写 N 套逻辑
//        Result 把所有返回统一成 {code, message, data} 格式
// [思维] 泛型 <T> 的作用：
//        Result<User> 的 data 是 User 类型
//        Result<List<JobFair>> 的 data 是 List<JobFair> 类型
//        一个类适配所有返回类型
// ============================================================
public class Result<T> {

    // [顺序 3.1.1] 三个核心字段
    //             code=200 表示成功，其他值表示不同错误类型
    //             message 给前端展示用的提示文字
    //             data 是实际返回的数据，泛型
    private int code;
    private String message;
    private T data;

    // [顺序 3.1.2] 构造方法私有，强迫外部用静态工厂方法
    //             这叫"静态工厂模式"
    //             好处：Result.ok(data) 比 new Result(200, "成功", data) 好读
    private Result() {}

    // [顺序 3.1.3] 成功时用这三个方法
    //             ok(data) → 有数据返回
    //             ok()     → 无数据返回（比如删除操作）
    public static <T> Result<T> ok(T data) {
        Result<T> r = new Result<>();
        r.code = 200;
        r.message = "成功";
        r.data = data;
        return r;
    }

    public static <T> Result<T> ok() {
        return ok(null);
    }

    // [顺序 3.1.4] 失败时用这两个方法
    //             fail(code, message) → 自定义错误码 + 信息
    //             fail(message)      → 默认 500 错误
    public static <T> Result<T> fail(int code, String message) {
        Result<T> r = new Result<>();
        r.code = code;
        r.message = message;
        return r;
    }

    public static <T> Result<T> fail(String message) {
        return fail(500, message);
    }

    // [顺序 3.1.5] getter/setter
    //             JSON 序列化需要 getter，Spring 默认用 Jackson
    //             返回给前端时：{"code":200, "message":"成功", "data":{...}}
    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
}

// [状态] Result 完成
// [下一步] 创建 BusinessException（业务异常类）
//         → 为什么？因为 Service 写业务逻辑时需要抛异常
//         → 不能让 Service 直接返回 Result.fail()，那样太耦合
//         → 应该是 Service 抛异常 → Controller 自动捕获 → 转成 Result
