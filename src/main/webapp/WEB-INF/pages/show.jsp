<%@ page pageEncoding='UTF-8' contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title></title>

    <script type="text/javascript" src="js/jquery-1.8.3.min.js"></script>

    <style>
        *{margin:0;padding:0;}
        img{display:block;}
        i{font-style:normal;}
        ul li{
            list-style-type:none;
            margin-left: 20px;
            float:left; /* 往左浮动 */
        }
        ul {
            text-align: center;
        }
        .prize_con{position: absolute;width: 100%;height: 100%;background: url(images/firstp_bg.jpg) no-repeat left top / 100% 100%;overflow: hidden;}
        .mydiv{overflow-y: auto;color:#f1bf90;width: 50%;margin:5px auto;font-family:Arial;float: none}
        .return{background: #fff;width:110px;height:50px;font-size:20px;border:1px solid #8f0000;margin-left: .1rem;color: #000;padding-left:10px;}
    </style>

</head>
<body>

<div class="wrap">
    <div class="prize_con">
        <%--<div style="overflow-y: auto;text-align:center;color:#f1bf90;width: 50%;margin:50px auto 10px;font-family:Arial;float: none"><!--一等奖结果-->--%>
            <%--<p>2等奖</p>--%>
            <%--<ul>--%>
                <%--<li>23leuro</li>--%>
                <%--<li>23leuro</li>--%>
            <%--</ul>--%>
        <%--</div>--%>
        <%--<div style="overflow-y: auto;text-align:center;color:#f1bf90;width: 50%;margin:5px auto;font-family:Arial;float: none"><!--一等奖结果-->--%>
            <%--<p>2等奖</p>--%>
            <%--<ul>--%>
                <%--<li>23leuro</li>--%>
                <%--<li>23leuro</li>--%>
            <%--</ul>--%>
        <%--</div>--%>
        <div style="text-align: center"><a class="return" href="${pageContext.request.contextPath}/goIndex">返回抽奖</a></div>
    </div>
</div>
<input type="hidden" value="0" id="prize_btn">
<script>
    var codeToNameMap=new Map();//工号-名字映射
    $(function () {
        initUserDatas();
    });
    //获取用户数据集合
    function initUserDatas(){
        var userInfo="${userInfo}";//字符串格式"工号_部门-名字,工号_部门-名字,"
        var users=userInfo.split(",");
        $.each( users, function(i, o){
            var tempCode= o.split("_")[0];
            var tempName= o.split("_")[1];
            codeToNameMap.set(tempCode, tempName);
        });

        //处理已抽奖
        //"字符串格式:奖品级别_工号1，工号2&奖品级别_工号1，工号2
        // 例如1_1,2,3&2_4,5,6";
        var bingoInfo="${bingoInfo}";
        var tempInfo=bingoInfo.split("&");
        <%--<div style="overflow-y: auto;text-align:center;color:#f1bf90;width: 50%;margin:50px auto 10px;font-family:Arial;float: none"><!--一等奖结果-->--%>
        <%--<p>2等奖</p>--%>
        <%--<ul>--%>
        <%--<li>23leuro</li>--%>
        <%--</ul>--%>
        <%--</div>--%>
        $.each(tempInfo,function(i,o){
            var items=o.split("_");
            var prizeLv=items[0]
            var codes=items[1].split(",");

            var firstDivStyle="";
            if(prizeLv=="1"){
                firstDivStyle="style='margin:50px auto 10px'"
            }
            var divHtml="<div class='mydiv' "+firstDivStyle+">"
            var pHtml="<p>"+prizeLv+"等奖"+"</p>";
            divHtml+=pHtml;

            var ulHtml="<ul>";
            $.each(codes,function(j,obj){
                if(obj!=""){
                    var name=codeToNameMap.get(obj);
                    var li="<li>"+name+"</li>"
                    ulHtml+=li;
                }
            })
            ulHtml+="</ul>";

            divHtml+=ulHtml+"</div>";

            $(".prize_con").append(divHtml);
        })
    }
</script>

</body>
</html>
