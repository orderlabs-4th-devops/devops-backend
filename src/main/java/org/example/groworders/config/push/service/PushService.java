package org.example.groworders.config.push.service;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.example.groworders.config.push.model.dto.PushDto;
import org.example.groworders.config.push.repository.PushHistoryRepository;
import org.example.groworders.config.push.repository.PushSubRepository;
import org.jose4j.lang.JoseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.util.concurrent.ExecutionException;


@Service
public class PushService {
    private final nl.martijndwars.webpush.PushService pushService;
    private final PushSubRepository pushSubRepository;

    public PushService(
            PushSubRepository pushSubRepository,
            PushHistoryRepository pushHistoryRepository,
            PushHistoryService pushHistoryService,
            @Value("${webpush.public-key}") String publicKey,
            @Value("${webpush.private-key}") String privateKey,
            @Value("${webpush.subject}") String subject
    ) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {

        this.pushSubRepository = pushSubRepository;

        if (Security.getProperty(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }

        nl.martijndwars.webpush.PushService svc = new nl.martijndwars.webpush.PushService();
        svc.setPublicKey(publicKey);
        svc.setPrivateKey(privateKey);
        svc.setSubject(subject);
        this.pushService = svc;
    }

    // 구독 정보 저장
    public void subscribe(PushDto.Subscribe dto, Long id) {
        if(!pushSubRepository.existsByEndpoint(dto.getEndpoint())) {
            pushSubRepository.save(dto.toEntity(id));
        }
    }

    // 푸시 알림 발송
    public void send(PushDto.Send dto) throws GeneralSecurityException, JoseException, IOException, ExecutionException, InterruptedException {
        pushService.send(dto.toEntity());
    }
}


