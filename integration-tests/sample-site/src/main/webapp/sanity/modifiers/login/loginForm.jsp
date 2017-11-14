<%--

    AET

    Copyright (C) 2013 Cognifide Limited

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

--%>
<%@ include file="/includes/header.jsp" %>
<h1>LoginForm:</h1>
                <form name="login" method="POST" >
                        <input id="input-username"  type="text" name="j_username" >
                        <input id="input-password"  type="password" name="j_password" >
                <input id="input-submit" type="submit" value="Submit" />
                </form>


            <br/>

            <h1>Just show posted values:</h1>
            <ul>
            <li><p><b>Login:</b>
               <%= request.getParameter("j_username")%>
            </p></li>
            <li><p><b>Pass:</b>
               <%= request.getParameter("j_password")%>
            </p></li>
            </ul>


<%-- set cookie based on posta parameters --%>
<%
 String user = request.getParameter("j_username");
 String pass = request.getParameter("j_password");

 Cookie loginToken = new Cookie("login-token", user+"_"+pass);
 response.addCookie(loginToken);
%>
<%-- set static cookie --%>
<%
 Cookie staticCookie = new Cookie("static-cookie", "cookievalue");
 response.addCookie(staticCookie);
%>


<%@ include file="/includes/footer.jsp" %>
