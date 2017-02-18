package com.Test_SMS.controller;
import com.messagebird.MessageBirdServiceImpl;
import com.messagebird.MessageBirdService;
import com.messagebird.MessageBirdClient;
import com.messagebird.exceptions.GeneralException;
import com.messagebird.exceptions.UnauthorizedException;
import com.messagebird.objects.MessageResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
/**
 * Created by Siddharth Varjivan on 21/01/2017.
 */
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by Siddharth Varjivan on 07/02/2017.
 */

@Service
public class TextAlertServiceImpl implements TextAlertService {

    private static final Log logger = LogFactory.getLog(TextAlertServiceImpl.class);

    final MessageBirdService wsr = new MessageBirdServiceImpl("HY06MihdXt2GAPkCzd94gegQP");
    //  MessageBirdClient messageBirdClient;
    final MessageBirdClient messageBirdClient = new MessageBirdClient(wsr);

   // @Value("${messagebird.originatorName}")
    String messageBirdOriginator = "MessageBird";

    @Override
    public MessageResponse sendSms(String receiverPhoneNumber, String message) throws Exception {
        List<BigInteger> phones = new ArrayList<BigInteger>();
        if(message.length()<=600) {
            //if (receiverPhoneNumber.length() == 12) {
                phones.add(new BigInteger(receiverPhoneNumber));
                MessageResponse response = sendMutipleSms(phones, message);
                logger.info("Status Text Alert >>>" + response.toString());
                return response;
           // } else {
              //  logger.error("Trying to send sms to : " + receiverPhoneNumber + " avoided as its not 12 digits");
              //  throw new Exception(receiverPhoneNumber + " is not 12 digits");
            //}
        } else {
            logger.error("BIG sms body to : " + receiverPhoneNumber + " avoided!!!");
            throw new Exception(receiverPhoneNumber + " has sms body more than 400 characters");
        }
    }


    public MessageResponse sendMutipleSms(List<BigInteger> phones, String msg) throws Exception {
        try {
            final MessageResponse response = messageBirdClient.sendMessage(messageBirdOriginator, msg, phones);
            System.out.println(response.toString());
            logger.error(response.toString());
            return response;
        } catch (UnauthorizedException unauthorized) {
            if (unauthorized.getErrors() != null) {
                System.out.println(unauthorized.getErrors().toString());
                logger.error(unauthorized.getErrors().toString());
            }
            unauthorized.printStackTrace();
            throw unauthorized;
        } catch (GeneralException generalException) {
            if (generalException.getErrors() != null) {
                System.out.println(generalException.getErrors().toString());
                logger.error(generalException.getErrors().toArray());
            }
            generalException.printStackTrace();
            throw generalException;
        }
    }

    public List<BigInteger> TestSendingSMS(String receiverPhoneNumber) {

        List<BigInteger> phones = new ArrayList<BigInteger>();
        phones.add(new BigInteger(receiverPhoneNumber));

        return phones;
    }
}