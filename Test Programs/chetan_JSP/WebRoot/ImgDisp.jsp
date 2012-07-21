<%@ page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
  <script type="text/javascript">
  /creating a array of the image object
var image=new Array("C:\Documents and Settings\dac12\Desktop\Blue hills.jpg",
                    "C:\Documents and Settings\dac12\Desktop\Sunset.jpg",
                    "C:\Documents and Settings\dac12\Desktop\Water lilies.jpg",
                    "C:\Documents and Settings\dac12\Desktop\Water lilies.jpg",
                    
                    )
//variable that will increment through the images
var num=0
// set the delay between images
var timeDelay

Preload Images
Preload the images in the cache so that the images load faster
//create new instance of images in memory 
var imagePreload=new Array()
for (i=0;i<image.length;i++)
{
   imagePreload[i]=new Image()
// set the src attribute
imagePreload[i].src=image[i]
}

//for automatic Slideshow of the Images
function slideshow_automatic()
{ 
if (slideshow.checked)
   {
    if (num<image.length)
     {
       num++
       //if last image is reached,display the first image
       if (num==image.length) 
       num=0
       image_effects()
       //sets the timer value to 4 seconds, 
       //we can create a timing loop 
       //by using the setTimeout method
       timeDelay=setTimeout("slideshow_automatic()",1000) 
       document.images.slideShow.src=image[num]   
     }
   }  
   if (slideshow.checked==false)
   { 
     //Cancels the time-out that was set with the setTimeout method. 
      clearTimeout(timeDelay)
   }
}



  </script>
    <base href="<%=basePath%>">
    
    <title>My JSP 'ImgDisp.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	

  </head>
  
  <body>
      <img src="C:\Documents and Settings\dac12\Desktop\Blue hills.jpg" alt="" name="image5" width="980" height="320" id="" onclick="slideshow_automatic()"/>    
     <br>
  </body>
</html>
