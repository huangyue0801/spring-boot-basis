<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <title>登录</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0">
    <link href="/css/bootstrap.min.css?v=3.3.6" rel="stylesheet">
    <link href="/css/font-awesome.min.css?v=4.4.0" rel="stylesheet">
    <link href="/css/animate.css" rel="stylesheet">
    <link href="/css/style.css?v=4.1.0" rel="stylesheet">
    <script>if(window.top !== window.self){ window.top.location = window.location;}</script>
</head>
<body>
<div class="middle-box text-center loginscreen  animated fadeInDown">
    <div>
        <div>
            <h3 class="logo-name">微 宝</h3>
        </div>
        <h3>欢迎使用 微宝</h3>
        <form class="m-t" role="form" action="/login" method="post">
            <div class="form-group">
                <input type="text" name="username" class="form-control" placeholder="用户名" required="">
            </div>
            <div class="form-group">
                <input type="password" name="password" class="form-control" placeholder="密码" required="">
            </div>
            <div class="form-group">
                <input type="text" name="code" class="form-control" placeholder="验证码">
            </div>
            <div class="form-group">
                <img id="captcha" src="/captcha" onclick="refresh()"/>
            </div>
            <button type="submit" class="btn btn-primary block full-width m-b">登 录</button>
            <p class="text-muted text-center"><a href="login.html#">
                <small>忘记密码了？</small>
            </a> | <a href="register.html">注册一个新账号</a>
            </p>
        </form>
    </div>
</div>
<script src="/js/jquery.min.js?v=2.1.4"></script>
<script src="/js/bootstrap.min.js?v=3.3.6"></script>
<script>
    function refresh(){
        $("#captcha").attr("src","/captcha");
    }
</script>
</body>
</html>
