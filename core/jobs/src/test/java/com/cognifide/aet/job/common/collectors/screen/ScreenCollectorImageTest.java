/**
 * AET
 * <p>
 * Copyright (C) 2013 Cognifide Limited
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.cognifide.aet.job.common.collectors.screen;

import com.cognifide.aet.job.api.collector.CollectorProperties;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.vs.ArtifactsDAO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.remote.RemoteWebDriver;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;


import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class ScreenCollectorImageTest {

    private static final Color black = new Color(0);

    @InjectMocks
    private ScreenCollector screenCollector;

    private BufferedImage testImage;


    @Before
    public void setup() throws Exception {
        String path = getClass().getResource("/screens/48x48.png").getFile();
        File image = new File(path);
        testImage = ImageIO.read(image);
    }

    @Test
    public void testSubImageSize() {
        Point point = new org.openqa.selenium.Point(12, 12);
        Dimension dimension = new Dimension(24, 24);

        BufferedImage subImage = screenCollector.getSubImage(testImage, point, dimension);

        assertThat(subImage.getHeight()).isEqualTo(24);
        assertThat(subImage.getWidth()).isEqualTo(24);

    }

    @Test
    public void testSubImageContent() {
        Point point = new org.openqa.selenium.Point(12, 12);
        Dimension dimension = new Dimension(24, 24);

        BufferedImage subImage = screenCollector.getSubImage(testImage, point, dimension);
        boolean onlyBlack = true;

        for (int i = 0; i < subImage.getHeight(); i++) {
            for (int j = 0; j < subImage.getWidth(); j++) {
                onlyBlack &= black.equals(new Color(subImage.getRGB(i, j)));
            }
        }
        assertThat(onlyBlack).isTrue();
    }
}
