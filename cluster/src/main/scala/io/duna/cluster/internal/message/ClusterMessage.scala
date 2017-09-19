package io.duna.cluster.internal.message

import io.duna.cluster.internal.message.MessageType.MessageType
import io.duna.cluster.internal.message.SignalType.SignalType
import io.duna.cluster.internal.message.TransmissonMode.TransmissonMode
import io.netty.buffer.ByteBuf

case class ClusterMessage(messageType: MessageType,
                          transmissonMode: TransmissonMode,
                          signalType: Option[SignalType],
                          headers: Map[String, String],
                          source: Option[String],
                          target: String,
                          replyTo: Option[String],
                          attachment: Option[Array[Byte]],
                          attachmentType: Option[String])

object ClusterMessage {
  def fromByteBuf(byteBuf: ByteBuf): ClusterMessage = {
    val messageType = byteBuf.readByte().toInt

    null
  }
}
