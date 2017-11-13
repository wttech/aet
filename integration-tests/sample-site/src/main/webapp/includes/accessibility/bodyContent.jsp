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
	<div class="navbar navbar-default navbar-fixed-top">
		<div class="container">
			<div class="navbar-header">
				<a href="#" class="navbar-brand">Navbar</a>
				<button class="navbar-toggle" type="button" data-toggle="collapse"
					data-target="#navbar-main" title="Toggle navbar">
                    <i class="glyphicon-menu-hamburger"></i>
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
					<li><a href="#">Some fun</a></li>
				</ul>

			</div>
		</div>
	</div>

	<div class="container">

		<div class="page-header" id="banner">
			<div class="row">
				<div class="col-lg-7 col-md-6 col-sm-5">
					<h1>AET Demo Page</h1>
					<p class="lead">for sanity tests</p>
					<p class="lead">
						build with:
						<ul>
							<li><a href="http://getbootstrap.com/">Bootstrap</a></li>
							<li><a href="http://bootswatch.com/">Bootswatch</a></li>
						<ul>
					</p>
				</div>

				<div class="col-lg-5 col-md-6 col-sm-7">
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
							<span class="small">This line of text is meant to be treated as fine
								print.</span>
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
							<span class="small">Someone famous in <cite title="Source Title">Source
									Title</cite></span>
						</blockquote>
					</div>
				</div>
				<div class="col-lg-6">
					<div class="bs-component">
						<blockquote class="pull-right">
							<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit.
								Integer posuere erat a ante.</p>
							<span class="small">Someone famous in <cite title="Source Title">Source
									Title</cite></span>
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
										<input type="text" class="form-control" id="inputEmail"
											placeholder="Email" />
									</div>
								</div>
								<div class="form-group">
									<label for="inputPassword" class="col-lg-2 control-label">Password</label>
									<div class="col-lg-10">
										<input type="password" class="form-control" id="inputPassword"
											placeholder="Password" />
										<div class="checkbox">
											<label for="checkbox"> <input type="checkbox" id="checkbox" /> Checkbox
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
									<label class="col-lg-2 control-label" for="optionsRadios1">Radios</label>
									<div class="col-lg-10">
										<div class="radio">
											<label for="optionsRadios1"> <input type="radio" name="optionsRadios"
												id="optionsRadios1" value="option1" checked="" /> Option one
												is this
											</label>
										</div>
										<div class="radio">
											<label for="optionsRadios2"> <input type="radio" name="optionsRadios"
												id="optionsRadios2" value="option2" /> Option two can be
												something else
											</label>
										</div>
									</div>
								</div>
								<!-- <div class="form-group">
									<label for="select" class="col-lg-2 control-label">Select</label>
									<div class="col-lg-10">
										<select class="form-control" id="select">
											<option selected="selected">1</option>
											<option>2</option>
											<option>3</option>
											<option>4</option>
											<option>5</option>
										</select>
									</div>
								</div>
								<div class="form-group">
									<label for="multiple" class="col-lg-2 control-label">Multiple</label>
									<div class="col-lg-10">
										<select id="multiple" multiple="" class="form-control">
											<option>1</option>
											<option>2</option>
											<option>3</option>
											<option>4</option>
											<option>5</option>
										</select>
									</div>
								</div>-->
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
							<label class="control-label" for="input-addon">Input addons</label>
							<div class="input-group">
								<span class="input-group-addon">$</span> <input type="text"
									class="form-control" id="input-addon" /> <span class="input-group-btn">
									<button class="btn btn-default" type="button">Button</button>
								</span>
							</div>
						</div>
						<div class="form-group">
							<button class="btn btn-default">Cancel</button>
							<button type="submit" class="btn btn-primary">Submit</button>
						</div>
					</form>

				</div>
			</div>
		</div>

