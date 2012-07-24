<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
<script type="text/javascript">
var cnt=0;
  
function myFunction()
{
setInterval(function(){document.getElementById("ip").innerHTML=cnt++},100);
}
   
   /*   function SwitchPic() {
      
    
        cnt++;
        document.getElementById("imag").src="./images/Sunset.jpg";
         document.getElementById("ip").innerHTML=cnt;
      } */
</script>
</head>
<body>
<img src="./images/Win.jpg" height="300px" width="600px" id="imag" onClick="myFunction()"  />
<span id="ip">text</span>
</body>
</html>