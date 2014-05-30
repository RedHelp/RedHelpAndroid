package org.redhelp.util;

import org.redhelp.common.RegisterRequest;

/**
 * Created by harshis on 5/19/14.
 */
public class RequestBuilder {

    public static RegisterRequest createRegisterRequest(String email, String name,
                                                        String password, String phone_number,
                                                        String externalId, Long accountType,
                                                        byte[] image) {
        RegisterRequest request = new RegisterRequest();
        request.setEmail(email);
        request.setName(name);
        request.setPassword(password);
        request.setPhoneNo(phone_number);
        request.setExternalAccountId(externalId);
        request.setAdditionalAccountType(accountType);
        request.setUser_image(image);

        return request;
    }
}
