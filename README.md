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
- app_debugger_controller.apk:    app_debugger_controller编译出来的安装包，可直接安装运行
- app-debugger:   在主app中依赖的lib module
- app_test:       依赖app-debugger的demo

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

使用app-debugger的应用默认只接收符合下列条件的服务端app进行连接

1. 服务端包名为com.billy.controller
2. 服务端签名用ControllerReceiver.getMetaData(Context context, String key) 获取的值为d0de25a2855e83080290318caea6aa5f

        如果需要修改服务端的签名，需要在客户端AndroidManifest.xml的<application>中创建meta-data子节点，例如：
            <meta-data android:name="debugger_sign" android:value="xxxxxxxxxxx" />
            xxxxxxxxxxx值可以通过与客户端连接时在客户端Log.e出来的日志获取，例如：
            E/ControllerReceiver: app-debugger signMd5 error! app with package name 'com.billy.controller' signMd5=d0de25a2855e83080290318caea6aa5f
        
        
        
