<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quiet Chess</title>
    <link href="https://fonts.googleapis.com/css2?family=Sawarabi+Mincho&display=swap" rel="stylesheet">
    <style>
        body {
            background-color: #121212;
            color: #dcdcdc;
            font-family: 'Sawarabi Mincho', serif;
            margin: 0;
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            height: 100vh;
            text-align: center;
        }

        h1 {
            font-size: 3em;
            letter-spacing: 5px;
            margin-bottom: 10px;
            font-weight: normal;
        }

        p {
            color: #888;
            margin-bottom: 40px;
        }

        .btn {
            background: transparent;
            color: #d4af37;
            border: 1px solid #d4af37;
            padding: 15px 40px;
            font-size: 1.2em;
            cursor: pointer;
            text-decoration: none;
            transition: all 0.3s;
            font-family: inherit;
        }

        .btn:hover {
            background: #d4af37;
            color: #121212;
        }
    </style>
</head>
<body>

    <h1>Quiet Chess</h1>
    <p>静寂の中で、自分の思考を打つ、相手の予想を打つ。</p>

    <a href="game" class="btn">START</a>

</body>
</html>