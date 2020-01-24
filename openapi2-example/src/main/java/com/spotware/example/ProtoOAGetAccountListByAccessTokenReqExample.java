package com.spotware.example;

import com.google.protobuf.MessageLite;
import com.spotware.connect.Config;
import com.spotware.connect.netty.AuthHelper;
import com.spotware.connect.netty.NettyClient;
import com.spotware.connect.netty.exception.AuthorizationException;
import com.spotware.connect.netty.handler.ProtoMessageReceiver;
import com.xtrader.protocol.openapi.v2.ProtoOAApplicationAuthRes;
import com.xtrader.protocol.openapi.v2.ProtoOAErrorRes;
import com.xtrader.protocol.openapi.v2.ProtoOAGetAccountListByAccessTokenReq;
import com.xtrader.protocol.openapi.v2.ProtoOAGetAccountListByAccessTokenRes;

public class ProtoOAGetAccountListByAccessTokenReqExample {

	private static Config config;

	public static void main(String[] args) throws InterruptedException {
		config = new Config();
		NettyClient nettyClient = new NettyClient(config.getHost(), config.getPort());
		Thread.sleep(1000);
		sendProtoOAGetAccountListByAccessTokenReq(nettyClient);
	}

	private static void sendProtoOAGetAccountListByAccessTokenReq(NettyClient nettyClient) throws InterruptedException {
		try {
			AuthHelper authHelper = nettyClient.getAuthHelper();

            MessageLite applicationAuthRes = authHelper.authorizeApplication(config.getClientId(), config.getClientSecret());
            if (applicationAuthRes instanceof ProtoOAApplicationAuthRes) {
                System.out.println("Response ProtoOAApplicationAuthRes received.");
                System.out.println("Response: " + ((ProtoOAApplicationAuthRes) applicationAuthRes).getPayloadType());
            } else {
                System.out.println("Something went wrong");
                System.out.println("Response: " + applicationAuthRes);
                throw new AuthorizationException("application can't be authorize");
            }
			
			ProtoOAGetAccountListByAccessTokenReq protoOAGetAccountListByAccessTokenReq = ProtoOAGetAccountListByAccessTokenReq
					.newBuilder()
					.setAccessToken(config.getAccessToken())
					.build();

			ProtoMessageReceiver receiver = nettyClient.writeAndFlush(protoOAGetAccountListByAccessTokenReq);

			MessageLite messageLite = receiver.waitSingleResult();

			if (messageLite instanceof ProtoOAGetAccountListByAccessTokenRes) {
				ProtoOAGetAccountListByAccessTokenRes response = (ProtoOAGetAccountListByAccessTokenRes) messageLite;
				System.out.println(response);
			} else if (messageLite instanceof ProtoOAErrorRes) {
				ProtoOAErrorRes errorRes = (ProtoOAErrorRes) messageLite;
				System.out.println(errorRes);
			}
		} finally {
			nettyClient.closeConnection();
		}
	}

}
