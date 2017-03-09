// 我的Markdown里，h3是一级标题
// 前后加空行，并换字体
$(window).load(function(){
	$("h3").addClass("lead");
	$("h3.lead").before("</br>");
	$("h3.lead").after("<hr>");
	$("h4").before("</br>");
})
