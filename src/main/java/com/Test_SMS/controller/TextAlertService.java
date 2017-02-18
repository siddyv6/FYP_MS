package com.Test_SMS.controller;
/**
 * Created by Siddharth Varjivan on 21/01/2017.
 */
import com.messagebird.objects.MessageResponse;

public interface TextAlertService {
    /**
     * Sending SMS/text messages to  users
     * @param receiverPhoneNumber
     * @param message
     */
    MessageResponse sendSms(String receiverPhoneNumber, String message) throws Exception;


}
