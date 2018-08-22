package com.example.aet.modifiers.pagebackground;

import com.cognifide.aet.job.api.collector.CollectorFactory;
import com.cognifide.aet.job.api.collector.CollectorJob;
import com.cognifide.aet.job.api.collector.CollectorProperties;
import com.cognifide.aet.job.api.collector.WebCommunicationWrapper;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import java.util.Map;
import org.osgi.service.component.annotations.Component;

@Component
public class PageBackgroundModifierCollectorFactory implements CollectorFactory {

  @Override
  public String getName() {
    return PageBackgroundModifier.NAME;
  }

  @Override
  public CollectorJob createInstance(CollectorProperties properties, Map<String, String> parameters,
      WebCommunicationWrapper webCommunicationWrapper) throws ParametersException {
    PageBackgroundModifier modifier = new PageBackgroundModifier(webCommunicationWrapper.getWebDriver());
    modifier.setParameters(parameters);
    return modifier;
  }
}