/*
FONTS
*/
@import url("https://fast.fonts.com/t/1.css?apiType=css&projectid=eab16f51-e7e4-4285-8b2b-3844b7921c78");
@font-face {
  font-family: "AvantGardeGothicITCW01B 731069";
  src: url("https://tokbox.com/fonts/2a334c60-3e0d-4f43-b0e9-5284ea33961a.eot?#iefix");
  src: url("https://tokbox.com/fonts/2a334c60-3e0d-4f43-b0e9-5284ea33961a.eot?#iefix") format("eot"), url("https://tokbox.com/fonts/c68f0543-0caf-4988-b234-355520476b8c.woff") format("woff"), url("https://tokbox.com/fonts/2d4f1d98-ddb3-4acc-ae78-c8b1863f780e.ttf") format("truetype"), url("https://tokbox.com/fonts/80f98a03-905d-49e6-8614-cec7c32ca4f2.svg#80f98a03-905d-49e6-8614-cec7c32ca4f2") format("svg");
}
@font-face {
  font-family: "AvantGardeGothicITCW01X";
  src: url("https://tokbox.com/fonts/d042c69d-2a74-4689-9915-3c34306a3b76.eot?#iefix");
  src: url("https://tokbox.com/fonts/d042c69d-2a74-4689-9915-3c34306a3b76.eot?#iefix") format("eot"), url("https://tokbox.com/fonts/0312a390-01c7-423e-ad0c-b5b4f25229af.woff") format("woff"), url("https://tokbox.com/fonts/16e3b729-9cc0-490e-9de3-d678f36aba08.ttf") format("truetype"), url("https://tokbox.com/fonts/2c90e8aa-95a7-463c-956c-c7fac7412d35.svg#2c90e8aa-95a7-463c-956c-c7fac7412d35") format("svg");
}
@font-face {
  font-family: "AvantGardeGothicITCW01M 731087";
  src: url("https://tokbox.com/fonts/5daf8f81-4f5b-4b44-8fd3-91c56d20e799.eot?#iefix");
  src: url("https://tokbox.com/fonts/5daf8f81-4f5b-4b44-8fd3-91c56d20e799.eot?#iefix") format("eot"), url("https://tokbox.com/fonts/43b723ac-a6f2-4d5d-9d72-c50aea85ecee.woff") format("woff"), url("https://tokbox.com/fonts/9093e944-c2da-4954-953f-ca2eb3a227dd.ttf") format("truetype"), url("https://tokbox.com/fonts/e3929a31-b148-4180-91be-4b490bdac87d.svg#e3929a31-b148-4180-91be-4b490bdac87d") format("svg");
}


