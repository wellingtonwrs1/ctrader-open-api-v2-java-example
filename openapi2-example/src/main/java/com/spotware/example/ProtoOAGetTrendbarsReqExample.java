package com.spotware.example;

import java.time.Duration;
import java.time.Instant;

import com.google.protobuf.MessageLite;
import com.spotware.connect.Config;
import com.spotware.connect.netty.AuthHelper;
import com.spotware.connect.netty.NettyClient;
import com.spotware.connect.netty.handler.ProtoMessageReceiver;
import com.xtrader.protocol.openapi.v2.ProtoOAErrorRes;
import com.xtrader.protocol.openapi.v2.ProtoOAGetTrendbarsReq;
import com.xtrader.protocol.openapi.v2.ProtoOAGetTrendbarsRes;
import com.xtrader.protocol.openapi.v2.model.ProtoOATrendbarPeriod;

public class ProtoOAGetTrendbarsReqExample {

	private static Config config;

	public static void main(String[] args) throws InterruptedException {
		config = new Config();
		NettyClient nettyClient = new NettyClient(config.getHost(), config.getPort());
		Thread.sleep(1000);
		sendProtoOAGetTrendbarsReq(nettyClient);
	}

	private static void sendProtoOAGetTrendbarsReq(NettyClient nettyClient) throws InterruptedException {
		try {
			AuthHelper authHelper = nettyClient.getAuthHelper();
            authHelper.authorizeOnlyOneTrader(config.getClientId(), config.getClientSecret(), config.getCtid(), config.getAccessToken());
			
            ProtoOAGetTrendbarsReq protoOAGetTrendbarsReq = ProtoOAGetTrendbarsReq
					.newBuilder()
					.setCtidTraderAccountId(config.getCtid())
					.setPeriod(ProtoOATrendbarPeriod.M5)
					.setSymbolId(1)
					.setFromTimestamp(Instant.now().minus(Duration.ofDays(2)).toEpochMilli())
					.setToTimestamp(Instant.now().toEpochMilli())
					.build();

			ProtoMessageReceiver receiver = nettyClient.writeAndFlush(protoOAGetTrendbarsReq);

			MessageLite messageLite = receiver.waitSingleResult();

			if (messageLite instanceof ProtoOAGetTrendbarsRes) {
				ProtoOAGetTrendbarsRes response = (ProtoOAGetTrendbarsRes) messageLite;
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
