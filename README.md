#Android app debug工具

##作用

    用于在没有连接电脑的时候对安装在手机上的自家app进行debug
    
    在进行android app开发时，会遇到紧急需要定位查找问题的情况，如果此时身边没有电脑，无法查看logcat，定位问题只能靠猜测。
    
    开发人员自己的手机上一般会装测试版的app，使用此工具可以看到比较完善的logcat日志，比如：网络请求报文等
    
##功能

- 读取Logcat日志
- 其它自定义的功能（比如：运行环境切换）

##目录介绍
    app_debugger_controller:   与主app安装在同一台设备上的app，用来连接主app进行debug
    app-debugger:   在主app中依赖的lib module
    app_test:       依赖app-debugger的demo

##使用方式

- 在工程app module的build.gradle中添加app-debugger的依赖:
    
    `compile 'com.billy.android:app-debugger:1.1.8'`
- 运行app_debugger_controller

- 运行添加app-debugger的app

- debug连接建立成功后即可进行相关debug（例如：日志查看）