body{
  position: absolute;
}
html, body, div, span, applet, object, iframe, h1, h2, h3, h4, h5, h6, p, blockquote, pre, a, abbr, acronym, address, big, cite, code, del, dfn, em, img, ins, kbd, q, s, samp, small, strike, strong, sub, sup, tt, var, b, u, i, center, dl, dt, dd, ol, ul, li, fieldset, form, label, legend, table, caption, tbody, tfoot, thead, tr, th, td, article, aside, canvas, details, embed, figure, figcaption, footer, header, hgroup, menu, nav, output, ruby, section, summary, time, mark, audio{
  font-weight: 300;
  margin: 0;
  padding: 0;
  border: 0;
  font-size: 100%;
  font: inherit;
  vertical-align: baseline;
}
ol, ul {
  list-style: none;
}
#chattr{
  width: 300px;
  position: fixed;
  top: 0;
  bottom: 0;
  right:-300px;
  z-index: 1;
  border: solid 1px #555;
  overflow: visible;
  box-shadow: 2px 2px 3px 2px rgba(0, 0, 0, 0.2);
  transition: all 0.5s;
  -webkit-transition: all 0.5s; 
}
#chattr.chatEnabled{
  width: 300px;
  position: fixed;
  top: 0;
  bottom: 0;
  right: 0px;
  z-index: 1;
  border: solid 1px #555;
  overflow: visible;
  box-shadow: 2px 2px 3px 2px rgba(0, 0, 0, 0.2);
  transition: all 0.5s;
  -webkit-transition: all 0.5s; 
}
#relative_wrapper{
  width:100%;
  height:100%;
  position: relative;
}
#chattr .chat-header{
  position: relative;
  top: 0px;
  width: 100%;
  height: 40px;
  background-color: #43879E;
}
#chattr .chat-header > div{
  position: absolute;
  left: 5px;
  top: 5px;
  height: 30px;
}
#chattr .chat-header > div span{
  width: 1px;
  border-left: solid 1px #fff;
  background-color: #000;
  margin-right: 4px;
  float: left;
  height: 100%;
  opacity: 0.2;
}
#chattr .chat-header h4{
  font-family: ;
  margin-left: 40px;
  line-height: 40px;
  font-size: 18px;
  color: white;
  font-family: "AvantGardeGothicITCW01X";
  font-weight: 200;
}
#chattr .inner-chat{
  position: absolute;
  left: 0;
  width: 100%;
  top: 40px;
  bottom: 50px;
  overflow: auto;
  background-color: #333;
  font-family: 'Muli', sans-serif;
}
#chattr .chat-input-wrapper{
  padding: 5px;
  background-color: #363636;
  position: absolute;
  bottom: 0px;
  width: 300px;
  height: 50px;
  box-sizing: border-box;
}
#chattr .chat-input-wrapper #chatInput{
  height: 40px;
  font-size: 16px;
  padding: 5px 10px;
  border: solid 1px;
  border-radius: 0px;
  width: 100%;
  box-shadow: inset 2px 2px 3px 2px rgba(0, 0, 0, 0.2);
  border-radius: 15px;
  box-sizing: border-box;
}
#chattr .inner-chat p#displayHelp{
  border-radius: 8px;
  padding: 8px 15px;
  max-width: 80%;
  position: relative;
  word-wrap: break-word;
  font-size: 14px;
  line-height: 16px;
  margin-bottom: 30px;
  color: #ad967d;
  width: 90%;
  margin-left: 5%;
  margin-right: 5%;
  padding-right: 0px;
  padding-left: 0px;
  text-align: center;
  float: left;
  max-width: none;
}
#chattr .inner-chat ul#messages li{
  margin: 10px;
  font-size: 14px;
  display: block;
  margin-bottom: 30px;
}
#chattr .inner-chat ul#messages li label{
  color: #888;
  margin-bottom: 10px;
  display: inline-block;
  width: 100%;
}
#chattr .inner-chat ul#messages li.from-me label{
  text-align: right;
}
#chattr .inner-chat ul#messages li p{
  border-radius: 8px;
  padding: 8px 15px;
  max-width: 80%;
  position: relative;
  word-wrap: break-word;
  font-size: 14px;
  line-height: 16px;
  margin-bottom: 30px;
  color: white;
  font-weight: 100;
}
#chattr .inner-chat ul#messages li.from-me p{
  background-color: #4f97af;
  float: right;
}
#chattr .inner-chat ul#messages li.from-others p{
  background-color: #555;
  float: left;
}
#chattr .inner-chat ul#messages li p:after{
  content: "";
  position: absolute;
  bottom: 100%;
  border-bottom: 6px solid black;
  border-left: 6px solid transparent;
  border-right: 6px solid transparent;
}
#chattr .inner-chat ul#messages li.from-me p:after{
  right: 15px;
  border-bottom-color: #4f97af;
}
#chattr .inner-chat ul#messages li.from-others p:after{
  left: 15px;
  border-bottom-color: #555; 
}
#chattr .inner-chat ul#messages li.status p {
  color: #ad967d;
  width: 90%;
  margin-left: 5%;
  margin-right: 5%;
  padding-right: 0px;
  padding-left: 0px;
  text-align: center;
  float: left;
  max-width: none
}
#chattr .inner-chat ul#messages li.status p span{
  color: white;
}
#chattr .inner-chat ul#messages li.status p:after {
  border: none;
}
#chattr .inner-chat ul#messages li.status.help p, #chattr .inner-chat ul#messages li.status p.userList{
  margin-bottom: 0px;
}
#chattr .inner-chat ul#messages li.status.help p.last, #chattr .inner-chat ul#messages li.status p.userList.last{
  margin-bottom: 30px;
}
