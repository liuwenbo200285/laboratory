package com.wenbo.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class DiscardServerHandler extends SimpleChannelInboundHandler<Object>{

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		System.out.println("server channelRead0");
		if(msg instanceof String){
			System.out.println("server accept:"+msg);
		}else{
			ByteBuf byteBuf = (ByteBuf)msg;
			byte[] b = new byte[byteBuf.readableBytes()];
			byteBuf.readBytes(b);
			System.out.println("server accept:"+new String(b));
		}
		ByteBuf byteBuf = Unpooled.copiedBuffer("reg that!\n".getBytes());
		ctx.writeAndFlush(byteBuf);
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("server channelActive");
		super.channelActive(ctx);
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("server channelReadComplete");
		super.channelReadComplete(ctx);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		// TODO Auto-generated method stub
		super.exceptionCaught(ctx, cause);
	}

}
