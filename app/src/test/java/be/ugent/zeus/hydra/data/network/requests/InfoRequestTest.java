package be.ugent.zeus.hydra.data.network.requests;

import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.data.models.info.InfoItem;
import be.ugent.zeus.hydra.data.network.ArrayJsonSpringRequestTest;
import be.ugent.zeus.hydra.data.network.JsonSpringRequest;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class InfoRequestTest extends ArrayJsonSpringRequestTest<InfoItem> {

    public InfoRequestTest() {
        super(InfoItem[].class);
    }

    @Override
    protected Resource getSuccessResponse() {
        return new ClassPathResource("info_content.json");
    }

    @Override
    protected JsonSpringRequest<InfoItem[]> getRequest() {
        return new InfoRequest();
    }
}