<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>用户列表</title>
</head>
<body>
	<a href="add">添加</a>

	<br />
	<c:forEach items="${allJobs }" var="job">
		${job.jobId }
 		----${job.jobName }
 		----${job.jobStatus }
 		----${job.cronExpression }  ---<a href="${job.jobGroup }/${job.jobName }/stop" >暂停 </a> 
 								 	---<a href="${job.jobGroup }/${job.jobName }/reStart" >恢复 </a>
 								 	---<a href="${job.jobGroup }/${job.jobName }/startNow" >立即执行一次 </a>
 								 	---<a href="${job.jobGroup }/${job.jobName }/del" >删除</a>
 								 	---<a href="${job.jobGroup }/${job.jobName }/oneSecond" >一秒执行一次</a>
 								 	---<a href="${job.jobGroup }/${job.jobName }/fiveSeconds" >五秒执行一次</a>
 		<br />
	</c:forEach>
</body>
</html>