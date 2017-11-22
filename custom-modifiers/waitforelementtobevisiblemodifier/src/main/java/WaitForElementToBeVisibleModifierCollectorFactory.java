import com.cognifide.aet.job.api.collector.CollectorFactory;
import com.cognifide.aet.job.api.collector.CollectorJob;
import com.cognifide.aet.job.api.collector.CollectorProperties;
import com.cognifide.aet.job.api.collector.WebCommunicationWrapper;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;

import java.util.Map;

@Component
@Service
public class WaitForElementToBeVisibleModifierCollectorFactory implements CollectorFactory {

    @Override
    public String getName() {
        return WaitForElementToBeVisibleModifier.NAME;
    }

    @Override
    public CollectorJob createInstance(CollectorProperties properties, Map<String, String> parameters,
                                       WebCommunicationWrapper webCommunicationWrapper) throws ParametersException {
        WaitForElementToBeVisibleModifier modifier =
                new WaitForElementToBeVisibleModifier(webCommunicationWrapper.getWebDriver());
        modifier.setParameters(parameters);
        return modifier;
    }
}
