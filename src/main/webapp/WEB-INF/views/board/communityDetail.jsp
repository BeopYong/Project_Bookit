<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>


<style>
div#board-container{width:400px;}
input, button, textarea {margin-bottom:15px;}
button { overflow: hidden; }
/* 부트스트랩 : 파일라벨명 정렬*/
div#board-container label.custom-file-label{text-align:left;}
</style>
<div id="board-container" class="mx-auto text-center">
	<input type="text" class="form-control" 
		   placeholder="제목" name="boardTitle" id="title" 
		   value="${board.title}" required>
	<input type="text" class="form-control" 
		   name="memberId" 
		   value="${board.member.name} (${board.member.id})" readonly required>
	
	<c:forEach items="${board.attachments}" var="attach" varStatus="vs">
	<%-- 		
		<a href="${pageContext.request.contextPath}/resources/upload/board/${attach.renamedFilename}"
			role="button" 
			class="btn btn-outline-success btn-block"
			download="${attach.originalFilename}">
			첨부파일${vs.count} - ${attach.originalFilename}
		</a> 
	--%>
		<a href="${pageContext.request.contextPath}/board/fileDownload.do?no=${attach.no}"
			role="button" 
			class="btn btn-outline-success btn-block">
			첨부파일${vs.count} - ${attach.originalFilename}
		</a>
	</c:forEach>
	<a href="${pageContext.request.contextPath}/board/urlResource.do"
		role="button" 
		class="btn btn-outline-success btn-block">
		UrlResource 확인 
	</a>
	
    <textarea class="form-control mt-3" name="content" 
    		  placeholder="내용" required>${board.content}</textarea>
    <input type="number" class="form-control" name="readCount" title="조회수"
		   value="${board.readCount}" readonly>
	<input type="datetime-local" class="form-control" name="regDate" 
		   value='<fmt:formatDate value="${board.regDate}" pattern="yyyy-MM-dd'T'HH:mm"/>'>
</div>


<jsp:include page="/WEB-INF/views/common/footer.jsp"/>