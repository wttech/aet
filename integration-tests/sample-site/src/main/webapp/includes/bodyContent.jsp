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
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
	<div class="navbar navbar-default navbar-fixed-top">
		<div class="container">
			<div class="navbar-header">
				<a href="#" class="navbar-brand">Navbar</a>
				<button class="navbar-toggle" type="button" data-toggle="collapse" data-target="#navbar-main">
					<span class="icon-bar"></span>
					<span class="icon-bar"></span>
					<span class="icon-bar"></span>
				</button>
			</div>
			<div class="navbar-collapse collapse" id="navbar-main">
				<ul class="nav navbar-nav">
					<li class="dropdown"><a class="dropdown-toggle"
						data-toggle="dropdown" href="#" id="themes">Dropdown <span
							class="caret"></span></a>
						<ul class="dropdown-menu">
							<li><a href="#">Default</a></li>
							<li class="divider"></li>
							<li><a href="#">Option</a></li>
							<li><a href="#">Second option</a></li>
							<li><a href="#">Last option</a></li>
						</ul></li>
					<li><a href="#">Help</a></li>
					<li><a href="#">Blog</a></li>
				</ul>

				<ul class="nav navbar-nav navbar-right">
					<li><a href="#" target="_blank">Some fun</a></li>
				</ul>

			</div>
		</div>
	</div>

	<div class="container">

		<div class="page-header" id="banner">
			<div class="row">
				<div class="col-lg-8 col-md-7 col-sm-6">
					<h1>AET Demo Page</h1>
					<p class="lead">for sanity tests</p>
					<p class="lead">
						build with <a href="http://getbootstrap.com/">Bootstrap</a> and <a
							href="http://bootswatch.com/">Bootswatch</a>
					</p>
				</div>

				<div class="col-lg-4 col-md-5 col-sm-6">
					<div class="sponsor">
						<img src="/sample-site/assets/demo_files/logo.png" alt="Bootswatch" />
					</div>
				</div>

			</div>
		</div>

		<div class="bs-docs-section">
			<div class="row">
				<p class="lead">AET is a tool
					that can be used to automate testing developed by Cognifide.</p>
				<p class="lead">The application allows to create regression
					tests easily and quickly and then to analyze their results (layout
					comparison, potential JavaScript and W3C validation errors, HTTP
					status codes).</p>
			</div>
		</div>


		<!-- Typography -->

		<div class="bs-docs-section">
			<div class="row">
				<div class="col-lg-12">
					<div class="page-header">
						<h1 id="type">Typography</h1>
					</div>
				</div>
			</div>

			<!-- Headings -->

			<div class="row">
				<div class="col-lg-4">
					<div class="bs-component">
						<h1>Heading 1</h1>
						<h2>Heading 2</h2>
						<h3>Heading 3</h3>
						<h4>Heading 4</h4>
						<h5>Heading 5</h5>
						<h6>Heading 6</h6>
						<p class="lead">Vivamus sagittis lacus vel augue laoreet
							rutrum faucibus dolor auctor.</p>
					</div>
				</div>
				<div class="col-lg-4">
					<div class="bs-component">
						<h2>Example body text</h2>
						<p>
							Nullam quis risus eget <a href="#">urna mollis ornare</a> vel eu
							leo. Cum sociis natoque penatibus et magnis dis parturient
							montes, nascetur ridiculus mus. Nullam id dolor id nibh ultricies
							vehicula.
						</p>
						<p>
							<small>This line of text is meant to be treated as fine
								print.</small>
						</p>
						<p>
							The following snippet of text is <strong>rendered as
								bold text</strong>.
						</p>
						<p>
							The following snippet of text is <em>rendered as italicized
								text</em>.
						</p>
						<p>
							An abbreviation of the word attribute is <abbr title="attribute">attr</abbr>.
						</p>
					</div>

				</div>
				<div class="col-lg-4">
					<div class="bs-component">
						<h2>Emphasis classes</h2>
						<p class="text-muted">Fusce dapibus, tellus ac cursus commodo,
							tortor mauris nibh.</p>
						<p class="text-primary">Nullam id dolor id nibh ultricies
							vehicula ut id elit.</p>
						<p class="text-warning">Etiam porta sem malesuada magna mollis
							euismod.</p>
						<p class="text-danger">Donec ullamcorper nulla non metus
							auctor fringilla.</p>
						<p class="text-success">Duis mollis, est non commodo luctus,
							nisi erat porttitor ligula.</p>
						<p class="text-info">Maecenas sed diam eget risus varius
							blandit sit amet non magna.</p>
					</div>

				</div>
			</div>

			<!-- Blockquotes -->

			<div class="row">
				<div class="col-lg-12">
					<h2 id="type-blockquotes">Blockquotes</h2>
				</div>
			</div>
			<div class="row">
				<div class="col-lg-6">
					<div class="bs-component">
						<blockquote>
							<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit.
								Integer posuere erat a ante.</p>
							<small>Someone famous in <cite title="Source Title">Source
									Title</cite></small>
						</blockquote>
					</div>
				</div>
				<div class="col-lg-6">
					<div class="bs-component">
						<blockquote class="pull-right">
							<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit.
								Integer posuere erat a ante.</p>
							<small>Someone famous in <cite title="Source Title">Source
									Title</cite></small>
						</blockquote>
					</div>
				</div>
			</div>
		</div>


		<!-- Forms -->
		<div class="bs-docs-section">
			<div class="row">
				<div class="col-lg-12">
					<div class="page-header">
						<h1 id="forms">Forms</h1>
					</div>
				</div>
			</div>

			<div class="row">
				<div class="col-lg-6">
					<div class="well bs-component">
						<form class="form-horizontal">
							<fieldset>
								<legend>Legend</legend>
								<div class="form-group">
									<label for="inputEmail" class="col-lg-2 control-label">Email</label>
									<div class="col-lg-10">
										<input type="email" class="form-control" id="inputEmail"
											placeholder="Email" />
									</div>
								</div>
								<div class="form-group">
									<label for="inputPassword" class="col-lg-2 control-label">Password</label>
									<div class="col-lg-10">
										<input type="password" class="form-control" id="inputPassword"
											placeholder="Password" />
										<div class="checkbox">
											<label> <input type="checkbox" /> Checkbox
											</label>
										</div>
									</div>
								</div>
								<div class="form-group">
									<label for="textArea" class="col-lg-2 control-label">Textarea</label>
									<div class="col-lg-10">
										<textarea class="form-control" rows="3" id="textArea"></textarea>
										<span class="help-block">A longer block of help text
											that breaks onto a new line and may extend beyond one line.</span>
									</div>
								</div>
								<div class="form-group">
									<label class="col-lg-2 control-label">Radios</label>
									<div class="col-lg-10">
										<div class="radio">
											<label> <input type="radio" name="optionsRadios"
												id="optionsRadios1" value="option1" checked="" /> Option one
												is this
											</label>
										</div>
										<div class="radio">
											<label> <input type="radio" name="optionsRadios"
												id="optionsRadios2" value="option2" /> Option two can be
												something else
											</label>
										</div>
									</div>
								</div>
								<div class="form-group">
									<div class="col-lg-10 col-lg-offset-2">
										<button class="btn btn-default">Cancel</button>
										<button type="submit" class="btn btn-primary">Submit</button>
									</div>
								</div>
							</fieldset>
						</form>
					</div>
				</div>
				<div class="col-lg-4 col-lg-offset-1">

					<form class="bs-component">
						<div class="form-group">
							<label class="control-label" for="focusedInput">Focused
								input</label> <input class="form-control" id="focusedInput" type="text"
								value="This is focused..." />
						</div>

						<div class="form-group">
							<label class="control-label" for="disabledInput">Disabled
								input</label> <input class="form-control" id="disabledInput" type="text"
								placeholder="Disabled input here..." disabled="" />
						</div>

						<div class="form-group has-warning">
							<label class="control-label" for="inputWarning">Input
								warning</label> <input type="text" class="form-control"
								id="inputWarning" />
						</div>

						<div class="form-group has-error">
							<label class="control-label" for="inputError">Input error</label>
							<input type="text" class="form-control" id="inputError" />
						</div>

						<div class="form-group has-success">
							<label class="control-label" for="inputSuccess">Input
								success</label> <input type="text" class="form-control"
								id="inputSuccess" />
						</div>

						<div class="form-group">
							<label class="control-label" for="inputLarge">Large input</label>
							<input class="form-control input-lg" type="text" id="inputLarge" />
						</div>

						<div class="form-group">
							<label class="control-label" for="inputDefault">Default
								input</label> <input type="text" class="form-control" id="inputDefault" />
						</div>

						<div class="form-group">
							<label class="control-label" for="inputSmall">Small input</label>
							<input class="form-control input-sm" type="text" id="inputSmall" />
						</div>

						<div class="form-group">
							<label class="control-label">Input addons</label>
							<div class="input-group">
								<span class="input-group-addon">$</span> <input type="text"
									class="form-control" /> <span class="input-group-btn">
									<button class="btn btn-default" type="button">Button</button>
								</span>
							</div>
						</div>
					</form>

				</div>
			</div>
		</div>

		<!-- Footer -->
		<footer>
			<div class="row">
				<div class="col-lg-12">

					<p>
						Made by <a href="http://thomaspark.me" rel="nofollow">Thomas
							Park</a>. Contact him at <a href="mailto:thomas@bootswatch.com">thomas@bootswatch.com</a>.
					</p>
					<p>
						Code released under the <a
							href="https://github.com/thomaspark/bootswatch/blob/gh-pages/LICENSE">MIT
							License</a>.
					</p>
					<p>
						Based on <a href="http://getbootstrap.com" rel="nofollow">Bootstrap</a>.
						Icons from <a href="http://fortawesome.github.io/Font-Awesome/"
							rel="nofollow">Font Awesome</a>. Web fonts from <a
							href="http://www.google.com/webfonts" rel="nofollow">Google</a>.
					</p>

				</div>
			</div>

		</footer>

	</div>
	<!-- /.container -->

	<!-- Bootstrap core JavaScript
	================================================== -->
	<!-- Placed at the end of the document so the pages load faster -->
<c:choose>
<c:when test="${combineJs}">
	<script src="/sample-site/assets/demo_files/combined.js"></script>
</c:when>
<c:otherwise>
	<script src="/sample-site/assets/demo_files/jquery.min.js"></script>
	<script src="/sample-site/assets/demo_files/bootstrap.min.js"></script>
	<!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
	<script src="/sample-site/assets/demo_files/ie10-viewport-bug-workaround.js"></script>
</c:otherwise>
</c:choose>