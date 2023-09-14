# 智能BI平台

## 项目开发技术栈

前端：react + ant design pro + umi3

后端：Springboot、Mybatis Plus、MySQL、RabbitMQ、Swagger、Knife4J、Redis

## 项目搭建

### 后端

请先在本地安装后MySQL8、Redis、RabbitMQ等软件。

![image-20230912213735870](https://raw.githubusercontent.com/Yucalis/MD_Pictures/master/image-20230912213735870.png)

先找到后端代码文件中的sql文件夹下的create_table.sql文件创建数据库及表结构。

![image-20230912213940147](https://raw.githubusercontent.com/Yucalis/MD_Pictures/master/image-20230912213940147.png)

之后到application.yml中找到上面的配置，将其中的username和password修改为自己本地MySQL数据库的username和password。

![image-20230912214055867](https://raw.githubusercontent.com/Yucalis/MD_Pictures/master/image-20230912214055867.png)

再然后是openAPI接口的调用配置，这里用的是鱼皮大佬开发的鱼聪明的API接口。这里需要在application.yml中找到上面的配置，将其中的access-key和secret-key换成自己的，具体获取方式如下：

登录鱼聪明网站（[鱼聪明AI - 做您强大的AI助手 (yucongming.com)](https://www.yucongming.com/)）

![image-20230912214359559](https://raw.githubusercontent.com/Yucalis/MD_Pictures/master/image-20230912214359559.png)

之后在用户名的下拉栏中点击开放平台选项

![image-20230912214457157](https://raw.githubusercontent.com/Yucalis/MD_Pictures/master/image-20230912214457157.png)

这里就可以看到自己账号的AccessKey和SecretKey了，记得换成自己的哟，不要耗我的羊毛（ps：虽然我也没有羊毛可以给你们耗，嘻嘻！！！）

**最后！！！！**

记得等Maven下载好依赖后再启动项目。

### 前端

请先在本地安装后nodejs哟！

![image-20230912214804998](https://raw.githubusercontent.com/Yucalis/MD_Pictures/master/image-20230912214804998.png)

文件打开后，请先在文件目录下打开命令板执行 npm install 命令安装依赖

之后也是在命令板执行 npm run dev 启动项目

## 项目其他注意事项

1 项目启动后请先注册好账号后再进行登录，因为create_table.sql中只提供了数据库和表的创建语句，没有数据的添加语句；

2 项目的后端代码是在鱼皮大佬提供的后端模板的基础上进行的二次开发，前端代码则是基于ant design pro脚手架塔建的代码的二次开发，所以代码其实有些是可以尝试删除的； 

3 项目目前还有些功能没有实现，之后如果实现了会进行更新，但核心的功能（智能分析已实现完毕），所以有能力的大佬可以自行进行扩展。

## 项目界面演示

![image-20230912213031562](https://raw.githubusercontent.com/Yucalis/MD_Pictures/master/image-20230912213031562.png)

![image-20230912213125542](https://raw.githubusercontent.com/Yucalis/MD_Pictures/master/image-20230912213125542.png)

![image-20230912213140326](https://raw.githubusercontent.com/Yucalis/MD_Pictures/master/image-20230912213140326.png)

![image-20230912213153543](https://raw.githubusercontent.com/Yucalis/MD_Pictures/master/image-20230912213153543.png)