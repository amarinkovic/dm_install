package pro.documentum.demo.isis.integtests.bootstrap;

import org.apache.isis.core.integtestsupport.IsisSystemForTest;
import org.apache.isis.objectstore.jdo.datanucleus.IsisConfigurationForJdoIntegTests;

import pro.documentum.demo.isis.app.DomainAppAppManifest;

public class DomainAppSystemInitializer {

    public static void initIsft() {
        IsisSystemForTest isft = IsisSystemForTest.getElseNull();
        if(isft == null) {
            isft = new IsisSystemForTest.Builder()
                    .withLoggingAt(org.apache.log4j.Level.INFO)
                    .with(new DomainAppAppManifest())
                    .with(new IsisConfigurationForJdoIntegTests())
                    .build();
            isft.setUpSystem();
            IsisSystemForTest.set(isft);
        }
    }

}
