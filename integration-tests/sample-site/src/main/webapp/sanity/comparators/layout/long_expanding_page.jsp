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

<%--
    This page simulates a page that takes a long time to load.
    The final height of the page is above 12k pixels.
--%>

<script>
window.onload = function () {
  var counter = 0;
  var looper = setInterval(function() {
      counter++;
      console.log("Counter is: " + counter);

      var emptyDiv = document.createElement("div");
      emptyDiv.style = "height: 1000px";

      var src = document.getElementById("enclosingDiv");
      src.appendChild(emptyDiv);

      if (counter >= 12) {
          clearInterval(looper);
      }
  }, 100);

  <%--
      TODO: Change 12 iterations to 30 when (https://github.com/Cognifide/aet/pull/387) is merged.
  --%>

};
</script>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="/includes/header.jsp" %>
<div id="enclosingDiv"></div>
<%@ include file="dynamic_content.jsp" %>

