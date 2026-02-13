# Quiet Chess （静寂のチェス）

## プロジェクト概要
Quiet Chessは、Javaで開発されたオンラインチェスプラットフォームです。リアルタイムの対戦機能、ゲーム履歴、そして直感的なUIを備えています。
## 🎮 ライブデモ
javachess-production.up.railway.app *(ブラウザで今すぐ対戦を開始できます)*

## 主な機能

- 🎮 **リアルタイムチェスゲーム** - プレイヤー間でのリアルタイム対戦
- 📜 **ゲーム履歴** - すべての指し手を記録・再生
- 🗄️ **データベース管理** - PostgreSQLを使用した永続的なデータ保存
- 🔄 **自動ポーリング** - サーバーからのゲーム状態の自動更新
- ♟️ **FIDE国際チェス規則対応** - チェスのルールを正確に実装

## 技術スタック

| カテゴリ | 技術 |
|---------|------|
| **フロントエンド** | HTML5, CSS3, JavaScript |
| **バックエンド** | Java 17, Jakarta Servlet, JSP |
| **データベース** | PostgreSQL 42.6.0 |
| **ビルドツール** | Maven |
| **アプリケーションサーバー** | Tomcat 10（Jakarta互換） |
| **JSON処理** | Gson |

## プロジェクト構成

```
javaChess-main/
├── src/
│   └── main/
│       ├── java/
│       │   ├── com/chess/           # チェスゲームロジック
│       │   ├── controller/          # Servletコントローラー
│       │   ├── model/
│       │   │   ├── dao/             # データベースアクセスオブジェクト
│       │   │   ├── entity/          # エンティティクラス
│       │   │   └── logic/           # ゲームルール実装
│       │   ├── test/                # テストコード
│       │   └── util/                # ユーティリティ
│       └── webapp/
│           ├── index.jsp            # メインページ
│           ├── static/              # 静的ファイル
│           └── WEB-INF/             # Webアプリケーション設定
├── public/                          # パブリックリソース
├── pom.xml                          # Maven設定ファイル
└── Dockerfile                       # Docker設定

```

## セットアップ手順

### 前提条件

- Java 17 以上
- Maven 3.6 以上
- PostgreSQL 12 以上
- Git

### インストール

1. **リポジトリをクローン**

```bash
git clone https://github.com/yourusername/javaChess.git
cd javaChess-main
```

2. **データベースを設定**

PostgreSQLでデータベースを作成し、必要なテーブルを初期化します：

```bash
createdb chess_platform
# テーブル初期化スクリプトを実行
psql chess_platform < schema.sql
```

3. **依存関係をインストール**

```bash
mvn clean install
```

4. **アプリケーションをビルド**

```bash
mvn package
```

5. **Tomcatにデプロイ**

生成されたWARファイル（`target/chess-platform-1.0-SNAPSHOT.war`）をTomcatの`webapps`ディレクトリにコピーします。

6. **Tomcatを起動**

```bash
cd $CATALINA_HOME/bin
./startup.sh  # Linux/Mac
startup.bat   # Windows
```

7. **ブラウザでアクセス**

```
http://localhost:8080/chess-platform-1.0-SNAPSHOT/
```

## 使用方法

### ゲームの開始

1. ウェブサイトにアクセス
2. 「START」ボタンをクリック
3. 別のプレイヤーが参加するまで待機
4. ゲームが開始されたら、白方が最初の手を指します

### 基本操作

- **駒の移動** - 移動させたい駒をクリック、目的地をクリック
- **新規ゲーム** - 🔄 NEW GAMEボタンをクリック
- **履歴表示** - 📜 HISTORYボタンでゲーム進行状況を確認

## APIエンドポイント

| エンドポイント | メソッド | 説明 |
|---|---|---|
| `/chess` | GET/POST | メインチェス操作 |
| `/game-status` | GET | ゲーム状態の取得 |
| `/polling` | GET | ゲーム更新のポーリング |

## ファイル説明

### コアクラス

- **Board.java** - チェスボードの状態管理
- **Move.java** - 指し手の表現と検証
- **FIDERules.java** - FIDEチェス規則の実装
- **RuleEngine.java** - ゲームルールエンジン
- **GameStatusServlet.java** - ゲーム状態の提供
- **PollingServlet.java** - クライアント更新用のポーリング機能

### マークアップとスタイル

- **game.jsp** - ゲーム画面
- **replay.jsp** - リプレイ画面
- **style.css** - スタイルシート

## データベーススキーマ

主なテーブル：

- **games** - ゲーム情報
- **moves** - 指し手の記録
- **players** - プレイヤー情報

## トラブルシューティング

### データベース接続エラー

`DBConnection.java`でコネクション設定を確認してください：

```bash
# PostgreSQLが起動しているか確認
pg_isready -h localhost -p 5432
```

### ポートがすでに使用されている場合

```bash
# 別のポートでTomcatを起動
# $CATALINA_HOME/conf/server.xmlで<Connector port="8080">を変更
```

## 開発

### テストの実行

```bash
mvn test
```

### コンパイル

```bash
mvn compile
```

### ログ確認

```bash
# Tomcatログ
tail -f $CATALINA_HOME/logs/catalina.out
```
