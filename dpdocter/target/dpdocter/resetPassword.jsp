<html>
<head>
<title>Insert title here</title>
</head>
<body>
<form action="/dpdocter/api/v1/forgotPassword/resetPassword" method="post">
<input type="hidden" value=<%= request.getParameter("uid") %> name="userId">
New Password : <input type="text" name="password"><br>
<!-- <a href="#" id="link" onclick="myFunction();">Reset Password</a> -->
<input type="submit" value="Reset">
</form>
</body>
</html>