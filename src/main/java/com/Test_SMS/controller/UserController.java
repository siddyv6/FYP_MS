package com.Test_SMS.controller;

import com.Test_SMS.Security.Model.JWTResponse;
import com.Test_SMS.Security.Service.SecretService;
import com.Test_SMS.service.TextAlertServiceImpl;
import com.messagebird.objects.MessageResponse;
import com.Test_SMS.model.Recipients;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.math.BigInteger;
import java.util.List;

@RestController
public class UserController {
//testr

    @Autowired
    TextAlertServiceImpl alertService;
    SecretService secretService;

    @RequestMapping("/user")
    public List<BigInteger> getUser(@RequestParam(value="id", defaultValue="1") String id) {
        return alertService.TestSendingSMS(id);
    }


    @RequestMapping(value = "{id}/body", method = RequestMethod.GET)
    public MessageResponse sendNumbers(@PathVariable("id") String id,
                                       @RequestParam(value = "test", required = false) String test,
                                       Model map){
            map.addAttribute("msg" +
                    id +", "+test);
        try {
            //return map.toString();
           return alertService.sendSms(id,test);
        } catch (Exception e) {
            if (e.getCause() != null) {
                System.out.println(e.getMessage());
            }
            e.printStackTrace();
            //return e.getMessage();
        }
        // just to prove that the key was successfully added
        return null;
    }

    @RequestMapping(value = "/")
    public ResponseEntity<Recipients> get() {

        Recipients test = new Recipients();
        test.setId(BigInteger.valueOf(3));
        test.setBody("Testing");

        return new ResponseEntity<Recipients>(test, HttpStatus.OK);
    }

    @RequestMapping(value = "/tess", method = RequestMethod.POST)
    public ResponseEntity<List<Recipients>> update(@RequestBody List<Recipients> car) {

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
        return new ResponseEntity<List<Recipients>>(car, HttpStatus.OK);
    }

    @RequestMapping(value = "/test-s")
    public JWTResponse testParse(@RequestHeader(value="Authorization") String Test,
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
                .setSigningKeyResolver(secretService.getSigningKeyResolver())
                .parseClaimsJws(Test);

        return new JWTResponse(jwsClaims);
    }

    /**
     * Error case, returns ErrorResponse which Spring automatically converts to JSON (using Jackson)
     * Content type will be application/json
     */
    @ExceptionHandler(OriginalExceptionFromAnotherApi.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ErrorResponse handle(OriginalExceptionFromAnotherApi e) {
        return new ErrorResponse(e.getMessage()); // use message from the original exception
    }

    private static class OriginalExceptionFromAnotherApi extends RuntimeException {
        public OriginalExceptionFromAnotherApi(String message) {
            super(message);
        }
    }

    /**
     * Defines the JSON output format of error responses
     */
    private static class ErrorResponse {
        public String message;

        public ErrorResponse(String message) {
            this.message = message;
        }
    }
}