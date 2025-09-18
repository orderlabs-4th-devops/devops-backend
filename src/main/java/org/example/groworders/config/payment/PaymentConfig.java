package org.example.groworders.config.payment;

//import com.siot.IamportRestClient.IamportClient;
import io.portone.sdk.server.payment.PaymentClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentConfig {

    @Bean
    public PaymentClient paymentClient() {
        return new PaymentClient(
                "Rv67lh9SDnUlwpPsYgVqnImJQavEhio0Sap2XN4QqF8oSDmw9z4z2huU51ZCNJvmiCsIg9VN50kZdzqt",
                "https://api.portone.io",
                "store-3513dda2-1134-48b8-bc99-e3d1487021d7"
        );
    }
}