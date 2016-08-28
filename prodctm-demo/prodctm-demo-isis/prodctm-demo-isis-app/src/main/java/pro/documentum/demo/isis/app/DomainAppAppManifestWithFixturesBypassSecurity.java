package pro.documentum.demo.isis.app;

public class DomainAppAppManifestWithFixturesBypassSecurity extends
        DomainAppAppManifestWithFixtures {

    public DomainAppAppManifestWithFixturesBypassSecurity() {
        super();
    }

    @Override
    public String getAuthenticationMechanism() {
        return "bypass";
    }

    @Override
    public String getAuthorizationMechanism() {
        return "bypass";
    }

}
