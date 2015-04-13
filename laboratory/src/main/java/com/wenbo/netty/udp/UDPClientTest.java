package com.wenbo.netty.udp;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public class UDPClientTest {

		public static long count = 0;
		
		public static void main(String[] args) throws Exception {
			
	        EventLoopGroup group = new NioEventLoopGroup();
	        try {
	            Bootstrap b = new Bootstrap();
	            b.group(group)
	             .channel(NioDatagramChannel.class)
	             //.option(ChannelOption.SO_BROADCAST, true)
	             .handler(new UDPClientHandler());

	            Channel ch = b.bind(0).sync().channel(); 
	            
				long startTime = System.nanoTime();
				
	            //for (int i = 0; i < 10000000; ++i) {
				//for (int i = 0; i < 100000; ++i) {
		            ByteBuf encoded = ch.alloc().buffer(37);
		            
		            int version = 0X10000001;
		            encoded.writeInt(version);
		            
		            short cmd = 1001;
		            encoded.writeShort(cmd);
		            
		            // flags 无效
		            encoded.writeShort(1);
		            
		            String pluginName = "mqc";
		            encoded.writeInt(pluginName.length());
		            encoded.writeBytes(pluginName.getBytes());
		            encoded.writeInt(3);
		            
		            String varName = "root";
		            encoded.writeInt(varName.length());
		            encoded.writeBytes(varName.getBytes());
		            
		            String var = "664111";
		            encoded.writeInt(var.length());
		            encoded.writeBytes(var.getBytes());
		            
	            	ch.writeAndFlush(new DatagramPacket(encoded, new InetSocketAddress("127.0.0.1", 7686))).sync();
				
//	            	 Thread.sleep(1);
//				}
				
	        ch.closeFuture().await();  
	        
			long estimatedTime = System.nanoTime();
			long t = estimatedTime - startTime;
			System.out.println("time" + TimeUnit.NANOSECONDS.toSeconds(t));

	        } finally {
	            group.shutdownGracefully();
	        }
	}
}
