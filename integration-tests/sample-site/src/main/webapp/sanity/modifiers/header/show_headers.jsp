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
<%@ page import="java.util.*" %>
    <html>

    <head>
        <title>Headers test</title>
    </head>

    <body>
        <h2>HTTP Request Headers Received</h2>
        <table>
            <% Enumeration enumeration = request.getHeaderNames(); while (enumeration.hasMoreElements())
            {
                String name=(String) enumeration.nextElement();
                String value = request.getHeader(name); %>
                <tr>
                    <td><%=name %></td>
                    <td id="<%=name %>"><%=value %></td>
                </tr>
                <% } %>
        </table>
    </body>
    </html>