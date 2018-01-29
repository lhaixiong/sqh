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
        .vetically{justify-content:center;align-items:center;display:-webkit-flex;}
        .prize_con{position: absolute;width: 100%;height: 100%;background: url(images/firstp_bg.jpg) no-repeat left top / 100% 100%;overflow: hidden;}
        .prize_grade{font-size:40px;color: #ffe9af;text-align: center;margin: 60px auto 0;}
        .prize_list{width:55%;height:300px;margin: 10px auto 55px;text-align: center;overflow: hidden;}
        .prize_list ul{width:100%;font-size:0;}
        .prize_list li{display:inline-block;font-size:15px;color:#f1bf90;text-align: center;width:20%;line-height:30px;font-family:Arial;}
        .start{width: 250px;height: 90px;margin:0 auto;cursor:pointer;}
        .prize_set{position: absolute;right: 60px;bottom:140px;font-size: 16px;color: #f7f3e8;line-height: 30px;}
        .prize_set li{display: inline-block;margin-left: 20px;}
        .set_grade select,.set_people input, .set_money input{background: #fff;width:110px;height:36px;border:1px solid #8f0000;margin-left: .1rem;color: #000;padding-left:10px;}
    </style>

</head>
<body>

<div class="wrap">
    <div class="prize_con">
        <p class="prize_grade">奖品级别:<span></span></p>
        <div class="prize_list vetically">
            <ul>
                <li>000000</li>
                <li>000000</li>
                <li>000000</li>
                <li>000000</li>
                <li>000000</li>
                <li>000000</li>
                <li>000000</li>
                <li>000000</li>
                <li>000000</li>
                <li>000000</li>
            </ul>
        </div>
        <p class="start"><img src="images/prize_start.png" alt=""></p>
        <ul class="prize_set">
            <li><input class="back" type="button" value="返回导入"></li>
            <li><input class="save" type="button" value="保存"></li>
            <li><input class="show" type="button" value="查看"></li>
            <li class="set_grade">奖等
                <select id="set_grade">
                    <option value="-1">选择奖等</option>
                    <option value="1">一等奖</option>
                    <option value="2">二等奖</option>
                    <option value="3">三等奖</option>
                    <option value="4">四等奖</option>
                    <option value="5">五等奖</option>
                </select>
            </li>
            <li class="set_people">人数<input type="tel" placeholder="输入中奖人数" id="prizeCount"></li>
        </ul>
    </div>
</div>
<input type="hidden" value="0" id="prize_btn">
<script>
    var myNumber;
//    var code = [302610,210022,159862,158602,145635,856997,586223,546221,145213,987451,251364,854136,581698,123785,521387,752169,718954,412321,898989,121245,788565,458558,589659,455212,964632,458412,223344,112233,335566,778899];
    var code = [];//工号集合
    var codeToNameMap=new Map();//工号-名字映射
    var allBingoCode=[];//所有中奖工号集合
    var thisBingoCode=[];//本次中奖工号集合
    var canStart=true;

    /*随机所有的code并且不重复*/
    function showRandomNum(num,unSelectedCodes) {
//        console.info("before showRandomNum arr:")
//        console.info(arr);
        var arr = [];
        var li = "";
        for(var i = 0; i < unSelectedCodes.length; i++){
//            var tempI=i;
            arr[i] = i;
        }
//        console.info("before arr.sort arr:")
//        console.info(arr);
        arr.sort(function(){
            return 0.5 - Math.random();
        });
//        console.info("after sort arr:")
//        console.info(arr);

        for(var i = 0; i < num; i++){
            var index = arr[i];
            var tempCode=unSelectedCodes[index];
            var tempName=codeToNameMap.get(tempCode);
            li += '<li codeData='+tempCode+'>'+tempName+'</li>';
        }

        $(".prize_list ul").html(li);
    }

    $(function () {
        initUserDatas();

        $(".start").click(function(){//开始抽奖
            if(!canStart){
                alert("请先保存本次抽奖结果!");
                return;
            }

            if($("#prize_btn").val() == 0){
                if($("#set_grade").val() == "-1") {
                    alert("请选择奖等");
                    return;
                }else if($("#prizeCount").val() == "") {
                    alert("请输入中奖人数");
                    return;
                }else if($("#prizeCount").val() > 10) {
                    alert("单次抽奖人数不能超过10人");
                    return;
                }else{
                    //下次抽奖前，要去除已经中过奖的工号
                    var unSelectedCodes=[];
                    $.each(code,function(i,o){
                        if($.inArray(o,allBingoCode)<0){//没在中奖名单中
                            unSelectedCodes.push(o);
                        }
                    });
                    var num = $("#prizeCount").val();
                    //当前输入中奖人数大于未中奖人数，重新输入中奖人数
                    if(num>unSelectedCodes.length){
                        alert("当前输入中奖人数大于未中奖人数，重新输入中奖人数!")
                        return;
                    }
//                    console.info("抽奖前:已经中奖的工号:");
//                    console.info(allBingoCode);
//                    console.info("抽奖前:待抽的工号:");
//                    console.info(unSelectedCodes);
                    $("#prize_btn").val(1);
                    $(this).find("img").attr("src","images/prize_stop.png");

                    myNumber = setInterval(function(){
                        showRandomNum(num,unSelectedCodes);
                    }, 30);//这里调节变化时间
                }
            }else{//停止抽奖
                $("#prize_btn").val(0);
                clearInterval(myNumber);
                $(this).find("img").attr("src","images/prize_start.png");
                canStart=false;//抽奖停止后，要保存结果再进行下一次抽奖
            }
        });

        //回车键控制开始和停止
        $(document).keydown(function (event) {
            var e = event || window.event || arguments.callee.caller.arguments[0];
            if (e && e.keyCode == 13) { // enter 键
                $(".start").click();
            }
        });

        $("#set_grade").change(function(){
            $(".prize_grade span").text($(this).val());
        });

        //保存抽奖结果到后台
        $(".save").click(function(){
            thisBingoCode=[];//本次结果清空
            if($(".prize_list li").first().html()=="000000"){
                alert("请先抽奖!");
                return;
            }
            $(".prize_list li").each(function(){
                var tempCode=$(this).attr("codeData");
                thisBingoCode.push(tempCode);//记录本次结果
                allBingoCode.push(tempCode);//添加到所有中奖集合中去
            });
            //组装数据保存到后台
            var prizeGrade=$("#set_grade").val()
            var thisCode=thisBingoCode.join(",");

            var saveData={};
            saveData.prizeGrade=prizeGrade;
            saveData.thisCode=thisCode;
//            console.info("这次抽奖后数据:")
//            console.info(saveData);
            $(".save").attr("disabled",true);
            $.ajax({
                type:"post",
                url:"${pageContext.request.contextPath}/saveResult?format=json",
                data:saveData,
                dataType : 'json',
                error : function(returndata){
                    alert(returndata);
                    alert("保存失败，请手动复制保存到记事本");
                    canStart=true;
                    $(".save").attr("disabled",false);
                },
                success:function(data){
                    if(data.success==true){
                        alert("保存成功")
                    }else{
                        alert("保存失败，请手动复制保存到记事本")
                    }
                    canStart=true;
                    $(".save").attr("disabled",false);
                }
            })
        });

        $(".show").click(function(){
            if(!canStart){
                alert("请先保存抽奖结果!")
                return;
            }
           if(allBingoCode.length==0){
               alert("没有任何结果，请先抽奖!");
               return;
           }
            location.href="${pageContext.request.contextPath}/show";
        });
        $(".back").click(function(){
            if(!canStart){
                alert("请先保存抽奖结果!")
                return;
            }
            location.href="${pageContext.request.contextPath}/import.jsp";
        });


    });
    //获取用户数据集合
    function initUserDatas(){
        var userInfo="${userInfo}";//字符串格式"工号_部门-名字,工号_部门-名字,"
        var users=userInfo.split(",");
        $.each( users, function(i, o){

            var tempCode= o.split("_")[0];
            var tempName= o.split("_")[1];
            code.push(tempCode);
            codeToNameMap.set(tempCode, tempName);
        });

        //处理已抽奖
        var alreadyBingoInfo="${alreadyBingo}";
        var tempCodes=alreadyBingoInfo.split(",");
        $.each(tempCodes,function(i,o){
            allBingoCode.push(o);
        })
    }
</script>

</body>
</html>
