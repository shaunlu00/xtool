package org.crudboy.toolbar.msexchange;

import microsoft.exchange.webservices.data.EWSConstants;
import microsoft.exchange.webservices.data.core.EwsSSLProtocolSocketFactory;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import java.security.GeneralSecurityException;

public class CustomExchangeService extends ExchangeService {
    private static final HostnameVerifier hostnameVerifierWithOutVerify = new HostnameVerifier() {
        @Override
        public boolean verify(String s, SSLSession sslSession) {
            return true;
        }
    };

    public CustomExchangeService() {
        super();
    }

    public CustomExchangeService(ExchangeVersion exchangeVersion) {
        super(exchangeVersion);
    }

    protected Registry<ConnectionSocketFactory> createConnectionSocketFactoryRegistry() {
        try {
            return RegistryBuilder.<ConnectionSocketFactory>create()
                    .register(EWSConstants.HTTP_SCHEME, new PlainConnectionSocketFactory())
                    .register(EWSConstants.HTTPS_SCHEME, EwsSSLProtocolSocketFactory
                            .build(null, hostnameVerifierWithOutVerify)).build();
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("Could not initialize ConnectionSocketFactory instances for HttpClientConnectionManager", e);
        }
    }
}
