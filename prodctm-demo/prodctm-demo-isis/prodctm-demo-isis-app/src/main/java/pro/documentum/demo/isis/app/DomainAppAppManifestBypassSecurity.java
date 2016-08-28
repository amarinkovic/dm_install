package pro.documentum.demo.isis.app;

public class DomainAppAppManifestBypassSecurity extends DomainAppAppManifest {

    public DomainAppAppManifestBypassSecurity() {
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
