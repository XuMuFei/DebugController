#Android app debug工具

##作用

    用于在没有连接电脑的时候对安装在手机上的自家app进行debug
    
##功能

- 读取Logcat日志
- 其它自定义的功能（比如：运行环境切换）

##使用方式

- 在工程app module的build.gradle中添加对lib_debuger的依赖后再打包

- 运行app_debuger

- 运行添加lib_debuger的app

- debug连接建立成功后即可进行相关debug（例如：日志查看）




