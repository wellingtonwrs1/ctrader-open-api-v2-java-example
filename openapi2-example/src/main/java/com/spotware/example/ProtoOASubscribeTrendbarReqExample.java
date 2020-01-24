package com.spotware.example;

import com.google.protobuf.MessageLite;
import com.spotware.connect.Config;
import com.spotware.connect.netty.AuthHelper;
import com.spotware.connect.netty.NettyClient;
import com.spotware.connect.netty.handler.ProtoMessageReceiver;
import com.xtrader.protocol.openapi.v2.ProtoOAErrorRes;
import com.xtrader.protocol.openapi.v2.ProtoOASpotEvent;
import com.xtrader.protocol.openapi.v2.ProtoOASubscribeLiveTrendbarReq;
import com.xtrader.protocol.openapi.v2.ProtoOASubscribeSpotsReq;
import com.xtrader.protocol.openapi.v2.ProtoOAUnsubscribeLiveTrendbarReq;
import com.xtrader.protocol.openapi.v2.ProtoOAUnsubscribeSpotsReq;
import com.xtrader.protocol.openapi.v2.ProtoOAUnsubscribeSpotsRes;
import com.xtrader.protocol.openapi.v2.model.ProtoOATrendbarPeriod;

public class ProtoOASubscribeTrendbarReqExample {
	
	private static final long symbolId = 1;
	
	private static Config config;
    
	public static void main(String[] args) throws InterruptedException {
        config = new Config();
        NettyClient nettyClient = new NettyClient(config.getHost(), config.getPort());
        try {
        	Thread.sleep(1000);
            AuthHelper authHelper = nettyClient.getAuthHelper();
            authHelper.authorizeOnlyOneTrader(config.getClientId(), config.getClientSecret(), config.getCtid(), config.getAccessToken());

            addSpotListener(nettyClient, config.getCtid());
            subscribeSymbolSpot(nettyClient, config.getCtid());
            subscribeSymbolTrendbar(nettyClient, config.getCtid());
        } finally {
//        	nettyClient.closeConnection();
        }
    }
	
	private static void addSpotListener(NettyClient nettyClient, long ctidTraderAccountId) {
        nettyClient.addListener(message -> {
            MessageLite messageLite = message.getMessage();
            
            if (messageLite instanceof ProtoOASpotEvent) {
                ProtoOASpotEvent event = (ProtoOASpotEvent) messageLite;
                System.out.println("Received spot event:");
                System.out.println(event);
            }
        });
    }

    private static void subscribeSymbolSpot(NettyClient nettyClient, long ctidTraderAccountId) throws InterruptedException {
        ProtoOASubscribeSpotsReq protoOASubscribeSpotsReq = ProtoOASubscribeSpotsReq
                .newBuilder()
                .setCtidTraderAccountId(ctidTraderAccountId)
                .addSymbolId(symbolId)
                .build();
        ProtoMessageReceiver receiver = nettyClient.writeAndFlush(protoOASubscribeSpotsReq);

        MessageLite messageLite = receiver.waitSingleResult();

        if (messageLite instanceof ProtoOAErrorRes) {
            ProtoOAErrorRes errorRes = (ProtoOAErrorRes) messageLite;
            System.out.println(errorRes);
        }
//        unsubscribeSymbolSpot(nettyClient, ctidTraderAccountId);
    }
    
    private static void subscribeSymbolTrendbar(NettyClient nettyClient, long ctidTraderAccountId) throws InterruptedException {
        ProtoOASubscribeLiveTrendbarReq protoOASubscribeSpotsReq = ProtoOASubscribeLiveTrendbarReq
                .newBuilder()
                .setCtidTraderAccountId(ctidTraderAccountId)
                .setPeriod(ProtoOATrendbarPeriod.M1)
                .setSymbolId(symbolId)
                .build();
        ProtoMessageReceiver receiver = nettyClient.writeAndFlush(protoOASubscribeSpotsReq);

        MessageLite messageLite = receiver.waitSingleResult();

        if (messageLite instanceof ProtoOAErrorRes) {
            ProtoOAErrorRes errorRes = (ProtoOAErrorRes) messageLite;
            System.out.println(errorRes);
        }
//        unsubscribeSymbolTrendbar(nettyClient, ctidTraderAccountId);
    }

    private static void unsubscribeSymbolSpot(NettyClient nettyClient, long ctidTraderAccountId) throws InterruptedException {
        ProtoOAUnsubscribeSpotsReq protoOAUnsubscribeSpotsReq = ProtoOAUnsubscribeSpotsReq
                .newBuilder()
                .setCtidTraderAccountId(ctidTraderAccountId)
                .addSymbolId(symbolId)
                .build();
        ProtoMessageReceiver receiver = nettyClient.writeAndFlush(protoOAUnsubscribeSpotsReq);

        MessageLite messageLite = receiver.waitSingleResult();

        if (messageLite instanceof ProtoOAUnsubscribeSpotsRes) {
            ProtoOAUnsubscribeSpotsRes response = (ProtoOAUnsubscribeSpotsRes) messageLite;
            System.out.println("ProtoOAUnsubscribeSpotsRes: " + response);
        } else if (messageLite instanceof ProtoOAErrorRes) {
            ProtoOAErrorRes errorRes = (ProtoOAErrorRes) messageLite;
            System.out.println(errorRes);
        }
    }
    
    private static void unsubscribeSymbolTrendbar(NettyClient nettyClient, long ctidTraderAccountId) throws InterruptedException {
    	ProtoOAUnsubscribeLiveTrendbarReq protoOAUnsubscribeLiveTrendbarReq = ProtoOAUnsubscribeLiveTrendbarReq
    			.newBuilder()
    			.setCtidTraderAccountId(ctidTraderAccountId)
    			.setSymbolId(symbolId)
    			.build();
    	ProtoMessageReceiver receiver = nettyClient.writeAndFlush(protoOAUnsubscribeLiveTrendbarReq);
    	
    	MessageLite messageLite = receiver.waitSingleResult();
    	
    	if (messageLite instanceof ProtoOAErrorRes) {
    		ProtoOAErrorRes errorRes = (ProtoOAErrorRes) messageLite;
    		System.out.println(errorRes);
    	}
    }
    
}
