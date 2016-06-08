/*
 * Cognifide AET :: Job Common
 *
 * Copyright (C) 2013 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cognifide.aet.job.common.comparators.source;

import com.cognifide.aet.job.api.comparator.ComparatorJob;
import com.cognifide.aet.job.api.datamodifier.DataModifierJob;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.job.common.comparators.source.diff.DiffParser;
import com.cognifide.aet.job.common.comparators.source.diff.ResultDelta;
import com.cognifide.aet.job.common.comparators.source.visitors.ContentVisitor;
import com.cognifide.aet.job.common.comparators.source.visitors.MarkupVisitor;
import com.cognifide.aet.job.common.comparators.source.visitors.NodeTraversor;
import com.cognifide.aet.vs.ComparatorProperties;
import com.cognifide.aet.vs.Node;
import com.cognifide.aet.vs.ResultStatus;
import com.cognifide.aet.vs.VersionStorageException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class SourceComparator implements ComparatorJob {

	private static final Logger LOGGER = LoggerFactory.getLogger(SourceComparator.class);

	public static final String COMPARATOR_TYPE = "source";

	public static final String COMPARATOR_NAME = "source";

	private static final String UTF_8_CHARSET_NAME = "UTF-8";

	private static final String DATA_ATTRIBUTE_NAME = "data";

	private static final String SOURCE_COMPARE_TYPE = "compareType";

	protected final Node dataNode;

	protected final Node patternNode;

	protected final Node resultNode;

	private final ComparatorProperties comparatorProperties;

	private final DiffParser diffParser;

	private SourceCompareType sourceCompareType = SourceCompareType.ALL;

	private final List<DataModifierJob> dataModifierJobs;

	public SourceComparator(final Node dataNode, final Node patternNode, final Node resultNode,
			ComparatorProperties comparatorProperties, DiffParser diffParser,
			List<DataModifierJob> dataModifierJobs) {
		this.dataNode = dataNode;
		this.patternNode = patternNode;
		this.resultNode = resultNode;
		this.comparatorProperties = comparatorProperties;
		this.diffParser = diffParser;
		this.dataModifierJobs = dataModifierJobs;
	}

	private String getPageSource(final Map<String, String> data, final Node node) throws ProcessingException {
		String source = null;
		if (data.containsKey(DATA_ATTRIBUTE_NAME)) {
			source = this.getDataSourceOfNode(node, data);
		}
		return source;
	}

	private String getDataSourceOfNode(final Node node, final Map<String, String> data)
			throws ProcessingException {
		String source = null;
		InputStream stream = null;
		try {
			stream = node.getData(data.get(DATA_ATTRIBUTE_NAME));
			if (stream != null) {
				source = IOUtils.toString(stream, UTF_8_CHARSET_NAME);
			}
		} catch (VersionStorageException | IOException e) {
			throw new ProcessingException(e.getMessage(), e);
		} finally {
			IOUtils.closeQuietly(stream);
		}
		return source;
	}

	@Override
	public final Boolean compare() throws ProcessingException {
		InputStream prettyDiffStream = null;
		Boolean compareResult = null;
		try {
			final Map<String, String> pattern = patternNode.getResult(Map.class);
			final Map<String, String> data = dataNode.getResult(Map.class);

			String patternSource = formatCode(getPageSource(pattern, patternNode));
			String dataSource = formatCode(getPageSource(data, dataNode));

			for (DataModifierJob<String> dataModifierJob : dataModifierJobs) {
				LOGGER.info(
						"Starting {}. TestName: {} UrlName: {} Url: {} CollectorModule: {} CollectorName: {} ComparatorModule: {} ComparatorName: {}",
						dataModifierJob.getInfo(), comparatorProperties.getTestName(),
						comparatorProperties.getUrlName(), comparatorProperties.getUrl(),
						comparatorProperties.getCollectorModule(),
						comparatorProperties.getCollectorModuleName(),
						comparatorProperties.getComparatorModule(),
						comparatorProperties.getComparatorModuleName());
				dataSource = dataModifierJob.modifyData(dataSource);
				LOGGER.info(
						"Successfully ended data modifications using {}. TestName: {} UrlName: {} Url: {} CollectorModule: {} CollectorName: {} ComparatorModule: {} ComparatorName: {}",
						dataModifierJob.getInfo(), comparatorProperties.getTestName(),
						comparatorProperties.getUrlName(), comparatorProperties.getUrl(),
						comparatorProperties.getCollectorModule(),
						comparatorProperties.getCollectorModuleName(),
						comparatorProperties.getComparatorModule(),
						comparatorProperties.getComparatorModuleName());
				patternSource = dataModifierJob.modifyPattern(patternSource);
				LOGGER.info(
						"Successfully ended pattern modifications using {}. TestName: {} UrlName: {} Url: {} CollectorModule: {} CollectorName: {} ComparatorModule: {} ComparatorName: {}",
						dataModifierJob.getInfo(), comparatorProperties.getTestName(),
						comparatorProperties.getUrlName(), comparatorProperties.getUrl(),
						comparatorProperties.getCollectorModule(),
						comparatorProperties.getCollectorModuleName(),
						comparatorProperties.getComparatorModule(),
						comparatorProperties.getComparatorModuleName());
			}

			if (StringUtils.isNotBlank(patternSource)) {
				compareResult = patternSource.equals(dataSource);

				final SourceComparatorResult result = new SourceComparatorResult(
						ResultStatus.fromBoolean(compareResult), comparatorProperties);

				List<ResultDelta> generatedDiffs = diffParser.generateDiffs(patternSource, dataSource,
						isTrimSourcesNeeded(sourceCompareType));

				result.setDeltas(generatedDiffs);

				result.setSourceCompareType(sourceCompareType.name());
				resultNode.saveData("formattedPattern.txt",
						IOUtils.toInputStream(patternSource, UTF_8_CHARSET_NAME));
				resultNode.saveData("formattedSource.txt",
						IOUtils.toInputStream(dataSource, UTF_8_CHARSET_NAME));
				resultNode.saveResult(result);
			}
		} catch (VersionStorageException | IOException e) {
			throw new ProcessingException(e.getMessage(), e);
		} finally {
			IOUtils.closeQuietly(prettyDiffStream);
		}
		return compareResult;
	}

	private boolean isTrimSourcesNeeded(SourceCompareType sourceCompareType) {
		return SourceCompareType.ALLFORMATTED.equals(sourceCompareType)
				|| SourceCompareType.MARKUP.equals(sourceCompareType);
	}

	@Override
	public void setParameters(final Map<String, String> params) throws ParametersException {
		if (params.containsKey(SOURCE_COMPARE_TYPE)) {
			this.sourceCompareType = SourceCompareType.valueOf(params.get(SOURCE_COMPARE_TYPE).toUpperCase());
		}
	}

	private String formatCode(String code) {
		String result;
		switch (sourceCompareType) {
			case MARKUP:
				result = formatCodeMarkup(code);
				break;
			case CONTENT:
				result = formatCodeContent(code);
				break;
			case ALLFORMATTED:
				result = formatCodeAllFormatted(code);
				break;
			default:
				result = code;
				break;
		}
		return result;
	}

	private String formatCodeAllFormatted(String code) {
		Document doc = Jsoup.parse(code);
		return removeEmptyLines(doc.outerHtml());
	}

	private String removeEmptyLines(String source) {
		String result = source;
		if (StringUtils.isNotBlank(source)) {
			result = result.replaceAll("(?m)^[ \t]*[\r\n]+", "");
		}
		return result;
	}

	private String formatCodeContent(String code) {
		Document doc = Jsoup.parse(code);
		ContentVisitor visitor = new ContentVisitor();
		NodeTraversor traversor = new NodeTraversor(visitor);
		traversor.traverse(doc);
		return visitor.getFormattedText();
	}

	private String formatCodeMarkup(String code) {
		Document doc = Jsoup.parse(code);
		NodeTraversor traversor = new NodeTraversor(new MarkupVisitor());
		traversor.traverse(doc);
		return doc.html();
	}
}
