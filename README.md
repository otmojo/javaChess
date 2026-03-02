【今、遊べます！！！】https://javachess-production.up.railway.app/lobby



# JavaChess: HTTPステートレス環境におけるリアルタイム対戦設計の検証

チェス機能自体よりも、以下の技術的課題の理解を目的としています：

* セッション管理
* マルチスレッド環境での排他制御
* インメモリ状態管理の限界
* Request-driven と Event-driven モデルの比較

---

## 現在のアーキテクチャ（javaChess）

* MVC（JSP / Servlet）
* `RoomManager` によるインメモリ状態管理（ConcurrentHashMap）
* Polling（約1秒間隔）
* JDBC + MySQL による永続化

---

## 次段階構想（goChess）

* Event-driven アーキテクチャへの移行
* WebSocket によるリアルタイムPush
* Redis による共有状態管理
* ステートレスサービス設計

設計視点を「状態の問い合わせ」から「状態変化のイベント通知」へ転換することを目標としています。

