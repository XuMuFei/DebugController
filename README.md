#Android app debug工具

##作用

    用于在没有连接电脑的时候对安装在手机上的自家app进行debug
    
    在进行android app开发时，会遇到紧急需要定位查找问题的情况，如果此时身边没有电脑，无法查看logcat，定位问题只能靠猜测。
    
    开发人员自己的手机上一般会装测试版的app，使用此工具可以看到比较完善的logcat日志，比如：网络请求报文等
     
##功能

- 读取Logcat日志
- 其它自定义的功能（比如：运行环境切换）

##目录介绍

- app_debugger_controller:   与主app安装在同一台设备上的app，用来连接主app进行debug
- app-debugger:   在主app中依赖的lib module
- app_test:       依赖app-debugger的demo
- app_test-demo.apk                     app_test编译出来的包，用demo.jks签名
- app_debugger_controller-demo.apk:     app_debugger_controller编译出来的安装包，用demo.jks签名

##名词

- 服务端：app_debugger_controller
- 客户端：需要被debug的app（依赖app-debugger）

##使用方式

- 安装&运行服务端程序： app_debugger_controller.apk

- 在客户端工程app module的build.gradle中添加app-debugger的依赖:
    
    `compile 'com.billy.android:app-debugger:1.3.1'`

- 运行客户端

- 服务端与客户端的连接建立成功后即可进行相关debug（例如：日志查看）

##安全性校验
服务端与客户端的签名必须一致

在根目录的local.properties中可自定义签名的相关信息

    storeFileValue=/Users/billy/Documents/xxxx/xxx.keystore
    storePasswordValue=xxxxx
    keyAliasValue=xxxx
    keyPasswordValue=xxxx        
