<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>add job</title>
</head>
<body>
	<!-- 此时没有写action,直接提交会提交给/add -->
	<sf:form method="post" modelAttribute="job">
	
	jobId:<sf:input path="jobId" /> 
		<br />
	jobName:<sf:input path="jobName" /> 
		<br />
	jobGroup:<sf:input path="jobGroup" />
		<br />
	cronExpression:<sf:input path="cronExpression" />  
 		<br />
	desc:<sf:input path="desc" />  
 		<br />
	 
		<input type="submit" value="添加job" />
	</sf:form>
</body>
</html>