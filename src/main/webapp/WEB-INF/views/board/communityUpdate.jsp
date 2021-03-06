<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%-- spring-webmvc의존 : security의 xss대비 csrf토큰 생성 --%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>


<jsp:include page="/WEB-INF/views/common/header.jsp"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/community.css"/>
<script>

function goCommunityList(){
	location.href = "${pageContext.request.contextPath}/board/community.do";
}
function boardValidate(){
	var $content = $("[name=content]");
	if(/^(.|\n)+$/.test($content.val()) == false){
		alert("내용을 입력하세요");
		return false;
	}
	return true;
}

$(() => {
	$("[name=upFiles]").change((e) => {
		const file = $(e.target).prop('files')[0];
		const $label = $(e.target).next();
		
		if(file != undefined)
			$label.html(file.name);
		else
			$label.html("파일을 선택하세요.");
	});
});
</script>
<div class="roberto-contact-form-area section-padding-100">        
<div class="container">
  <form 		
  		name="communityFrm" 
		action="${pageContext.request.contextPath}/board/updateCommunity.do?${_csrf.parameterName}=${_csrf.token}" 
		method="post"
		enctype="multipart/form-data" 
		onsubmit="return boardValidate();">
     <div class="row" >    
     <div class="col-12">
         <div class="section-heading text-left wow fadeInUp" data-wow-delay="100ms">       
 	     <input type="submit" class="btn btn-outline-success float-right" value="수정 완료">
 	      	<h2>게시글 수정</h2>
 	      	</div>
 	      	</div>  <div name="form-body" style="margin-left: 2%;">
	<input type="hidden" name ="communityNo" value="${community.communityNo}">
    </div>
    </div>    
                      
        <select id="category" name="category" >
          <option value="사담" >사담</option>
          <option value="독서모임" >독서모임</option>
          <option value="도서추천" >도서추천</option>
        </select>
            <input type="text" value = "${community.title}" id="title" name="title" placeholder="제목" style="width: 90%; padding: 8px;
  				margin-bottom: 10px; margin-left:10px; border: 1px solid #ccc; border-radius: 4px;
resize: vertical;" required>
        <textarea id="content" name="content" placeholder="내용" style="height:250px">${community.content}</textarea>
        		<div class="input-group mb-3" style="padding:0px;" >
		  <div class="input-group-prepend" style="padding:0px;">
		    <span class="input-group-text">첨부파일</span>
		  </div>
		  <div class="custom-file">
		    <input type="file" class="custom-file-input" name="upFiles" id="upFiles" multiple>
		    <label class="custom-file-label" for="upFiles">파일을 선택하세요</label>
		  </div>
		  	<div class="custom-file">
		    <input type="file" class="custom-file-input" name="upFiles" id="upFiles2" multiple>
		    <label class="custom-file-label" for="upFiles2">파일을 선택하세요</label>
		  </div>
		
		</div>
		</form>
		</div>
  </div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"></jsp:include>
