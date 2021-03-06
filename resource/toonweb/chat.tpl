
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<meta name = "viewport" content = "width=640;user-scalable=1">
	<title>Sample of Toontalk</title>
	
	<!-- Include these three JS files: -->
	<script type="text/javascript" src="/toonweb/swfobject.js"></script>
	<script type="text/javascript" src="/toonweb/web_socket.js"></script>

	<script type="text/javascript">

    WEB_SOCKET_SWF_LOCATION = "/toonweb/WebSocketMain.swf";  // Set URL of your WebSocketMain.swf here:
    WEB_SOCKET_DEBUG = true;  // Set this to dump debug message from Flash to console.log:


	var webClient = {
		
	} ;
	
	// Everything below is the same as using standard WebSocket.
	function initChat(){

		webClient.chat = function(){

			var chatMsg = {head:{}, body:{}} ;
			var input = document.getElementById("input") ;
			chatMsg.head.command = 'app.message'
			chatMsg.head.sender = '$config.sender$' ;
			chatMsg.head.topicid = '$config.topicId$' ;
			chatMsg.head.request = new Date().getUTCMilliseconds() + '' + '$config.sender$' ;
			chatMsg.head.receiver = '$config.receiver$' ;
			chatMsg.head.created = new Date().getUTCMilliseconds() ;
			chatMsg.head.msgid = new Date().getUTCMilliseconds() ;
			
			chatMsg.body.expression = '0000000078' ; // default const
			chatMsg.body.message = input.value ;
			chatMsg.body.nickname = '$config.sender$' ;
			chatMsg.body.sound = 'ttas.wav' ;
			chatMsg.body.background = '' ;
			chatMsg.body.action = '' ;
			chatMsg.body.account = '' ;
			chatMsg.body.code = '01' ;
			chatMsg.body.groupid = '' ;
			chatMsg.body.balloon = '' ;
			chatMsg.body.push = input.value ;
			chatMsg.body.additional = '' ;
			//receivers 
			
			this.getWebSocket().send(this.jsonToString(chatMsg));
			this.output('Send : ' + input.value, 'send') ;
			input.value = "";
			input.focus();
		} ;


		webClient.close = function(){
        	this.getWebSocket().close();
		} ;

		webClient.output = function(str, align) {
	    	var log = document.getElementById("outputDiv");
	    	var escaped = str.replace(/&/, "&amp;").replace(/</, "&lt;").replace(/>/, "&gt;").replace(/"/, "&quot;"); // "
	    	if (align == 'received') {
	    		log.innerHTML = "<div align='right' style=\"width:500; padding:7; background-color:dfdfdf \">" + escaped + "</div><br>" + log.innerHTML;
	    	} else {
	    		log.innerHTML = "<div align='left' style=\"width:500; padding:7; background-color:efefef \">" + escaped + "</div><br>" + log.innerHTML;
	    	} 
	    } ;
		
		webClient.jsonToString = function(obj){
			var t = typeof (obj);
		    if (t != "object" || obj === null) {
		        if (t == "string") obj = '"' + obj + '"'; // simple data type
		        return String(obj);
		    } else { // recurse array or object
		        var n, v, json = [], arr = (obj && obj.constructor == Array);
		        for (n in obj) {
		            v = obj[n]; t = typeof(v);
		            if (t == "string") v = '"'+v+'"';
		            else if (t == "object" && v !== null) v = this.jsonToString(v);
		            json.push((arr ? "" : '"' + n + '":') + String(v));
		        }
		        return (arr ? "[" : "{") + String(json) + (arr ? "]" : "}");
		    }
		} ;
		

   		var ws = new WebSocket('$config.address$') ;
		
	  	ws.onopen = function(){
	  		webClient.output("open") ;
	  		
	  	} ;
   		ws.onmessage = function(e){
   			var msg = eval('(' + e.data + ')') ;
   			console.log(e.data) ;
   			
   			var msgText = (typeof msg.body != 'undefined') ? msg.body.message : '' ;
   			
   			
			if ( typeof msg.head != 'undefined' && msg.head.command == 'ping'){
				ws.send('{head:{command:"app.keepalive"}, body:{sender:"$config.sender$"}}'); // reply
			} else if (typeof msg.head != 'undefined' && msg.aradon.senderName == '$config.sender$') {
	  			; 
			} else {
				webClient.output("received from " + ((msg.aradon != null) ? msg.aradon.senderName : '') + " : " + msgText, 'received'); // e.data contains received string.
			} 
			
			if (msg.head != null && msg.head.aradon == 'reply') {
//				webClient.output("received from " + ((msg.aradon != null) ? msg.aradon.senderName : '') + " : " + msgText, 'received'); // e.data contains received string.
				ws.send(e.data) ;
			} 
   		} ;
   		
   		ws.onclose = function(){
   			webClient.output("closed") ;
   		} ;
   		ws.onerror = function(){
   			webClient.output("error") ;
   		} ;
   		
   		webClient.getWebSocket = function(){
   			return ws ;
   		} ;
	}

  </script>
</head>
<body onload="initChat();" leftmargin=0 topmargin=0>

<table border=0 cellpadding=0 cellspacing=0 width=640 background="/toonweb/img/body_bg_pattern.png">
<form onsubmit="webClient.chat(); return false;">
	<tr><td height=166 background="/toonweb/img/top_bg.png"></td></tr>
	<tr><td height=425 style="background-image: url(/toonweb/img/body_logo.png) ; background-repeat:no-repeat; background-position:center center">
		<table width=600 align=center><tr><td><div id="outputDiv" style="overflow:scroll; width:600; height:380"></div></td></tr></table>
		</td></tr>
	<tr><td height=120 background="/toonweb/img/bottom_bg.png" valign="center">
		<table width='95%' align=center><tr><td width="100%"><textarea id="input" cols="60" rows="3" style="width:95%; font-size:16px"></textarea></td><td align=right><input type=image src="/toonweb/img/send_bt.png" onclick="webClient.chat(); return false;"/></td></tr></table>
	</td></tr>
</form>
</table>



</body>
</html>