<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!DOCTYPE html>
<html>
	 <head>

	 	<!-- Common imports in pages -->
	 	<jsp:include page="../header.jsp" />
	 	
	   <title>Visualizza Elemento</title>
	   
	 </head>
	   <body class="d-flex flex-column h-100">
	   
	   		<!-- Fixed navbar -->
	   		<jsp:include page="../navbar.jsp"></jsp:include>
	    
			
			<!-- Begin page content -->
			<main class="flex-shrink-0">
			  <div class="container">
			  
			  		<div class='card'>
					    <div class='card-header'>
					        <h5>Sta per essere applicata la procedura di emergenza. Si è sicuri di voler procedere?</h5>
					    </div>
					    
					<form:form modelAttribute="insert_satellite_attr" method="post" action="${pageContext.request.contextPath}/satellite/disabilita" class="row g-3" novalidate="novalidate">
					    <div class='card-body'>
					    	<dl class="row">
							  <dt class="col-sm-3 text-right">Numero Voci totali presenti:</dt>
							  <dd class="col-sm-9">${satellite_list_all.size()}</dd>
					    	</dl>
					    	
					    	<dl class="row">
							  <dt class="col-sm-3 text-right">Numero Voci che verranno modificate in seguito alla procedura:</dt>
							  <dd class="col-sm-9">${satellite_list_attribute.size()}</dd>
					    	</dl>
					    </div>
					    <!-- end card body -->
					    <div class='card-footer'>
			   				<div class="col-12">
								<button class='btn btn-outline-danger' style='width:80px' type="submit" name="submit" value="submit" id="submit" class="btn btn-primary">Conferma</button>
							</div>
					    
					        <a href="${pageContext.request.contextPath}/satellite" class='btn btn-outline-secondary' style='width:80px'>
					            <i class='fa fa-chevron-left'></i> Back
					        </a>
					    </div>
					    </form:form>
					<!-- end card -->
					</div>	
			  
			    
			  <!-- end container -->  
			  </div>
			  
			</main>
			
			<!-- Footer -->
			<jsp:include page="../footer.jsp" />
	  </body>
</html>