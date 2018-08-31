<!--

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

-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" indent="yes"/>

	<xsl:template match="suite">
		<xsl:copy>
			<xsl:copy-of select="@*"/> 
			<xsl:apply-templates select="document('partials/js-errors.xml')/*/test"/>
			<xsl:apply-templates select="document('partials/js-errors-filter-by-error.xml')/*/test"/>
			<xsl:apply-templates select="document('partials/js-errors-filter-by-errorPattern.xml')/*/test"/>
			<xsl:apply-templates select="document('partials/js-errors-filter-by-source-and-line.xml')/*/test"/>
			<xsl:apply-templates select="document('partials/status-codes.xml')/*/test"/>
			<xsl:apply-templates select="document('partials/w3c-html5.xml')/*/test"/>
			<xsl:apply-templates select="document('partials/w3c-html5-filtered.xml')/*/test"/>
			<xsl:apply-templates select="document('partials/source.xml')/*/test"/>
			<xsl:apply-templates select="document('partials/layout.xml')/*/test"/>
			<xsl:apply-templates select="document('partials/sleep-modifier.xml')/*/test"/>
			<xsl:apply-templates select="document('partials/cookie.xml')/*/test"/>
			<xsl:apply-templates select="document('partials/click.xml')/*/test"/>
			<xsl:apply-templates select="document('partials/wait-for-page-loaded-modifier.xml')/*/test"/>
			<xsl:apply-templates select="document('partials/client-side-performance.xml')/*/test"/>
			<xsl:apply-templates select="document('partials/accessibility.xml')/*/test"/>
			<xsl:apply-templates select="document('partials/accessibility-filtered.xml')/*/test"/>
			<xsl:apply-templates select="document('partials/header.xml')/*/test"/>
			<xsl:apply-templates select="document('partials/loginmodifier.xml')/*/test"/>
			<xsl:apply-templates select="document('partials/replacetext.xml')/*/test"/>
			<xsl:apply-templates select="document('partials/executejavascript.xml')/*/test"/>
			<xsl:apply-templates select="document('partials/wait-for-element-to-be-visible.xml')/*/test"/>
			<xsl:apply-templates select="document('partials/wait-for-image-completion.xml')/*/test"/>
			<xsl:copy-of select="reports"/>
		</xsl:copy>
	</xsl:template>

	<xsl:template match="@* | node()">
		<xsl:copy>
			<xsl:apply-templates select="@* | node()"/>
		</xsl:copy>
	</xsl:template>

</xsl:stylesheet>
