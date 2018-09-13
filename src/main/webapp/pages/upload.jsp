<%--
  Created by IntelliJ IDEA.
  User: liman
  Date: 2018/9/12
  Time: 18:01
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>上传文件</title>
</head>
<body>
<h1>文件上传</h1>

<form method="POST" enctype="multipart/form-data" action="UploadFileServlet" name="uploadForm">
    选择一个文件: <input type="file" name="upfile"><br/>
    <br/>
    <input type="submit" value="上传">
</form>
</body>
</html>
