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
            <li class="set_grade">奖等
                <select id="set_grade">
                    <option value="-1">选择奖等</option>
                    <option value="0">特等奖</option>
                    <option value="1">一等奖</option>
                    <option value="2">二等奖</option>
                    <option value="3">三等奖</option>
                </select>
            </li>
            <li class="set_people">人数<input type="tel" placeholder="输入中奖人数" id="prizeCount"></li>
        </ul>
    </div>
</div>
<input type="hidden" value="0" id="prize_btn">
<script>
    var myNumber;
    var arr = [];
//    var code = [302610,210022,159862,158602,145635,856997,586223,546221,145213,987451,251364,854136,581698,123785,521387,752169,718954,412321,898989,121245,788565,458558,589659,455212,964632,458412,223344,112233,335566,778899];
    var code = [];//工号集合
    var codeToNameMap=new Map();

    /*随机所有的code并且不重复*/
    function showRandomNum(num) {
        var li = "";
        for(var i = 0; i < code.length; i++){
            arr[i] = i;
        }
        arr.sort(function(){
            return 0.5 - Math.random();
        });

        for(var i = 0; i < num; i++){
            var index = arr[i];
            var tempCode=code[index];
            var tempName=codeToNameMap.get(tempCode);
            li += '<li>'+tempName+'('+code[index]+')</li>';
        }

        $(".prize_list ul").html(li);
    }

    $(function () {
        initUserDatas();

        $(".start").click(function(){
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
                    $("#prize_btn").val(1);
                    var num = $("#prizeCount").val();
                    $(this).find("img").attr("src","images/prize_stop.png");

                    myNumber = setInterval(function(){
                        showRandomNum(num);
                    }, 100);//这里调节变化时间
                }
            }else{
                $("#prize_btn").val(0);
                clearInterval(myNumber);
                $(this).find("img").attr("src","images/prize_start.png");
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

    });
    //获取用户数据集合
    function initUserDatas(){
        var userInfo="${userInfo}";//字符串格式"工号_名字,工号_名字,"
        var users=userInfo.split(",");
        $.each( users, function(i, o){

            var tempCode= o.split("_")[0];
            var tempName= o.split("_")[1];
            code.push(tempCode);
            codeToNameMap.set(tempCode, tempName);
        });
        console.info(code);
        console.info(codeToNameMap);
    }
</script>

</body>
</html>
