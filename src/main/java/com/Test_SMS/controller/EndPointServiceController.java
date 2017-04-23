package com.Test_SMS.controller;

import com.Test_SMS.model.JWTResponse;
import com.Test_SMS.model.PublicCreds;
import com.Test_SMS.service.EndPointService;
import com.Test_SMS.model.Recipients;
import com.Test_SMS.service.TextAlertServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.sun.deploy.trace.Trace.print;

@RestController
public class EndPointServiceController {

    @Autowired
    EndPointService endPointService;
    @Autowired
    TextAlertServiceImpl alertService;


    @RequestMapping("/rMyCreds")
    public PublicCreds rMyCreds() {
        return endPointService.refreshMyCreds();
    }

    @RequestMapping("/get-my-public-creds")
    public PublicCreds getMyPCreds() {
        return endPointService.getMyPublicCreds();
    }

    @RequestMapping("/add-public-creds")
    public PublicCreds addPublicCreds(@RequestBody PublicCreds publicCreds) {
        endPointService.addPublicCreds(publicCreds);
        // just to prove that the key was successfully added
        return endPointService.getPublicCreds(publicCreds.getKid());
    }
//build operation uses the microservice’s auto-generated private key to sign the JWT
    //JWT with some hard-coded custom and registered claims

    @RequestMapping("/buildTestJWT")//tb
    public JWTResponse buildTestJWT() {
        String jws = Jwts.builder()
                .setHeaderParam("kid", endPointService.getMyPublicCreds().getKid())//public key id public key is stored in a Map identified by the unique kid
                .setSubject("Test ")
                .claim("id", "Test Sid User")
                .claim("Reciepents", true)
                .signWith( //microservice’s private key with RS266 Encryption
                        SignatureAlgorithm.RS256,
                        endPointService.getMyPrivateKey()
                )
                .compact();
        return new JWTResponse(jws);
    }
//parse operation uses the matching public key to verify the signature.


    @RequestMapping(value = "/test-jwt")
    public ResponseEntity testJWT(@RequestHeader(value = "Authorization") String Test,
                                  @RequestBody List<Recipients> car) {

        Jws<Claims> jwsClaims = Jwts.parser()
                .setSigningKeyResolver(endPointService.getSigningKeyResolver())
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


        //RequestWrapper requestWrapper = new RequestWrapper(jwsClaims);
        return new ResponseEntity<>(car, HttpStatus.OK);
    }

}
