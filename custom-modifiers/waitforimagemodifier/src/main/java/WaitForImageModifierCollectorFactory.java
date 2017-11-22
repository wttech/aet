import com.cognifide.aet.job.api.collector.CollectorFactory;
import com.cognifide.aet.job.api.collector.CollectorJob;
import com.cognifide.aet.job.api.collector.CollectorProperties;
import com.cognifide.aet.job.api.collector.WebCommunicationWrapper;
import com.cognifide.aet.job.api.exceptions.ParametersException;

import java.util.Map;

public class WaitForImageModifierCollectorFactory implements CollectorFactory {
    @Override
    public String getName() {
        return WaitForImageModifier.NAME;
    }

    @Override
    public CollectorJob createInstance(CollectorProperties collectorProperties, Map<String, String> parameters,
                                       WebCommunicationWrapper webCommunicationWrapper) throws ParametersException {
        WaitForImageModifier modifier = new WaitForImageModifier(webCommunicationWrapper.getWebDriver());
        modifier.setParameters(parameters);
        return modifier;
    }
}
