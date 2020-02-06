package com.spotware.connect.netty;

import com.google.protobuf.MessageLite;
import com.spotware.connect.netty.exception.AuthorizationException;
import com.spotware.connect.netty.handler.ProtoMessageReceiver;
import com.xtrader.protocol.openapi.v2.ProtoOAAccountAuthReq;
import com.xtrader.protocol.openapi.v2.ProtoOAAccountAuthRes;
import com.xtrader.protocol.openapi.v2.ProtoOAApplicationAuthReq;
import com.xtrader.protocol.openapi.v2.ProtoOAApplicationAuthRes;

public class AuthHelper {

    public static MessageLite authorizeApplication(NettyClient nettyClient, String clientId, String clientSecret) throws InterruptedException {
        ProtoOAApplicationAuthReq appAuthReq = createAuthorizationRequest(clientId, clientSecret);
        ProtoMessageReceiver receiver = nettyClient.writeAndFlush(appAuthReq);
        return receiver.waitSingleResult();
    }

    public static MessageLite authorizeAccount(NettyClient nettyClient, long ctidTraderAccountId, String accessToken) throws InterruptedException {
        ProtoOAAccountAuthReq accountAuthorizationRequest = createAccountAuthorizationRequest(accessToken, ctidTraderAccountId);
        ProtoMessageReceiver receiver = nettyClient.writeAndFlush(accountAuthorizationRequest);
        return receiver.waitSingleResult();
    }

    public static void authorizeOnlyOneTrader(NettyClient nettyClient, String clientId, String clientSecret,long ctidTraderAccountId, String accessToken) throws InterruptedException {
        MessageLite applicationAuthRes = authorizeApplication(nettyClient, clientId, clientSecret);
        if (!(applicationAuthRes instanceof ProtoOAApplicationAuthRes)) {
            throw new AuthorizationException(applicationAuthRes.toString());
        }
        MessageLite accountAuthRes = authorizeAccount(nettyClient, ctidTraderAccountId, accessToken);
        if (!(accountAuthRes instanceof ProtoOAAccountAuthRes)) {
            throw new AuthorizationException(accountAuthRes.toString());
        }
    }

    private static ProtoOAApplicationAuthReq createAuthorizationRequest(String clientId, String clientSecret) {
        return ProtoOAApplicationAuthReq.newBuilder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .build();
    }

    private static ProtoOAAccountAuthReq createAccountAuthorizationRequest(String accessToken, long ctidTraderAccountId) {
        return ProtoOAAccountAuthReq.newBuilder()
                .setAccessToken(accessToken)
                .setCtidTraderAccountId(ctidTraderAccountId)
                .build();
    }
}
