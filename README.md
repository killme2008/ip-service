## 简介

一个 Clojure 开发 REST API 的简单项目，使用 [17mon](http://tool.17mon.cn/) 提供的 IP 库提供一个 IP 地理位置信息查询。目录介绍:

* [version-0](https://github.com/killme2008/ip-service/tree/master/version-0) `lein new compojure ip-service` 的 hello world项目
* [version-1](https://github.com/killme2008/ip-service/tree/master/version-1) 实现基础功能
* [version-2](https://github.com/killme2008/ip-service/tree/master/version-2) 添加单元测试，拆分模块
* [ip-service](https://github.com/killme2008/ip-service/tree/master/ip-service) 最终“成品”，添加 Jetty Server 启动和启动脚本，以及 web driver 集成测试演示

## 运行

开发测试运行，进入项目目录执行下列命令

```
$ lein ring server
```

会自动打开浏览器，访问 [http://localhost:3000](http://localhost:3000) 进入主界面。

除了 HTML 界面外，还提供一个 REST API，可以使用 curl 测试：

```
curl -X GET -H 'Content-Type: application/json' \
    http://localhost:3000/1/ip?ip=180.117.51.245
```

进入 [ip-service](https://github.com/killme2008/ip-service/tree/master/17monipdb)， 打包成独立 jar 包运行，可以执行下列命令：

```
$ lein uberjar
$ sh bin/start.sh
```

在 `target` 目录会生成 `ip-service-0.1.0-SNAPSHOT-standalone.jar`，单独执行这个 jar 即可，通过环境变量 `-Dip-service.db={{path_to_db}}` 来传入 IP 数据文件地址。

## 运行测试

单元测试:

```
$ lein test
```

集成测试依赖 Ruby ，使用 [watirwebdriver](http://watirwebdriver.com/) 框架：

```
$ ruby test_scripts/test_main.rb
```

## License

IP 库的版权遵循 [17mon](http://tool.17mon.cn/) 自身协议，类库遵循各类库协议。

项目源码遵循 [WTFPL – Do What the Fuck You Want to Public License](http://www.wtfpl.net/)
