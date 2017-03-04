$(window).load(function(){
    $("img").addClass("img-responsive center-block"); // 利用Bootstrap的img-responsive类
    $("img.img-responsive").css("width","50%"); // 修改大小
    $("img.img-responsive").before("</br></br>"); // 前空行
    $("img.img-responsive").after("</br></br>"); // 后空行
})
