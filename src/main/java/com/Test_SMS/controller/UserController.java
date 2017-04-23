package com.Test_SMS.controller;

import com.Test_SMS.model.JWTResponse;
import com.Test_SMS.service.EndPointService;
import com.Test_SMS.model.Recipients;
import com.Test_SMS.service.TextAlertServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

@RestController
public class UserController {


    @Autowired
    TextAlertServiceImpl alertService;
    @Autowired
    EndPointService endPointService;

    @RequestMapping("/user")
    public List<BigInteger> getUser(@RequestParam(value = "id", defaultValue = "1") String id) {
        return alertService.TestSendingSMS(id);
    }

//Contains Test Mappings or Request methods used to test code
    @RequestMapping(value = "/{id}/body", method = RequestMethod.GET)
    public ResponseEntity sendNumbers(@PathVariable("id") String id,
                                       @RequestParam(value = "test", required = false) String S, Recipients car) {
       car.setBody(S + "Testing");



        // TODO: call persistence layer to update
        return new ResponseEntity<>(car, HttpStatus.OK);
    }

  /*  @RequestMapping(value = "/")
    public ResponseEntity<Recipients> get() {

        Recipients test = new Recipients();
        test.setId(BigInteger.valueOf(3));
        test.setBody("Testing");

        return new ResponseEntity<Recipients>(test, HttpStatus.OK);
    }*/



    @RequestMapping(value = "/tess", method = RequestMethod.POST)
    public ResponseEntity<List<Recipients>> update(@RequestBody List<Recipients> car) {
//Java 8 Foreach used to loop and parse through JSON
        car.stream().forEach(c -> c.setBody(c.getBody() + "Testing"));
        car.stream().forEach(c -> {
            try {
                alertService.sendSms(String.valueOf(c.getId()), c.getBody());
            } catch (Exception e) {
                //e.toString();
                new OriginalExceptionFromAnotherApi(e.toString());
                //e.printStackTrace();
            }
        });


        // TODO: call persistence layer to update
        return new ResponseEntity<>(car, HttpStatus.OK);
    }
//Test Build to tet json parsing
    @RequestMapping(value = "/test-building")
    public ResponseEntity testPars(@RequestHeader(value = "Authorization") String Test,
                                                      @RequestBody List<Recipients> car) {
        Jws<Claims> jwsClaims = Jwts.parser()
                .setSigningKeyResolver(endPointService.getSigningKeyResolver())
                .parseClaimsJws(Test);

        car.stream().forEach(c -> c.setBody(c.getBody() + "Testing"));

        //RequestWrapper requestWrapper = new RequestWrapper(jwsClaims);

        //car.stream().forEach(c -> c.setBody(c.getBody() + "Testing"));
        car.stream().forEach(c -> {
            try {
                alertService.sendSms(String.valueOf(c.getId()), c.getBody());
            } catch (Exception e) {
                //e.toString();
                new OriginalExceptionFromAnotherApi(e.toString());
                //e.printStackTrace();
            }
        });

        return new ResponseEntity<>(car, HttpStatus.OK);

    }

    @RequestMapping(value = "/test-s")
    public JWTResponse testAuth(@RequestHeader(value = "Authorization") String Test,
                                 @RequestBody List<Recipients> car) {

        car.stream().forEach(c -> System.out.println(c.getBody() + "Testing"));
        car.stream().forEach(c -> {
            try {
                alertService.sendSms(String.valueOf(c.getId()), c.getBody());
            } catch (Exception e) {
                //e.toString();
                new OriginalExceptionFromAnotherApi(e.toString());
                //e.printStackTrace();
            }
        });

        Jws<Claims> jwsClaims = Jwts.parser()
                .setSigningKeyResolver(endPointService.getSigningKeyResolver())
                .parseClaimsJws(Test);

        return new JWTResponse(jwsClaims);
    }

///https://stackoverflow.com/questions/21923857/how-to-pass-list-parameters-for-each-object-using-spring-mvc?rq=1

    //https://stackoverflow.com/questions/21923663/how-to-have-multiple-parameters-using-spring-mvc-requestmapping
    /**
     * Error case, returns ErrorResponse which Spring automatically converts to JSON (using Jackson)
     * Content type will be application/json
     */
    @ExceptionHandler(OriginalExceptionFromAnotherApi.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ErrorResponse handle(OriginalExceptionFromAnotherApi e) {
        return new ErrorResponse(e.getMessage()); // use message from the original exception
    }

    public static class OriginalExceptionFromAnotherApi extends RuntimeException {
        public OriginalExceptionFromAnotherApi(String message) {
            super(message);
        }
    }

    /**
     * Defines the JSON output format of error responses
     */
    public static class ErrorResponse {
        public String message;

        public ErrorResponse(String message) {
            this.message = message;
        }
    }
}