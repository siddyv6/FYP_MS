package com.Test_SMS.controller;

import com.Test_SMS.model.JWTResponse;
import com.Test_SMS.model.PublicCreds;
import com.Test_SMS.model.Recipients;
import com.Test_SMS.model.RequestWrapper;
import com.Test_SMS.service.SecretService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@RestController
public class SecretServiceController extends BaseController {

    @Autowired
    SecretService secretService;

    @RequestMapping("/refresh-my-creds")
    public PublicCreds refreshMyCreds() {
        return secretService.refreshMyCreds();
    }

    @RequestMapping("/get-my-public-creds")
    public PublicCreds getMyPublicCreds() {
        return secretService.getMyPublicCreds();
    }

    @RequestMapping("/add-public-creds")
    public PublicCreds addPublicCreds(@RequestBody PublicCreds publicCreds) {
        secretService.addPublicCreds(publicCreds);
        // just to prove that the key was successfully added
        return secretService.getPublicCreds(publicCreds.getKid());
    }
    @RequestMapping("/test-build")
    public JWTResponse testBuild() {
        String jws = Jwts.builder()
            .setHeaderParam("kid", secretService.getMyPublicCreds().getKid())
            .setIssuer("Stormpath")
            .setSubject("msilverman")
            .claim("id", "Micah Silverman")
            .claim("hasMotorcycle", true)
            .setIssuedAt(Date.from(Instant.ofEpochSecond(1466796822L)))   // Fri Jun 24 2016 15:33:42 GMT-0400 (EDT)
            .setExpiration(Date.from(Instant.ofEpochSecond(4622470422L))) // Sat Jun 24 2116 15:33:42 GMT-0400 (EDT)
            .signWith(
                SignatureAlgorithm.RS256,
                secretService.getMyPrivateKey()
            )
            .compact();
        return new JWTResponse(jws);
    }

    @RequestMapping(value = "/test-parse")
    public RequestWrapper testParse(@RequestHeader(value="Authorization") String Test,
                                    @RequestBody List<Recipients> car) {

        Jws<Claims> jwsClaims = Jwts.parser()
            .setSigningKeyResolver(secretService.getSigningKeyResolver())
            .parseClaimsJws(Test);
        car.stream().forEach(c -> c.setBody(c.getBody() + "Testing"));
        RequestWrapper media = new RequestWrapper(jwsClaims);
        media.setRecipientsList(car);

        //RequestWrapper requestWrapper = new RequestWrapper(jwsClaims);
        return media;
    }
}
