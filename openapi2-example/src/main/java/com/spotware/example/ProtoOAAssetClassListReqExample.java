package com.spotware.example;

import com.google.protobuf.MessageLite;
import com.spotware.connect.Config;
import com.spotware.connect.netty.AuthHelper;
import com.spotware.connect.netty.NettyClient;
import com.spotware.connect.netty.handler.ProtoMessageReceiver;
import com.xtrader.protocol.openapi.v2.ProtoOAErrorRes;
import com.xtrader.protocol.openapi.v2.ProtoOAAssetClassListReq;
import com.xtrader.protocol.openapi.v2.ProtoOAAssetClassListRes;

public class ProtoOAAssetClassListReqExample {

	private static Config config;

	public static void main(String[] args) throws InterruptedException {
		config = new Config();
		NettyClient nettyClient = new NettyClient(config.getHost(), config.getPort());
		sendProtoOAGetAccountListByAccessTokenReq(nettyClient);
	}

	private static void sendProtoOAGetAccountListByAccessTokenReq(NettyClient nettyClient) throws InterruptedException {
		try {
			AuthHelper authHelper = nettyClient.getAuthHelper();
			
			authHelper.authorizeOnlyOneTrader(config.getClientId(), config.getClientSecret(), config.getCtid(), config.getAccessToken());

			ProtoOAAssetClassListReq protoOAAssetClassListReq = ProtoOAAssetClassListReq
					.newBuilder()
					.setCtidTraderAccountId(config.getCtid())
					.build();

			ProtoMessageReceiver receiver = nettyClient.writeAndFlush(protoOAAssetClassListReq);

			MessageLite messageLite = receiver.waitSingleResult();

			if (messageLite instanceof ProtoOAAssetClassListRes) {
				ProtoOAAssetClassListRes response = (ProtoOAAssetClassListRes) messageLite;
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
