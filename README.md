# Chess Platform

Java/Jakarta Servlet ベースのチェス対戦プラットフォームです。  
PostgreSQL を利用してユーザー情報や対局履歴などを管理します。

## 技術スタック

- Java 17
- Jakarta Servlet / JSP / JSTL
- PostgreSQL
- Maven
- Servlet Container (e.g. Apache Tomcat 10)

## プロジェクト構成

- `src/main/java/`
  - `controller/` … サーブレットやコントローラ
  - `model/` … エンティティや DAO
  - `util/DBConnection.java` … DB 接続ユーティリティ
- `src/main/webapp/`
  - `WEB-INF/`
    - `views/` … JSP テンプレート
- `pom.xml` … Maven 設定

## セットアップ手順

### 1. 前提条件

- JDK 17 以上
- Maven
- PostgreSQL (ローカルまたは外部サーバ)

### 2. データベースの準備

1. データベース作成（例）:

   ```sql
   CREATE DATABASE chess_db;

2. アプリケーションの設定

    src/main/java/util/DBConnection.java などで DB 接続設定を行います。

    環境変数を使う場合の例:
    private static final String URL = System.getenv("CHESS_DB_URL");   // e.g. jdbc:postgresql://localhost:5432/chess_db
    private static final String USER = System.getenv("CHESS_DB_USER"); // e.g. postgres
    private static final String PASS = System.getenv("CHESS_DB_PASS"); // e.g. your_password

3. ビルド & 実行

```bash
# ビルド
mvn clean package

# Tomcat などでデプロイして起動
```

## 主な機能
- チェス対局の参加
- 対局履歴の閲覧

