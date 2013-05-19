play2_sample
============

WEB+DB Press vol.71 Play framework2 sample project

twitter oauthと連携させる方法

1. Twitter Developersにアプリケーションを作成 （ローカル環境で試す場合はcallback urlは http://xxx.xxx.xxx.xxx:9000/authと指定）
2. `conf/oauth.conf`を作成
以下の様に設定
```
# twitter oauth
consumer.key="xxxx"
consumer.secret="xxxxx"
callback="http://xxx.xxx.xxx.xxx:9000/auth"
```

各値は登録した内容
