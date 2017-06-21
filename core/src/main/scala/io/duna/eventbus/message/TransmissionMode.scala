package io.duna.eventbus.message

sealed abstract class TransmissionMode

object Unicast extends TransmissionMode
object Broadcast extends TransmissionMode
