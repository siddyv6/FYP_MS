package com.Test_SMS.Security.Controller;

import com.Test_SMS.Security.Model.JWTResponse;
import com.Test_SMS.Security.Model.PublicCreds;
import com.Test_SMS.Security.Service.SecretService;
import com.Test_SMS.controller.UserController;
import com.Test_SMS.model.Recipients;
import com.Test_SMS.model.RequestWrapper;
import com.Test_SMS.service.TextAlertServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import static com.sun.deploy.trace.Trace.print;

@RestController
public class SecretServiceController extends BaseController {

    @Autowired
    SecretService secretService;
    @Autowired
    TextAlertServiceImpl alertService;


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
//build operation uses the microservice’s auto-generated private key to sign the JWT
    //JWT with some hard-coded custom and registered claims

    @RequestMapping("/test-build")
    public JWTResponse testBuild() {
        String jws = Jwts.builder()
                .setHeaderParam("kid", secretService.getMyPublicCreds().getKid())//public key id public key is stored in a Map identified by the unique kid
                .setIssuer("Test")
                .setSubject("Test ")
                .claim("id", "Test Sid User")
                .claim("Reciepents", true)
                .setIssuedAt(Date.from(Instant.ofEpochSecond(1466796822L)))   //
                .setExpiration(Date.from(Instant.ofEpochSecond(4622470422L))) //
                .signWith( //microservice’s private key with RS266 Encryption
                        SignatureAlgorithm.RS256,
                        secretService.getMyPrivateKey()
                )
                .compact();
        return new JWTResponse(jws);
    }
//parse operation uses the matching public key to verify the signature.


    @RequestMapping(value = "/test-parse")
    public RequestWrapper testParse(@RequestHeader(value = "Authorization") String Test,
                                    @RequestBody List<Recipients> car) {

        Jws<Claims> jwsClaims = Jwts.parser()
                .setSigningKeyResolver(secretService.getSigningKeyResolver())
                .parseClaimsJws(Test);
      /*  car.stream().forEach((Recipients c) -> {

            try {
                alertService.sendSms(String.valueOf(c.getId()), c.getBody());
                System.out.println(String.valueOf(c.getId()) + c.getBody() + "Testing");
            } catch (Exception e) {
                e.printStackTrace();
            }

        });*/

        car.stream().forEach(c -> c.setBody(c.getBody() + "Testing"));
        car.stream().forEach(c -> System.out.println(String.valueOf(c.getId()) + c.getBody() + "Testing"));
        car.stream().forEach(c -> {
            try {
                alertService.sendSms(String.valueOf(c.getId()), c.getBody());
            } catch (Exception e) {
                //e.toString();
                //new UserController.OriginalExceptionFromAnotherApi(e.toString());
                e.printStackTrace();
            }
        });

        RequestWrapper media = new RequestWrapper(jwsClaims);
        media.setRecipientsList(car);

        //RequestWrapper requestWrapper = new RequestWrapper(jwsClaims);
        return media;
    }

}
