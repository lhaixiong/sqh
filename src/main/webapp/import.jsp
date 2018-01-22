<%@ page pageEncoding='UTF-8' contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title></title>

    <script type="text/javascript" src="js/jquery-1.8.3.min.js"></script>


</head>
<body>
    <div>
        <%--<h1>苏菇凉要先导入数据，然后再去抽奖</h1>--%>
    </div>
    <div>
        <form id="importForm" method="post" enctype="multipart/form-data">
            <input id="fileName" type="file" name="file" >&nbsp;
            <input id="importBtn" type="button" value="导入" onclick="importFile()">
        </form>

    </div>
    <br>
    <div>
        <a href="/download">下载上传的文件</a>
    </div>
    <br>
    <div>
        <a href="/goIndex">去抽奖</a>
    </div>


<script>
   $(function(){

   })

   //验证是否是excel的格式
   function isExcel(){
       var fileName=$("#fileName").val();
       if(fileName=='' || fileName==null){
           alert("请选择上传excel文件");
           return false;
       }else{
           var index =fileName.lastIndexOf(".");
           if(index<0){
               alert("上传的文件格式不正确，请选择Excel文件(后缀xls或xlsx)！");
               return false;
           }else{
               var xxs =fileName.substring(index+1,fileName.length);
               if(xxs=="xls" || xxs=="xlsx"){
                   return true;
               }else{
                   alert("上传的文件格式不正确，请选择Excel文件(后缀xls或xlsx)！");
                   return false;
               }
           }
       }
   }
   function importFile(){
       if(!isExcel()){
           return;
       }
//       var formData=new FormData($("#importForm")[0]);
       var formData=new FormData(document.getElementById("importForm"));
       debugger;
       var url="http://localhost:8080/importFile?format=json";
       console.log("url:",url);
       $("#importBtn").attr("disabled",true);
       $.ajax({
           type:"post",
           url:url,
           data:formData,
           async: false,
           cache: false,
           contentType : false,
           dataType : 'json',
           processData : false,
           error : function(returndata){debugger;
               alert(returndata);
               $("#importBtn").attr("disabled",false);
           },
           success:function(data){debugger;
               if(data.success==true){
                  alert("导入成功")
               }else{
                   alert("导入失败")
               }
               $("#importBtn").attr("disabled",false);
           }
       })

   }
</script>

</body>
</html>
