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
<%
    java.text.SimpleDateFormat dt = new java.text.SimpleDateFormat("EEEdMMMyyyyHHmmssSSSZ",java.util.Locale.ENGLISH);
    java.util.Date date = new java.util.Date();
    Cookie dynamicCookieValue = new Cookie("DynamicSampleCookieName",dt.format(date));
    Cookie dynamicCookieName = new Cookie(dt.format(date),"staticCookieValue");
    dynamicCookieValue.setMaxAge(60*60*24);
    // expire cookie after 60 second in order to have it cleared for next run
    dynamicCookieName.setMaxAge(60);
    response.addCookie( dynamicCookieValue );
    response.addCookie( dynamicCookieName );
%>
<%@ include file="/includes/basePage.jsp" %>
