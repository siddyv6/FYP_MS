package com.Test_SMS.service;

import com.Test_SMS.model.PublicCreds;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.TextCodec;
import io.jsonwebtoken.impl.crypto.RsaProvider;
import io.jsonwebtoken.lang.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class EndPointService {

    private static final Logger log = LoggerFactory.getLogger(EndPointService.class);

    private KeyPair myKeyPair;//Public and Private Key.
    private String kid;

    private Map<String, PublicKey> publicKeys = new HashMap<>();

    @PostConstruct
    public void setup() {
        refreshMyCreds();
    }
//microservice creates a key-pair for itself

    //https://github.com/jwtk/jjwt
    private SigningKeyResolver signingKeyResolver = new SigningKeyResolverAdapter() {
        @Override
        public Key resolveSigningKey(JwsHeader header, Claims claims) {
            String kid = header.getKeyId();
            if (!Strings.hasText(kid)) {
                throw new JwtException("Missing 'kid':" + claims);
            }
            Key key = publicKeys.get(kid);
            if (key == null) {
                throw new JwtException("No public key for kid: " + kid + ". JWT claims: " + claims);
            }
            return key;
        }
    };

    public SigningKeyResolver getSigningKeyResolver() {
        return signingKeyResolver;
    }

    public PublicCreds getPublicCreds(String kid) {
        return createPublicCreds(kid, publicKeys.get(kid));
    }

    public PublicCreds getMyPublicCreds() {
        return createPublicCreds(this.kid, myKeyPair.getPublic());
    }

    private PublicCreds createPublicCreds(String kid, PublicKey key) {
        return new PublicCreds(kid, TextCodec.BASE64URL.encode(key.getEncoded()));
    }
//https://www.leveluplunch.com/java/tutorials/014-post-json-to-spring-rest-webservice/
    public PrivateKey getMyPrivateKey() {
        return myKeyPair.getPrivate();
    }

    public PublicCreds refreshMyCreds() {
        myKeyPair = RsaProvider.generateKeyPair(1024); //KeyPairGenerator class is used to generate pairs of public and private keys. Key pair generators are constructed using the getInstance factory methods (static methods that return instances of a given class).

        kid = UUID.randomUUID().toString();
        //generate unique sequences of bytes
        PublicCreds publicCreds = getMyPublicCreds();

        // this microservice will trust itself
        addPublicCreds(publicCreds);

        return publicCreds;
    }

    public void addPublicCreds(PublicCreds publicCreds) {
        byte[] encoded = TextCodec.BASE64URL.decode(publicCreds.getB64UrlPublicKey());

        PublicKey publicKey = null;
        try {
            publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(encoded));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.error("Unable to create public key: {}", e.getMessage(), e);
        }

        publicKeys.put(publicCreds.getKid(), publicKey);
    }
}
