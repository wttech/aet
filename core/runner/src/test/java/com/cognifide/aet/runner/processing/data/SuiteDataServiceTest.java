///**
// * AET
// *
// * Copyright (C) 2013 Cognifide Limited
// *
// * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
// * in compliance with the License. You may obtain a copy of the License at
// *
// * http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software distributed under the License
// * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
// * or implied. See the License for the specific language governing permissions and limitations under
// * the License.
// */
//package com.cognifide.aet.runner.processing.data;
//
//import static org.mockito.Matchers.anyObject;
//import static org.mockito.Matchers.anyString;
//import static org.mockito.Mockito.when;
//
//import com.cognifide.aet.communication.api.metadata.Suite;
//import com.cognifide.aet.vs.MetadataDAO;
//import com.cognifide.aet.vs.StorageException;
//import org.apache.sling.testing.mock.osgi.MockOsgi;
//import org.apache.sling.testing.mock.osgi.junit.OsgiContext;
//import org.junit.Before;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.runners.MockitoJUnitRunner;
//import org.osgi.framework.BundleContext;
//
//@RunWith(MockitoJUnitRunner.class)
//public class SuiteDataServiceTest {
//
//  @Rule
//  public final OsgiContext context = new OsgiContext();
//
////  @InjectMocks
//  SuiteDataService suiteDataService = new SuiteDataService();
//
//  Suite currentRun;
//
////  @Mock
//  private MetadataDAO metadataDAO = context.getService(MetadataDAO.class);
//
//  @Before
//  public void setUp() throws Exception {
//    currentRun = new Suite("company-project-test-suite-1539942898542", "company", "project", "test", "1539942898542","hascode");
//  }
//
//  @Test
//  public void tt() throws StorageException {
////    when(metadataDAO.getLatestRun(anyObject(), anyString())).thenReturn(currentRun);
//    BundleContext bundleContext = MockOsgi.newBundleContext();
////    bundleContext.registerService(SuiteDataServiceTest.class, suiteDataService, metadataDAO);
////    when(metadataDAO).thenReturn("");
//
//
////    SuiteDataService service1 = context.registerInjectActivateService( SuiteDataService.class);
////    BundleContext bundleContext = MockOsgi.newBundleContext();
////    bundleContext.registerService(SuiteDataServiceTest.class, suiteDataService, metadataDAO);
////    MockOsgi.injectServices(suiteDataService, bundleContext);
////
////    MockOsgi.activate(suiteDataService,bundleContext, metadataDAO);
//
//
//
//
//// inject dependencies
//    MockOsgi.injectServices(suiteDataService, bundleContext);
//
//// activate service
//    MockOsgi.activate(suiteDataService,bundleContext, metadataDAO);
//
//
//
//    Suite suite = suiteDataService.enrichWithPatterns(currentRun);
//  }
//}